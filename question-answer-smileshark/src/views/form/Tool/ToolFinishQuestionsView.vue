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
    <el-row type="flex" justify="space-around">
      <el-col :span="12" style="padding: 2rem 1rem">
        <el-slider v-model="interval" :min="2" :disabled="isStart"></el-slider>
        <el-input-number
          v-model="interval"
          :min="2"
          :max="100"
          :disabled="isStart"
        ></el-input-number>
        <span style="padding: 10px; font-weight: bold">间隔时间(秒)</span>
        <span v-if="isStart"
          >倒计时
          <span style="font-weight: bold; color: #66b1ff">{{
            showInterval
          }}</span>
          秒</span
        >
      </el-col>
      <el-col :span="6" style="line-height: normal; padding: 2rem 1rem">
        <el-button type="primary" @click.prevent="start" :disabled="isStart"
          >点击开始</el-button
        >
      </el-col>
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
          <div class="pre" ref="pre">
            <p v-for="(item, index) in showAnswerList" :key="index">
              <i
                class="el-icon-loading"
                v-if="item.finish == 0"
                style="color: #409eff"
              ></i>
              <i
                class="el-icon-success"
                v-if="item.finish == 1"
                style="color: #85ce61"
              ></i>
              <i
                class="el-icon-error"
                v-if="item.finish == 2"
                style="color: #f56c6c"
              ></i>
              <span v-html="item.question"></span>
            </p>
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
import { utils } from "@/utils/globalUtils";
export default {
  data() {
    return {
      interval: 5,
      showInterval: 0,
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
      nowFinishSubsection: {
        subsection: {},
        questionList: [],
        questionLength: 0,
        havaError: false,
      },
      showAnswerList: [
        // {
        //   question:
        //     "从第一台计算机诞生到现在的50年中，按计算机采用的电子器件来划分，计算机的发展经历了&nbsp;",
        //   finish: 0, // 0 完成中，1 正确，2 错误
        // },
      ],
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
        .get(utils.getProxyUrl("/chapter/list-by-course-id"), {
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
        .get(utils.getProxyUrl("/subsection/list-by-chapter-id"), {
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
      this.finishCount = {
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
          utils.getProxyUrl("/questionAndAnswer/need-question-list"),
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
            this.changeShowInterval();
            setTimeout(() => {
              this.setInserver();
            }, 1000 * this.interval);
          } else {
            this.$message.error(res.data.message);
            this.isState = false;
            this.isStart = false;
          }
        })
        .catch((err) => {
          console.log(err);

          this.isState = false;
          this.isStart = false;
          this.$message.error("请求失败，请检查网络连接");
        });
    },
    changeShowInterval() {
      this.showInterval = this.interval;
      // 倒计时
      let down = () => {
        setTimeout(() => {
          this.showInterval--;
          if (this.showInterval > 0) {
            down();
          }
        }, 1000);
      };
      down();
    },
    setInserver() {
      if (this.subsectionIdList.length == 0) {
        this.$message.success("所有题目已完成");
        this.isState = false;
        this.isStart = false;
        return;
      }

      if (this.subsectionIdList.length > 0) {
        this.nowFinishSubsection = {
          subsection: this.subsectionIdList[0],
          questionList: [],
          questionLength: 0,
          havaError: false,
        };
      }
      // 根据subsectionId获取题目列表
      if (this.nowFinishSubsection.questionList.length == 0) {
        if (this.nowFinishSubsection.subsection) {
          // 获取考试的题目
          axios
            .get(
              utils.getProxyUrl("/questionAndAnswer/questionIdsBySubsectionId"),
              {
                params: {
                  subsectionId:
                    this.nowFinishSubsection.subsection.subsectionId,
                },
              }
            )
            .then((res) => {
              if (res.data.success) {
                this.nowFinishSubsection.questionList =
                  res.data.data.questionList;
                this.nowFinishSubsection.questionLength =
                  res.data.data.questionList.length;
                // 完成题目考试
                this.changeShowInterval();
                this.questionToShow();
                setTimeout(() => {
                  this.finishQuestion();
                }, 1000 * this.interval);
              } else {
                this.$message.error("题目获取失败");
              }
            });
        } else {
          this.isState = false;
          this.isStart = false;
        }
      }

      /*axios
        .post(
          utils.getProxyUrl("/questionAndAnswer/normal-exam-finish"),
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
          if (
            localStorage.getItem("identity") == 0 &&
            this.finishCount.noAnswerCount > 0
          ) {
            axios
              .post(utils.getProxyUrl("/questionAndAnswer/save-answer"))
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
        });*/
    },
    questionToShow() {
      // 1. 将问题存入展示列表
      if (this.nowFinishSubsection.questionList.length != 0) {
        this.showAnswerList.push({
          question: this.nowFinishSubsection.questionList[0].questionTitle,
          finish: 0,
        });
        this.$nextTick(() => {
          const container = this.$refs.pre;
          container.scrollTop = container.scrollHeight;
        });
      }
    },
    finishQuestion() {
      if (this.nowFinishSubsection.questionList.length == 0) {
        // 一次小节的问题回答完成
        axios
          .post(utils.getProxyUrl("/questionAndAnswer/answer-question"), null, {
            params: {
              questionId: null,
              subsectionId: this.nowFinishSubsection.subsection.subsectionId,
            },
          })
          .then((res) => {
            this.subsectionIdList.splice(0, 1);
            // 判断这个小节列表中是否存在错误，有错误就会进行对应的题目保存
            if(this.nowFinishSubsection.havaError){
              axios.post(utils.getProxyUrl("/questionAndAnswer/saveAnswerPageList"),null,{params:{size:this.nowFinishSubsection.questionLength}}).then(res=>{
                if(res.data.success){
                  this.$message.success("新答案保存成功")
                }else{
                  this.$message.error("新答案保存失败")
                }
              })
            }

            // 移除这个小节
            this.nowFinishSubsection = {
              subsection: {},
              questionList: [],
              questionLength: 0,
              havaError: false,
            };
            this.changeShowInterval();
            setTimeout(() => {
              this.setInserver();
            }, 1000 * this.interval);
          });
      } else {
        // 还有问题没有回答完成
        // 2. 开始回答问题
        axios
          .post(utils.getProxyUrl("/questionAndAnswer/answer-question"), null, {
            params: {
              questionId: this.nowFinishSubsection.questionList[0].id,
              subsectionId: this.nowFinishSubsection.subsection.subsectionId,
            },
          })
          .then((res) => {
            if (res.data.message!='没有找到答案') {
              this.FinishCount++;
              this.finishCount.rightAnswerCount++;
              // 修改展示的完成进度为完成
              this.showAnswerList[this.showAnswerList.length - 1].finish = 1;
            } else {
              this.finishCount.noAnswerCount++;
              this.showAnswerList[this.showAnswerList.length - 1].finish = 2;
              this.nowFinishSubsection.havaError = true;
            }
            // 移除这个问题
            this.nowFinishSubsection.questionList.splice(0, 1);
            // 进行自调用
            this.changeShowInterval();
            this.questionToShow();
            setTimeout(() => {
              this.finishQuestion();
            }, 1000 * this.interval);
          });
      }
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
      .get(utils.getProxyUrl("/course/list"))
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

<style scoped>
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
