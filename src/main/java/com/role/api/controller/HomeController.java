package com.role.api.controller;

import com.role.api.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EventoService eventoService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("eventos", eventoService.listarTodos());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("eventos", eventoService.listarTodos());
        return "dashboard";
    }
}
