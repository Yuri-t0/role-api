package com.role.api.controller.web;

import com.role.api.model.Evento;
import com.role.api.model.Usuario;
import com.role.api.service.EventoService;
import com.role.api.service.PresencaService;
import com.role.api.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoWebController {

    private final EventoService eventoService;
    private final PresencaService presencaService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("eventos", eventoService.listarTodos());
        return "eventos";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
        Evento evento = eventoService.buscarPorId(id);
        model.addAttribute("evento", evento);

        if (userDetails != null) {
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
            model.addAttribute("jaConfirmou",
                    presencaService.jaConfirmou(id, usuario.getId()));
        }
        return "evento-detalhe";
    }

    @PostMapping("/{id}/confirmar-presenca")
    public String confirmarPresenca(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            presencaService.confirmar(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("sucesso", "Presença confirmada com sucesso!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/eventos/" + id;
    }

    @PostMapping("/{id}/cancelar-presenca")
    public String cancelarPresenca(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        try {
            presencaService.cancelar(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("sucesso", "Presença cancelada.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/eventos/" + id;
    }
}
