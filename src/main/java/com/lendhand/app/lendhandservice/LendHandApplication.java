package com.lendhand.app.lendhandservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LendHandApplication {

    public static void main(String[] args) {
        SpringApplication.run(LendHandApplication.class, args);
    }

}
