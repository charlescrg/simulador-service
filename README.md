# CaixaHackathonBackend

- evento: https://github.com/HelloysaPires/hackaton-caixa, https://github.com/rodriguesla/api-simulador

- pagina resultado testes: https://github.com/HelloysaPires/hackaton-caixa, https://github.com/rodriguesla/api-simulador

- teste automatizado newman: https://github.com/rodriguesla/api-simulador

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
    - PostgreSQL
	- GitHub Actions (CI/CD)
	- JUnit / Mockito
	- Caffeine (Cache)
	- MicroProfile Fault Tolerance (Circuit Breaker)
    - Redis
    - Jaeger
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
   
   3. Execute com Docker:

      		docker-compose up -d

   5. Acesse a API:
   
      Localmente (ambiente de desenvolvimento):
	         http://localhost:8080/api/v1/simulacoes

## Testes com Postman
Importe a coleção Postman disponível em:
[aqui](postman/simulador-service.postman_collection.json)


## Documentação da API  
   Acesse a documentação Swagger:
            http://localhost:8080/swagger-ui
        
         Obs. Se quiser testar via swagger, use o token JWT já incluso na collection do Postman para testes

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
	- Inicialização de banco Postgres em container
	- Build do projeto com Maven
	
	O workflow está definido em `.github/workflows/build.yml`.

## Resiliência e Tolerância a Falhas

	O serviço utiliza MicroProfile Fault Tolerance / Resilience4j para aumentar a confiabilidade:
	
      - `@CircuitBreaker`: evita sobrecarga em caso de falhas repetidas.
	  - `@Retry`: tenta executar novamente operações temporariamente instáveis.
	  - `@Timeout`: limita o tempo máximo de execução de uma requisição.
	  - `@RateLimit`: controla o número de requisições para evitar abuso.
	
	Estas estratégias garantem que o serviço continue disponível mesmo diante de falhas temporárias ou alta carga.

## Cache

	O projeto utiliza **Caffeine Cache** para armazenar resultados de simulações em memória:
	
	  - Reduz o tempo de resposta em chamadas repetidas.
	  - Diminui o processamento desnecessário de cálculos.
	  - Melhora o desempenho geral do serviço.

## Observabilidade

Este projeto inclui uma infraestrutura de observabilidade utilizando métricas, logging e tracing.

**Métricas:**

- **Prometheus**  
  Prometheus coleta métricas da aplicação Quarkus expostas no endpoint `/metrics`.
    - Essas métricas incluem tempo de resposta e volume de requisições.
    - Acesse o Prometheus em: [http://localhost:9090](http://localhost:9090)

- **Grafana**  
   Grafana é usado para visualizar as métricas coletadas pelo Prometheus em dashboards interativos.
   - Acesse o Grafana em: [http://localhost:3000](http://localhost:3000)
      
                1️⃣ Acessar o Grafana
                    Abra no navegador: http://localhost:3000
                    Login padrão (definido no Compose):
                    Usuário: admin
                    Senha: admin
                    Você será solicitado a trocar a senha no primeiro login (recomendado).
            
                2️⃣ Configurar o datasource
                    No menu lateral, clique em Conections → Data Sources.
                    Clique em Add data source.
                    Escolha Prometheus. 
                        Configure:
                        URL: http://prometheus:9090

                    Clique em Save & Test.
            
                3️⃣ Criar um dashboard 
                    No menu lateral, clique em + → Dashboard → Add new panel.
                    Configure a query para métricas do Quarkus, por exemplo: 
                    Query: http_server_requests_seconds_count (conta de requisições) 
                    Ajuste Legend para identificar endpoints ou métodos. 
                    Escolha o tipo de gráfico (Time series, Gauge, Bar, etc.). 
                    
                     Clique em Apply.

**Logging**
    Implementado com SLF4J para registrar eventos importantes da aplicação, incluindo erros e informações de auditoria.
    Os logs incluem informações como correlationId, usuário e parâmetros da requisição, garantindo rastreabilidade.

**Tracing**
    Configurado com OpenTelemetry para rastrear requisições distribuídas.
    Os dados de tracing são enviados para o Jaeger, permitindo a análise detalhada do fluxo de requisições.
    Acesse o Jaeger em: [http://localhost:16686/](http://localhost:14250/)

## Idempotência
    Garante que executar a mesma operação várias vezes não cause efeitos colaterais extras.

    No projeto, a idempotência é implementada usando Redis via Jedis:
        Cada evento enviado recebe um correlationId único.
        Antes de processar/enviar o evento, o sistema verifica no Redis se o correlationId já existe (isDuplicate).
        Se já existir, o evento não é reprocessado, evitando duplicidade.
        O correlationId é armazenado com um tempo de expiração (TTL), garantindo que o controle seja temporário e eficiente.
	
## Testes de Carga com K6

	Este projeto inclui um script de teste de carga usando K6, localizado na raiz como `k6_test_script.js`.

    como testar:
        docker-compose run --rm k6 run /scripts/k6_test_script.js --out json=/scripts/results/result.json

 		
