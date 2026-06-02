# Questoes Aprova

Plataforma de questoes de concursos criada como projeto de portfolio Java. O projeto combina uma API REST em Spring Boot com um frontend React demonstrativo para consumir os principais fluxos: cadastro, login, categorias, questoes e tentativa de resposta.

## Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security com JWT stateless
- PostgreSQL
- Flyway
- Redis
- Swagger/OpenAPI
- Actuator
- Docker Compose
- LangChain4j/OpenAI
- Lombok
- React
- Vite

## Arquitetura

O projeto usa arquitetura em camadas dentro do pacote `com.portfolio.questoes_aprova`:

```text
controller  -> recebe requisicoes HTTP
dto         -> define contratos de entrada e saida da API
service     -> concentra regras de negocio
repository  -> acessa o banco com Spring Data JPA
entity      -> mapeia tabelas do PostgreSQL com JPA/Hibernate
config      -> configura seguranca, JWT e OpenAPI
exception   -> padroniza erros JSON
```

Fluxo principal:

```text
Cliente/Swagger -> Controller -> RequestDTO -> Service -> Entity -> Repository -> PostgreSQL
PostgreSQL -> Entity -> Service -> ResponseDTO -> Controller -> Cliente/Swagger
```

O frontend fica em `frontend/` e consome a API usando proxy do Vite:

```text
React/Vite -> /api -> Spring Boot -> PostgreSQL/Redis
```

## Decisoes Tecnicas

- **DTOs**: separam o contrato publico da API das entidades do banco. Isso evita vazar dados internos, como `senhaHash`.
- **JPA/Hibernate**: mapeia classes Java para tabelas relacionais e gera SQL para operacoes comuns.
- **Flyway**: versiona o schema do banco com scripts SQL em `src/main/resources/db/migration`.
- **PostgreSQL**: banco relacional principal da aplicacao.
- **Redis**: cacheia explicacoes de IA para evitar chamadas repetidas e reduzir latencia/custo.
- **JWT stateless**: a API autentica por token sem manter sessao no servidor.
- **Swagger**: documenta e permite testar endpoints pelo navegador.
- **Actuator**: expoe saude e metricas basicas da aplicacao.
- **Docker Compose**: sobe Postgres, Redis, pgAdmin e a aplicacao em ambiente local.
- **Frontend React**: oferece uma interface simples para demonstrar os endpoints da API.

## Como Rodar

Pre-requisitos:

- Java 21
- Docker e Docker Compose
- Node.js 22+

Suba a infraestrutura:

```bash
docker compose up -d postgres redis pgadmin
```

Rode a aplicacao localmente:

```bash
./mvnw spring-boot:run
```

No Windows, se o wrapper falhar, use o Maven instalado ou o Maven baixado pelo wrapper em `~/.m2/wrapper/dists`.

URLs uteis:

- API: `http://localhost:8080`
- Frontend: `http://localhost:5173`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- pgAdmin: `http://localhost:5050`

Credenciais locais do banco:

```text
host: localhost
port: 5432
database: concursos
user: concursos
password: concursos
```

pgAdmin:

```text
email: admin@questoesaprova.local
password: admin
```

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

## Endpoints Principais

Endpoints publicos:

```text
POST /api/usuarios
POST /api/auth/login
GET  /swagger-ui.html
GET  /actuator/health
GET  /actuator/metrics
```

Endpoints protegidos por JWT:

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

Para endpoints protegidos, envie:

```text
Authorization: Bearer <token>
```

## Fluxo Minimo Para Testar

Pelo frontend:

1. Abra `http://localhost:5173`.
2. Crie um usuario.
3. Faca login para armazenar o JWT.
4. Crie uma categoria.
5. Crie uma questao.
6. Carregue questoes e responda uma alternativa.

Pelo Swagger ou cliente HTTP:

1. Criar usuario:

```http
POST /api/usuarios
Content-Type: application/json

{
  "nome": "Artur",
  "email": "artur@example.com",
  "senha": "123456",
  "role": "ALUNO"
}
```

2. Login:

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "artur@example.com",
  "senha": "123456"
}
```

3. Criar categoria:

```http
POST /api/categorias
Authorization: Bearer <token>
Content-Type: application/json

