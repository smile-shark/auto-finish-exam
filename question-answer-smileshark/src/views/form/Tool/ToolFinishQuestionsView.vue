<template>
  <div class="tool-finish-questions-view-container">
    <div class="select-questions-title">
      <span>Automatic Answering Tool</span><span style="font-size: 12px"></span>
    </div>
    <el-form
      ref="form"
      :model="form"
      label-width="80px"
      style="
        line-height: 50px;
        display: flex;
        justify-content: center;
        align-items: center;
      "
    >
      <el-form-item label="课程名称">
        <el-select
          filterable
          clearable
          v-model="form.selectCourseName"
          placeholder="请选择课程"
          @change="changeCourse"
        >
          <el-option
            v-for="(item, index) in courseList"
            v-bind:key="index"
            :label="item.courseName"
            :value="item.courseId"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="章节名称">
        <el-select
          filterable
          clearable
          v-model="form.selectChapterName"
          placeholder="请选择章节"
          @change="changeChapter"
        >
          <el-option
            v-for="(item, index) in chapterList"
            v-bind:key="index"
            :label="item.chapterTitle + '：' + item.chapterName"
            :value="item.chapterId"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="小节名称">
        <el-select
          filterable
          clearable
          v-model="form.selectSubsectionName"
          placeholder="请选择小节"
        >
          <el-option
            v-for="(item, index) in subsectionList"
            v-bind:key="index"
            :label="item.subsectionName"
            :value="item.subsectionId"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <el-row>
      <el-button type="primary" @click.prevent="start" :disabled="isStart"
        >点击开始</el-button
      >
    </el-row>
    <el-row>
      <el-col :span="8">
        <div class="isFinish-box">
          <p>题目完成数量</p>
          <el-progress
            type="circle"
            :percentage="changeState()"
            :color="customColorMethod"
          ></el-progress>
          <p>完成数量：{{ FinishCount }} 题目数量：{{ QuestionCount }}</p>
        </div>
      </el-col>
      <el-col :span="16">
        <div v-highlight class="tool-finish-show-box">
          <div class="pre">
            <p>正确完成题目数量：{{ finishCount.rightAnswerCount }}</p>
            <p>未完成题目数量：{{ finishCount.noAnswerCount }}</p>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/* eslint-disable */
