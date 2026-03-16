FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

# Cache dependencies trước (chỉ re-download khi pom.xml thay đổi)
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src

CMD ["mvn", "spring-boot:run"]