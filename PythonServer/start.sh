#!/bin/bash

# 启动 FastAPI 应用
uvicorn app.main:app --host 0.0.0.0 --port 80 &

# 假设 sign.py 调用 app.js，可以通过 node 运行 app.js
# 例如，使用子进程或单独启动
node /code/resources/app.js &

# 等待所有进程
wait