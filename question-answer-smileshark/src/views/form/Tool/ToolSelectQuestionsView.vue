<template>
  <div
    id="tool-select-questions-view-container"
    style="text-align: left; line-height: normal; width: 100%; height: 100%"
  >
    <div class="select-questions-title">
      <span>Topic Search Tool</span
      ><span style="font-size: 12px"> 搜索工具</span>
      <span style="float: right"
        ><label class="label77">
          <input
            class="inp77"
            size="16"
            type="search"
            placeholder="请输入题目关键字"
            v-model="searchStr"
            @keyup.enter="
              pageIndex = 1;
              searchQuestions();
            "
            required
          />
          <span
            class="search-btn77"
            @click="
              pageIndex = 1;
              searchQuestions();
            "
          ></span> </label
      ></span>
    </div>
    <div class="select-questions-content" v-loading="loading">
      <div
        class="question-answer-container"
        v-for="(item, index) in answerList.records"
        v-bind:key="index"
      >
        <div
          class="question-title"
          v-html="'第' + (index + 1) + '题:\n' + item.question"
          style="font-weight: bold; font-size: 18px"
        ></div>
        <div class="answer-list">
          <div
            v-for="(answer, indexA) in item.answerList"
            v-bind:key="indexA"
            v-html="'答案' + (indexA + 1) + ':' + answer.answer"
            :style="{
              color:
                answer.isTrue ? '#67c23a' : 'black',
              fontWeight: answer.isTrue ? 'bold' : 'normal',
            }"
            class="answer-item-box"
          ></div>
        </div>
        <hr />
      </div>
    </div>
    <div style="text-align: center; margin-top: 20px">
      <el-pagination
        background
        layout="prev, pager, next"
        hide-on-single-page
        @current-change="handleCurrentChange"
        @prev-click="
          pageIndex--;
          serachQuestions();
        "
        @next-click="
          pageIndex++;
          searchQuestions();
        "
        :current-page="pageIndex"
        :total="answerList.total"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script>
/* eslint-disable */
import axios from "@/axios";
export default {
  data() {
    return {
      activeIndex: "2",
      searchStr: null,
      pageIndex: 1,
      answerList: {},
      loading: false,
    };
  },
  methods: {
    searchQuestions() {
      if (this.searchStr === "") {
        this.$message({
          message: "请输入题目关键字",
          type: "warning",
        });
        return;
      }
      this.loading = true;
      axios
        .post(utils.getProxyUrl("/questionAndAnswer/page-list"), {
          question: this.searchStr,
          index: this.pageIndex,
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
          // 处理获取到的答案
          for (let question of res.data.data.records) {
            question.answerList = [];
            question.question=question.question.replace(/\/oss\/api\/ImageViewer\//g,"https://ai.cqzuxia.com/oss/api/ImageViewer/")
            let splitList = question.answers.split("LBT_1534_LX_5212_WZL_4818");
            for (let i = 0; i < splitList.length; i+=2) {
              // 偶数索引为id，奇数索引为答案，一个id对应一个答案
              let answer = {
                answer: splitList[i+1].replace(/\/oss\/api\/ImageViewer\//g,"https://ai.cqzuxia.com/oss/api/ImageViewer/"),
                isTrue: splitList[i]!="该位置为错误答案",
              }
              question.answerList.push(answer)
            }
          }

          this.answerList = res.data.data;
          this.loading = false;
          this.$message({
            message: res.data.message,
            type: "success",
          });
        })
        .catch((err) => {
          this.loading = false;
          this.$message({
            message: "请求失败",
            type: "error",
          });
        });
    },
    handleCurrentChange(pag) {
      this.pageIndex = pag;
      this.searchQuestions();
    },
    /**
    filteredAnswerList(obj) {
      for (let question of obj.data) {
        for (let i = 3; i >= 0; i--) {
          if (question && !question.answerList[i]) {
            question.answerList.pop()
          }
        }
      }
      return obj
    } */
  },
};
</script>

<style>
/* * {
  border: solid 1px red;
} */

.label77 {
  width: 240px;
  height: 42px;
  position: relative;
  overflow: hidden;
}

.inp77 {
  width: 240px;
  height: 42px;
  line-height: 42px;
  padding: 16px;
  padding-right: 42px;
  border: 1px solid rgba(0, 0, 0, 0.2);
  background-color: #ffffff;
  box-sizing: border-box;
  transition: all 0.3s;
  font-size: 14px;
  border-radius: 21px;
  color: #333;
  position: absolute;
  list-style: none;
  outline-style: none;
}

.search-btn77 {
  position: absolute;
  top: 23px;
  left: 205px;
}

.search-btn77:before {
  content: "";
  width: 16px;
  height: 16px;
  border: 2px solid rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: block;
  position: absolute;
  top: -14px;
  transition: border 0.32s linear;
}

.search-btn77:after {
  content: "";
  width: 2px;
  height: 8px;
  background-color: rgba(0, 0, 0, 0.6);
  display: block;
  position: absolute;
  top: 3px;
  right: -19px;
  transform: rotate(-45deg);
  transition: background-color 0.32s linear;
}

.label77:hover.inp77,
.inp77:focus,
.inp77:valid {
  color: #000000;
  border: 1px solid #19b65a;
}

.inp77:valid + .search-btn77:before {
  border: 2px solid #19b65a;
}

.inp77:valid + .search-btn77:after {
  background-color: #19b65a;
}

/* .tool-select-questions-view-container {} */
.answer-item-box {
  padding: 10px;
}

.select-questions-title > span {
  height: 60px;
  line-height: 60px;
  color: #19b65a;
  font-weight: bold;
  font-size: 30px;
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
}

.select-questions-content {
  min-height: 80vh;
}
</style>
