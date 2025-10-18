package com.xcg.servicepayment.controller;

import com.xcg.servicepayment.service.AlipayService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/alipay")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "支付宝")
public class AlipayController {

    private final AlipayService alipayService;

    /**
     * 支付接口
     * @param orderNo 订单号
     * @param totalAmount 支付金额
     * @param title 标题
     * @return form表单
     */
    @PostMapping("/pay")
    public String pay(String orderNo, String totalAmount, String title) {
        log.info("订单号:{},金额:{},标题:{}", orderNo, totalAmount, title);
        return alipayService.pay(orderNo, totalAmount, title);
    }

    /**
     * 支付宝异步通知处理接口
     * 流程：
     * 1. 获取支付宝POST过来反馈信息
     * 2. 验证签名
     * 3. 解析业务参数
     * 4. 根据支付结果更新订单状态,记录支付结果
     * 5. 返回success响应给支付宝
     */
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        return alipayService.asyncNotify(request);
    }

    /**
     * 支付宝同步返回处理接口
     * @param request 请求
     * @return 支付成功跳转页面
     */
    @GetMapping("/return")
    public String returnCallback(HttpServletRequest request) {
        // 处理同步返回，展示支付结果给用户
        log.info("用户从支付宝返回");
        return "paySuccess"; // 返回支付成功页面
    }


}