import axios from "@/axios";
let setIntervalContiner = null;
export default {
  data() {
    return {
      form: {
        selectCourseName: "",
        selectChapterName: "",
        selectSubsectionName: "",
      },
      courseList: [],
      chapterList: [],
      subsectionList: [],
      process: "输出过程...\n",
      isStart: false,
      QuestionCount: 0,
      FinishCount: 0,
      subsectionIdList: [],
      finishCount: {
        rightAnswerCount: 0,
        noAnswerCount: 0,
      },
    };
  },
  watch: {
    process() {
      this.$nextTick(() => {
        this.scrollToBottom();
      });
    },
  },
  methods: {
    scrollToBottom() {
      const textarea = this.$refs.textarea;
      textarea.scrollTop = textarea.scrollHeight;
    },
    customColorMethod(percentage) {
      if (percentage < 30) {
        return "#909399";
      } else if (percentage < 70) {
        return "#e6a23c";
      } else {
        return "#67c23a";
      }
    },
    changeCourse() {
      this.form.selectChapterName = "";
      this.chapterList = [];
      this.form.selectSubsectionName = "";
      this.subsectionList = [];
      axios
        .get("/javaSever/chapter/list-by-course-id", {
          params: {
            courseId: this.form.selectCourseName,
          },
        })
        .then((res) => {
          if (res.data.code != 200) {
            this.loading = false;
            this.$message({
              message: res.data.message,
              type: "error",
            });
            return;
          }
          this.chapterList = res.data.data;
        })
        .catch((err) => {
          this.$message.error("请求失败，请检查网络连接");
        });
    },
    changeChapter() {
      this.form.selectSubsectionName = "";
      this.subsectionList = [];
      axios
        .get("/javaSever/subsection/list-by-chapter-id", {
          params: {
            chapterId: this.form.selectChapterName,
          },
        })
        .then((res) => {
          if (res.data.code != 200) {
            this.loading = false;
            this.$message({
              message: res.data.message,
              type: "error",
            });
            return;
          }
          this.subsectionList = res.data.data;
        })
        .catch((err) => {
          this.$message.error("请求失败，请检查网络连接");
        });
    },
    start() {
      this.FinishCount = 0;
      this.QuestionCount = 0;
      this.finishCounts = {
        rightAnswerCount: 0,
        noAnswerCount: 0,
      };
      if (this.form.selectCourseName == "") {
        this.$confirm(
          "您确定不选择课程，这将直接完成你所有课程的题目",
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        )
          .then(() => {
            this.respQuestion();
          })
          .catch(() => {});
      } else if (this.form.selectChapterName == "") {
        this.$confirm(
          "您确定不选择章节，这将直接完成你本课程的所有章节",
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        )
          .then(() => {
            this.respQuestion();
          })
          .catch(() => {});
      } else if (this.form.selectSubsectionName == "") {
        this.$confirm(
          "您确定不选择小节，这将直接完成你本章节的所有小节",
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        )
          .then(() => {
            this.respQuestion();
          })
          .catch(() => {});
      } else {
        this.respQuestion();
      }
    },
    respQuestion() {
      this.isStart = true;
      axios
        .post(
          "/javaSever/questionAndAnswer/need-question-list",
          {
            courseId: this.form.selectCourseName,
            chapterId: this.form.selectChapterName,
            subsectionId: this.form.selectSubsectionName,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        )
        .then((res) => {
          if (res.data.success) {
            this.QuestionCount = res.data.data.total;
            this.subsectionIdList = res.data.data.data;
            this.setInserver();
          } else {
            this.$message.error(res.data.message);
            this.isState = false;
            this.isStart = false;
          }
        })
        .catch((err) => {
          this.isState = false;
          this.isStart = false;
          this.$message.error("请求失败，请检查网络连接");
        });
    },
    setInserver() {
      if (this.subsectionIdList.length == 0) {
        this.$message("没有任何题目需要回答");
        this.isState = false;
        this.isStart = false;
        return;
      }
      axios
        .post(
          "/javaSever/questionAndAnswer/normal-exam-finish",
          this.subsectionIdList
        )
        .then((res) => {
          if (res.data.code != 200) {
            this.loading = false;
            this.$message({
              message: res.data.message,
              type: "error",
            });
            return;
          }
          this.$message({
            message: res.data.message,
            type: "success",
          });
          this.finishCount = res.data.data;
          this.FinishCount =
            this.finishCount.rightAnswerCount + this.finishCount.noAnswerCount;
          // 存储答案
          if (localStorage.getItem("identity") == 0 && this.finishCount.noAnswerCount > 0) {
            axios
              .post("/javaSever/questionAndAnswer/save-answer")
              .then((res) => {
                if (res.data.code == 200) {
                  this.$message({
                    message: res.data.message,
                    type: "success",
                  });
                } else {
                  this.$message({
                    message: res.data.message,
                    type: "error",
                  });
                }
              });
          }
        })
        .catch((err) => {
          this.$message.error("请求失败，请检查网络连接");
        })
        .finally(() => {
          this.isState = false;
          this.isStart = false;
        });
    },
    changeState() {
      let num;
      if (this.QuestionCount == 0) {
        num = 0;
      } else {
        num = Math.round((this.FinishCount / this.QuestionCount) * 100);
      }
      return num;
    },
  },
  mounted() {
    axios
      .get("/javaSever/course/list")
      .then((res) => {
        if (res.data.code != 200) {
          this.loading = false;
          this.$message({
            message: res.data.message,
            type: "error",
          });
          return;
        }
        this.courseList = res.data.data;
      })
      .catch((err) => {
        console.log(err);
        this.$message.error("请求失败，请检查网络连接");
      });
  },
};
</script>

<style>
.el-form-item {
  width: 300px;
  display: inline-block;
  line-height: 40px;
}

.isFinish-box {
  line-height: 30px;
  font-size: 14px;
}

.pre {
  text-align: left;
  line-height: 20px;
  font-size: 14px;
  background: #f5f5f5;
  padding: 10px;
  border-radius: 5px;
  border: none;
  width: 100%;
  min-height: 200px;
  max-height: 200px;
  resize: none;
  overflow: auto;
  outline: none;
  padding: 30px;
}

.select-questions-title > span {
  height: 60px;
  line-height: 60px;
  color: #19b65a;
  font-weight: bold;
  font-size: 30px;
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
}
</style>
