package edu.du.myproject1101_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    
    //통합 에러페이지
    @RequestMapping("/error")
    public String handleError() {
        return "view/error/error"; // templates/view/error/error.html 경로
    }
}
