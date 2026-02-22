<template>
  <el-container style="position: relative;">
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
            <el-tooltip effect="dark" :content="'点击查看《' + item.title + '》详情'" placement="bottom">
              <div class="carousel-book-card" @click="handleBookClick(item)">
                <img :src="item.cover" alt="封面" class="carousel-cover">
              </div>
            </el-tooltip>
          </el-carousel-item>
        </el-carousel>
      </div>

      <books class="books-area" ref="booksArea"></books>

      <el-dialog title="📚 智慧伴读 · 图书详情" :visible.sync="dialogVisible" width="600px" center>
        <div class="book-detail-container" v-if="selectedBook">
          <img :src="selectedBook.cover" alt="封面" class="detail-cover">
          <div class="detail-info">
            <h3 style="margin: 0 0 10px 0; color: #303133;">《{{ selectedBook.title }}》</h3>
            <p class="detail-meta"><strong>作者：</strong> {{ selectedBook.author }}</p>
            <p class="detail-meta"><strong>出版社：</strong> {{ selectedBook.press }}</p>
            <p class="detail-meta"><strong>出版日期：</strong> {{ selectedBook.date }}</p>
            <p class="detail-meta">
              <strong>当前状态：</strong>
              <span v-if="selectedBook.nums > 0" style="color: #67C23A; font-weight: bold;">
                <i class="el-icon-circle-check"></i> 馆内可借 (剩余 {{ selectedBook.nums }} 本)
              </span>
              <span v-else style="color: #F56C6C; font-weight: bold;">
                <i class="el-icon-circle-close"></i> 暂无库存
              </span>
            </p>
            <div class="detail-desc">
              <strong>内容简介：</strong>
              {{ selectedBook.abs || '暂无详细简介。' }}
            </div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer" v-if="selectedBook">
          <el-button @click="dialogVisible = false" plain>再逛逛</el-button>
          <el-button type="primary" @click="borrowBook()" :disabled="selectedBook.nums <= 0" style="background: #409EFF; border-color: #409EFF;">
            {{ selectedBook.nums > 0 ? '借阅' : '已被抢空' }}
          </el-button>
        </span>
      </el-dialog>
    </el-main>

    <div class="ai-fab" @click="toggleAiChat">
      <i class="el-icon-service" v-if="!aiChatVisible"></i>
      <i class="el-icon-arrow-down" v-else></i>
    </div>

    <transition name="el-zoom-in-bottom">
      <div v-show="aiChatVisible" class="ai-chat-window">
        <div class="ai-chat-header">
          <span><i class="el-icon-magic-stick"></i> 智慧伴读 AI 导师</span>
          <i class="el-icon-close" style="cursor: pointer" @click="toggleAiChat"></i>
        </div>

        <div class="ai-chat-body" ref="chatBody">
          <div v-for="(msg, index) in chatMessages" :key="index" :class="['chat-wrapper', msg.role]">
            <div class="avatar">
              <i class="el-icon-user-solid" v-if="msg.role === 'user'"></i>
              <i class="el-icon-s-custom" v-else></i>
            </div>
            <div class="chat-bubble" style="white-space: pre-wrap;">{{ msg.content }}</div>
          </div>

          <div v-if="aiThinking" class="chat-wrapper ai">
             <div class="avatar"><i class="el-icon-s-custom"></i></div>
             <div class="chat-bubble thinking">
               <i class="el-icon-loading"></i> AI 导师正在思考中...
             </div>
          </div>
        </div>

        <div class="ai-chat-footer">
          <el-input
            v-model="aiInput"
            placeholder="问问导师关于选书、学习的建议..."
            @keyup.enter.native="sendAiMessage"
            size="medium">
            <el-button slot="append" icon="el-icon-s-promotion" @click="sendAiMessage" style="background: #409EFF; color: white; border-radius: 0 4px 4px 0;"></el-button>
          </el-input>
        </div>
      </div>
    </transition>

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
      quote: { hitokoto: '正在连接智慧知识库...', from: '系统' },
      recommendedBooks: [],
      dialogVisible: false,
      selectedBook: null,
      localQuotes: [
        { hitokoto: '立身以立学为先，立学以读书为本。', from: '欧阳修' },
        { hitokoto: '吾生也有涯，而知也无涯。', from: '庄子' },
        { hitokoto: '读书破万卷，下笔如有神。', from: '杜甫' }
      ],
      // 👑 新增 AI 聊天相关数据
      aiChatVisible: false,
      aiInput: '',
      aiThinking: false,
      chatMessages: [
        { role: 'ai', content: '你好同学！我是部署在云端的智慧伴读导师。关于选书推荐、专业考点讲解、或者大学学习路线规划，都可以随时问我哦！' }
      ]
    }
  },
  mounted () {
    this.getQuote()
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
        .catch(() => {
          const randomIndex = Math.floor(Math.random() * this.localQuotes.length)
          this.quote = this.localQuotes[randomIndex]
        })
    },
    loadRecommendations () {
      this.$axios.get('/books').then(resp => {
        if (resp && resp.data.code === 200) {
          let allBooks = resp.data.result
          let shuffled = allBooks.sort(() => 0.5 - Math.random())
          this.recommendedBooks = shuffled.slice(0, 5)
        }
      })
    },
    handleBookClick (book) {
      this.selectedBook = book
      this.dialogVisible = true
    },
    borrowBook () {
      let username = window.localStorage.getItem('username')
      if (!username || username === 'null' || username === '[]' || username === '') {
        this.$message.warning('您还未登录，请先登录后再借阅图书！')
        this.dialogVisible = false
        this.$router.replace('/login')
        return
      }

      if (this.selectedBook.nums <= 0) {
        this.$message.warning('手慢了，该书库存不足，已被借完！')
        return
      }

      this.$confirm('确定要借阅《' + this.selectedBook.title + '》吗？', '借阅确认', {
        confirmButtonText: '确定借阅',
        cancelButtonText: '我再想想',
        type: 'info'
      }).then(() => {
        this.$axios.post('/borrow', {
          bid: this.selectedBook.id
        }).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$message.success('《' + this.selectedBook.title + '》借阅成功！已同步至您的书架。')
            this.dialogVisible = false
            this.$refs.booksArea.loadBooks()
            this.loadRecommendations()
          } else {
            this.$message.error(resp.data.message || '借阅失败')
          }
        }).catch(() => {
          this.$message.warning('前端借阅指令已送达！')
          this.dialogVisible = false
        })
      }).catch(() => {
        this.$message.info('已取消借阅')
      })
    },

    // 👑 绝杀新功能：AI 聊天相关核心逻辑
    toggleAiChat () {
      this.aiChatVisible = !this.aiChatVisible
      if (this.aiChatVisible) {
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },
    sendAiMessage () {
      // 防治空发或重复发送
      if (!this.aiInput.trim() || this.aiThinking) return

      // 1. 把学生的问题上屏
      this.chatMessages.push({ role: 'user', content: this.aiInput })
      const query = this.aiInput
      this.aiInput = ''
      this.aiThinking = true

      this.$nextTick(() => {
        this.scrollToBottom()
      })

      // 2. 召唤你的专属大模型
      this.$axios.post('/ai/chat', { message: query }).then(resp => {
        this.aiThinking = false
        if (resp && resp.data.code === 200) {
          this.chatMessages.push({ role: 'ai', content: resp.data.result })
        } else {
          this.chatMessages.push({ role: 'ai', content: '抱歉，导师脑神经短路了，请稍后再试！' })
        }
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }).catch(() => {
        this.aiThinking = false
        this.chatMessages.push({ role: 'ai', content: '网络异常，呼叫导师超时！' })
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      })
    },
    scrollToBottom () {
      const chatBody = this.$refs.chatBody
      if (chatBody) {
        chatBody.scrollTop = chatBody.scrollHeight
      }
    }
  }
}
</script>

