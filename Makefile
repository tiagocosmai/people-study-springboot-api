# Makefile espelhando os scripts npm do people-api (NestJS).
# Uso: make help | make dev | make docker-up | …

SHELL := bash
.SHELLFLAGS := -eu -o pipefail -c

.DEFAULT_GOAL := help
.PHONY: help install clean build test test-cov verify run dev start-prod stop \
	docker-dev docker-dev-down docker-dev-logs \
	docker-build docker-up docker-down docker-logs docker-up-api \
	keyfile fmt-check

# Maven: use `mvn` local ou `make MVN="docker run --rm -v $$(pwd):/app -w /app maven:3.9-eclipse-temurin-21 mvn"`
MVN ?= mvn

# Repositório people-api ao lado (Mongo dev só): npm run docker:dev
PEOPLE_API_ROOT ?= $(abspath $(CURDIR)/../people-api)
COMPOSE_DEV ?= $(PEOPLE_API_ROOT)/docker-compose.dev.yml

COMPOSE := docker compose
JAR := target/people-study-springboot-api-1.0.0.jar
PORT ?= 3000

help: ## Mostra esta ajuda
	@echo "People Study Spring Boot — alinhado aos scripts do package.json (people-api Nest)"
	@echo ""
	@grep -E '^[a-zA-Z0-9_-]+:.*##' $(MAKEFILE_LIST) | sort | sed 's/:.*##/: /'
	@echo ""
	@echo "Variáveis: MVN, PEOPLE_API_ROOT (default: ../people-api), PORT (default: 3000)"

# --- Maven (npm install / build / test / start) ---

install: ## npm install — resolve dependências Maven
	$(MVN) -B dependency:resolve

clean: ## Limpar target/
	$(MVN) clean

build: ## nest build — compila JAR (sem testes)
	$(MVN) -B -DskipTests package

test: ## npm test
	$(MVN) -B test

test-cov: ## npm run test:cov — testes (Jacoco opcional; hoje equivale a test)
	@echo "Nota: acrescenta Jacoco ao pom para relatório HTML em target/site/jacoco"
	$(MVN) -B test

verify: ## npm run test:e2e (aprox.) — mvn verify
	$(MVN) -B verify

run: dev ## npm run start — alias para spring-boot:run

dev: ## npm run start:dev — API em primeiro plano (PORT=3000 por defeito)
	$(MVN) -B spring-boot:run

start-prod: build ## npm run start:prod — java -jar após package
	test -f $(JAR)
	java -jar $(JAR)

stop: ## Encerra o que estiver a escutar na porta $(PORT) (fuser)
	@command -v fuser >/dev/null 2>&1 && fuser -k $(PORT)/tcp 2>/dev/null && echo "Porta $(PORT) libertada." || { echo "fuser não disponível ou porta já livre."; exit 0; }

fmt-check: ## Verificação de formato (placeholder — p.ex. Spotless no pom)
	@echo "Configure Spotless/spring-javaformat no pom para ativar."

# --- Docker: Mongo partilhado (como npm run docker:dev) ---

docker-dev: ## npm run docker:dev — só Mongo (compose do people-api)
	@test -f "$(COMPOSE_DEV)" || (echo "Ficheiro em falta: $(COMPOSE_DEV). Ajusta PEOPLE_API_ROOT ou coloca people-api ao lado."; exit 1)
	$(COMPOSE) -f "$(COMPOSE_DEV)" up -d

docker-dev-down: ## npm run docker:dev:down
	@test -f "$(COMPOSE_DEV)" || exit 0
	$(COMPOSE) -f "$(COMPOSE_DEV)" down

docker-dev-logs: ## Logs do Mongo dev (people-api)
	@test -f "$(COMPOSE_DEV)" || exit 1
	$(COMPOSE) -f "$(COMPOSE_DEV)" logs -f

# --- Docker: stack completa neste repo (como npm run docker:up) ---

keyfile: ## Garante docker/mongo-keyfile (replica set Mongo)
	@mkdir -p docker
	@test -s docker/mongo-keyfile || openssl rand -base64 756 | tr -d '\n' > docker/mongo-keyfile
	@chmod 644 docker/mongo-keyfile
	@echo "docker/mongo-keyfile OK"

docker-build: keyfile ## npm run docker:build — build da imagem API
	$(COMPOSE) build api

docker-up: keyfile ## npm run docker:up — Mongo + API Spring
	$(COMPOSE) up -d --build

docker-down: ## npm run docker:down — para stack deste repo (mantém volumes)
	$(COMPOSE) down

docker-down-v: ## docker compose down -v (apaga volume Mongo deste compose)
	$(COMPOSE) down -v

docker-logs: ## npm run docker:logs — segue logs de todos os serviços
	$(COMPOSE) logs -f

# Mongo do people-api em background, depois Spring local (como npm run docker:up:api)
docker-up-api: docker-dev ## Sobe Mongo (people-api) e indica como arrancar a API
	@echo "Mongo em Docker. Na mesma máquina, com DATABASE_URL apontando para localhost:27017 (+ replicaSet=rs0), corre:"
	@echo "  make dev"
