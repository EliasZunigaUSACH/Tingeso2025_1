FROM eclipse-temurin:17-jdk
ARG JAR_FILE=target/payroll-backend.jar
COPY ${JAR_FILE} payroll-backend.jar
ENTRYPOINT ["java","-jar","/payroll-backend.jar"]