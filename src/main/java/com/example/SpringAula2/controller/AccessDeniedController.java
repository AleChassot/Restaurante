package com.example.SpringAula2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

    @GetMapping("/403")
    public String acessoNegado() {
        return "403"; // Retorna o template 403.html
    }
}

