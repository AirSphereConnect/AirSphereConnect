package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.AlertsDto;
import com.airsphereconnect.dtos.response.WeatherAlertDto;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailHogSenderImpl {

    private final JavaMailSender mailSender;

    public EmailHogSenderImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String username, String subject, AlertsDto dto) {
        WeatherAlertDto weatherAlert = dto.getParsedMessage();

        String mailText;
        if (weatherAlert != null) {
            mailText = String.join("\n",
                    "Bonjour " + username,
                    weatherAlert.senderName(),
                    weatherAlert.event(),
                    weatherAlert.description()
            );
        } else {
            mailText = dto.getMessage();
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(mailText);

        mailSender.send(mail);
    }
}
