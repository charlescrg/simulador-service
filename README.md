# CaixaHackathonBackend

Simulador-Service (Hackathon VITEC)
	Microserviço desenvolvido em Quarkus para simulação de empréstimos, conforme desafio do Hackathon VITEC 2025.

## Tecnologias Utilizadas
	- Java 17
	- Quarkus
	- Docker / Docker Compose
	- Swagger (OpenAPI)
	- Bucket4j (Rate Limiting)
	- SLF4J (Logging)
	- SQL Server
	- GitHub Actions (CI/CD)
	- JUnit / Mockito
	- Caffeine (Cache)
	- MicroProfile Fault Tolerance (Circuit Breaker)

	- Prometheus / Grafana	
	- K6 (Teste de carga)

## Como Rodar o Projeto

   ### Pré-requisitos
	   - Java 17
	   - Docker e Docker Compose
	   - Maven

   ### Passos
   1. Clone o repositório:
      
	      git clone https://github.com/charlescrg/simulador-service.git
	      cd superapp-backend
   
   3. Execute com Docker:

      		docker-compose up -d

   5. Acesse a API:
   
	      Localmente (ambiente de desenvolvimento):
	         http://localhost:8080/api/vi/simulacao

   
   6. Acesse a documentação Swagger:
   
		      http://localhost:8080/swagger-ui
		      Para acessar a documentação quando estiver rodando o servidor localmente.
	

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

## Integração Contínua

	Este projeto utiliza GitHub Actions para realizar o build automático a cada push na branch `master`.  
	A pipeline inclui:
	
	- Configuração do ambiente Java 17
	- Inicialização de banco SQL Server em container
	- Execução de script de criação de tabelas (`init.sql`)
	- Build do projeto com Maven
	
	O workflow está definido em `.github/workflows/build.yml`.

## Resiliência e Tolerância a Falhas

	O serviço utiliza MicroProfile Fault Tolerance / Resilience4j para aumentar a confiabilidade:
	
	- `@CircuitBreaker`: evita sobrecarga em caso de falhas repetidas.
	  - `@Fallback`: define um método alternativo caso a simulação falhe.
	  - `@Retry`: tenta executar novamente operações temporariamente instáveis.
	  - `@Timeout`: limita o tempo máximo de execução de uma requisição.
	  - `@RateLimit`: controla o número de requisições para evitar abuso.
	
	Estas estratégias garantem que o serviço continue disponível mesmo diante de falhas temporárias ou alta carga.

## Cache

	O projeto utiliza **Caffeine Cache** para armazenar resultados de simulações em memória:
	
	- Reduz o tempo de resposta em chamadas repetidas.
	  - Diminui o processamento desnecessário de cálculos.
	  - Melhora o desempenho geral do serviço.
