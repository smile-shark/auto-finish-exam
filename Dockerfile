# 基础镜像
FROM openjdk:22-jdk-slim

# 设置工作目录
WORKDIR /app

# 将构建好的 Spring Boot 应用程序复制到镜像中
COPY ./AnswerQuestionServer/target/AnswerQuestionServer-3.6.1.jar /app/SharkTool.jar

# 定义环境变量，用于在启动时动态设置 Spring Boot 参数
ENV MYSQL_ADDRESS=localhost:3306
ENV MYSQL_USERNAME=root
ENV MYSQL_PASSWORD=123456
ENV REDIS_ADDRESS=localhost
ENV REDIS_PORT=6379
ENV REDIS_PASSWORD=123456
ENV QQ_GROUP_ID=958803816
ENV AI_KEY=6379
ENV AI_URL=123456
ENV AI_MODEL=958803816
ENV KPID_SIGN_URL=http://sharktool-sign-server/sign/decode
ENV SHIRO_WS_URL=/shark-bot

# 启动应用程序
ENTRYPOINT ["java", "-jar", "SharkTool.jar"]

# 暴露应用程序端口
EXPOSE 8080