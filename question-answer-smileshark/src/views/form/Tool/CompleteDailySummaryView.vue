<template>
  <div style="line-height:normal;text-align: left;">
    <h2>一键完成日精进功能如下：</h2>
    <li>
      <strong>签到</strong>
    </li>
    <li>
      <strong>运动打卡：</strong>
      <p>通过随机的运动词条，交由AI优化，使用损坏图片，进行运动打卡提交。
    （目前提示词比较单一，有想法的可以在群里面提出来）</p>
    </li>
    <li>
      <strong>日精进：</strong>
      <p>
        提示词结构:今天我学习了（<span style="color: red;font-weight: bold;">____</span>），请帮我写200字日精进。<br>
      只需要填写空格中的内容就可以使用该功能，后续的日精进内容由AI优化。
      </p>
    </li>
    <li>
      <strong>重复提交不会提示</strong>
    </li>
    <li>
      <strong>日精进提交接口有速率限制，所以结果会慢一些</strong>
    </li>
    <el-row type="flex" justify="center" style="padding: 20px;">
      <el-col :span="5">
        <el-input v-model="dailySummary" placeholder="请输入提示词" ref="input" @keydown.enter.native="completeDailySummary"></el-input>
      </el-col>
      <el-col :span="3">
        <el-button type="primary" @click="completeDailySummary" :disabled="loading">一键完成</el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axiosInstance from '@/axios';
import { utils } from '@/utils/globalUtils';
export default {
    data(){
      return{
        dailySummary:'',
        loading:false
      }
    },
    methods:{
      completeDailySummary(){
          if(this.dailySummary.trim() == ''){
            this.$message.error('请输入提示词')
            return
          }
          this.loading=true
          axiosInstance.post(
              utils.getProxyUrl('/util/sign-in-today'),
              null,{params:{message:this.dailySummary}}
            ).then(res=>{
              if(res.data.code == 200){
                this.$message.success(res.data.message)
              }
              this.loading=false
            })
      }
    },
    mounted(){
      this.$refs.input.focus()
    }
}
</script>

<style scoped>

</style>
