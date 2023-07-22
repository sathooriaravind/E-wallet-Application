package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Bean
    SimpleMailMessage getMailMessage(){
        return new SimpleMailMessage();
    }

    // Responsible for sending mail from java app
    @Bean
    JavaMailSenderImpl getMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("jbdlwallet@gmail.com");
        javaMailSender.setPassword("shjhgvsbzctbbvxk");

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.debug", true);

        // this property is required if we need to send email from this app
        properties.put("mail.smtp.starttls.enable", true);

        return javaMailSender;

    }
}