{
  "nome": "Direito Constitucional",
  "slug": "direito-constitucional",
  "descricao": "Questoes de direito constitucional",
  "categoriaPaiId": null
}
```

4. Criar questao:

```http
POST /api/questoes
Authorization: Bearer <token>
Content-Type: application/json

{
  "ano": 2026,
  "enunciado": "A Constituicao Federal de 1988 e considerada a lei maior do Brasil.",
  "banca": "Exemplo",
  "orgao": "Orgao Exemplo",
  "origem": "MANUAL",
  "categoriaId": 1,
  "alternativas": [
    { "letra": "A", "texto": "Certo", "correta": true },
    { "letra": "B", "texto": "Errado", "correta": false }
  ]
}
```

5. Responder tentativa:

```http
POST /api/tentativas
Authorization: Bearer <token>
Content-Type: application/json

{
  "questaoId": 1,
  "questaoAno": 2026,
  "alternativaEscolhidaId": 1
}
```

O backend identifica o aluno pelo JWT. Por isso o cliente nao envia `usuarioId` ao registrar tentativa.

6. Consultar desempenho:

```http
GET /api/tentativas/desempenho
Authorization: Bearer <token>
```

## Validacoes Implementadas

- Usuario exige nome, e-mail valido e senha com tamanho minimo.
- Categoria exige nome e slug em formato amigavel para URL.
- Questao exige ano, enunciado, categoria e pelo menos duas alternativas.
- Questao deve ter exatamente uma alternativa correta.
- Tentativa exige usuario, questao, ano e alternativa escolhida.
- Tentativa usa o usuario autenticado pelo JWT, evitando que o cliente escolha outro `usuarioId`.
- Erros de validacao retornam JSON padronizado pelo `GlobalExceptionHandler`.

## Como Explicar Em Entrevista

Frase curta:

> Desenvolvi uma API REST em Java 21 com Spring Boot, usando arquitetura em camadas, DTOs para separar contrato da API das entidades JPA, Flyway para versionamento do PostgreSQL, Redis para cache de explicacoes de IA, JWT stateless para autenticacao, Swagger para documentacao e Docker Compose para ambiente local.

Conceitos-chave:

- **DTO vs Entity**: DTO representa o JSON da API; Entity representa a tabela do banco.
- **Service**: concentra regras de negocio e evita controller com logica pesada.
- **Repository**: isola acesso ao banco usando Spring Data JPA.
- **JPA vs Hibernate vs PostgreSQL**: JPA e o padrao, Hibernate e a implementacao, PostgreSQL e o banco real.
- **Flyway**: cria e evolui o schema com migrations versionadas.
- **JWT stateless**: o servidor valida o token sem guardar sessao.
- **Tentativa autenticada**: o backend identifica o aluno pelo JWT, sem confiar em `usuarioId` enviado pelo frontend.
- **Redis**: guarda respostas frequentes em cache para melhorar performance.
- **Frontend React**: consome os endpoints para demonstrar o fluxo real da aplicacao.

## Post Sugerido Para LinkedIn

```text
Estou desenvolvendo o Questoes Aprova, uma plataforma de questoes de concursos com backend Java/Spring Boot e frontend React.

O projeto foi pensado como um laboratorio pratico de backend Java, com arquitetura em camadas, DTOs, JPA/Hibernate, PostgreSQL, Flyway, Redis, autenticacao JWT, Swagger, Actuator, Docker Compose e uma integracao inicial com IA usando LangChain4j/OpenAI. Para demonstrar o uso real da API, adicionei um frontend em React/Vite consumindo os fluxos de cadastro, login, categorias, questoes e tentativas.

O foco foi construir uma base proxima de um projeto real: banco versionado com migrations, seguranca stateless, cache para reduzir chamadas repetidas de IA, documentacao dos endpoints e ambiente local reproduzivel com Docker.

Tecnologias: Java, Spring Boot, Spring Data JPA, Spring Security, PostgreSQL, Redis, Flyway, Docker, Swagger/OpenAPI, React e Vite.

Proximos passos: testes automatizados, filtros avancados de questoes e melhoria do fluxo de usuario autenticado.
```

## Proximos Passos

- Adicionar testes unitarios com JUnit e Mockito.
- Adicionar testes de controller com MockMvc.
- Usar o usuario autenticado pelo JWT em vez de receber `usuarioId` no corpo da tentativa.
- Criar filtros por banca, orgao, ano e categoria.
- Evoluir a integracao com IA e o agente de editais.
