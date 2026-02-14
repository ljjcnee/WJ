<template>
  <div id="admin-body">
    <nav-menu></nav-menu>
    <el-container style="height: calc(100vh - 61px); margin: 0; padding: 0;">
      <el-aside style="width: 250px; background-color: #545c64; padding: 0; margin: 0;">
        <admin-menu></admin-menu>
      </el-aside>
      <el-main style="background-color: #f0f2f5; padding: 20px; margin: 0;">
        <router-view/>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import AdminMenu from './AdminMenu'
import NavMenu from '../common/NavMenu'

export default {
  name: 'AdminIndex',
  components: { AdminMenu, NavMenu }
}
</script>

<style scoped>
#admin-body {
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
}
.el-main {
  padding: 0;
}
.el-container {
  padding: 0;
  margin: 0;
}
</style>
