package com.example.lamlaisecurity.event.listener;

import com.example.lamlaisecurity.entity.User;
import com.example.lamlaisecurity.event.RegistrationCompleteEvent;
import com.example.lamlaisecurity.service.design.EmailVerifyTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private EmailVerifyTokenService verifyTokenService;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        User user = event.getUser();
        String verificationToken = UUID.randomUUID().toString();
        Long expiredTime = verifyTokenService.saveUserVerificationToken(user, verificationToken);
        String url = event.getApplicationUrl() + "/api/v1/auth/emailVerifyToken?token=" + verificationToken;

        try {
            sendVerificationEmail(user, url, expiredTime);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        log.info("Nhấn vào đây để xác thực tài khoản :  {}", url);
    }

    public void sendVerificationEmail(User user, String url, Long expiredTime) throws MessagingException, UnsupportedEncodingException {
        long countTime = expiredTime - System.currentTimeMillis();
        long hours = TimeUnit.MILLISECONDS.toHours(countTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(countTime) % 60;

        String result;

        if (hours > 0) {
            result = String.format("%02dh%02dp", hours, minutes);
        } else {
            result = minutes + "p";
        }

        String subject = "Xác nhận email";
        String senderName = "Shop mua sắm";
        String mailContent = "<p> Xin chào, " + user.getFullName() + ", </p>" +
                "<p>Cảm ơn bạn đã tin dùng dịch vụ của chúng tôi," +
                "Nhấn vào link sau để kích hoạt tài khoản của bạn</p>" +
                "<a href=\"" + url + "\">Kích hoạt tài khoản</a>" +
                "<p> Xin cảm ơn </p>" +
                "<p>Mã kích hoạt sẽ hết hạn sau " + result + "</p>";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("PhamHuggg@gmail.com", senderName);
        messageHelper.setTo("manhthangthang@gmail.com");
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
