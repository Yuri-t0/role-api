package com.role.api.service;

import com.role.api.dto.EventoFormDTO;
import com.role.api.model.Evento;
import com.role.api.model.LocalEvento;
import com.role.api.model.Usuario;
import com.role.api.repository.EventoRepository;
import com.role.api.repository.LocalEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioService usuarioService;
    private final LocalEventoRepository localEventoRepository;

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    // FIX C: count eficiente para dashboard
    public long contarTotal() {
        return eventoRepository.count();
    }

    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + id));
    }

    public Evento salvar(EventoFormDTO dto) {
        validarDatas(dto);

        Usuario organizador = usuarioService.buscarPorId(dto.getOrganizadorId());
        LocalEvento local = buscarLocal(dto.getLocalId());

        Evento evento = new Evento();
        evento.setTitulo(dto.getTitulo());
        evento.setDescricao(dto.getDescricao());
        evento.setInicio(dto.getInicio());
        evento.setFim(dto.getFim());
        evento.setVagasTotais(dto.getVagasTotais());
        evento.setVagasDisponiveis(dto.getVagasTotais());
        evento.setOrganizador(organizador);
        evento.setLocal(local);

        return eventoRepository.save(evento);
    }

    public Evento atualizar(Long id, EventoFormDTO dto) {
        validarDatas(dto);

        Evento evento = buscarPorId(id);
        Usuario organizador = usuarioService.buscarPorId(dto.getOrganizadorId());
        LocalEvento local = buscarLocal(dto.getLocalId());

        evento.setTitulo(dto.getTitulo());
        evento.setDescricao(dto.getDescricao());
        evento.setInicio(dto.getInicio());
        evento.setFim(dto.getFim());
        evento.setVagasTotais(dto.getVagasTotais());
        evento.setOrganizador(organizador);
        evento.setLocal(local);

        return eventoRepository.save(evento);
    }

    public void excluir(Long id) {
        eventoRepository.delete(buscarPorId(id));
    }

    // FIX #5: decremento atômico via query UPDATE para evitar race condition
    @Transactional
    public void decrementarVaga(Evento evento) {
        int updated = eventoRepository.decrementarVagaAtomico(evento.getId());
        if (updated == 0) {
            throw new IllegalStateException("Não há vagas disponíveis para este evento.");
        }
    }

    // Usado por PresencaService para devolver vaga no cancelamento
    public Evento salvarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    private LocalEvento buscarLocal(Long localId) {
        return localEventoRepository.findById(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado: " + localId));
    }

    private void validarDatas(EventoFormDTO dto) {
        if (dto.getFim().isBefore(dto.getInicio())) {
            throw new IllegalArgumentException("A data de fim deve ser após a data de início.");
        }
    }
}
