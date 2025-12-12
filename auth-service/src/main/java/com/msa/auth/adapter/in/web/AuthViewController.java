package com.msa.auth.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller // @RestController 아님
public class AuthViewController {

    @GetMapping("/view/login")
    public String loginPage() {
        return "login"; // login.html을 보여줘라
    }

    @GetMapping("/view/signup")
    public String signupPage() {
        return "signup"; // signup.html을 보여줘라
    }
}
