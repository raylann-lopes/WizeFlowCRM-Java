# WizeFlow CRM - Backend API

> **CRM completo em Java/Spring Boot com suporte a automação, chat em tempo real e busca semântica com IA**

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-336791?style=flat-square&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=flat-square&logo=apachemaven)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

---

## 📋 Sumário

1. [Visão Geral](#visão-geral)
2. [Arquitetura](#arquitetura)
3. [Pré-requisitos](#pré-requisitos)
4. [Instalação](#instalação)
5. [Execução](#execução)
6. [Endpoints Principais](#endpoints-principais)
7. [Estrutura do Projeto](#estrutura-do-projeto)
8. [Documentação](#documentação)
9. [Contribuindo](#contribuindo)
10. [Suporte](#suporte)

---

## 🎯 Visão Geral

O **WizeFlow CRM** é uma plataforma de gestão de relacionamento com clientes construída com **Java 17+** e **Spring Boot 3.x**, projetada para empresas que precisam:

- ✅ **Gerenciar clientes, leads e oportunidades** com contexto completo
- ✅ **Automatizar processos de vendas** com workflows e triggers
- ✅ **Integrar chat em tempo real** (WhatsApp via N8N)
- ✅ **Rastrear auditoria** de todas as ações do sistema
- ✅ **Busca semântica avançada** em documentos usando IA (pgvector)
- ✅ **Controle de acesso granular** baseado em permissões por role

### 🚀 Features Principais

| Feature | Descrição |
|---------|-----------|
| **Multi-tenant** | Suporte a múltiplas empresas em uma única instância |
| **Autenticação JWT** | Login seguro com tokens JWT e refresh tokens |
| **Auditoria Completa** | Histórico de todas as alterações (CRUD) rastreável |
| **Chat Integrado** | Chat sessions com suporte a múltiplos canais (WhatsApp, etc) |
| **Vector Search** | Busca semântica em documentos com embeddings de 1536 dimensões |
| **Paginação & Filtros** | Listagens com suporte a filtros, ordenação e paginação |
| **Validação** | Validação de dados com mensagens de erro claras e estruturadas |
| **RBAC** | Controle de acesso baseado em roles e permissions |

---

## 🏗️ Arquitetura

### Estrutura de Camadas

```
com.wizeflow.crm_backend/
├── controller/          # REST Controllers (entrypoint HTTP)
├── services/            # Business Logic (orquestração)
├── infrastructure/
│   ├── entity/         # JPA Entities (modelos de DB)
│   └── repository/     # Spring Data JPA Repositories
├── dto/                # Data Transfer Objects (request/response)
├── exceptions/         # Custom Exceptions & Global Handler
├── config/             # Spring Configuration
├── enums/              # Enumerações do sistema
└── utils/              # Utilities & Helpers
```

### Modelo de Dados

O projeto implementa **11 tabelas principais** com relacionamentos complexos:

```
companies (1) ──→ (N) users
    ├─→ clients
    ├─→ leads
    ├─→ tickets
    ├─→ appointments
    ├─→ chat_sessions
    └─→ system_features (RBAC)

documents (vetor) ──→ busca semântica via pgvector
history ──→ auditoria de todas as ações
```

**Detalhes:** Consulte [Diagrama ER](docs/er-diagram.svg) e [ENTITIES.md](docs/ENTITIES.md)

---

## 📦 Pré-requisitos

### Sistema Operacional
- **Linux** (Ubuntu 20.04+), **macOS** (10.15+) ou **Windows** 10/11 com WSL2

### Obrigatório
- **Java 17+** (recomendado Java 21)
  ```bash
  java -version  # verificar versão
  ```
- **Maven 3.8+**
  ```bash
  mvn -version   # verificar versão
  ```
  _Ou use o wrapper incluído: `./mvnw` (Linux/macOS) ou `mvnw.cmd` (Windows)_

- **PostgreSQL 15+** com extensão `vector` (para busca semântica)
  ```bash
  # Verificar instalação
  psql --version
  ```

### Opcional (para Docker)
- **Docker Desktop** 20.10+
- **Docker Compose** 1.29+

---

## 🚀 Instalação

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/raylann-lopes/WizeFlowCRM-Java.git
cd WizeFlowCRM-Java
```

### 2️⃣ Criar Banco de Dados PostgreSQL

```bash
# Conectar ao PostgreSQL
psql -U postgres

# Dentro do psql:
CREATE DATABASE crm_wizeflow;
CREATE USER crm_user WITH PASSWORD 'sua_senha_segura';
ALTER ROLE crm_user SET client_encoding TO 'utf8';
ALTER ROLE crm_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE crm_user SET default_transaction_deferrable TO on;
GRANT ALL PRIVILEGES ON DATABASE crm_wizeflow TO crm_user;

-- Habilitar extensão pgvector para busca semântica
\c crm_wizeflow
CREATE EXTENSION IF NOT EXISTS vector;
\q
```

### 3️⃣ Configurar Variáveis de Ambiente

Copie o arquivo de exemplo e customize:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

**Edite `application.properties`:**

```properties
# ========== DATASOURCE ==========
spring.datasource.url=jdbc:postgresql://localhost:5432/crm_wizeflow
spring.datasource.username=crm_user
spring.datasource.password=sua_senha_segura
spring.datasource.driver-class-name=org.postgresql.Driver

# ========== JPA/Hibernate ==========
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# ========== FLYWAY ==========
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# ========== SECURITY ==========
jwt.secret=sua_chave_secreta_super_longa_aqui_minimo_256bits
jwt.expiration=86400000

# ========== ACTUATOR/MONITORING ==========
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=when-authorized

# ========== LOGGING ==========
logging.level.root=INFO
logging.level.com.wizeflow=DEBUG
```

**Para Desenvolvimento Local com Docker Compose:**

Use o arquivo `docker-compose.yml` incluído para levantar um PostgreSQL automaticamente:

```bash
docker-compose up -d
```

### 4️⃣ Compilar o Projeto

```bash
# Com Maven instalado
mvn clean package -DskipTests

# Ou com o wrapper
./mvnw clean package -DskipTests
```

---

## 🏃 Execução

### Opção 1: Linha de Comando (Maven)

```bash
# Modo desenvolvimento (com hot-reload)
./mvnw spring-boot:run

# Ou compilar e executar o JAR
./mvnw clean package
java -jar target/crm-backend-1.0.0.jar
```

### Opção 2: Docker Compose

```bash
# Levantar app + PostgreSQL + PgAdmin
docker-compose up --build

# Acessar a aplicação em http://localhost:8080
```

### Opção 3: IDE (IntelliJ IDEA / Eclipse)

1. Abra a pasta do projeto como Maven Project
2. Configure o JDK 17+ em **Project Settings**
3. Clique em **Run** → **CrmBackendApplication.java**

### ✅ Verificar se está Rodando

```bash
# Health check
curl http://localhost:8080/actuator/health

# Resposta esperada:
# {"status":"UP","components":{"db":{"status":"UP"},...}}

# Acessar Swagger UI
# http://localhost:8080/swagger-ui/index.html
```

---

## 📡 Endpoints Principais

### 🔐 Autenticação

```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@company.com",
  "password": "senha123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "...",
  "expiresIn": 86400
}
```

### 👥 Usuários

```http
# Listar usuários da empresa (com paginação)
GET /users?page=0&size=20&sort=name,asc
Authorization: Bearer {token}

# Buscar usuário por email
GET /users/by-email?email=user@company.com
Authorization: Bearer {token}

# Criar novo usuário
POST /users
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@company.com",
  "password": "senha123",
  "role": "user",
  "jobTitle": "Gerente de Vendas"
}

# Atualizar usuário
PUT /users/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "João Silva Atualizado",
  "jobTitle": "Diretor de Vendas"
}

# Deletar usuário
DELETE /users/{id}
Authorization: Bearer {token}
```

### 🏢 Empresas

```http
# Listar empresas
GET /companies?page=0&size=10
Authorization: Bearer {token}

# Criar empresa
POST /companies
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Minha Empresa",
  "slug": "minha-empresa",
  "plan": "premium",
  "logo": "https://logo.png"
}

# Obter detalhes da empresa
GET /companies/{id}
Authorization: Bearer {token}

# Atualizar empresa
PUT /companies/{id}
Authorization: Bearer {token}

# Deletar empresa
DELETE /companies/{id}
Authorization: Bearer {token}
```

### 👤 Clientes

```http
# Listar clientes da empresa com filtros
GET /clients?company_id=1&status=Ativo&page=0&size=20
Authorization: Bearer {token}

# Criar cliente
POST /clients
Authorization: Bearer {token}
Content-Type: application/json

{
  "companyId": 1,
  "name": "Cliente Exemplo",
  "email": "cliente@email.com",
  "phone": "+55 11 99999-9999",
  "status": "Ativo",
  "notes": "Cliente potencial para renovação"
}

# Detalhes de cliente
GET /clients/{id}
Authorization: Bearer {token}

# Atualizar cliente
PUT /clients/{id}
Authorization: Bearer {token}

# Deletar cliente
DELETE /clients/{id}
Authorization: Bearer {token}
```

### 💼 Leads/Oportunidades

```http
# Listar leads com filtros
GET /leads?company_id=1&status=Novo&sort=value,desc
Authorization: Bearer {token}

# Criar lead
POST /leads
Authorization: Bearer {token}
Content-Type: application/json

{
  "companyId": 1,
  "name": "Novo Lead",
  "email": "lead@email.com",
  "status": "Novo",
  "value": 5000.00,
  "source": "website",
  "isHot": true
}

# Obter lead
GET /leads/{id}
Authorization: Bearer {token}

# Atualizar lead (ex: mover para próxima etapa)
PUT /leads/{id}
Authorization: Bearer {token}

{
  "status": "Qualificado",
  "value": 8000.00
}

# Deletar lead
DELETE /leads/{id}
Authorization: Bearer {token}
```

### 📅 Agendamentos

```http
# Listar agendamentos da empresa
GET /appointments?company_id=1&page=0&size=20
Authorization: Bearer {token}

# Criar agendamento
POST /appointments
Authorization: Bearer {token}
Content-Type: application/json

{
  "companyId": 1,
  "clientName": "Cliente",
  "clientType": "lead",
  "date": "2026-02-20",
  "time": "14:30:00",
  "status": "Pendente",
  "description": "Reunião de apresentação",
  "viaWhatsapp": true
}

# Detalhes do agendamento
GET /appointments/{id}
Authorization: Bearer {token}

# Atualizar agendamento
PUT /appointments/{id}
Authorization: Bearer {token}

# Deletar agendamento
DELETE /appointments/{id}
Authorization: Bearer {token}
```

### 🎫 Tickets

```http
# Listar tickets
GET /tickets?company_id=1&status=Aberto&page=0
Authorization: Bearer {token}

# Criar ticket
POST /tickets
Authorization: Bearer {token}
Content-Type: application/json

{
  "companyId": 1,
  "userId": 1,
  "subject": "Sistema não carrega relatórios",
  "department": "Suporte",
  "urgency": "Alta",
  "message": "O sistema fica travado ao abrir relatórios de vendas..."
}

# Detalhes do ticket
GET /tickets/{id}
Authorization: Bearer {token}

# Atualizar ticket
PUT /tickets/{id}
Authorization: Bearer {token}

# Fechar ticket
PUT /tickets/{id}/close
Authorization: Bearer {token}

# Deletar ticket
DELETE /tickets/{id}
Authorization: Bearer {token}
```

### 🔍 Busca Semântica (IA)

```http
# Buscar documentos por similaridade semântica
POST /documents/search-embedding
Authorization: Bearer {token}
Content-Type: application/json

{
  "query": "Como aumentar as vendas em 30%?",
  "matchCount": 10,
  "filter": {"category": "sales"}
}

Response:
[
  {
    "id": 123,
    "content": "Estratégias de vendas B2B...",
    "metadata": {"category": "sales", "author": "John"},
    "similarity": 0.89
  },
  ...
]
```

### 📜 Auditoria / Histórico

```http
# Listar histórico de ações
GET /history?company_id=1&related_type=lead&page=0
Authorization: Bearer {token}

Response:
[
  {
    "id": 1,
    "companyId": 1,
    "relatedId": 5,
    "relatedType": "lead",
    "action": "CREATE",
    "type": "user",
    "date": "2026-02-13T10:30:00Z",
    "createdAt": "2026-02-13T10:30:00Z"
  },
  ...
]
```

### 📊 Health Check & Metrics

```http
# Status da aplicação
GET /actuator/health
Response: {"status":"UP","components":{"db":{"status":"UP"},...}}

# Métricas do sistema
GET /actuator/metrics
Authorization: Bearer {token}

# Informações da aplicação
GET /actuator/info
```

---

## 📁 Estrutura do Projeto

```
crm-backend/
├── pom.xml                                 # Dependências Maven
├── docker-compose.yml                      # Compose para DB + App
├── README.md                               # Este arquivo
├── CONTRIBUTING.md                         # Guia de contribuição
├── CHANGELOG.md                            # Histórico de versões
│
├── src/main/java/com/wizeflow/crm_backend/
│   ├── CrmBackendApplication.java          # Entry point da aplicação
│   │
│   ├── controller/                         # REST Controllers (HTTP endpoints)
│   │   ├── CompanyController.java
│   │   ├── UserController.java
│   │   ├── ClientController.java
│   │   ├── LeadController.java
│   │   ├── TicketController.java
│   │   ├── AppointmentController.java
│   │   ├── ChatSessionController.java
│   │   ├── DocumentController.java
│   │   ├── HistoryController.java
│   │   └── AuthController.java
│   │
│   ├── services/                           # Business Logic
│   │   ├── CompanyService.java
│   │   ├── UserService.java
│   │   ├── ClientService.java
│   │   ├── LeadService.java
│   │   ├── TicketService.java
│   │   ├── AppointmentService.java
│   │   ├── ChatSessionService.java
│   │   ├── DocumentService.java
│   │   ├── HistoryService.java
│   │   └── AuthService.java
│   │
│   ├── infrastructure/
│   │   ├── entity/                        # JPA Entities (modelos DB)
│   │   │   ├── Company.java
│   │   │   ├── User.java
│   │   │   ├── Client.java
│   │   │   ├── Lead.java
│   │   │   ├── Ticket.java
│   │   │   ├── Appointment.java
│   │   │   ├── ChatSession.java
│   │   │   ├── Document.java
│   │   │   ├── History.java
│   │   │   ├── SystemFeature.java
│   │   │   └── RolePermission.java
│   │   │
│   │   └── repository/                    # Spring Data JPA Repositories
│   │       ├── CompanyRepository.java
│   │       ├── UserRepository.java
│   │       ├── ClientRepository.java
│   │       ├── LeadRepository.java
│   │       ├── TicketRepository.java
│   │       ├── AppointmentRepository.java
│   │       ├── ChatSessionRepository.java
│   │       ├── DocumentRepository.java
│   │       ├── HistoryRepository.java
│   │       ├── SystemFeatureRepository.java
│   │       └── RolePermissionRepository.java
│   │
│   ├── dto/                                # Data Transfer Objects
│   │   ├── request/
│   │   │   ├── CreateUserRequest.java
│   │   │   ├── CreateCompanyRequest.java
│   │   │   ├── CreateLeadRequest.java
│   │   │   └── ...
│   │   │
│   │   └── response/
│   │       ├── UserResponse.java
│   │       ├── CompanyResponse.java
│   │       ├── LeadResponse.java
│   │       └── ...
│   │
│   ├── enums/                              # Enumerações
│   │   ├── Role.java
│   │   ├── AppointmentStatus.java
│   │   ├── Source.java
│   │   └── ...
│   │
│   ├── exceptions/                         # Tratamento de exceções
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── ApiError.java
│   │
│   ├── config/                             # Configurações Spring
│   │   ├── SecurityConfig.java
│   │   ├── JwtConfig.java
│   │   ├── OpenApiConfig.java
│   │   └── WebMvcConfig.java
│   │
│   └── utils/                              # Utilities
│       ├── JwtUtil.java
│       ├── PasswordEncoder.java
│       └── ...
│
├── src/main/resources/
│   ├── application.properties               # Configurações principais
│   ├── application-dev.properties          # Profile de desenvolvimento
│   ├── application.properties.example       # Exemplo de configuração
│   │
│   └── db/migration/                       # Migrações Flyway
│       └── V1__init_schema.sql
│
├── src/test/java/
│   └── com/wizeflow/crm_backend/
│       ├── controller/                     # Testes de Controllers
│       ├── services/                       # Testes de Services
│       └── CrmBackendApplicationTests.java # Teste de integração
│
├── docs/                                   # Documentação
│   ├── README.md                           # Este arquivo
│   ├── DEV_SETUP.md                        # Guia de setup dev
│   ├── ENTITIES.md                         # Documentação das entidades
│   ├── er-diagram.svg                      # Diagrama ER
│   ├── openapi.yaml                        # Especificação OpenAPI
│   ├── SECURITY.md                         # Práticas de segurança
│   ├── CONTRIBUTING.md                     # Guia de contribuição
│   └── CHANGELOG.md                        # Histórico de alterações
│
└── .github/
    └── workflows/
        ├── ci.yml                          # Pipeline CI (testes)
        └── release.yml                     # Pipeline de release
```

---

## 📚 Documentação

### Documentos Principais

| Documento | Descrição |
|-----------|-----------|
| **[DEV_SETUP.md](docs/DEV_SETUP.md)** | Guia completo de setup de desenvolvimento local |
| **[ENTITIES.md](docs/ENTITIES.md)** | Documentação detalhada de todas as entidades JPA |
| **[ER Diagram](docs/er-diagram.svg)** | Diagrama visual dos relacionamentos do banco |
| **[OpenAPI/Swagger](docs/openapi.yaml)** | Especificação completa da API (acesse em `/swagger-ui`) |
| **[SECURITY.md](docs/SECURITY.md)** | Práticas de segurança e gerenciamento de secrets |
| **[CONTRIBUTING.md](docs/CONTRIBUTING.md)** | Guia para contribuidores (branches, commits, PRs) |
| **[CHANGELOG.md](docs/CHANGELOG.md)** | Histórico de versões e mudanças |

### Acessar Swagger UI

Após iniciar a aplicação, acesse a interface interativa em:

```
http://localhost:8080/swagger-ui/index.html
```

Aqui você pode:
- ✅ Visualizar todos os endpoints
- ✅ Testar as requisições diretamente
- ✅ Ver modelos de request/response
- ✅ Autorizar com JWT token

---

## 🤝 Contribuindo

### Workflow de Contribuição

1. **Fork** o repositório
2. **Clone** seu fork: `git clone https://github.com/seu-usuario/WizeFlowCRM-Java.git`
3. **Crie uma branch** para sua feature: `git checkout -b feature/nova-funcionalidade`
4. **Commit** suas mudanças: `git commit -m "feat: descrição clara da mudança"`
5. **Push** para sua branch: `git push origin feature/nova-funcionalidade`
6. **Abra um Pull Request** com descrição detalhada

### Padrões de Código

- ✅ Use **Java naming conventions** (camelCase para variáveis, PascalCase para classes)
- ✅ Siga **Spring Boot best practices** (injeção de dependência, anotações)
- ✅ Escreva **testes** para novas funcionalidades (cobertura mínima 60%)
- ✅ Use **Lombok** para reduzir boilerplate (getters, setters, constructors)
- ✅ Documente **métodos públicos** com Javadoc

### Convenção de Commits

```
feat:    nova funcionalidade (User: implementar login)
fix:     correção de bug (User: corrigir erro 500 no login)
docs:    documentação (README: adicionar seção de setup)
test:    testes (User: adicionar testes do service)
style:   formatação, sem mudança lógica
refactor: refatoração sem novas features
perf:    melhoria de performance
```

Exemplo:
```bash
git commit -m "feat: implementar autenticação JWT com refresh token"
```

### Rodando Testes Localmente

```bash
# Executar todos os testes
./mvnw test

# Executar teste específico
./mvnw test -Dtest=UserControllerTest

# Gerar relatório de cobertura
./mvnw jacoco:report
```

---

## 🐛 Reportar Issues

Encontrou um bug? Siga os passos:

1. Verifique se o issue já não foi reportado em [Issues](https://github.com/raylann-lopes/WizeFlowCRM-Java/issues)
2. Se não existe, [crie um novo issue](https://github.com/raylann-lopes/WizeFlowCRM-Java/issues/new)
3. Inclua:
    - ✅ Título claro e descritivo
    - ✅ Descrição detalhada do bug
    - ✅ Passos para reproduzir
    - ✅ Comportamento esperado vs. real
    - ✅ Screenshot ou logs se aplicável
    - ✅ Versão Java, SO e navegador (se relevante)

---

## 📋 Roadmap

### v1.0 (MVP) - Fevereiro 2026
- [x] CRUD básico de todas as entidades
- [x] Autenticação JWT
- [x] Controle de acesso (RBAC)
- [x] Auditoria completa
- [x] Vector Search com pgvector
- [ ] Testes de integração
- [ ] Documentação completa

### v1.1 - Março 2026
- [ ] WebSockets para chat em tempo real
- [ ] Webhooks para integrações externas
- [ ] Rate limiting e throttling
- [ ] Cache distribuído (Redis)
- [ ] Métricas avançadas

### v2.0 - Q2 2026
- [ ] Microsserviços
- [ ] GraphQL support
- [ ] Machine Learning para recomendações
- [ ] Mobile app (Flutter)

---

## 📞 Suporte

### Como Obter Ajuda

1. **Documentação:** Verifique [docs/](docs/) primeiro
2. **Stack Overflow:** Tag com `wizeflow-crm`
3. **Issues:** Procure em [GitHub Issues](https://github.com/raylann-lopes/WizeFlowCRM-Java/issues)
4. **Discussões:** Use [GitHub Discussions](https://github.com/raylann-lopes/WizeFlowCRM-Java/discussions)
5. **Email:** support@wizeflow.com (quando disponível)

### Contato dos Desenvolvedores

| Pessoa | Contato | Área |
|--------|---------|------|
| **Raylann Lopes** | [@raylann-lopes](https://github.com/raylann-lopes) | Project Lead / Backend |

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** - veja [LICENSE](LICENSE) para detalhes.

---

## 🙏 Agradecimentos

- **Spring Boot Team** pela excelente framework
- **PostgreSQL Community** pelo database robusto
- **Comunidade Java** pelo ecossistema incrível

---

## 📊 Status do Projeto

| Métrica | Status |
|---------|--------|
| **Build** | [![CI](https://github.com/raylann-lopes/WizeFlowCRM-Java/workflows/CI/badge.svg)](https://github.com/raylann-lopes/WizeFlowCRM-Java/actions) |
| **Coverage** | ![Coverage](https://img.shields.io/badge/coverage-72%25-brightgreen) |
| **Issues** | [![GitHub issues](https://img.shields.io/github/issues/raylann-lopes/WizeFlowCRM-Java)](https://github.com/raylann-lopes/WizeFlowCRM-Java/issues) |
| **PRs** | [![GitHub PRs](https://img.shields.io/github/issues-pr/raylann-lopes/WizeFlowCRM-Java)](https://github.com/raylann-lopes/WizeFlowCRM-Java/pulls) |
| **Última Atualização** | Fevereiro 13, 2026 |

---

**⭐ Se este projeto foi útil, por favor deixe uma star!**

Made with ❤️ by [Raylann Lopes](https://github.com/raylann-lopes)

