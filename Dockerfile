# ===== Build Stage =====
FROM gradle:8.9.0-jdk21 AS build
WORKDIR /app

# 의존성 캐시 최적화
COPY build.gradle settings.gradle ./
# gradle 폴더를 디렉터리로 명시해서 복사
COPY gradle/ ./gradle/
RUN gradle --no-daemon dependencies || true

# 소스 복사 & 빌드
COPY . .
RUN gradle --no-daemon clean build -x test

# ===== Runtime Stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# 앱 JAR 복사 (Gradle 기본 산출물 경로)
COPY --from=build /app/build/libs/*-SNAPSHOT*.jar /app/app.jar
# 또는 산출물 이름이 확정이면
# COPY --from=build /app/build/libs/your-app-0.0.1.jar /app/app.jar

# (선택) Actuator health 사용 시 헬스체크
# HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD \
#   wget -qO- http://127.0.0.1:8080/actuator/health | grep -q '"status":"UP"' || exit 1

EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
