# 🎉 Role — Plataforma de Gestão de Eventos

> Projeto desenvolvido para a disciplina **Java Advanced** — FIAP  
> Sprint 4 · Entrega Final

---

## 📌 Sobre o Projeto

A **Role** é uma plataforma web para descoberta e gestão de eventos presenciais. O sistema resolve o problema de organização de eventos corporativos e acadêmicos, centralizando cadastro, confirmação de presença e notificações em uma única aplicação.

**Fluxo principal:**
1. Usuário se cadastra e faz login
2. Navega pela lista de eventos disponíveis
3. Confirma presença em um evento com vagas disponíveis
4. Uma mensagem é publicada no RabbitMQ
5. O consumer processa a mensagem e registra o log de notificação
6. Administradores gerenciam eventos, locais e usuários pelo painel admin

---

## 🏗️ Arquitetura

```
role-api/
├── config/          # SecurityConfig, RabbitConfig (com DLQ), DataInitializer
├── controller/
│   ├── web/         # Controllers Thymeleaf (AdminController, EventoWebController...)
│   └── api/         # REST interno (EventoInternoController)
├── service/         # Regras de negócio (EventoService, PresencaService...)
├── repository/      # Spring Data JPA
├── model/           # Entidades JPA
├── messaging/       # Producer e Consumer RabbitMQ
├── dto/             # DTOs de entrada, resposta e mensageria
└── security/        # CustomUserDetailsService
```

**Padrão:** Arquitetura em camadas (Controller → Service → Repository), com separação clara de responsabilidades.

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem |
| Spring Boot 3.3 | Framework principal |
| Spring Security | Autenticação e autorização (BCrypt + form login) |
| Spring Data JPA | Persistência |
| H2 Database | Banco de dados em memória |
| Flyway | Migrations de banco |
| RabbitMQ | Mensageria assíncrona com DLQ |
| Thymeleaf | Templates HTML |
| OpenFeign | Cliente HTTP declarativo |
| Lombok | Redução de boilerplate |
| JUnit 5 + Mockito | Testes unitários |

---

## ✅ Conceitos da Disciplina Aplicados

- **Spring Security** com `DaoAuthenticationProvider`, `BCryptPasswordEncoder`, controle de roles (`ADMIN` / `USER`) e CSRF configurado corretamente para aplicação com sessão
- **RabbitMQ** com fila principal, exchange `Direct`, routing key e **Dead Letter Queue (DLQ)** implementada via `x-dead-letter-exchange`
- **Mensagem enviada após commit** usando `TransactionSynchronizationManager`, garantindo consistência entre banco e mensageria
- **Decremento atômico de vagas** via `@Modifying @Query`, evitando race condition em confirmações simultâneas
- **Flyway** para versionamento do schema (`V1`, `V2`, `V3`)
- **OpenFeign** configurado com timeouts via `application.properties`
- **Validação** com Bean Validation (`@Valid`, `@NotBlank`, `@Email`, `@Min`) em DTOs e entidades

---

## 🚀 Como Executar Localmente

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Docker (para o RabbitMQ)

### 1. Subir o RabbitMQ

```bash
docker run -d \
  --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

Acesse o painel: http://localhost:15672 (guest/guest)

### 2. Rodar a aplicação

```bash
cd role-api
mvn spring-boot:run
```

A aplicação sobe em: **http://localhost:8080**

### 3. Usuários de acesso

| Perfil | Email | Senha |
|---|---|---|
| Admin | admin@role.com | admin@Role2025 |
| Usuário | yuri@role.com | user@Role2025 |

> As senhas podem ser sobrescritas pelas variáveis de ambiente `ADMIN_PASSWORD` e `USER_PASSWORD`.

### 4. Console H2

Acesse: http://localhost:8080/h2-console  
JDBC URL: `jdbc:h2:mem:roledb`

---

## 🔄 Principais Fluxos

### Confirmação de Presença
```
POST /eventos/{id}/confirmar-presenca
  → PresencaService.confirmar()
    → EventoService.decrementarVaga()  [query atômica no banco]
    → presencaRepository.save()
    → [após commit] PresencaProducer.enviarConfirmacao()
      → RabbitMQ exchange → queue
        → PresencaConsumer.consumir()
          → NotificacaoProcessamentoService.processarConfirmacao()
            → notificacaoRepository.save()
```

### Dead Letter Queue
Se o consumer lançar exceção, a mensagem é redirecionada para `role.presenca.queue.dlq` automaticamente via `x-dead-letter-exchange`, evitando perda de mensagens.

---

## 🧪 Testes

```bash
mvn test
```

Testes unitários cobrem:
- `PresencaServiceTest` — confirmação duplicada, cancelamento com devolução de vaga
- `EventoServiceTest` — decremento atômico, busca com exceção

---

## 🔗 Integração Multidisciplinar

| Disciplina | Como aparece no projeto |
|---|---|
| **Mastering Relational and Non-Relational Database** | Schema normalizado com 5 tabelas, chaves estrangeiras, constraint de unicidade em `presenca(usuario_id, evento_id)`. Migrations versionadas com Flyway (`V1`, `V2`, `V3`) garantindo rastreabilidade de cada alteração no banco |
| **DevOps Tools & Cloud Computing** | Dockerfile multi-stage (Maven → JRE), deploy no Railway com PostgreSQL e RabbitMQ como serviços gerenciados, variáveis de ambiente para todas as credenciais, CI/CD automático a cada push no GitHub |
| **Compliance, Quality Assurance & Tests** | Testes unitários com JUnit 5 + Mockito cobrindo fluxos críticos (`PresencaServiceTest`, `EventoServiceTest`). Bean Validation em todos os DTOs com mensagens de erro explícitas. CSRF habilitado corretamente para app com sessão |
| **Disruptive Architectures: IoT, IOB & Generative IA** | Padrão de mensageria assíncrona Producer → Queue → Consumer → DLQ — o mesmo modelo usado em pipelines IoT para processar eventos de dispositivos em tempo real sem perda de dados |
| **Mobile Application Development** | Endpoint REST `/api/interno/eventos/{id}/resumo` com resposta JSON enxuta, desenhado para consumo por clientes externos como apps Android/iOS |
| **Advanced Business Development with .NET** | Separação entre camada de negócio (Services) e apresentação (Controllers) seguindo princípios de Clean Architecture — lógica de negócio sem dependência de framework web, facilitando reuso e teste |

---

## 📁 Migrations Flyway

| Arquivo | Conteúdo |
|---|---|
| `V1__create_tables.sql` | Criação de todas as tabelas |
| `V2__seed_data.sql` | Dados iniciais de locais |
| `V3__seed_eventos.sql` | Eventos de exemplo |

---

## 👥 Equipe

> Preencha com os nomes do grupo

---

## 📎 Links

- 🔗 **Aplicação em produção:** `[inserir link do deploy]`
- 🎥 **Vídeo de apresentação:** `[inserir link do vídeo]`
- 📁 **Repositório:** `[inserir link do GitHub]`
