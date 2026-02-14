<template>
  <div>
    <el-row style="min-height: 840px;">
      <search-bar @onSearch="searchResult" ref="searchBar"></search-bar>

      <el-tooltip effect="dark" placement="right"
                  v-for="item in books.slice((currentPage-1)*pagesize,currentPage*pagesize)"
                  :key="item.id">
        <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.title}}</p>
        <p slot="content" style="font-size: 13px;margin-bottom: 6px">
          <span>{{item.author}}</span> /
          <span>{{item.date}}</span> /
          <span>{{item.press}}</span>
        </p>
        <p slot="content" style="width: 300px" class="abstract">{{item.abs}}</p>

        <el-card style="width: 160px; margin-bottom: 25px; height: 330px; float: left; margin-right: 20px" class="book"
                 bodyStyle="padding:10px" shadow="hover">
          <div class="cover">
            <img :src="item.cover" alt="封面">
          </div>
          <div class="info" style="text-align: center;">
            <div class="title">
              <a href="">{{item.title}}</a>
            </div>
            <div style="font-size: 12px; color: #999; margin-top: 8px; text-align: center;">
              剩余库存: <span style="color: #f56c6c; font-weight: bold;">{{item.nums}}</span> 本
            </div>
          </div>
          <div class="author">{{item.author}}</div>

          <div style="text-align: center; margin-top: 10px;">
            <el-button
              size="mini"
              type="primary"
              plain
              :disabled="item.nums <= 0"
              @click.stop="borrowBook(item)">
              {{ item.nums > 0 ? '借阅图书' : '已无库存' }}
            </el-button>
          </div>

        </el-card>
      </el-tooltip>
    </el-row>

    <el-row style="text-align: center; margin-top: 30px; clear: both;">
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pagesize"
        :total="books.length">
      </el-pagination>
    </el-row>
  </div>
</template>

<script>
  import SearchBar from './SearchBar'

  export default {
    name: 'Books',
    components: {SearchBar},
    data () {
      return {
        books: [],
        currentPage: 1,
        pagesize: 17 // 你也可以考虑把这里改成 15，因为卡片变大了，一页放 17 个可能有点满
      }
    },
    mounted () {
      this.loadBooks()
    },
    methods: {
      loadBooks () {
        var _this = this
        this.$axios.get('/books').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.books = resp.data.result
          }
        })
      },
      handleCurrentChange: function (currentPage) {
        this.currentPage = currentPage
      },
      searchResult () {
        var _this = this
        this.$axios
          .get('/search?keywords=' + this.$refs.searchBar.keywords, {
          }).then(resp => {
          if (resp && resp.data.code === 200) {
            _this.books = resp.data.result
          }
        })
      },
      borrowBook (item) {
        if (item.nums <= 0) {
          this.$message.warning('该书库存不足，已被借完！')
          return
        }

        this.$confirm('确认要借阅《' + item.title + '》吗？', '借阅确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        }).then(() => {
          this.$axios.post('/borrow', {
            bid: item.id
          }).then(resp => {
            if (resp && resp.data.code === 200) {
              this.$message.success('《' + item.title + '》 借阅成功！')
              this.loadBooks()
            } else {
              this.$message.error(resp.data.message || '借阅失败')
            }
          }).catch(() => {
             this.$message.error('借阅请求失败，请确保已登录')
          })
        }).catch(() => {
          this.$message.info('已取消借阅')
        })
      }
    }
  }
</script>

<style scoped>
  .book {
    border-radius: 12px;
    border: none;
    transition: all 0.3s ease;
  }

  .book:hover {
    transform: translateY(-8px);
    box-shadow: 0 15px 30px rgba(0,0,0,0.1) !important;
  }

  .cover {
    width: 140px;
    height: 200px;
    margin-bottom: 7px;
    overflow: hidden;
    cursor: pointer;
    border-radius: 6px;
  }

  img {
    width: 140px;
    height: 200px;
    transition: all 0.3s;
  }

  img:hover {
    transform: scale(1.05);
  }

  .title {
    font-size: 15px;
    font-weight: bold;
    text-align: center;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .author {
    color: #666;
    width: 140px;
    font-size: 13px;
    margin-bottom: 6px;
    text-align: center;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .abstract {
    display: block;
    line-height: 17px;
  }

  .el-icon-delete {
    cursor: pointer;
    float: right;
  }

  a {
    text-decoration: none;
  }

  a:link, a:visited, a:focus {
    color: #3377aa;
  }
</style>
