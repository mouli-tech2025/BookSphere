
# Build stage
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY src ./src
COPY lib ./lib
COPY frontend ./frontend

# Compile all Java source files
RUN mkdir -p out && \
    find src -name "*.java" -print0 | \
    xargs -0 javac -cp "lib/*" -d out

# Runtime stage
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/out ./out
COPY --from=build /app/lib ./lib
COPY --from=build /app/frontend ./frontend

EXPOSE 8080

CMD ["java", "-cp", "out:lib/*", "com.library.Main"]
