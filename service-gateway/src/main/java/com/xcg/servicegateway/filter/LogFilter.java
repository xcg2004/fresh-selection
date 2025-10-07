package com.xcg.servicegateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LogFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();

        ServerHttpRequest request = exchange.getRequest();

        // 在响应完成后记录耗时
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;

                    // 记录请求路径、状态码和耗时
                    log.info("Request: {} {} | Status: {} | Duration: {} ms slow: {}",
                            request.getMethod(),
                            request.getURI().getPath(),
                            response.getStatusCode(),
                            duration,
                            duration > 1000 ? "true" : "false");
                })
        );
    }

    @Override
    public int getOrder() {
        // 设置过滤器优先级，数值越小优先级越高
        return 0;
    }
}
