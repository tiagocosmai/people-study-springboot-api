# people-study-springboot-api

API **People** em **Spring Boot 3** espelhando o contrato da aplicação **people-api** (NestJS + Prisma + MongoDB): mesmos caminhos REST, papéis JWT, seed e modelo de dados nas coleções `users` e `people`.

Repositório de estudo: [tiagocosmai/people-study-springboot-api](https://github.com/tiagocosmai/people-study-springboot-api).

## Stack

- Java **21**, Spring Boot **3.4**
- MongoDB (Spring Data MongoDB) — mesma URI que o Nest, incluindo **`replicaSet=rs0`** quando usas o `docker-compose` do `people-api`
- JWT (access + refresh, `expiresIn` **3600**), BCrypt **10**
- OpenAPI / Swagger UI (`/swagger-ui`, redirecionamentos `/api-docs` e `/swagger` como no Nest)

## Pré-requisitos

- JDK **21**
- Maven **3.9+** (ou compilar com Docker: ver secção abaixo)
- MongoDB acessível (podes subir só o Mongo com o `docker-compose.dev.yml` do projeto **people-api**)

## Configuração

Copia variáveis (ou exporta no shell):

```bash
cp .env.example .env
# Ajusta DATABASE_URL, JWT_SECRET, JWT_REFRESH_SECRET, PORT se precisares
```

O Spring lê propriedades por variável de ambiente; por exemplo `DATABASE_URL` mapeia para `spring.data.mongodb.uri` **se** definires no `application.yml` — já está como `${DATABASE_URL:...}`.

Ficheiro principal: `src/main/resources/application.yml`.

**JWT:** os segredos são derivados com **SHA-256** para cumprir HS256 no Java. Tokens **não** são intercambiáveis com o Nest mesmo com o mesmo texto em `JWT_SECRET`; a API e o modelo de dados são que são alinhados.

## Makefile (paridade com `package.json` do Nest)

| Comando Make | Equivalente Nest (`npm run …`) | Descrição |
|--------------|-------------------------------|-----------|
| `make help` | — | Lista todos os alvos |
| `make install` | `install` | Dependências Maven |
| `make clean` | — | `mvn clean` |
| `make build` | `build` | Gera o JAR |
| `make test` | `test` | Testes |
| `make test-cov` | `test:cov` | Testes (ver nota no Makefile sobre Jacoco) |
| `make verify` | `test:e2e` (aprox.) | `mvn verify` |
| `make dev` / `make run` | `start:dev` | `spring-boot:run` |
| `make start-prod` | `start:prod` | `java -jar` após `build` |
| `make stop` | — | Liberta a porta `3000` (`fuser`) |
| `make docker-dev` | `docker:dev` | Só Mongo via `../people-api/docker-compose.dev.yml` |
| `make docker-dev-down` | `docker:dev:down` | Para esse Mongo |
| `make docker-dev-logs` | — | Logs do Mongo dev |
| `make docker-up` | `docker:up` | Mongo + API (este repo, `docker-compose.yml`) |
| `make docker-down` | `docker:down` | Para a stack deste repo |
| `make docker-down-v` | `docker compose down -v` | Remove também o volume Mongo |
| `make docker-logs` | `docker:logs` | Logs da stack |
| `make docker-build` | `docker:build` | Build da imagem da API |
| `make docker-up-api` | `docker:up:api` | Mongo do people-api + indicação para `make dev` |

Variáveis úteis:

- `PEOPLE_API_ROOT` — caminho para o clone do **people-api** (defeito: `../people-api`).
- `MVN` — por exemplo `MVN="docker run --rm -v $$(pwd):/app -w /app maven:3.9-eclipse-temurin-21 mvn"` se não tiveres Maven instalado.

```bash
make help
make docker-dev && make dev    # Mongo ao lado (Nest) + API Spring local
make docker-up                 # Tudo em Docker neste repositório
```

## Correr (sem Make)

```bash
mvn spring-boot:run
```

Por defeito: **http://localhost:3000** (porta igual ao Nest).

- Health: `GET /health`, `GET /health-check`
- Swagger UI: **http://localhost:3000/swagger-ui/index.html** (ou **http://localhost:3000/api-docs** → redireciona)

## Compilar com Docker (sem Maven local)

```bash
docker run --rm -v "$PWD":/app -w /app maven:3.9-eclipse-temurin-21 mvn -DskipTests package
java -jar target/people-study-springboot-api-1.0.0.jar
```

## Utilizadores seed (idempotente)

| Utilizador | Password   | Role   |
|------------|------------|--------|
| admin      | Admin@123  | ADMIN  |
| system     | Admin@123  | SYSTEM |
| viewer     | Admin@123  | VIEWER |

O seed corre ao arranque e **só cria** utilizadores em falta (compatível com base já povoada pelo Prisma).

## Endpoints (resumo)

| Método | Caminho | Auth / roles |
|--------|---------|----------------|
| POST | `/api/user/login` | Público |
| POST | `/api/user/refresh` | Público |
| POST | `/api/user/forgot-password` | Público |
| POST | `/api/user` | ADMIN |
| GET | `/api/user?search=` | ADMIN, SYSTEM |
| GET | `/api/user/{id}` | ADMIN, SYSTEM |
| PUT | `/api/user/{id}` | ADMIN |
| DELETE | `/api/user/{id}` | ADMIN |
| POST | `/api/people` | ADMIN, SYSTEM |
| GET | `/api/people?search=` | ADMIN, SYSTEM, VIEWER |
| GET | `/api/people/{id}` | ADMIN, SYSTEM, VIEWER |
| PUT | `/api/people/{id}` | ADMIN, SYSTEM |
| DELETE | `/api/people/{id}` | ADMIN, SYSTEM |

Header: `Authorization: Bearer <accessToken>`.

## Licença

MIT
