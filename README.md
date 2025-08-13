# CaixaHackathonBackend

   Simulador-Service (Hackathon VITEC)
	   Microserviço desenvolvido em Quarkus para simulação de empréstimos, conforme desafio do Hackathon VITEC 2025.

## Tecnologias Utilizadas
- Java 17
- Spring Boot
- Docker / Docker Compose
- Swagger (OpenAPI)
- JUnit / Mockito
- GitHub Actions (CI/CD)
- PostgreSQL
- Prometheus / Grafana
- Caffeine (Cache)
- Resilience4j (Circuit Breaker)
- K6 (Teste de carga)

## Como Rodar o Projeto

   ### Pré-requisitos
   - Java 17
   - Docker e Docker Compose
   - Maven

   ### Passos
   1. Clone o repositório:
      
      git clone https://github.com/charlescrg/superapp-backend.git
      cd superapp-backend
   
   3. Execute com Docker:
      docker-compose up --build
   
   4. Acesse a API:
   
      Localmente (ambiente de desenvolvimento):
         http://localhost:8080/api
   
      Produção (servidor online):
         https://seu-dominio-ou-ip-de-producao/api
   
   5. Acesse a documentação Swagger:
   
      Localmente:
      http://localhost:8080/swagger-ui
      Para acessar a documentação quando estiver rodando o servidor localmente.
   
      Produção:
      https://seu-dominio-ou-ip-de-producao/swagger-ui
      Para acessar a documentação da API em produção.

## Testes com Postman
   Importe a coleção Postman disponível em:
      [aqui](postman/SuperApp.postman_collection.json) 


### Autenticação da API

      Esta API usa **JWT (JSON Web Token)** para autenticação. 
      Todos os endpoints requerem um token JWT válido no header `Authorization`.

### Como usar

      Envie o token JWT no header (Authorization) da sua requisição assim:

      Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL215LWFwcCIsInN1YiI6InVzZXJvMSIsImF1ZCI6Imh0dHBzOi8vbXktYXBwIiwiZ3JvdXBzIjoidXNlcixhZG1pbiIsImlhdCI6MTY5MjYxMDAwMCwiZXhwIjoxNzM1Njg5NjAwfQ.QzKH-E2vYPyrq68J8gIpqTSy6t42DmPCWltxF6DYAX4iXkBpcX2xbiTIu4Tx5QKfTkUo-rCS6RhnK5oIT_0ZpP61N6JYwcGIJajSLOiK7ZTgujzHYwqCYl5DHosHG0VLQuGvZ4rCG9y0RtJr8TspmspIZ-r9JuNpkQFk89YZ9dwPc
