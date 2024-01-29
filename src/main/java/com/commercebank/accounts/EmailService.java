package com.commercebank.accounts;


import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
@Service
public class EmailService {
    @Autowired private JavaMailSender mailSender;
    public void sendMultiFacAuthEmail(String to, int code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("commercebank@mail.com");
        message.setTo(to);
        message.setSubject("One-time login verification code");
        message.setText("Here is your code: " + code);

        mailSender.send(message);
    }

}