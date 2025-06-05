<template>
  <div>
    <el-row>
      <el-col :span="24" style="padding: 20px">
        <el-table :data="useList" style="min-height: 30vh; width: 100vw">
          <el-table-column
            label="用户名称"
            prop="username"
            fixed
          ></el-table-column>
          <el-table-column label="用户账号" prop="userId" width="240">
            <template slot-scope="scope">
              <!-- 默认隐藏 -->
              <span v-if="scope.row.showId">
                {{ scope.row.userId }}
              </span>
              <span v-else>
                {{ utils.passwordUtil(scope.row.userId) }}
              </span>
              <i
                class="el-icon-view"
                @click="scope.row.showId = !scope.row.showId"
                style="float: left; padding: 5px"
              ></i>
            </template>
          </el-table-column>
          <el-table-column label="用户密码" prop="userPassword" width="200">
            <template slot-scope="scope">
              <!-- 默认隐藏 -->
              <span v-if="scope.row.showPassword">
                {{ scope.row.userPassword }}
              </span>
              <span v-else>
                {{ utils.passwordUtil(scope.row.userPassword) }}
              </span>
              <i
                class="el-icon-view"
                @click="scope.row.showPassword = !scope.row.showPassword"
                style="float: left; padding: 5px"
              ></i>
            </template>
          </el-table-column>
          <el-table-column label="账号类型" prop="isTest" width="100">
            <template slot-scope="scope">
              {{ scope.row.isTest === 1 ? "测试账号" : "正式账号" }}
            </template>
          </el-table-column>
          <el-table-column
            label="班级"
            prop="className"
            width="240"
          ></el-table-column>
          <el-table-column
            label="教师"
            prop="teacherName"
            width="100"
          ></el-table-column>
          <el-table-column
            label="阶段"
            prop="levelName"
            width="180"
          ></el-table-column>
          <el-table-column
            label="学校"
            prop="schoolName"
            width="240"
          ></el-table-column>
          <el-table-column label="身份" prop="identity">
            <template slot-scope="scope">
              {{ scope.row.identity === 0 ? "学生" : "教师" }}
            </template>
          </el-table-column>
          <el-table-column
            width="200"
            label="最后一次使用时间"
            prop="tokenCreateTime"
          ></el-table-column>
          <el-table-column label="操作" width="380" fixed="right">
            <template slot-scope="scope">
              <el-button
                :type="scope.row.saveAnswer == 'error' ? 'danger' : 'success'"
                size="mini"
                @click="saveAnswer(scope.row)"
                v-if="scope.row.identity === 0"
              >
                <span v-if="scope.row.saveAnswer == 'notStart'">保存答案</span>
                <span v-if="scope.row.saveAnswer == 'start'"
                  ><i class="el-icon-loading"></i
                ></span>
                <span v-if="scope.row.saveAnswer == 'success'"
                  ><i class="el-icon-circle-check"></i
                ></span>
                <span v-if="scope.row.saveAnswer == 'error'"
                  ><i class="el-icon-circle-close"></i
                ></span>
              </el-button>
              <el-button
                type="primary"
                size="mini"
                @click="assignCourseExam(scope.row)"
                >指定课程考试</el-button
              >
              <el-button
                :type="
                  scope.row.loginTest == 'error'
                    ? 'danger'
                    : scope.row.loginTest == 'success'
                    ? 'success'
                    : 'primary'
                "
                size="mini"
                @click="loginTest(scope.row)"
              >
                <span v-if="scope.row.loginTest == 'notStart'">登录测试</span>
                <span v-if="scope.row.loginTest == 'start'"
                  ><i class="el-icon-loading"></i
                ></span>
                <span v-if="scope.row.loginTest == 'success'"
                  ><i class="el-icon-circle-check"></i
                ></span>
                <span v-if="scope.row.loginTest == 'error'"
                  ><i class="el-icon-circle-close"></i
                ></span>
              </el-button>
              <el-button
                type="danger"
                size="mini"
                @click="deleteUser(scope.row.userId)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24" style="padding: 20px">
        <el-col :span="9">
          <el-pagination
            :current-page.sync="page"
            :page-size="size"
            :total="total"
            layout="prev, pager, next, jumper"
            @current-change="getUserList"
          ></el-pagination>
        </el-col>
        <el-col :span="3">
          <el-input
            @keyup.enter.native="getUserList"
            placeholder="请输入用户名称"
            v-model="userName"
            class="input-with-select"
          >
          </el-input>
        </el-col>
        <el-col :span="1"> &nbsp; </el-col>
        <el-col :span="4">
          <el-input
            @keyup.enter.native="getUserList"
            placeholder="请输入用户账号"
            v-model="account"
            class="input-with-select"
          >
            <el-button
              style="
                background-color: #19b65a;
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;
                color: white;
              "
              @click="getUserList"
              slot="append"
              icon="el-icon-search"
            ></el-button>
          </el-input>
        </el-col>
        <el-col :span="1"> &nbsp; </el-col>
        <el-col :span="2">
          <el-checkbox v-model="isTest">测试账号</el-checkbox>
          <el-checkbox v-model="identity">教师</el-checkbox>
        </el-col>
        <el-col :span="3">
          <el-button type="warning" @click="completionSaveAnswer">
            一键保存答案
          </el-button>
        </el-col>
      </el-col>
    </el-row>
    <el-dialog title="指定课程考试" :visible.sync="dialogVisible">
      <el-row>
        <el-col :span="24">
          <el-form>
            <el-col :span="9">
              <el-form-item
                :label="'用户：' + assignExamUser.username"
                label-width="100px"
              >
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
              </el-form-item>
            </el-col>
            <el-col :span="3">
              <el-button
                type="primary"
                @click="startExam"
                :disabled="finishCount.start"
                >开始考试</el-button
              >
            </el-col>
            <el-col :span="12">
              <el-progress
                :text-inside="true"
                :stroke-width="20"
                :percentage="
                  utils.nanUtil(
                    ((finishCount.rightAnswerCount +
                      finishCount.noAnswerCount) /
                      questionCount) *
                      100
                  )
                "
                status="success"
              ></el-progress>
              <el-descriptions>
                <el-descriptions-item label="题目数量">{{
                  questionCount
                }}</el-descriptions-item>
                <el-descriptions-item label="正确数量">{{
                  finishCount.rightAnswerCount
                }}</el-descriptions-item>
                <el-descriptions-item label="错误数量">{{
                  finishCount.noAnswerCount
                }}</el-descriptions-item>
              </el-descriptions>
            </el-col>
          </el-form>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import axios from "@/axios";
