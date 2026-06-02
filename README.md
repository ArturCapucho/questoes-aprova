# Questoes Aprova

Plataforma de estudos para concursos com backend em Java/Spring Boot e frontend em React. O projeto permite cadastrar usuarios, autenticar com JWT, cadastrar categorias e questoes, responder alternativas e acompanhar um desempenho basico do aluno.

## Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Redis
- Swagger/OpenAPI
- Actuator
- Docker Compose
- LangChain4j/OpenAI
- React
- Vite

## Funcionalidades

- Cadastro e login de usuarios
- Autenticacao stateless com JWT
- Cadastro de categorias
- Cadastro e listagem paginada de questoes
- Registro de tentativas usando o usuario autenticado pelo token
- Painel basico de desempenho do aluno
- Cache Redis para explicacoes de IA
- Documentacao da API com Swagger
- Ambiente local com PostgreSQL, Redis e pgAdmin via Docker Compose

## Arquitetura

O backend usa arquitetura em camadas:

```text
controller  -> endpoints REST
dto         -> contratos de entrada e saida da API
service     -> regras de negocio
repository  -> acesso ao banco com Spring Data JPA
entity      -> mapeamento JPA das tabelas
config      -> seguranca, JWT, CORS e OpenAPI
exception   -> tratamento global de erros
```

Fluxo principal:

```text
React/Vite -> API Spring Boot -> Service -> Repository -> PostgreSQL
```

## Como Rodar

Pre-requisitos:

- Java 21
- Docker e Docker Compose
- Node.js 22+

Suba a infraestrutura:

```bash
docker compose up -d postgres redis pgadmin
```

Rode o backend:

```bash
./mvnw spring-boot:run
```

No Windows, se o wrapper falhar, use o Maven instalado localmente.

Rode o frontend:

```bash
cd frontend
npm install
npm run dev
```

No Windows, se o PowerShell bloquear `npm`, use:

```bash
npm.cmd install
npm.cmd run dev
```

URLs:

- Frontend: `http://localhost:5173`
- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`
- pgAdmin: `http://localhost:5050`

## Endpoints Principais

Publicos:

```text
POST /api/usuarios
POST /api/auth/login
GET  /swagger-ui.html
GET  /actuator/health
```

Protegidos por JWT:

```text
GET  /api/usuarios
POST /api/categorias
GET  /api/categorias
POST /api/questoes
GET  /api/questoes?page=0&size=20
GET  /api/questoes/{ano}/{id}
POST /api/tentativas
GET  /api/tentativas/desempenho
POST /api/editais/mapear-categorias
```

Header para endpoints protegidos:

```text
Authorization: Bearer <token>
```

## Fluxo De Teste

1. Acesse `http://localhost:5173`.
2. Crie um usuario.
3. Faca login para obter o JWT.
4. Crie uma categoria.
5. Crie uma questao com alternativas.
6. Carregue as questoes.
7. Responda uma alternativa.
8. Consulte o painel de desempenho.

## Banco De Dados

O schema e versionado com Flyway em:

```text
src/main/resources/db/migration
```

Credenciais locais:

```text
host: localhost
port: 5432
database: concursos
user: concursos
password: concursos
```

## Status

Projeto em desenvolvimento para estudo e portfolio. Proximas melhorias previstas incluem testes automatizados, filtros avancados de questoes, area administrativa mais completa e evolucao do dashboard do aluno.
