from fastapi import FastAPI
from app.routers import sign # 引入子路由

app=FastAPI(
    title="解签服务器",
    version='1.0.0'
)

# 依次挂载
app.include_router(sign.router)