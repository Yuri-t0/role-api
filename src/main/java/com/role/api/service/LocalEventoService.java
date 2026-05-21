package com.role.api.service;

import com.role.api.model.LocalEvento;
import com.role.api.repository.LocalEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

 @Service
@RequiredArgsConstructor
public class LocalEventoService {

    private final LocalEventoRepository localEventoRepository;

    public List<LocalEvento> listarTodos() {
        return localEventoRepository.findAll();
    }

    public LocalEvento buscarPorId(Long id) {
        return localEventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado: " + id));
    }
}
