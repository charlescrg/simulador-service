# CaixaHackathonBackend

   Simulador-Service (Hackathon VITEC)
	   Microserviço desenvolvido em Quarkus para simulação de empréstimos, conforme desafio do Hackathon VITEC 2025.

## Tecnologias Utilizadas
- Java 17
- Quarkus
- Docker / Docker Compose
- Swagger (OpenAPI)
- Bucket4j (Rate Limiting)
- SLF4J + Logback (Logging)
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
      docker-compose up -d

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
      [aqui](postman/simulador-service.postman_collection.json) 


### Autenticação da API

      Esta API usa **JWT (JSON Web Token)** para autenticação. 
      Todos os endpoints requerem um token JWT válido no header `Authorization`.
	  Para fins de avaliação a collection já está com o token, já que este projeto não vai gerá-lo, apenas validar.


 **Aviso de Segurança**

	As chaves privadas e públicas estão incluídas diretamente no código apenas para fins de avaliação no Hackathon.

	**Em ambientes de produção**, recomenda-se fortemente:
	- Utilizar ferramentas como **HashiCorp Vault** ou **AWS Secrets Manager** para gerenciar segredos.


## Segurança

O endpoint de simulação está protegido com:

- `@Authenticated`: exige autenticação do usuário.
- `@Valid`: valida os dados da requisição.
- `@Schema`: documenta os campos da API.
- **Validação de entrada**: com limites mínimos e máximos para valor e prazo.
- **Auditoria e logging**: registra IP, usuário e parâmetros da requisição.
