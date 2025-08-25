# Usando a imagem do Quarkus JVM
FROM quay.io/quarkus/ubi-quarkus-jvm:3.12

WORKDIR /app

# Copia o jar da aplicação
COPY target/simulador-service-1.0.0-SNAPSHOT-runner.jar /app/app.jar

# Expõe as portas do Quarkus e da depuração
EXPOSE 8080 5005

# Comando para rodar a aplicação com debug remoto na porta 5005
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app/app.jar"]
