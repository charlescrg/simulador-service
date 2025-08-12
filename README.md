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

  
