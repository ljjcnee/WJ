<template>
  <el-menu
    class="categories"
    default-active="0"
    @select="handleSelect"
    active-text-color="#409EFF">
    <el-menu-item index="0" class="category-item">
      <i class="el-icon-menu"></i>
      <span slot="title">全部</span>
    </el-menu-item>
    <el-menu-item index="1" class="category-item">
      <i class="el-icon-collection-tag"></i>
      <span slot="title">文学</span>
    </el-menu-item>
    <el-menu-item index="2" class="category-item">
      <i class="el-icon-collection-tag"></i>
      <span slot="title">流行</span>
    </el-menu-item>
    <el-menu-item index="3" class="category-item">
      <i class="el-icon-collection-tag"></i>
      <span slot="title">文化</span>
    </el-menu-item>
    <el-menu-item index="4" class="category-item">
      <i class="el-icon-collection-tag"></i>
      <span slot="title">生活</span>
    </el-menu-item>
    <el-menu-item index="5" class="category-item">
      <i class="el-icon-collection-tag"></i>
      <span slot="title">经管</span>
    </el-menu-item>
    <el-menu-item index="6" class="category-item">
      <i class="el-icon-collection-tag"></i>
      <span slot="title">科技</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
export default {
  name: 'SideMenu',
  data () {
    return {
      cid: ''
    }
  },
  methods: {
    handleSelect (key, keyPath) {
      this.cid = key
      this.$emit('indexSelect')
    }
  }
}
</script>

<style scoped>
.categories {
  position: fixed;
  margin-left: 50%;
  left: -600px;
  top: 100px;
  width: 150px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-right: none;
}

/* 核心美化：给每个分类加上浅色底部分隔线 */
.category-item {
  border-bottom: 1px solid #f0f2f5;
}
.category-item:last-child {
  border-bottom: none; /* 最后一个不加线 */
}
</style>
