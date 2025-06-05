<template>
  <div>
    <el-row>
      <el-col :span="24" style="padding: 20px">
        <el-table :data="courseLsit" style="min-height: 30vh">
          <el-table-column label="课程名称" prop="courseName"></el-table-column>
          <el-table-column label="课程编号" prop="courseId"></el-table-column>
          <el-table-column label="收录时间" prop="createTime"></el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template slot-scope="scope">
              <el-button
                :type="
                  scope.row.getDetail == 'error'
                    ? 'danger'
                    : scope.row.getDetail == 'success'
                    ? 'success'
                    : 'primary'
                "
                size="mini"
                @click="getChapterAndSection(scope.row)"
              >
                <span v-if="scope.row.getDetail == 'notStart'"
                  >获取对应依赖章节和小节</span
                >
                <span v-if="scope.row.getDetail == 'start'"
                  ><i class="el-icon-loading"></i
                ></span>
                <span v-if="scope.row.getDetail == 'success'"
                  ><i class="el-icon-circle-check"></i
                ></span>
                <span v-if="scope.row.getDetail == 'error'"
                  ><i class="el-icon-circle-close"></i
                ></span>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24" style="padding: 20px">
        <el-col :span="9">
          <el-pagination
            :current-page="page"
            :page-size="size"
            :total="total"
            layout="prev, pager, next, jumper"
            @current-change="getCourseList"
          ></el-pagination>
        </el-col>
        <el-col :span="8">
          <el-input
            @keyup.enter.native="getCourseList"
            placeholder="请输入课程名称"
            v-model="couseName"
            class="input-with-select"
          >
            <el-button
              style="
                background-color: #19b65a;
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;
                color: white;
              "
              @click="getCourseList"
              slot="append"
              icon="el-icon-search"
            ></el-button>
          </el-input>
        </el-col>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from "@/axios";
import { utils } from "@/utils/globalUtils";
export default {
  data() {
    return {
      courseLsit: [],
      page: 1,
      size: 10,
      couseName: "",
      total: 0,
    };
  },
  methods: {
    getCourseList(page) {
      // 获取分页页数
      if (typeof page == "number") {
        this.page = page;
      } else {
        this.page = 1;
      }
      // 获取课程列表
      axios
        .get(utils.getProxyUrl("/course/page-list"), {
          params: {
            page: this.page,
            size: this.size,
            courseName: this.couseName,
          },
        })
        .then((res) => {
          if (res.data.code == 200) {
            this.courseLsit = res.data.data.records.map((item) => ({
              ...item,
              getDetail: "notStart",
            }));
            this.total = res.data.data.total;
            this.$message.success(res.data.message);
          } else {
            this.$message.error(res.data.message);
          }
        });
    },
    getChapterAndSection(row) {
      row.getDetail='start'
      axios.post(utils.getProxyUrl("/course/admin-course-detail"),{
        courseId: row.courseId
      }).then((res) => {
        if (res.data.code == 200) {
          this.$message.success(res.data.message)
          row.getDetail='success'
        } else {
          this.$message.error(res.data.message)
          row.getDetail='error'
        }
      })
    }
  },
  mounted() {
    this.getCourseList();
  },
};
</script>

<style></style>
