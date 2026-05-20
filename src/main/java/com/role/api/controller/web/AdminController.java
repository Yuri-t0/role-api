package com.role.api.controller.web;

import com.role.api.dto.EventoFormDTO;
import com.role.api.model.Evento;
import com.role.api.service.EventoService;
import com.role.api.service.LocalEventoService;
import com.role.api.service.NotificacaoProcessamentoService;
import com.role.api.service.PresencaService;
import com.role.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EventoService eventoService;
    private final UsuarioService usuarioService;
    private final LocalEventoService localEventoService;
    private final NotificacaoProcessamentoService notificacaoService;
    private final PresencaService presencaService;

    @GetMapping
    public String dashboard(Model model) {
        // FIX C: count() eficiente — não carrega listas inteiras só para pegar o tamanho
        model.addAttribute("totalUsuarios", usuarioService.contarTotal());
        model.addAttribute("totalEventos", eventoService.contarTotal());
        model.addAttribute("totalPresencas", presencaService.contarTotal());
        return "admin/dashboard";
    }

    @GetMapping("/eventos")
    public String listarEventos(Model model) {
        model.addAttribute("eventos", eventoService.listarTodos());
        return "admin/eventos";
    }

    @GetMapping("/eventos/novo")
    public String novoEvento(Model model) {
        model.addAttribute("evento", new EventoFormDTO());
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("locais", localEventoService.listarTodos());
        model.addAttribute("modoEdicao", false);
        return "admin/evento-form";
    }

    @PostMapping("/eventos")
    public String salvarEvento(@Valid @ModelAttribute("evento") EventoFormDTO dto,
                               BindingResult result, Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("locais", localEventoService.listarTodos());
            model.addAttribute("modoEdicao", false);
            return "admin/evento-form";
        }
        try {
            eventoService.salvar(dto);
            redirectAttributes.addFlashAttribute("sucesso", "Evento cadastrado com sucesso!");
            return "redirect:/admin/eventos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("locais", localEventoService.listarTodos());
            model.addAttribute("modoEdicao", false);
            return "admin/evento-form";
        }
    }

    @GetMapping("/eventos/editar/{id}")
    public String editarEvento(@PathVariable Long id, Model model) {
        Evento evento = eventoService.buscarPorId(id);

        EventoFormDTO dto = new EventoFormDTO();
        dto.setTitulo(evento.getTitulo());
        dto.setDescricao(evento.getDescricao());
        dto.setInicio(evento.getInicio());
        dto.setFim(evento.getFim());
        dto.setVagasTotais(evento.getVagasTotais());
        dto.setOrganizadorId(evento.getOrganizador().getId());
        dto.setLocalId(evento.getLocal().getId());

        model.addAttribute("evento", dto);
        model.addAttribute("eventoId", id);
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("locais", localEventoService.listarTodos());
        model.addAttribute("modoEdicao", true);
        return "admin/evento-form";
    }

    @PostMapping("/eventos/editar/{id}")
    public String atualizarEvento(@PathVariable Long id,
                                  @Valid @ModelAttribute("evento") EventoFormDTO dto,
                                  BindingResult result, Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("eventoId", id);
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("locais", localEventoService.listarTodos());
            model.addAttribute("modoEdicao", true);
            return "admin/evento-form";
        }
        try {
            eventoService.atualizar(id, dto);
            redirectAttributes.addFlashAttribute("sucesso", "Evento atualizado com sucesso!");
            return "redirect:/admin/eventos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("eventoId", id);
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("locais", localEventoService.listarTodos());
            model.addAttribute("modoEdicao", true);
            return "admin/evento-form";
        }
    }

    @PostMapping("/eventos/excluir/{id}")
    public String excluirEvento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        eventoService.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Evento excluído com sucesso!");
        return "redirect:/admin/eventos";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/usuarios";
    }

    @GetMapping("/processamentos")
    public String listarProcessamentos(Model model) {
        model.addAttribute("processamentos", notificacaoService.listarProcessamentos());
        return "admin/processamentos";
    }
}
