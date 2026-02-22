<template>
  <div style="text-align: left">
    <el-button class="add-button" type="success" icon="el-icon-circle-plus-outline" @click="dialogFormVisible = true">添加图书</el-button>
    <el-dialog
      :title="form.id ? '📝 智慧知识库 - 编辑图书信息' : '📚 智慧知识库 - 新书入库'"
      :visible.sync="dialogFormVisible"
      @close="clear">
      <el-form v-model="form" style="text-align: left" ref="dataForm">

        <el-form-item v-if="!form.id" label="✨ 智能获取" :label-width="formLabelWidth">
          <el-input
            v-model="isbn"
            autocomplete="off"
            placeholder="输入ISBN条形码 (如: 9780140328721)"
            style="width: 240px; margin-right: 15px;"
            @keyup.enter.native="fetchBookByIsbn">
          </el-input>
          <el-button
            type="warning"
            icon="el-icon-magic-stick"
            @click="fetchBookByIsbn"
            :loading="isFetching"
            style="background-color: #E6A23C; border-color: #E6A23C;">
            {{ isFetching ? '正在跨洋拉取...' : '一键自动填充' }}
          </el-button>
          <div style="font-size: 12px; color: #909399; margin-top: 5px;">
            (调用 OpenLibrary 全球开源书库 API，无需 VPN，永久免费)
          </div>
        </el-form-item>

        <el-divider v-if="!form.id" content-position="left">人工确认与完善</el-divider>

        <el-form-item label="书名" :label-width="formLabelWidth" prop="title">
          <el-input v-model="form.title" autocomplete="off" placeholder="不加《》"></el-input>
        </el-form-item>
        <el-form-item label="作者" :label-width="formLabelWidth" prop="author">
          <el-input v-model="form.author" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="出版日期" :label-width="formLabelWidth" prop="date">
          <el-input v-model="form.date" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="出版社" :label-width="formLabelWidth" prop="press">
          <el-input v-model="form.press" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="封面" :label-width="formLabelWidth" prop="cover">
          <el-input v-model="form.cover" autocomplete="off" placeholder="图片 URL"></el-input>
          <transition name="el-zoom-in-top">
            <div v-if="form.cover" style="margin-top: 15px;">
              <img :src="form.cover" alt="封面预览" style="height: 140px; border-radius: 6px; box-shadow: 0 4px 12px 0 rgba(0,0,0,0.1);">
            </div>
          </transition>
          <img-upload @onUpload="uploadImg" ref="imgUpload" style="margin-top: 10px;"></img-upload>
        </el-form-item>
        <el-form-item label="简介" :label-width="formLabelWidth" prop="abs">
          <el-input type="textarea" v-model="form.abs" autocomplete="off" :rows="4" placeholder="OpenLibrary 暂不提供中文简介，请根据抓取到的书籍手动完善..."></el-input>
        </el-form-item>
        <el-form-item label="分类" :label-width="formLabelWidth" prop="cid">
          <el-select v-model="form.category.id" placeholder="请选择本馆对应分类">
            <el-option label="文学" value="1"></el-option>
            <el-option label="流行" value="2"></el-option>
            <el-option label="文化" value="3"></el-option>
            <el-option label="生活" value="4"></el-option>
            <el-option label="经管" value="5"></el-option>
            <el-option label="科技" value="6"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="库存数" :label-width="formLabelWidth" prop="nums">
          <el-input v-model="form.nums" type="number" autocomplete="off" placeholder="默认 5"></el-input>
        </el-form-item>

        <el-form-item prop="id" style="height: 0">
          <el-input type="hidden" v-model="form.id" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" plain>取 消</el-button>
        <el-button type="primary" @click="onSubmit" style="background-color: #409EFF; border-color: #409EFF;">
          {{ form.id ? '确 定' : '确 定 入 库' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import ImgUpload from './ImgUpload'
  export default {
    name: 'EditForm',
    components: {ImgUpload},
    data () {
      return {
        dialogFormVisible: false,
        isbn: '',
        isFetching: false,
        form: {
          id: '',
          title: '',
          author: '',
          date: '',
          press: '',
          cover: '',
          abs: '',
          category: {
            id: '',
            name: ''
          },
          nums: 5
        },
        formLabelWidth: '120px'
      }
    },
    // 👑 修复：处理 ESLint 格式及数据响应式，确保编辑旧书时库存框不消失
    watch: {
      form: {
        deep: true,
        handler (newVal) {
          if (newVal.id && (newVal.nums === undefined || newVal.nums === null)) {
            this.$set(this.form, 'nums', 1)
          }
        }
      }
    },
    methods: {
      clear () {
        this.$refs.imgUpload.clear()
        this.isbn = ''
        this.form = {
          id: '',
          title: '',
          author: '',
          date: '',
          press: '',
          cover: '',
          abs: '',
          category: {
            id: '',
            name: ''
          },
          nums: 5
        }
      },
      fetchBookByIsbn () {
        let cleanIsbn = this.isbn.replace(/[-\s]/g, '')
        if (!cleanIsbn) {
          this.$message.warning('请先输入 ISBN 码哦！')
          return
        }
        this.isFetching = true
        const url = `https://openlibrary.org/api/books?bibkeys=ISBN:${cleanIsbn}&jscmd=data&format=json`

        fetch(url)
          .then(res => res.json())
          .then(data => {
            this.isFetching = false
            const bookKey = `ISBN:${cleanIsbn}`
            if (data[bookKey]) {
              const bookInfo = data[bookKey]
              this.form.title = bookInfo.title || ''
              this.form.author = bookInfo.authors ? bookInfo.authors.map(a => a.name).join(', ') : ''
              this.form.date = bookInfo.publish_date || ''
              this.form.press = bookInfo.publishers ? bookInfo.publishers.map(p => p.name).join(', ') : ''
              if (bookInfo.cover) {
                this.form.cover = bookInfo.cover.large || bookInfo.cover.medium || bookInfo.cover.small || ''
              }
              this.$message.success('🎉 抓取成功！请补充简介并选择分类。')
            } else {
              this.$message.error('未找到该书记录，请手动录入。')
            }
          })
          .catch(() => {
            this.isFetching = false
            this.$message.error('请求超时，请重试！')
          })
      },
      onSubmit () {
        this.$axios
          .post('/admin/content/books', {
            id: this.form.id,
            cover: this.form.cover,
            title: this.form.title,
            author: this.form.author,
            date: this.form.date,
            press: this.form.press,
            abs: this.form.abs,
            category: this.form.category,
            // 👑 修复：Radix 转换及强转数字
            nums: Number(this.form.nums) || 0
          }).then(resp => {
            if (resp && resp.data.code === 200) {
              this.dialogFormVisible = false
              this.$emit('onSubmit')
              this.$message.success(this.form.id ? '信息已更新' : '新书已入库')
            }
        })
      },
      uploadImg () {
        this.form.cover = this.$refs.imgUpload.url
      }
    }
  }
</script>

<style scoped>
  .add-button {
    margin: 18px 0 0 10px;
    background-color: #67C23A;
    border-color: #67C23A;
    font-weight: bold;
  }
</style>
