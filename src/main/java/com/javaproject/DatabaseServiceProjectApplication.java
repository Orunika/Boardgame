package com.javaproject;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DatabaseServiceProjectApplication {

    private static final Logger logger = Logger.getLogger(DatabaseServiceProjectApplication.class.getName());

    public static void main(String[] args) {
        logger.log(Level.INFO, "Starting DatabaseServiceProjectApplication");
        SpringApplication.run(DatabaseServiceProjectApplication.class, args);
    }
}
