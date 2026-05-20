package com.role.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// FIX A: removida @EnableFeignClients — EventoClient foi eliminado (era chamada circular)
@SpringBootApplication
public class RoleApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoleApiApplication.class, args);
    }
}
