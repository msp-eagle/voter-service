package com.example.regclient_newVersion.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {
        "/login",
        "/dashboard",
        "/registration",
        "/demographic",
        "/document",
        "/biometric",
        "/review"
    })
    public String forwardSpaRoutes() {
        return "forward:/index.html";
    }
}