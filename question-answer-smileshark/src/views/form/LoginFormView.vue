<template>
  <div class="login-container">
    <!-- <link rel="stylesheet" type="text/css" href="@/css/card.css" /> -->
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
            <el-radio :label="0" style="padding:2px">学生</el-radio>
            <el-radio :label="1" style="padding:2px">教师</el-radio>
            <el-radio :label="2" style="padding:2px">管理员</el-radio>
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
        <el-dialog
      :visible.sync="dialogVisible"
      title="QQ群：958803816"
      width="23%"
    >
    <img src="@/assets/image/qq-group.png" style="width:20vw;"/>
    <p>
      在qq群中发出以下消息以验证身份：
    </p>
    <div style="display: flex;align-items: center;justify-content: center;">
        <span style="width:50%;background-color: #415a94;padding:10px;color:white;font-weight: bold;font-size: 20px;text-align: center;margin:5px">
          {{ verifyCode }}
        </span>
    </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmDialog" style="color: white;background:#21b85f">验 证</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from "@/axios";
import { utils } from "@/utils/globalUtils";
/* eslint-disable */
export default {
  data() {
    return {
      userName: "",
      password: "",
      identity: 0,
      dialogVisible: false, // 控制弹出框的显示和隐藏
      verifyCode:""
    };
  },
  methods: {
    confirmDialog() {
      // 确认操作
      axios.get(utils.getProxyUrl('/user/verify-code')).then(res=>{
        if(res.data.code==200){
            this.getUserInfo();
            this.dialogVisible = false;
            this.$message.success(res.data.message)
        }else{
          this.errorMessage(res.data.message);
        }
      })
    },
    login() {
      // 验证非空
      if (this.userName == "" || this.password == "") {
        this.errorMessage("用户名或密码不能为空");
        return;
      }
      const loading = this.$loading({
        lock: true,
        text: "登陆中...",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });
      axios
        .post(utils.getProxyUrl("/user/login"), {
          identity: this.identity,
          userId: this.userName,
          userPassword: this.password,
        })
        .then((res) => {
          //登陆成功就转跳页面,失败就输出返回信息
          if (res.data.success) {
            loading.close();
              localStorage.setItem("token", res.data.data);
              // 登录成功弹出二维码框
              if(this.identity != 2){
                // 弹出群聊框已验证身份
                // 到这里说明账号和密码都是正确的，这里就可以生成验证码了，发送一个请求对对应的账号创建一个验证码
                axios.get(utils.getProxyUrl("/user/create-code")).then(res=>{
                  if(res.data.code==200){
                    this.verifyCode = res.data.data;
                    this.dialogVisible = true;
                  }else{
                    this.errorMessage(res.data.message);
                  }
                })

                return;
              }
            // 成功后就去获取用户的信息
            this.getUserInfo();
            this.trueMessage(res.data.message);
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
      axios.get(utils.getProxyUrl("/user/info")).then((res) => {
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
      axios.get(getProxyUrl("/user/login-in")).then((res) => {
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
