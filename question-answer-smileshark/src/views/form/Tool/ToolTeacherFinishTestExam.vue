<template>
  <div>
    <div class="select-questions-title">
      <span>Teacher Evaluation Tool</span><span style="font-size: 12px"></span>
    </div>
    <el-form
      ref="form"
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
          v-model="chourseId"
          placeholder="请选择课程"
        >
          <el-option
            v-for="(item, index) in courseList"
            v-bind:key="index"
            :label="item.courseName"
            :value="item.courseId"
          ></el-option>
        </el-select>
        <span style="color: red; font-size: 12px" v-if="!chourseId"
          >教师测评课程必选</span
        >
        <span style="color: red; font-size: 12px" v-else>&nbsp;</span>
      </el-form-item>
    </el-form>

    <el-form
      ref="form"
      label-width="80px"
      style="
        line-height: 50px;
        display: flex;
        justify-content: center;
        align-items: center;
      "
    >
      <el-form-item>
        <el-button type="primary" @click="startTestExam" :disabled="start">开始测评</el-button>
      </el-form-item>
    </el-form>
    <div class="answer-stats-container" >
      <el-card class="stats-card" shadow="hover">
        <div class="stats-content">
          <el-row class="stats-row">
            <!-- 正确完成题目 -->
            <el-col :span="12">
              <div class="stat-item success">
                <div class="stat-icon">
                  <i class="el-icon-circle-check"></i>
                </div>
                <div class="stat-text">
                  <div class="stat-title">正确完成</div>
                  <div class="stat-value">
                    {{ finishCount.rightAnswerCount }}
                  </div>
                </div>
              </div>
            </el-col>

            <!-- 未完成题目 -->
            <el-col :span="12">
              <div class="stat-item warning">
                <div class="stat-icon">
                  <i class="el-icon-time"></i>
                </div>
                <div class="stat-text">
                  <div class="stat-title">未找到答案</div>
                  <div class="stat-value">{{ finishCount.noAnswerCount }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import axios from "@/axios";
export default {
  data() {
    return {
      chourseId: "",
      courseList: [],
      finishCount: {
        rightAnswerCount: 0,
        noAnswerCount: 0,
        start:false
      },
    };
  },
  methods: {
    customColorMethod(percentage) {
      if (percentage < 30) {
        return "#909399";
      } else if (percentage < 70) {
        return "#e6a23c";
      } else {
        return "#67c23a";
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
    startTestExam() {
      // 课程id不能为空
      if (!this.chourseId) {
        this.$message.error(" ");
        return;
      }
      // 考试开始
      this.finishCount = {
        rightAnswerCount: 0,
        noAnswerCount: 0,
      };
      this.start = true;
      axios
        .post(utils.getProxyUrl("/questionAndAnswer/teacher-course-test-exam"),
            {
                "courseId": this.chourseId
            }
        )
        .then((res) => {
          if (res.data.code == 200) {
            this.finishCount = res.data.data;
            // 如果错题数量大于0就获取答案
            if (this.finishCount.noAnswerCount > 0) {
              axios
                .post(utils.getProxyUrl("/questionAndAnswer/teacher-save-answer"),{},{
                    params:{
                        "courseId": this.chourseId
                    }
                })
                .then((res) => {
                  if (res.data.code == 200) {
                    this.$message.success(res.data.message);
                  } else {
                    this.$message.error(res.data.message);
                  }
                });
            }
            this.$message.success(res.data.message);
          } else {
            this.$message.error(res.data.message);
          }
          this.start = false;
        })
    },
  },
  mounted() {
    axios.get(utils.getProxyUrl("/course/list")).then((res) => {
      if (res.data.code != 200) {
        this.$message({
          message: res.data.message,
          type: "error",
        });
      } else {
        this.courseList = res.data.data;
      }
    });
  },
};
</script>

<style scoped>
.answer-stats-container {
    line-height:normal;
  padding: 16px;
}

.stats-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.stats-row {
  padding: 16px 0;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.stat-item:hover {
  transform: scale(1.02);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.success .stat-icon {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.warning .stat-icon {
  background-color: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}
</style>
