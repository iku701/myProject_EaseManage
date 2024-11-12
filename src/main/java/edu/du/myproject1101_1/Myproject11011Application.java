package edu.du.myproject1101_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "edu.du.myproject1101_1")
public class Myproject11011Application {
    public static void main(String[] args) {
        SpringApplication.run(Myproject11011Application.class, args);
    }

}
