package com.xcg.serviceorder.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    

    /**
     * 发送纯文本邮件
     * @param to 收件人邮箱
     * @param subject 主题
     * @param text 内容
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("3039049837@qq.com");  // 发件人（需与配置中的username一致）
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    
    /**
     * 发送HTML邮件（支持富文本、附件等）
     * @param to 收件人邮箱
     * @param subject 主题
     * @param htmlContent HTML内容
     * @param filePath 附件路径
     * @throws MessagingException 邮件发送异常
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent, String filePath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("3039049837@qq.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);  // 第二个参数表示是否为HTML

        // 添加附件（可选）
        // FileSystemResource file = new FileSystemResource(new File(filePath));
        // helper.addAttachment("附件名称.pdf", file);

        mailSender.send(message);
    }
}