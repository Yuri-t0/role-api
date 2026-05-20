package com.role.api.controller.web;

import com.role.api.service.PresencaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/minhas-presencas")
@RequiredArgsConstructor
public class PresencaWebController {

    private final PresencaService presencaService;

    @GetMapping
    public String minhasPresencas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("presencas",
                presencaService.listarPorUsuario(userDetails.getUsername()));
        return "minhas-presencas";
    }
}