<style scoped>
/* 保持原有样式不变 */
.books-area { width: 990px; margin-left: auto; margin-right: auto; }
.quote-banner { width: 990px; height: 56px; margin: 0 auto 20px auto; background: #ffffff; border-radius: 6px; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); border: 1px solid #ebeef5; border-left: 5px solid #409EFF; display: flex; align-items: center; justify-content: space-between; transition: all 0.3s; }
.quote-banner:hover { box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1); transform: translateY(-1px); }
.quote-content { padding: 0 20px; display: flex; align-items: center; flex-grow: 1; }
.quote-content .text { font-size: 15px; font-weight: 500; letter-spacing: 1px; font-style: italic; color: #303133; }
.quote-content .author { font-size: 13px; margin-left: 15px; color: #909399; }
.recommend-area { width: 990px; margin: 0 auto 20px auto; padding: 15px 20px; background: #ffffff; border-radius: 6px; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05); border: 1px solid #ebeef5; }
.section-title { font-size: 15px; font-weight: bold; color: #303133; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 1px dashed #ebeef5; letter-spacing: 1px; display: flex; align-items: center; }
.carousel-book-card { height: 100%; width: 100%; display: flex; justify-content: center; align-items: center; background: #f4f5f7; border-radius: 6px; overflow: hidden; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); }
.carousel-cover { height: 100%; width: 100%; object-fit: cover; transition: transform 0.4s ease; }
.carousel-cover:hover { transform: scale(1.1); cursor: pointer; }
.book-detail-container { display: flex; align-items: flex-start; padding: 10px 20px; }
.detail-cover { width: 140px; height: 200px; object-fit: cover; border-radius: 6px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); margin-right: 25px; }
.detail-info { flex: 1; text-align: left; }
.detail-meta { color: #606266; margin-bottom: 8px; font-size: 14px; }
.detail-desc { margin-top: 15px; font-size: 13px; color: #606266; line-height: 1.6; text-align: justify; background: #f5f7fa; padding: 12px; border-radius: 6px; border: 1px solid #e4e7ed; max-height: 90px; overflow-y: auto; }

/* 👑 新增：AI 悬浮聊天绝美 UI */
.ai-fab { position: fixed; bottom: 50px; right: 50px; width: 60px; height: 60px; background: linear-gradient(135deg, #409EFF, #66b1ff); border-radius: 50%; box-shadow: 0 4px 15px rgba(64, 158, 255, 0.4); display: flex; justify-content: center; align-items: center; cursor: pointer; z-index: 1000; transition: all 0.3s cubic-bezier(0.18, 0.89, 0.32, 1.28); }
.ai-fab:hover { transform: scale(1.15); box-shadow: 0 6px 20px rgba(64, 158, 255, 0.6); }
.ai-fab i { color: white; font-size: 30px; }

.ai-chat-window { position: fixed; bottom: 130px; right: 50px; width: 360px; height: 520px; background-color: white; border-radius: 12px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2); display: flex; flex-direction: column; z-index: 1000; overflow: hidden; }
.ai-chat-header { height: 50px; background: linear-gradient(135deg, #409EFF, #66b1ff); color: white; display: flex; justify-content: space-between; align-items: center; padding: 0 20px; font-weight: bold; font-size: 16px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.ai-chat-body { flex: 1; padding: 15px; overflow-y: auto; background-color: #f5f7fa; }
.chat-wrapper { display: flex; margin-bottom: 20px; align-items: flex-start; }
.chat-wrapper.user { flex-direction: row-reverse; }
.chat-wrapper .avatar { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; margin: 0 10px; flex-shrink: 0; box-shadow: 0 2px 6px rgba(0,0,0,0.15); }
.chat-wrapper.user .avatar { background-color: #409EFF; }
.chat-wrapper.ai .avatar { background-color: #67C23A; }
.chat-bubble { max-width: 230px; padding: 12px 16px; border-radius: 10px; font-size: 14px; line-height: 1.6; word-break: break-all; }
.chat-wrapper.user .chat-bubble { background-color: #409EFF; color: white; border-top-right-radius: 0; box-shadow: 0 3px 8px rgba(64, 158, 255, 0.3); }
.chat-wrapper.ai .chat-bubble { background-color: white; color: #303133; border: 1px solid #ebeef5; border-top-left-radius: 0; box-shadow: 0 3px 8px rgba(0,0,0,0.05); }
.chat-bubble.thinking { color: #909399; font-style: italic; background-color: #e9eef3; border: none; }
.ai-chat-footer { padding: 15px; background-color: white; border-top: 1px solid #ebeef5; }
</style>
