<template>
  <el-container>
    <el-main>
      <el-card class="box-card" style="width: 80%; margin: 20px auto;" shadow="hover">
        <div slot="header" class="clearfix">
          <span style="font-weight: bold; font-size: 20px; color: #409EFF;">
            <i class="el-icon-reading"></i> 我的专属书架
          </span>
        </div>

        <el-table
          :data="records"
          style="width: 100%"
          stripe>
          <el-table-column type="index" label="序号" width="80"></el-table-column>

          <el-table-column prop="book.title" label="书名" width="200"></el-table-column>

          <el-table-column prop="book.author" label="作者" width="150"></el-table-column>

          <el-table-column prop="borrowTime" label="借阅时间"></el-table-column>

          <el-table-column label="状态" width="120">
            <template slot-scope="scope">
              <el-tag type="success" v-if="scope.row.status === 0">正在借阅</el-tag>
              <el-tag type="info" v-else>已归还</el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button
                @click="returnBook(scope.row)"
                type="danger"
                size="small"
                v-if="scope.row.status === 0">
                归还图书
              </el-button>
            </template>
          </el-table-column>

        </el-table>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'MyBooks',
  data () {
    return {
      records: [] // 存放从后端拿到的借阅记录
    }
  },
  mounted () {
    // 页面一加载，就去请求后端要数据
    this.loadMyBooks()
  },
  methods: {
    loadMyBooks () {
      // 注意：这里不需要传任何参数了，后端会自动识别你是谁！
      this.$axios.get('/mybooks').then(resp => {
        if (resp && resp.data.code === 200) {
          this.records = resp.data.result
        }
      }).catch(() => {
        this.$message.error('加载书架失败，请确保已登录！')
      })
    },

    // 还书逻辑
    returnBook (row) {
      this.$confirm('确认归还《' + row.book.title + '》吗？', '还书提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 调用后端的还书接口，只传借阅记录的 id 过去
        this.$axios.post('/return', {
          id: row.id
        }).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('还书成功！书籍库存已恢复。')
            this.loadMyBooks() // 重新加载书架列表，刷新状态
          } else {
            this.$message.error(resp.data.message || '还书失败')
          }
        })
      }).catch(() => {
        this.$message.info('已取消还书')
      })
    }
  }
}
</script>

<style scoped>
.box-card {
  text-align: left;
}
</style>
