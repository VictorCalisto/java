# Estágio de build
# Usa uma imagem Maven com Java 17 (Eclipse Temurin)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# Copia o pom.xml primeiro para aproveitar o cache do Docker se as dependências não mudarem
COPY pom.xml .
# Copia o restante do código fonte
COPY src ./src
# Compila o projeto e cria um JAR executável
# O -DskipTests é para pular os testes durante o build do Docker, você pode remover se quiser que os testes rodem.
RUN mvn clean install -DskipTests

# Estágio de execução
# Usa uma imagem JRE menor para execução (sem as ferramentas de desenvolvimento)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copia o JAR executável do estágio de build
# O padrão de nome do JAR é "artifactId-version.jar" (do pom.xml)
# Se seu artifactId for "meu-projeto", o JAR será "meu-projeto-1.0-SNAPSHOT.jar"
COPY --from=build /app/target/meu-projeto-1.0-SNAPSHOT.jar app.jar

# Define o comando de execução
CMD ["java", "-jar", "app.jar"]