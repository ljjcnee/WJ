<template>
  <el-menu
    :default-active="currentPath"
    class="el-menu-admin"
    router
    mode="vertical"
    background-color="#545c64"
    text-color="#fff"
    active-text-color="#409EFF"
    style="height: 100%; border-right: none;">

    <template v-for="(item, i) in customMenus">

      <el-menu-item v-if="!item.children || item.children.length === 0" :key="'item-' + i" :index="item.path" style="text-align: left;">
        <i :class="item.iconCls" style="font-size: 18px; margin-right: 8px;"></i>
        <span slot="title" style="font-size: 15px;">{{ item.nameZh }}</span>
      </el-menu-item>

      <el-submenu v-else :key="'sub-' + i" :index="(i).toString()" style="text-align: left;">
        <span slot="title" style="font-size: 15px;">
          <i :class="item.iconCls" style="font-size: 18px; margin-right: 8px;"></i>
          {{ item.nameZh }}
        </span>
        <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
          <i :class="child.icon"></i>
          {{ child.nameZh }}
        </el-menu-item>
      </el-submenu>

    </template>
  </el-menu>
</template>

<script>
export default {
  name: 'AdminMenu',
  computed: {
    customMenus () {
      let originalMenus = this.$store.state.adminMenus || []

      if (originalMenus.length === 0) {
        return []
      }

      // 过滤掉不需要的菜单
      let filteredMenus = originalMenus.filter(m => m.nameZh !== '系统配置' && m.nameZh !== '广告管理')

      return filteredMenus.map(m => {
        if (m.nameZh === '内容管理') {
          return {
            ...m,
            nameZh: '图书管理',
            iconCls: 'el-icon-reading',
            path: '/admin/content/book',
            children: []
          }
        }
        return m
      })
    },
    currentPath () {
      return this.$route.path
    }
  }
}
</script>

<style scoped>
.el-menu-admin {
  border-radius: 0;
  min-height: calc(100vh - 61px);
}
</style>
