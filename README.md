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

      Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL215LWFwcCIsInVwbiI6ImpvYW8uZ29tZXMiLCJncm91cHMiOlsiYWRtaW4iXSwiaWF0IjoxNzU1MDQ5MzgwLCJleHAiOjE3ODY1ODUzODAsImp0aSI6ImE4NmY2N2QwLTMzODAtNGE0NC04NDI2LWRmYmQ3YjUwODQwZSJ9.JKy9adiZxDDV-H9Re-WT4tv7DiYgpgHdHL-66tHkj3AmYlRu6ELJajLlGYDEt1w_Vn9lLCB6LTfdd_LIhmwdN1Jdkzeb5JRBDp7QnsXNFOutwOCfQPMuPuMuKHuecaEQmBtvYtWVqAXDuQb-bTshRTz4mBaTZNetvrA17zb1tfCq2lyqNqMttO7ktgE9R0AhUAjrM7gd_CMUq31EVXms5pUt7tuk7sEBuJSHzRPlut9Gc8fCqRAAqJ82kbd5BbAvM_TebL549uC9fyn-Nyy7oKjl710i_yyG2hQRXdJSt8qhJqOLIA0R1fDYCpSPvDtz0f21DKuIe2eLe0Ra6JR2Ng

