<template>
  <el-container :class="backgroungColorIsTrue?'bg':'bg-light'">
    <el-header>
      <el-row>
        <el-col :xs="11" :sm="11" :md="9" :lg="7" :xl="6">
          <div class="grid-content bg-purple text-center">
            <img src="../../assets/image/icon.png" class="logo-img">
            <span class="text-center"> SharkTool </span>
            <span class="text-center-two">帮助你更好的完成工作</span>
          </div>
        </el-col>
        <!-- 间隔行 -->
        <el-col :xs="5" :sm="5" :md="9" :lg="11" :xl="14">
          <div class="grid-content bg-purple-light"></div>
        </el-col>
        <el-col :xs="8" :sm="8" :md="6" :lg="6" :xl="4">
          <div class="grid-content bg-purple">
            <el-menu :default-active="activeIndex" class="el-menu-demo new-box-weight" mode="horizontal"
              active-text-color="#19b65a">
              <el-menu-item index="1" @click="ToMain">主页</el-menu-item>
              <el-submenu index="2">
                <template slot="title">工具箱</template>
                <el-menu-item index="2-1" @click="SelectQuestion">评估题目搜索</el-menu-item>
                <el-menu-item index="2-2" @click="FinishQuestion">评估自动答题</el-menu-item>
                <el-menu-item index="2-3" @click="$router.push('/TeacherFinishTestExam')">教师测评一键完成</el-menu-item>
                <el-menu-item index="2-4" @click="ToCompleteDaily">日精进一键完成</el-menu-item>
              </el-submenu>
              <el-menu-item index="3" @click="dialogVisible = true">QQ群</el-menu-item>
              <el-menu-item index="4" @click="exitToolBox">退出</el-menu-item>
              <el-menu-item index="5"><el-tooltip :content="'背景颜色：' + backgroundColor" placement="top">
                <el-switch active-color="#19b65a" v-model="backgroundColor" active-value="流光背景" inactive-value="原背景"
                  @change="changeBgColor"></el-switch>
              </el-tooltip></el-menu-item>
              
            </el-menu>
          </div>
        </el-col>
      </el-row>
    </el-header>
    <!-- 内容 -->
    <el-main>
      <div class="main-container">
        <router-view>
        </router-view>
      </div>
    </el-main>

    <el-dialog
      :visible.sync="dialogVisible"
      title="加入QQ群：958803816"
      width="30%"
      @opened="dialogOpened"
      @closed="dialogClosed"
    >
    <img src="@/assets/image/qq-group.jpg" style="width:100%;"/>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmDialog">确 定</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
/* eslint-disable */
export default {
  data() {
    return {
      activeIndex: '1',
      value2: false,
      backgroundColor: '原背景',
      backgroungColorIsTrue: false,
      dialogVisible: false, // 控制弹出框的显示和隐藏
    };
  },
  methods: {
    changeBgColor() {
      localStorage.setItem('backgroundColor', this.backgroundColor)
      if (localStorage.getItem('backgroundColor') === '流光背景') {
        this.backgroundColor = '流光背景'
        this.backgroungColorIsTrue = true
      } else {
        this.backgroundColor = '原背景'
        this.backgroungColorIsTrue = false
      }
    },
    SelectQuestion() {
      window.location.href.includes('SelectTool') ? '' : this.$router.push('/SelectTool');
    },
    ToMain() {
      window.location.href.includes('main') ? '' : this.$router.push('/main');
    },
    FinishQuestion() {
      window.location.href.includes('FinishTool') ? '' : this.$router.push('/FinishTool');
    },
    exitToolBox() {
      localStorage.removeItem('token')
      this.$router.push('/login');
    },
    ToCompleteDaily() {
      window.location.href.includes('CompleteDaily') ? '' : this.$router.push('/CompleteDaily');
    },
    dialogOpened() {
      // 弹出框打开时的动画效果
      this.$nextTick(() => {
        const dialog = this.$refs.dialog.$el;
        dialog.style.transition = "transform 0.3s ease";
        dialog.style.transform = "scale(1)";
      });
    },
    dialogClosed() {
      // 弹出框关闭时的动画效果
      this.$nextTick(() => {
        const dialog = this.$refs.dialog.$el;
        dialog.style.transition = "transform 0.3s ease";
        dialog.style.transform = "scale(0)";
      });
    },
        confirmDialog() {
      // 确认操作
      this.dialogVisible = false;
    },
  },
  mounted() {
    this.$notify({
      title: '免责声明',
      message: `
数据准确性：本系统的题目搜索和自动答题功能依赖于数据库中存储的数据。我们无法保证数据库中的题目内容绝对准确、完整或最新。用户应自行核实题目的准确性和适用性。
\n
使用限制：本系统仅用于教育和练习目的。未经授权，不得将本系统用于任何商业活动或考试作弊行为。用户在使用系统时应遵守相关法律法规和道德规范。
\n
系统稳定性：虽然我们尽力保证系统的稳定性和性能，但不能保证系统在所有情况下都能正常运行。系统可能会受到网络环境、数据库状态等因素的影响。对于由于系统故障导致的数据丢失或其他问题，我们不承担任何责任。
\n
隐私保护：用户在使用本系统时提供的任何个人信息将严格保密，除非法律要求或用户同意，我们不会将用户信息泄露给第三方。
\n
责任限制：使用本系统的风险由用户自行承担。我们不对因使用系统而导致的直接或间接损失负责，包括但不限于数据丢失、时间浪费或其他损害。`,
      position: 'bottom-right',
    })
    this.$notify({
      title: '您目前登录的用户是：',
      message: localStorage.getItem('userName'),
      position: 'top-right',
      offset: 100
    })
    if (localStorage.getItem('backgroundColor') === '流光背景') {
      this.backgroungColorIsTrue = true
      this.backgroundColor = '流光背景'
    } else {
      this.backgroungColorIsTrue = false
    }
    setTimeout(() => {
      this.dialogVisible = true;
    }, 500);


  }
}
</script>

<style scoped>
/* 自定义弹出框的样式 */
.el-dialog {
  transform: scale(0); /* 初始状态缩放为0 */
  transition: transform 0.3s ease; /* 添加动画效果 */
}
html {
  height: 100vh;
}

body {
  height: 100%;
  background: none;
  display: block;
  justify-content: none;
  align-items: none;
  font-size: 16px;
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
