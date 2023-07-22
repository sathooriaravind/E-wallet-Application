package org.example;

import org.example.configs.Constants;
import org.example.models.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class userServiceApplication implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    public static void main(String[] args) {
        SpringApplication.run(userServiceApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        /*userRepository.save(User.builder()
                        .name("Transaction_Service")
                        .email("txnJbdl@gmail.com")
                        .phone("txn-service")
                        .password("txn@123")
                        .authorities(Constants.OTHER_SERVICE_ACCESS)
                        .build());*/
    }
}