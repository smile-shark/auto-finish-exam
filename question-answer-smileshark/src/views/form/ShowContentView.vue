<template>
  <div>
    <template>
      <el-carousel :interval="4000" type="card" height="400px">
        <el-carousel-item v-for="item in imgData" :key="item">
          <img :src="item" alt="无" class="show-image">
        </el-carousel-item>
      </el-carousel>
    </template>
<div style="width:100%;min-height: 200px;text-align: left;padding: 20px;line-height: normal;">
  <span style="font-size: 14px;text-align: left;">
    <h2>功能介绍</h2><br>
      <h4>1. 输入题目搜索相关题目</h4><br>
      该功能允许用户通过输入题目关键词来搜索数据库中与之相关的题目。系统通过模糊匹配技术，根据用户输入的关键词，从题库中查找并返回所有包含这些关键词的题目。用户可以利用这个功能快速找到与特定主题相关的题目，从而提高学习和复习的效率。<br>
      <br><h4>示例：</h4><br>
      用户输入关键词“微积分”。<br>
      系统返回所有包含“微积分”这个词的题目及其详细信息。<br>
      <br><h4>2. 自动答题功能</h4><br>
      该功能旨在通过设置课程、章节和小节等参数，自动从题库中提取题目并模拟答题过程，其中课程、章节和小节都是可选项。此功能可以帮助用户完成评估试题。答题过程均是可视化，注意的是在答题过程中不要中断网络或网页，可能造成不可预料的后果。<br>
      <br><h4>示例：</h4><br>
      用户搜索web,可以筛选出选项Web前端框架开发技术（V14.0.0）<br>
    </span>
</div>
  </div>
</template>

<script>
/* eslint-disable */
export default {
  data() {
    return {
      imgData: ['https://spring.io/img/icons/microservices.svg'
        , 'https://spring.io/img/icons/reactive.svg'
        , 'https://spring.io/img/icons/cloud.svg'
        , 'https://spring.io/img/icons/web-apps.svg'
        , 'https://spring.io/img/icons/serverless.svg'
        , 'https://spring.io/img/icons/event-driven.svg'
        , 'https://spring.io/img/icons/batch.svg'
      ]
    };
  }
}
</script>

<style scoped>
.el-carousel__item:nth-child(2n) {
  background-color: #80ea6e;
}

.el-carousel__item:nth-child(2n+1) {
  background-color: #80ea6e;
}
</style>