package com.xcg.servicegateway.filter;

import com.xcg.freshcommon.core.utils.JwtUtil;
import com.xcg.freshcommon.rabbitmq.constants.RedisConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private final StringRedisTemplate stringRedisTemplate;
    // 路径匹配器
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 白名单路径 - 无需认证的路径
    private static final List<String> WHITE_LIST = List.of(
            "/api/user/login",
            "/api/user/register",
            "/api/public/**",
            "/api/user/token"
    );



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.info("AuthFilter processing request: {}", path);

        // 1. 检查白名单路径
        if (isWhiteListPath(path)) {
            log.info("Whitelist path, skip authentication: {}", path);
            return chain.filter(exchange);
        }

        // 2. 从请求头中获取认证信息
        String token = extractToken(request);
        if (token == null || token.isEmpty()) {
            log.warn("Missing authentication token for path: {}", path);
            return unauthorized(exchange.getResponse());
        }
//        eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4Y2ciLCJ1c2VySWQiOjEsImlhdCI6MTc1OTQ3ODYwOSwiZXhwIjoxNzU5NDgyMjA5fQ.W1ijEO-CBIyvIKElF4dKfeGNNVjZ-paxhLVCM8n4-TQ
        // 3. 验证token有效性
        if (!validateToken(token)) {
            log.warn("Invalid or expired token: {}", token);
            return forbidden(exchange.getResponse());
        }

        // 4. 将用户信息传递给下游服务（可选）
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-ID", String.valueOf(getUserIdFromToken(token)))
//                .header("X-User-Roles", getUserRolesFromToken(token))
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    /**
     * 检查是否为白名单路径
     */
    private boolean isWhiteListPath(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 从请求中提取token
     */
    private String extractToken(ServerHttpRequest request) {
        // 从 Authorization 头获取 Bearer token
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 从 query parameter 获取 token
        return request.getQueryParams().getFirst("token");
    }

    /**
     * 验证token有效性（需要根据实际认证机制实现）
     */
    private boolean validateToken(String token) {
        try {
            // 1. JWT token解析和验证
            Long userIdFromToken = jwtUtil.getUserIdFromToken(token);
            if(userIdFromToken == null){
                return false;
            }

            // 2. 查询Redis缓存验证token
            String s = stringRedisTemplate.opsForValue().get(RedisConstants.TOKEN_PREFIX + userIdFromToken);
            return s != null && s.equals(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }


    /**
     * 从token中提取用户ID（需要根据实际token结构实现）
     */
    private Long getUserIdFromToken(String token) {
        // 实际项目中应该解析token获取用户ID
        return jwtUtil.getUserIdFromToken(token);
    }

    /**
     * 从token中提取用户角色（需要根据实际token结构实现）
     */
    private String getUserRolesFromToken(String token) {
        // 实际项目中应该解析token获取用户角色
        return "USER,ADMIN";
    }

    /**
     * 返回401未认证响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = "{\"code\": 401, \"message\": \"未提供认证信息\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    /**
     * 返回403禁止访问响应
     */
    private Mono<Void> forbidden(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = "{\"code\": 403, \"message\": \"登录状态已失效\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        return 0; // 数值越小优先级越高
    }
}
