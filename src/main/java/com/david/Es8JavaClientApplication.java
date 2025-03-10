package com.david;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.david.mapper")
public class Es8JavaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(Es8JavaClientApplication.class, args);
    }

}
