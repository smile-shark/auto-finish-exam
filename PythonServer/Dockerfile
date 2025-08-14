# 使用官方 Python 基础镜像
FROM python:3.13

# 设置工作目录
WORKDIR /code

# 复制 requirements.txt 并安装依赖
COPY ./requirements.txt /code/requirements.txt
RUN pip install --no-cache-dir -r /code/requirements.txt

# 复制项目文件到容器中
COPY ./app /code/app

# 可选：安装 Node.js 和其他 JavaScript 依赖
RUN apt-get update && apt-get install -y nodejs npm
COPY ./resources /code/resources
WORKDIR /code/resources
RUN npm install

WORKDIR /code

# 暴露端口
EXPOSE 80

# 设置容器启动时运行的命令
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "80"]