# 基础镜像
FROM openjdk:22-jdk-slim


# 设置工作目录
WORKDIR /app

# 将构建好的 Spring Boot 应用程序复制到镜像中
COPY ./AnswerQuestionServerChild/target/AnswerQuestionServerChild-2.1.0.jar /app/SharkTool.jar

# 定义环境变量，用于在启动时动态设置 Spring Boot 参数
ENV MYSQL_ADDRESS=localhost:3306
ENV MYSQL_USERNAME=root
ENV MYSQL_PASSWORD=123456
ENV REDIS_ADDRESS=localhost
ENV REDIS_PORT=6379
ENV REDIS_PASSWORD=123456
ENV QQ_GROUP_ID=958803816

# 启动应用程序
ENTRYPOINT ["java", "-jar", "SharkTool.jar"]

# 暴露应用程序端口
EXPOSE 8080