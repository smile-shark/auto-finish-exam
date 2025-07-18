<template>
  <div>
    <el-row>
      <el-col :span="24">
        <el-table
          :data="exerciseCueWords"
          style="min-height: 30vh; width: 100vw"
        >
          <el-table-column
            label="提示词编号"
            prop="exerciseCueWordsId"
            width="320"
          ></el-table-column>
          <el-table-column label="提示词" prop="content"></el-table-column>
          <el-table-column label="操作" fixed="right" width="200">
            <template slot-scope="scope">
              <el-button
                type="primary"
                size="small"
                @click="showDialogUpdate(scope.row)"
                >编辑</el-button
              >
              <el-button type="danger" size="small" @click="deleteExerciseCueWord(scope.row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-row style="padding: 20px" type="flex" justify="space-between">
      <el-col :span="9">
        <el-pagination
          :current-page="page"
          :page-size="size"
          :total="total"
          layout="prev,pager,next,jumper"
          @current-change="getExerciseCueWords"
        >
        </el-pagination>
      </el-col>
      <el-col :span="4" :offset="8">
        <el-input
          @keyup.enter.native="getExerciseCueWords"
          placeholder="输入关键词"
          v-model="word"
        >
          <el-button
            style="
              background-color: #19b65a;
              border-top-left-radius: 0;
              border-bottom-left-radius: 0;
              color: white;
            "
            @click="getExerciseCueWords"
            slot="append"
            icon="el-icon-search"
          ></el-button>
        </el-input>
      </el-col>
      <el-col :span="2">
        <el-button type="success" @click="showDialog">添加</el-button>
      </el-col>
    </el-row>
    <el-dialog :visible.sync="dialogInfo.visible" :title="dialogInfo.title">
      <el-input
        v-model="dialogInfo.data.content"
        placeholder="请输入提示词"
      ></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogInfo.visible = false">取消</el-button>
        <el-button type="primary" @click="submitInfo">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from "@/axios";
import { utils } from "@/utils/globalUtils";

export default {
  data() {
    return {
      word: "",
      page: 1,
      size: 10,
      total: 0,
      exerciseCueWords: [],
      dialogInfo: {
        title: "添加新提示词",
        visible: false,
        data: {
          content: null,
        },
      },
    };
  },
  methods: {
    getExerciseCueWords(page) {
      if (typeof page != "number") {
        page = 1;
      }
      this.page = page;
      axios
        .get(utils.getProxyUrl("/exerciseCueWords/page-list"), {
          params: {
            page: this.page,
            size: this.size,
            word: this.word,
          },
        })
        .then((res) => {
          if (res.data.code == 200) {
            this.exerciseCueWords = res.data.data.records;
            this.total = res.data.data.total;
            this.$message.success("获取成功");
          }
        });
    },
    showDialog() {
      this.dialogInfo.title = "添加新提示词";
      this.dialogInfo.data.content = "";
      this.dialogInfo.visible = true;
    },
    showDialogUpdate(row) {
      this.dialogInfo.title = "修改提示词";
      this.dialogInfo.data = row;
      this.dialogInfo.visible = true;
    },
    submitInfo() {
      if (
        this.dialogInfo.data.content == null ||
        this.dialogInfo.data.content.trim() == ""
      ) {
        this.$message.error("提示词不能为空");
        return;
      }
      this.dialogInfo.title == "添加新提示词"
        ? this.addExerciseCueWord()
        : this.updateExerciseCueWord();
    },
    addExerciseCueWord() {
      axios
        .post(utils.getProxyUrl("/exerciseCueWords/add"), this.dialogInfo.data)
        .then((res) => {
          if (res.data.code == 200) {
            this.$message.success(res.data.message);
            this.dialogInfo.visible = false;
            this.getExerciseCueWords();
          }
        });
    },
    updateExerciseCueWord() {
      axios
        .put(
          utils.getProxyUrl("/exerciseCueWords/update"),
          this.dialogInfo.data
        )
        .then((res) => {
          if (res.data.code == 200) {
            this.$message.success(res.data.message);
            this.dialogInfo.visible = false;
            this.getExerciseCueWords();
          }
        });
    },
    deleteExerciseCueWord(row) {
      axios.delete(utils.getProxyUrl("/exerciseCueWords/delete"), {
        params: { id: row.exerciseCueWordsId },
      }).then(res=>{
        if(res.data.code == 200){
            this.$message.success(res.data.message);
            this.getExerciseCueWords();
        }
      })
    },
  },
  mounted() {
    this.getExerciseCueWords();
  },
};
</script>

<style></style>
