<template>
  <div class="login-container">
    <link rel="stylesheet" type="text/css" href="../../css/card.css" />
    <div class="out-card">
      <div class="loginBox card">
      <h2>
        登录
        <span style="font-size: 30px; color: rgb(33, 184, 95)">Shark Tool</span>
      </h2>
      <form action="">
        <div class="item">
          <input type="text" v-model="userName" id="userName" required />
          <label for="userName">用户名：</label>
        </div>
        <div class="item">
          <input type="password" v-model="password" id="password" required />
          <label for="password">密码：</label>
        </div>
        <div>
          <el-radio-group v-model="identity">
            <el-radio :label="0">学生</el-radio>
            <el-radio :label="1">教师</el-radio>
            <el-radio :label="2">管理员</el-radio>
          </el-radio-group>
        </div>
        <button class="btn" @click.prevent="login">
          登陆
          <span></span>
          <span></span>
          <span></span>
          <span></span>
        </button>
      </form>
    </div>
    </div>
  </div>
</template>

<script>
import axios from "@/axios";
/* eslint-disable */
export default {
  data() {
    return {
      userName: "",
      password: "",
      identity: 0,
    };
  },
  methods: {
    login() {
      const loading = this.$loading({
        lock: true,
        text: "登陆中...",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });
      axios
        .post("/javaSever/user/login", {
          identity: this.identity,
          userId: this.userName,
          userPassword: this.password,
        })
        .then((res) => {
          //登陆成功就转跳页面,失败就输出返回信息
          if (res.data.success) {
            // 成功后就去获取用户的信息
            this.getUserInfo();
            localStorage.setItem("token", res.data.data);
            this.trueMessage(res.data.message);
            loading.close();
          } else {
            this.errorMessage(res.data.message);
            loading.close();
          }
        })
        .catch((err) => {
          console.error(err);
          this.errorMessage("登录错误");
          loading.close();
        });
    },
    errorMessage(message) {
      this.$message({
        message: message,
        type: "error",
        duration: 1000,
      });
    },
    trueMessage(message) {
      this.$message({
        message: message,
        type: "success",
        duration: 1000,
      });
    },
    getUserInfo() {
      axios.get("/javaSever/user/info").then((res) => {
        if (res.data.success) {
          localStorage.setItem("userName", res.data.data.username);
          localStorage.setItem("identity", res.data.data.identity);
          if (res.data.data.identity == 2) {
            this.$router.push("/admin");
          } else {
            this.$router.push("/main");
          }
        } else {
          this.errorMessage(res.data.message);
        }
      });
    },
  },
  created() {
    if (localStorage.getItem("token")) {
      axios.get("/javaSever/user/login-in").then((res) => {
        if (res.data.success) {
          this.getUserInfo();
        }
      });
    }
    if (localStorage.getItem("identity")) {
      this.identity = parseInt(localStorage.getItem("identity"));
    }
  },
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
}

a {
  text-decoration: none;
}

input,
button {
  background: transparent;
  border: 0;
  outline: none;
}

.login-container {
  height: 100vh;
  background: linear-gradient(#141e30, #243b55);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 16px;
  color: #03e9f4;
}

.loginBox {
  width: 50vh;
  height: 50vh;
  background-color: #0c1622;
  margin: 5px auto;
  border-radius: 10px;
  padding: 40px;
  box-sizing: border-box;
}

h2 {
  text-align: center;
  color: aliceblue;
  margin-bottom: 30px;
  font-family: "Courier New", Courier, monospace;
}

.item {
  height: 45px;
  border-bottom: 1px solid #fff;
  margin-bottom: 40px;
  position: relative;
}

.item input {
  width: 100%;
  height: 100%;
  color: #fff;
  padding-top: 20px;
  box-sizing: border-box;
}

.item input:focus + label,
.item input:valid + label {
  top: 0px;
  font-size: 13px;
}

.item label {
  position: absolute;
  font-weight: bold;
  left: 0;
  top: 12px;
  transition: all 0.2s linear;
}

.btn {
  font-weight: bold;
  padding: 10px 20px;
  margin-top: 30px;
  color: #03e9f4;
  position: relative;
  overflow: hidden;
  text-transform: uppercase;
  letter-spacing: 2px;
  left: 35%;
}

.btn:hover {
  border-radius: 5px;
  color: #fff;
  background: #03e9f4;
  box-shadow: 0 0 5px 0 #03e9f4, 0 0 25px 0 #03e9f4, 0 0 50px 0 #03e9f4,
    0 0 100px 0 #03e9f4;
  transition: all .3s linear;
}

.btn > span {
  position: absolute;
}

.btn > span:nth-child(1) {
  width: 100%;
  height: 2px;
  background: -webkit-linear-gradient(left, transparent, #03e9f4);
  left: -100%;
  top: 0px;
  animation: line1 1s linear infinite;
}

@keyframes line1 {
  50%,
  100% {
    left: 100%;
  }
}

.btn > span:nth-child(2) {
  width: 2px;
  height: 100%;
  background: -webkit-linear-gradient(top, transparent, #03e9f4);
  right: 0px;
  top: -100%;
  animation: line2 1s 0.25s linear infinite;
}

@keyframes line2 {
  50%,
  100% {
    top: 100%;
  }
}

.btn > span:nth-child(3) {
  width: 100%;
  height: 2px;
  background: -webkit-linear-gradient(left, #03e9f4, transparent);
  left: 100%;
  bottom: 0px;
  animation: line3 1s 0.75s linear infinite;
}

@keyframes line3 {
  50%,
  100% {
    left: -100%;
  }
}

.btn > span:nth-child(4) {
  width: 2px;
  height: 100%;
  background: -webkit-linear-gradient(top, transparent, #03e9f4);
  left: 0px;
  top: 100%;
  animation: line4 1s 1s linear infinite;
}

@keyframes line4 {
  50%,
  100% {
    top: -100%;
  }
}

/* 另一种样式 */
@property --rotate {
  syntax: "<angle>";
  initial-value: 132deg;
  inherits: false;
}

:root {
  --card-height: 65vh;
  --card-width: calc(var(--card-height) / 1.5);
}

.card {
  background: #191c29;
  position: relative;
  border-radius: 6px;
  border-radius: 6px;
}

.out-card {
  content: "";
  width: 51vh;
  height: 51vh;
  border-radius: 8px;
  background-image: linear-gradient(
    var(--rotate),
    #5ddcff,
    #3c67e3 43%,
    #4e00c2
  );
  animation: spin 2.5s linear infinite;
  box-shadow: 0 15px 25px 0 rgba(0, 0, 0, 0.6);
}

/* .card::after {
  position: absolute;
  content: "";
  top: calc(var(--card-height) / 6);
  left: 0;
  right: 0;
  z-index: -1;
  height: 100%;
  width: 100%;
  margin: 0 auto;
  transform: scale(0.8);
  filter: blur(calc(var(--card-height) / 6));
  background-image: linear-gradient(
    var(--rotate),
    #5ddcff,
    #3c67e3 43%,
    #4e00c2
  );
  opacity: 1;
  transition: opacity 0.5s;
  animation: spin 2.5s linear infinite;
} */

@keyframes spin {
  0% {
    --rotate: 0deg;
  }

  100% {
    --rotate: 360deg;
  }
}
</style>
