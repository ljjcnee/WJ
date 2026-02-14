<template>
  <div>
    <el-row style="height: 840px;">
      <search-bar @onSearch="searchResult" ref="searchBar"></search-bar>
      <view-switch class="switch"></view-switch>
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

        <el-card style="width: 135px;margin-bottom: 20px;height: 275px;float: left;margin-right: 15px" class="book"
                 bodyStyle="padding:10px" shadow="hover">
          <div class="cover">
            <img :src="item.cover" alt="封面">
          </div>
          <div class="info">
            <div class="title">
              <a href="">{{item.title}}</a>
            </div>
            <div style="font-size: 12px; color: #999; margin-top: 5px; text-align: left;">
              剩余库存: <span style="color: #f56c6c; font-weight: bold;">{{item.nums}}</span> 本
            </div>
          </div>
          <div class="author">{{item.author}}</div>

          <div style="text-align: center; margin-top: 5px;">
            <el-button
              size="mini"
              type="primary"
              plain
              :disabled="item.nums <= 0"
              @click.stop="borrowBook(item)">
              {{ item.nums > 0 ? '借阅' : '缺货' }}
            </el-button>
          </div>

        </el-card>
      </el-tooltip>
    </el-row>
    <el-row>
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
  import ViewSwitch from './ViewSwitch'

  export default {
    name: 'Books',
    components: {SearchBar, ViewSwitch},
    data () {
      return {
        books: [],
        currentPage: 1,
        pagesize: 17
      }
    },
    mounted: function () {
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
      // 核心修复：去掉了所有的分号，并且把 catch(err) 改成了 catch(() => {})
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
  .cover {
    width: 115px;
    height: 172px;
    margin-bottom: 7px;
    overflow: hidden;
    cursor: pointer;
  }

  img {
    width: 115px;
    height: 172px;
  }

  .title {
    font-size: 14px;
    text-align: left;
  }

  .author {
    color: #333;
    width: 102px;
    font-size: 13px;
    margin-bottom: 6px;
    text-align: left;
  }

  .abstract {
    display: block;
    line-height: 17px;
  }

  .el-icon-delete {
    cursor: pointer;
    float: right;
  }

  .switch {
    display: flex;
    position: absolute;
    left: 780px;
    top: 25px;
  }

  a {
    text-decoration: none;
  }

  a:link, a:visited, a:focus {
    color: #3377aa;
  }
</style>
