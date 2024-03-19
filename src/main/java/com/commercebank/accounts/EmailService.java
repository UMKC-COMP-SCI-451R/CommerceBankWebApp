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
        //message.setFrom("commercebank@mail.com");
        message.setTo(to);
        message.setSubject("One-time login verification code");
        message.setText("Thank you for using multi-factor authentication to protect your account! Here is your code: " + code
                + "\n Please keep in mind that this code will expire after 5 minutes");
        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String to, int code) {
        SimpleMailMessage message = new SimpleMailMessage();
        //message.setFrom("commercebank@mail.com");
        message.setTo(to);
        message.setSubject("One-time reset-password code");
        message.setText("We've received a request to reset your account's password! If you did not request a new password," +
                " please contact our customer service to take immediate action to protect your account. \n Otherwise, here is your code: " + code
                + "\n Please keep in mind that this code will expire after 5 minutes");
        mailSender.send(message);
    }

    public void sendTransferCodeEmail(String to, int code) {
        SimpleMailMessage message = new SimpleMailMessage();
        //message.setFrom("commercebank@mail.com");
        message.setTo(to);
        message.setSubject("One-time transfer code");
        message.setText("We've received a request to make a transfer! If it wasn't you," +
                " please contact our customer service to take immediate action to protect your account. \n Otherwise, here is your code: " + code
                + "\n Please keep in mind that this code will expire after 5 minutes");
        mailSender.send(message);
    }

}