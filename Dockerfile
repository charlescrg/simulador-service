FROM mcr.microsoft.com/mssql/server:2022-latest

USER root
RUN apt-get update && \
    ACCEPT_EULA=Y apt-get install -y mssql-tools unixodbc-dev && \
    rm -rf /var/lib/apt/lists/*
ENV PATH="$PATH:/opt/mssql-tools/bin"
USER mssql
