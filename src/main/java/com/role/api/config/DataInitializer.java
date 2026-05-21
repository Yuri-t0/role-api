package com.role.api.config;

import com.role.api.model.*;
import com.role.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final LocalEventoRepository localEventoRepository;
    private final EventoRepository eventoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-password:admin@Role2025}")
    private String adminPassword;

    @Value("${app.seed.user-password:user@Role2025}")
    private String userPassword;

    @Override
    public void run(ApplicationArguments args) {
        Usuario admin = garantirUsuario("admin@role.com", "Admin Sistema", "11999990000", adminPassword, RoleUsuario.ADMIN);
        garantirUsuario("yuri@role.com", "Yuri Ferreira", "11988880000", userPassword, RoleUsuario.USER);

        if (eventoRepository.count() == 0) {
            criarEventosDemo(admin);
        }
    }

    private Usuario garantirUsuario(String email, String nome, String telefone,
                                     String senha, RoleUsuario role) {
        return usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setEmail(email);
            u.setNome(nome);
            u.setTelefone(telefone);
            u.setSenha(passwordEncoder.encode(senha));
            u.setRole(role);
            log.info("Criando usuario: {}", email);
            return usuarioRepository.save(u);
        });
    }

    private void criarEventosDemo(Usuario organizador) {
        LocalEvento local1 = garantirLocal("FIAP Paulista",
                "Av. Paulista, 1106 - Bela Vista, Sao Paulo", -23.5630, -46.6544);

        LocalEvento local2 = garantirLocal("Auditorio Central",
                "Rua da Consolacao, 500 - Consolacao, Sao Paulo", -23.5505, -46.6600);

        criarEvento("Hackathon Spring Boot",
                "Maratona de desenvolvimento com Spring Boot, JPA e APIs REST. Venha aprender e competir!",
                LocalDateTime.now().plusDays(30), LocalDateTime.now().plusDays(31), 100, organizador, local1);

        criarEvento("Workshop RabbitMQ",
                "Aprenda mensageria assincrona com RabbitMQ na pratica. Producers, consumers e filas.",
                LocalDateTime.now().plusDays(45), LocalDateTime.now().plusDays(45).plusHours(4), 50, organizador, local2);

        criarEvento("Meetup Java Advanced",
                "Encontro da comunidade Java com talks sobre Spring Security, Feign e boas praticas.",
                LocalDateTime.now().plusDays(60), LocalDateTime.now().plusDays(60).plusHours(3), 80, organizador, local1);

        log.info("Eventos demo criados.");
    }

    private LocalEvento garantirLocal(String nome, String endereco, double lat, double lon) {
        return localEventoRepository.findAll().stream()
                .filter(l -> l.getNome().equals(nome))
                .findFirst()
                .orElseGet(() -> {
                    LocalEvento l = new LocalEvento();
                    l.setNome(nome);
                    l.setEndereco(endereco);
                    l.setLatitude(lat);
                    l.setLongitude(lon);
                    log.info("Criando local: {}", nome);
                    return localEventoRepository.save(l);
                });
    }

    private void criarEvento(String titulo, String descricao, LocalDateTime inicio,
                              LocalDateTime fim, int vagas, Usuario org, LocalEvento local) {
        Evento e = new Evento();
        e.setTitulo(titulo);
        e.setDescricao(descricao);
        e.setInicio(inicio);
        e.setFim(fim);
        e.setVagasTotais(vagas);
        e.setVagasDisponiveis(vagas);
        e.setOrganizador(org);
        e.setLocal(local);
        eventoRepository.save(e);
    }
}
