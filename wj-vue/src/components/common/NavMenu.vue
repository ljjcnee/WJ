<template>
  <el-menu
    :default-active="$route.path"
    router
    mode="horizontal"
    background-color="white"
    text-color="#333"
    active-text-color="#409EFF"
    style="min-width: 1300px; box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.05); border-bottom: none; z-index: 100; position: relative; margin: 0;">

    <template v-for="(item, i) in navList">
      <el-menu-item
        v-if="!item.adminOnly || isAdmin"
        :key="i"
        :index="item.name"
        style="padding: 0 30px;"
        class="nav-item-with-line">
        <i :class="item.icon" style="font-size: 18px; margin-right: 5px;"></i>
        <span style="font-size: 16px; font-weight: bold;">{{ item.navItem }}</span>
      </el-menu-item>
    </template>

    <span style="position: absolute; padding-top: 14px; right: 38%; font-size: 24px; font-weight: bold; color: #409EFF; letter-spacing: 2px;">
      <i class="el-icon-reading" style="font-size: 28px; vertical-align: middle;"></i> 智慧学习图书管理系统
    </span>

    <div style="float: right; padding: 12px 20px;">
      <el-button v-if="isLoggedIn" type="danger" plain size="medium" icon="el-icon-switch-button" @click="logout">
        退出系统
      </el-button>

      <el-button v-else type="primary" plain size="medium" icon="el-icon-user" @click="goToLogin">
        登录系统
      </el-button>
    </div>

  </el-menu>
</template>

<script>
export default {
  name: 'NavMenu',
  data () {
    return {
      navList: [
        { name: '/library', navItem: '图书馆', icon: 'el-icon-collection', adminOnly: false },
        { name: '/mybooks', navItem: '我的借阅', icon: 'el-icon-notebook-1', adminOnly: false },
        // 👑 专属标签：标记它只能由管理员可见
        { name: '/admin/dashboard', navItem: '后台管理', icon: 'el-icon-setting', adminOnly: true }
      ],
      isLoggedIn: false,
      isAdmin: false // 新增：专门记录是否拥有管理员权限
    }
  },
  mounted () {
    this.checkLoginStatus()
  },
  watch: {
    '$route' () {
      this.checkLoginStatus()
    }
  },
  methods: {
    checkLoginStatus () {
      let storeUsername = this.$store.state.username
      let localUsername = window.localStorage.getItem('username')

      let loggedIn = false
      let currentUsername = ''

      // 提取当前的登录账号
      if (storeUsername && storeUsername !== '') {
        loggedIn = true
        currentUsername = storeUsername
      } else if (localUsername && localUsername !== 'null' && localUsername !== '""' && localUsername !== '[]') {
        loggedIn = true
        currentUsername = localUsername
      }

      // 去除可能从 LocalStorage 带来的字符串首尾双引号，保证精确匹配不出错
      if (typeof currentUsername === 'string') {
        currentUsername = currentUsername.replace(/(^"|"$)/g, '')
      }

      // 如果正在登录页，强行复位状态
      if (this.$route.path === '/login') {
        loggedIn = false
        currentUsername = ''
      }

      this.isLoggedIn = loggedIn

      // 👑 终极判定：在咱们的系统中，默认超级管理员账号就是 'admin'
      // 只有当解析出的账号名严格等于 'admin' 时，才允许渲染后台管理菜单！
      this.isAdmin = (currentUsername === 'admin')
    },

    goToLogin () {
      this.$router.push('/login')
    },

    logout () {
      this.$confirm('确定要退出当前账号吗？', '系统提示', {
        confirmButtonText: '确定退出',
        cancelButtonText: '暂不退出',
        type: 'warning'
      }).then(() => {
        this.$axios.get('/logout').then(resp => {
          if (resp && resp.data.code === 200) {
            this.$store.commit('logout')
            this.isLoggedIn = false
            this.isAdmin = false // 退出时同步撤销管理员标识
            this.$message.success('已安全退出')
            this.$router.replace('/library').catch(err => err)
          }
        }).catch(() => {
           this.$store.commit('logout')
           this.isLoggedIn = false
           this.isAdmin = false
           this.$router.replace('/library').catch(err => err)
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
a {
  text-decoration: none;
}
span {
  pointer-events: none;
}
.nav-item-with-line {
  position: relative;
}
.nav-item-with-line:not(:last-child)::after {
  content: '';
  position: absolute;
  right: 0;
  top: 20px;
  height: 20px;
  width: 1px;
  background-color: #ebeef5;
}
</style>
