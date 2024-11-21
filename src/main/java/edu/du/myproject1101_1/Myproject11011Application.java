package edu.du.myproject1101_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = "edu.du.myproject1101_1",
        exclude = {org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration.class} // MyBatis 자동 구성 비활성화
)
public class Myproject11011Application {
    public static void main(String[] args) {
        SpringApplication.run(Myproject11011Application.class, args);
    }
}