import { utils } from "@/utils/globalUtils";
export default {
  data() {
    return {
      utils,
      useList: [],
      page: 1,
      size: 10,
      total: 0,
      account: "",
      userName: "",
      isTest: false,
      identity: false,
      courseList: [],
      assignExamUser: {},
      dialogVisible: false,
      chourseId: "",
      questionCount: 0,
      finishCount: {
        rightAnswerCount: 0,
        noAnswerCount: 0,
        start: false,
      },
      saveAnswerGlobalLock: false,
      getUserListLock: false,
    };
  },
  methods: {
    startExam() {
      // 获取考试题目
      this.finishCount = {
        rightAnswerCount: 0,
        noAnswerCount: 0,
        start: true,
      };
      this.questionCount = 0;
      axios
        .post(utils.getProxyUrl("/questionAndAnswer/admin-need-question-list"), {
          courseId: this.chourseId,
          userId: this.assignExamUser.userId,
          userPassword: this.assignExamUser.userPassword,
          identity: this.assignExamUser.identity,
        })
        .then((res) => {
          if (res.data.code == 200) {
            this.questionCount = res.data.data.total;
            if (this.questionCount == 0) {
              this.$message.info("没有要考试的题目");
              this.finishCount.start = false;
              return;
            }

            // 提交题目开始考试
            axios
              .post(
                utils.getProxyUrl("/questionAndAnswer/admin-normal-exam-finish"),
                res.data.data.data,
                {
                  params: {
                    userId: this.assignExamUser.userId,
                    userPassword: this.assignExamUser.userPassword,
                    identity: this.assignExamUser.identity,
                  },
                }
              )
              .then((res) => {
                if (res.data.code == 200) {
                  this.finishCount.rightAnswerCount =
                    res.data.data.rightAnswerCount;
                  this.finishCount.noAnswerCount = res.data.data.noAnswerCount;
                  this.$message.success(res.data.message);
                } else {
                  this.$message.error(res.data.message);
                }
                this.finishCount.start = false;
              });
          } else {
            this.message = res.data.message;
            this.finishCount.start = false;
          }
        });
    },
    assignCourseExam(row) {
      this.assignExamUser = row;
      this.dialogVisible = true;
    },
    async getUserList(page) {
      this.getUserListLock = true;
      // 获取分页页数
      if (typeof page == "number") {
        this.page = page;
      } else {
        this.page = 1;
      }
      // 获取课程列表
      await axios
        .get(utils.getProxyUrl("/user/page-list"), {
          params: {
            page: this.page,
            size: this.size,
            account: this.account,
            userName: this.userName,
            isTest: this.isTest ? 1 : 0,
            identity: this.identity ? 1 : 0,
          },
        })
        .then((res) => {
          if (res.data.code == 200) {
            this.useList = res.data.data.records.map((item) => ({
              ...item,
              showId: false,
              showPassword: false,
              saveAnswer: "notStart",
              loginTest: "notStart",
            }));
            this.total = res.data.data.total;
            this.$message.success(res.data.message);
          } else {
            this.$message.error(res.data.message);
          }
          this.getUserListLock = false;
        });
    },
    deleteUser(userId) {
      // 提示
      this.$confirm("此操作将永久删除该用户, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          // 删除用户
          axios
            .delete(utils.getProxyUrl("/user"), {
              params: {
                userId: userId,
              },
            })
            .then((res) => {
              if (res.data.code == 200) {
                this.$message.success(res.data.message);
                this.getUserList();
              } else {
                this.$message.error(res.data.message);
              }
            })
            .catch((error) => {
              console.log(error);
            });
        })
        .catch(() => {});
    },
    loginTest(row) {
      row.loginTest = "start";
      axios
        .post(utils.getProxyUrl("/user/login"), {
          userId: row.userId,
          userPassword: row.userPassword,
          identity: row.identity,
        })
        .then((res) => {
          if (res.data.code == 200) {
            row.loginTest = "success";
            this.$message.success(res.data.message);
          } else {
            row.loginTest = "error";
            this.$message.error(res.data.message);
          }
        });
    },
   async saveAnswer(row) {
      row.saveAnswer = "start";
      if (this.saveAnswerGlobalLock) {
        this.$message.warning("还有任务在执行");
        row.saveAnswer = "error";
        return;
      }

      this.saveAnswerGlobalLock = true;
      // 保存答案
     await axios
        .post(utils.getProxyUrl("/questionAndAnswer/admin-save-answer"), {
          userId: row.userId,
          userPassword: row.userPassword,
          userIdentity: row.identity,
        })
        .then((res) => {
          if (res.data.code == 200) {
            row.saveAnswer = "success";
            this.$message.success(res.data.message);
          } else {
            row.saveAnswer = "error";
            this.$message.error(res.data.message);
          }
          this.saveAnswerGlobalLock = false;
        });
    },
    async completionSaveAnswer() {
      // 去掉教师选项
      this.identity = false;
      // 首先回到第一页
      await this.getUserList(1);
      // 计算总页数
      let totalPage = Math.ceil(this.total / this.size);

      for (let page = 1; page <= totalPage; page++) {
        await this.getUserList(page);

        // 处理当前页的用户
        for (const user of this.useList) {
          await this.saveAnswer(user);
        }
      }
    },
  },
  mounted() {
    this.getUserList();
    axios.get(utils.getProxyUrl("/course/list")).then((res) => {
      if (res.data.code == 200) {
        this.courseList = res.data.data;
      } else {
        this.$message.error(res.data.message);
      }
    });
  },
};
</script>

<style></style>
