<template>
  <div class="manage_page">
    <el-row style="height: 9vh">
      <el-col :span="24" class="title-container">
        <div style="padding: 5px; display: flex; align-items: center">
         <div class="grid-content bg-purple text-center">
            <img src="../../assets/image/icon.png" class="logo-img">
            <span class="text-center"> SharkTool </span>
            <span class="text-center-two">帮助你更好的完成工作</span>
          </div>
          <div
            style="
              display: inline-block;
              position: absolute;
              right: 20px;
              color: #3477f4;
            "
          >
            <div
              v-if="username"
              style="display: flex; align-items: center"
            >
              <span
                class="login-text"
                style="
                  font-size: 14px;
                  color: black;
                  margin: 0 20px;
                  font-weight: bold;
                "
              >
                {{ username }}
              </span>
              <el-link @click="outToLogin">退出</el-link>
            </div>
            <div v-else>
              <el-link @click="outToLogin">去登录</el-link>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-row style="height: 91vh">
      <el-col
        :span="3"
        style="
          height: 100%;
          background-color: white;
          border-right: 1px solid #c0c6cc;
        "
      >
        <el-scrollbar
          style="height: 100%"
          wrap-class="scrollbar"
          view-class="scroll-view"
        >
          <div>
            <el-menu
              :default-active="defaultActive"
              router
              background-color="white"
              text-color="black"
              ref="menuRef"
              active-text-color="#19b65a"
            >
              <el-menu-item index="/admin">
                <i class="el-icon-menu"></i>首页
              </el-menu-item>
              <el-submenu index="">
                <span slot="title">
                  <i class="el-icon-s-management"></i>课程管理</span
                >
                <el-menu-item index="/admin/courseList"
                  >课程列表</el-menu-item
                >
              </el-submenu>
              <el-submenu index="">
                <span slot="title"
                  ><i class="el-icon-user-solid"></i>用户管理</span
                >
                <el-menu-item index="/admin/userList"
                  >用户列表</el-menu-item
                >
              </el-submenu>
              <el-submenu index="">
                <span slot="title"
                  ><i class="el-icon-s-cooperation"></i>题目管理</span
                >
                <el-menu-item 
                  >题目列表</el-menu-item
                >
              </el-submenu>
              <el-submenu index="">
                <span slot="title"><i class="el-icon-s-grid"></i>其他管理</span>
                <el-menu-item>Another</el-menu-item>
              </el-submenu>
            </el-menu>
          </div>
        </el-scrollbar>
      </el-col>
      <el-col :span="21" style="height: 100%; overflow: auto">
        <el-row>
          <el-col :span="24" style="padding: 10px">
            <router-view></router-view>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  data() {
    return {
      defaultActive: "/platform/home",
      breadcrumbList: [],
      platformInfo: {},
      username: "",
    };
  },
  methods: {
    outToLogin(){  
      localStorage.removeItem('token')
      this.$router.push('/login');
    },
    
  },
  watch: {
    $route(to, from) {
      let currentPath = to.path;
      // 正则表达式匹配32位数字和小写字母组成的字符串
      const regex = /\/[0-9a-z]{32}$/;
      if (regex.test(currentPath)) {
        // 如果匹配到，去掉最后符合条件的部分
        currentPath = currentPath.replace(regex, "");
      }
      this.defaultActive = currentPath;
    },
  },
  mounted() {
    this.$refs.menuRef.open("");
    if (localStorage.getItem("userName")) {
      this.username = localStorage.getItem("userName")
    }
    this.defaultActive = this.$route.path;
  },
};
</script>

<style scoped>
.manage_page {
  height: 100vh;
}
.title-container {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 100;
  position: relative;
}

.main-container {
  /* background-color: blanchedalmond; */
  min-height: 100%;
  margin: auto;
  width: 70%;
}

.background-change {
  position: relative;
  top: 30%;
}

.background-change-continer {
  height: 60px;
  line-height: 60px;
  background-color: #f2f2f2;
}

.new-box-weight {
  position: relative;
  top: -2px;
  height: 100%;
  font-weight: bold;
}

.text-center-two {
  font-size: 12px;
  font-weight: normal;
}

.logo-img {
  position: relative;
  top: 5px;
}

.text-center {
  color: #19b65a;
  font-weight: bold;
  font-size: 30px;
  font-family: Open Sans, sans-serif;
}

/* .bg-purple {
  background: #d3dce6;
}

.bg-purple-light {
  background: #e5e9f2;
} */

.grid-content {
  min-height: 36px;
  height: 60px;
  line-height: 60px;
}

* {
  margin: 0;
  padding: 0;
  /* border:solid 1px red; */
}

.show-image {
  height: 100%;
}

.el-header,
.el-footer {
  box-shadow: 0 1px 2px #888888;
  background-color: white;
  color: #333;
  text-align: center;
  min-height: 60px;
  height: 20vh;
}

.el-aside {
  background-color: #D3DCE6;
  color: #333;
  text-align: center;
  line-height: 200px;
}

.el-main {
  margin-top: 2px;
  /* background-color: #E9EEF3; */
  color: #333;
  text-align: center;
  line-height: 160px;
  height: 100%;
}

body>.el-container {
  height: 100%;
}

.el-container:nth-child(5) .el-aside,
.el-container:nth-child(6) .el-aside {
  line-height: 260px;
}

.el-container:nth-child(7) .el-aside {
  line-height: 320px;
}

/* 另一个背景 */
.bg {
  background: linear-gradient(-45deg, #dae, #f66, #3c9, #09f, #66f);
  background-size: 200% 200%;
  animation: gradient 8s ease infinite;
}
.bg-light{
  background:white;
}

@keyframes gradient {
  0% {
    background-position: 0 12%;
  }

  50% {
    background-position: 100% 100%;
  }

  100% {
    background-position: 0 12%;
  }
}
</style>
