package com.role.api.controller;

import com.role.api.dto.CadastroUsuarioDTO;
import com.role.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    // Spring Security cuida do POST /login automaticamente.
    // Só precisamos servir a página HTML do login.
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastroForm(Model model) {
        model.addAttribute("usuario", new CadastroUsuarioDTO());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("usuario") CadastroUsuarioDTO dto,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            return "cadastro";
        }
        try {
            usuarioService.cadastrar(dto);
            return "redirect:/login?cadastrado";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "cadastro";
        }
    }
}
