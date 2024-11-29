package edu.du.myproject1101_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashBoardController {
    @GetMapping("/view/dashboard")
    public String dashboard() {
        return "view/dashboard/dashboard";
    }
}
