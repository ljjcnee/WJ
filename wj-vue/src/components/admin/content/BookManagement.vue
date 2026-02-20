<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>内容管理</el-breadcrumb-item>
        <el-breadcrumb-item>图书管理</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <edit-form @onSubmit="loadBooks()" ref="edit"></edit-form>
    <el-card style="margin: 18px 2%;width: 95%">

      <el-table
        :data="books"
        stripe
        style="width: 100%"
        :max-height="tableHeight"
        @expand-change="handleExpand">

        <el-table-column type="selection" width="55"></el-table-column>

        <el-table-column type="expand">
          <template slot-scope="props">
            <el-row :gutter="20" style="padding: 10px 30px;">

              <el-col :span="10">
                <span style="font-weight: bold; color: #409EFF; font-size: 15px;">
                  <i class="el-icon-reading"></i> 图书简介
                </span>
                <p style="text-indent: 2em; line-height: 1.8; color: #606266; margin-top: 10px; word-break: break-all;">
                  {{ props.row.abs || '暂无简介' }}
                </p>
              </el-col>

              <el-col :span="14" style="border-left: 2px dashed #ebeef5; min-height: 100px;">
                 <div style="padding-left: 20px;">
                     <span style="font-weight: bold; color: #f56c6c; font-size: 15px;">
                       <i class="el-icon-document-copy"></i> 当前外借明细
                     </span>

                     <el-table
                       v-if="props.row.borrowRecords && props.row.borrowRecords.length > 0"
                       :data="props.row.borrowRecords"
                       size="mini"
                       border
                       stripe
                       style="width: 100%; margin-top: 10px;"
                       v-loading="props.row.recordsLoading">
                       <el-table-column prop="username" label="借阅账号" width="100"></el-table-column>
                       <el-table-column prop="borrowTime" label="借出日期" min-width="140"></el-table-column>
                       <el-table-column label="状态" width="80">
                         <template slot-scope="scope">
                           <el-tag :type="scope.row.status === '已逾期' ? 'danger' : 'success'" size="mini" effect="dark">
                             {{ scope.row.status || '借阅中' }}
                           </el-tag>
                         </template>
                       </el-table-column>
                     </el-table>

                     <div v-else style="text-align: center; color: #909399; margin-top: 30px; font-size: 13px;" v-loading="props.row.recordsLoading">
                       <i class="el-icon-coffee-cup" style="font-size: 24px; display: block; margin-bottom: 5px;"></i>
                       目前此书全部在馆，暂无外借记录
                     </div>
                 </div>
              </el-col>

            </el-row>
          </template>
        </el-table-column>

        <el-table-column prop="title" label="书名（展开查看明细）" fit></el-table-column>
        <el-table-column prop="category.name" label="分类" width="100"></el-table-column>
        <el-table-column prop="author" label="作者" fit></el-table-column>
        <el-table-column prop="date" label="出版日期" width="120"></el-table-column>
        <el-table-column prop="press" label="出版社" fit></el-table-column>
        <el-table-column prop="nums" label="馆藏数量" width="80"></el-table-column>
        <el-table-column fixed="right" label="操作" width="120">
          <template slot-scope="scope">
            <el-button @click.native.prevent="editBook(scope.row)" type="text" size="small">编辑</el-button>
            <el-button @click.native.prevent="deleteBook(scope.row.id)" type="text" size="small">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin: 20px 0 20px 0;float: left">
        <el-button>取消选择</el-button>
        <el-button>批量删除</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
  import EditForm from './EditForm'
  export default {
    name: 'BookManagement',
    components: {EditForm},
    data () {
      return {
        books: []
      }
    },
    mounted () {
      this.loadBooks()
    },
    computed: {
      tableHeight () {
        return window.innerHeight - 320
      }
    },
    methods: {
      deleteBook (id) {
        this.$confirm('此操作将永久删除该书籍, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
            this.$axios
              .post('/admin/content/books/delete', {id: id}).then(resp => {
              if (resp && resp.data.code === 200) {
                this.loadBooks()
              }
            })
          }
        ).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
      },
      editBook (item) {
        this.$refs.edit.dialogFormVisible = true
        this.$refs.edit.form = {
          id: item.id,
          cover: item.cover,
          title: item.title,
          author: item.author,
          date: item.date,
          press: item.press,
          abs: item.abs,
          category: {
            id: item.category.id.toString(),
            name: item.category.name
          },
          nums: item.nums
        }
      },
      loadBooks () {
        var _this = this
        this.$axios.get('/books').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.books = resp.data.result
          }
        })
      },

      handleExpand (row, expandedRows) {
        if (expandedRows.includes(row)) {
          if (!row.borrowRecords) {
            this.$set(row, 'recordsLoading', true)
            this.$axios.get(`/books/${row.id}/records`).then(resp => {
              if (resp && resp.data.code === 200) {
                this.$set(row, 'borrowRecords', resp.data.result)
              } else {
                this.$set(row, 'borrowRecords', [])
              }
              row.recordsLoading = false
            }).catch(() => {
              this.$set(row, 'borrowRecords', [])
              row.recordsLoading = false
            })
          }
        }
      }
    }
  }
</script>

<style scoped>
</style>
