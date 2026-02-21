<template>
  <el-container>
    <el-aside style="width: 200px;margin-top: 20px">
      <switch></switch>
      <SideMenu @indexSelect="listByCategory" ref="sideMenu"></SideMenu>
    </el-aside>

    <el-main>
      <div class="quote-banner">
        <div class="quote-content">
          <i class="el-icon-reading" style="color: #409EFF; margin-right: 10px; font-size: 22px;"></i>
          <span class="text">"{{ quote.hitokoto }}"</span>
          <span class="author">—— 《{{ quote.from }}》</span>
        </div>

        <el-button type="text" style="color: #409EFF; font-weight: bold; margin-right: 20px;" @click="getQuote()">
          <i class="el-icon-refresh"></i> 换一句
        </el-button>
      </div>

      <div class="recommend-area" v-if="recommendedBooks.length > 0">
        <div class="section-title">
          <i class="el-icon-collection" style="color: #F56C6C; margin-right: 8px; font-size: 18px;"></i>
          <span>智慧伴读 · 为你推荐</span>
        </div>
        <el-carousel :interval="4000" type="card" height="180px" indicator-position="none">
          <el-carousel-item v-for="item in recommendedBooks" :key="item.id">
            <el-tooltip effect="dark" :content="'《' + item.title + '》 - ' + item.author" placement="bottom">
              <div class="carousel-book-card">
                <img :src="item.cover" alt="封面" class="carousel-cover">
              </div>
            </el-tooltip>
          </el-carousel-item>
        </el-carousel>
      </div>

      <books class="books-area" ref="booksArea"></books>
    </el-main>
  </el-container>
</template>

<script>
import SideMenu from './SideMenu'
import Books from './Books'

export default {
  name: 'AppLibrary',
  components: { SideMenu, Books },
  data () {
    return {
      quote: {
        hitokoto: '正在连接智慧知识库...',
        from: '系统'
      },
      // 👑 新增：存放推荐书籍的数组
      recommendedBooks: [],
      localQuotes: [
        { hitokoto: '立身以立学为先，立学以读书为本。', from: '欧阳修' },
        { hitokoto: '吾生也有涯，而知也无涯。', from: '庄子' },
        { hitokoto: '读书破万卷，下笔如有神。', from: '杜甫' },
        { hitokoto: '书籍是人类进步的阶梯。', from: '高尔基' },
        { hitokoto: '我扑在书籍上，就像饥饿的人扑在面包上。', from: '高尔基' },
        { hitokoto: '读一本好书，就是和许多高尚的人谈话。', from: '笛卡尔' },
        { hitokoto: '知识就是力量。', from: '培根' },
        { hitokoto: '黑发不知勤学早，白首方悔读书迟。', from: '颜真卿' }
      ]
    }
  },
  mounted () {
    this.getQuote()
    // 👑 页面一挂载，立刻去拉取并生成推荐书单
    this.loadRecommendations()
  },
  methods: {
    listByCategory () {
      var _this = this
      var cid = this.$refs.sideMenu.cid
      var url = 'categories/' + cid + '/books'
      this.$axios.get(url).then(resp => {
        if (resp && resp.data.code === 200) {
          _this.$refs.booksArea.books = resp.data.result
          _this.$refs.booksArea.currentPage = 1
        }
      })
    },

    getQuote () {
      const controller = new AbortController()
      const timeoutId = setTimeout(() => controller.abort(), 3000)

      fetch('https://v1.hitokoto.cn/?c=d&c=i&c=k', { signal: controller.signal })
        .then(response => response.json())
        .then(data => {
          this.quote = data
          clearTimeout(timeoutId)
        })
        .catch(err => {
          console.log('网络稍作波动，已启动本地智慧库', err)
          const randomIndex = Math.floor(Math.random() * this.localQuotes.length)
          this.quote = this.localQuotes[randomIndex]
        })
    },

    // 👑 核心逻辑：获取图书并生成动态推荐
    loadRecommendations () {
      // 复用系统自带的获取所有图书接口，绝对安全
      this.$axios.get('/books').then(resp => {
        if (resp && resp.data.code === 200) {
          let allBooks = resp.data.result
          // 极简前端洗牌算法：打乱书籍顺序
          let shuffled = allBooks.sort(() => 0.5 - Math.random())
          // 只截取前 5 本书作为当天的“智慧推荐”
          this.recommendedBooks = shuffled.slice(0, 5)
        }
      })
    }
  }
}
</script>

<style scoped>
.books-area {
  width: 990px;
  margin-left: auto;
  margin-right: auto;
}

/* 横幅样式 */
.quote-banner {
  width: 990px;
  height: 56px;
  margin: 0 auto 20px auto;
  background: #ffffff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #ebeef5;
  border-left: 5px solid #409EFF;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.3s;
}

.quote-banner:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.quote-content {
  padding: 0 20px;
  display: flex;
  align-items: center;
  flex-grow: 1;
}

.quote-content .text {
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 1px;
  font-style: italic;
  color: #303133;
}

.quote-content .author {
  font-size: 13px;
  margin-left: 15px;
  color: #909399;
}

/* 👑 新增：智慧推荐专区样式 */
.recommend-area {
  width: 990px;
  margin: 0 auto 20px auto;
  padding: 15px 20px;
  background: #ffffff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #ebeef5;
}

.section-title {
  font-size: 15px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #ebeef5;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
}

.carousel-book-card {
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f4f5f7; /* 浅色背景托底 */
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.carousel-cover {
  height: 100%;
  width: 100%;
  object-fit: cover; /* 保证封面不被拉伸变形 */
  transition: transform 0.4s ease;
}

.carousel-cover:hover {
  transform: scale(1.1); /* 鼠标放上去会有轻微放大特效 */
  cursor: pointer;
}
</style>
