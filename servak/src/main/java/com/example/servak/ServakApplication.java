package com.example.servak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServakApplication {

    public static void main(String[] args) {

        new Thread(()->{
            SpringApplication.run(ServakApplication.class, args);
        }).start();

    }

}



