<template>
  <div>
    <el-row>
      <el-col :span="4" class="title-card">
        <el-card shadow="hover">
          <el-statistic title="活跃用户数">
            <template slot="formatter">
              <DigitalAnimation
                :order-all-limit="activeCustomerTotal"
                :decimal-places="0"
              >
              </DigitalAnimation
              >/<DigitalAnimation
                :order-all-limit="customerTotal"
                :decimal-places="0"
              >
              </DigitalAnimation>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="4" class="title-card">
        <el-card shadow="hover">
          <el-statistic group-separator="," title="题目收录数量">
            <template slot="formatter">
              <DigitalAnimation
                :order-all-limit="questionTotal"
                :decimal-places="0"
              ></DigitalAnimation>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="4" class="title-card">
        <el-card shadow="hover">
          <el-statistic group-separator="," title="课程数量">
            <template slot="formatter">
              <DigitalAnimation
                :order-all-limit="courseTotal"
                :decimal-places="0"
              ></DigitalAnimation>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from "@/axios";
import DigitalAnimation from "@/views/views/global/DigitalAnimation.vue";
import { utils } from "@/utils/globalUtils";
export default {
  components: {
    DigitalAnimation,
  },
  data() {
    return {
      customerTotal: 0,
      activeCustomerTotal: 0,
      questionTotal: 0,
      courseTotal: 0,
    };
  },
  mounted() {
    try {
      axios.get(utils.getProxyUrl("/user/active-count")).then((res) => {
        if (res.data.code == 200) {
          this.activeCustomerTotal = res.data.data;
        } else {
          this.$message.error(res.data.message);
        }
      });
      axios.get(utils.getProxyUrl("/user/count")).then((res) => {
        if (res.data.code == 200) {
          this.customerTotal = res.data.data;
        } else {
          this.$message.error(res.data.message);
        }
      });
      axios.get(utils.getProxyUrl("/questionAndAnswer/count")).then((res) => {
        if (res.data.code == 200) {
          this.questionTotal = res.data.data;
        } else {
          this.$message.error(res.data.message);
        }
      });
      axios.get(utils.getProxyUrl("/course/count")).then(res=>{
                if (res.data.code == 200) {
          this.courseTotal = res.data.data;
        } else {
          this.$message.error(res.data.message);
        }
      })
    } catch (_) {
      console.log(_);
    }
  },
};
</script>

<style scoped>
.title-card {
  padding: 10px;
}
</style>
