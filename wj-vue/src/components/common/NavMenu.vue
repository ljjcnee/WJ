<template>
  <el-menu
    :default-active="$route.path"
    router
    mode="horizontal"
    background-color="white"
    text-color="#333"
    active-text-color="#409EFF"
    style="min-width: 1300px; box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.05); border-bottom: none; z-index: 100; position: relative; margin: 0;">

    <el-menu-item v-for="(item,i) in navList" :key="i" :index="item.name" style="padding: 0 30px;" class="nav-item-with-line">
      <i :class="item.icon" style="font-size: 18px; margin-right: 5px;"></i>
      <span style="font-size: 16px; font-weight: bold;">{{ item.navItem }}</span>
    </el-menu-item>

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
        { name: '/library', navItem: '图书馆', icon: 'el-icon-collection' },
        { name: '/mybooks', navItem: '我的书架', icon: 'el-icon-notebook-1' },
        { name: '/admin/dashboard', navItem: '后台管理', icon: 'el-icon-setting' }
      ],
      // 放弃自动计算，改为手动控制的明确变量
      isLoggedIn: false
    }
  },
  // 页面刚加载时，查一次岗
  mounted () {
    this.checkLoginStatus()
  },
  // 👑 终极探照灯：死死盯住每一次页面跳转
  watch: {
    '$route' () {
      this.checkLoginStatus()
    }
  },
  methods: {
    // 强制检查逻辑：双重保险
    checkLoginStatus () {
      let storeUser = this.$store.state.user
      let localUser = window.localStorage.getItem('user')

      let loggedIn = false

      if (storeUser && storeUser.username) {
        loggedIn = true
      } else if (localUser && localUser !== 'null' && localUser !== '""') {
        loggedIn = true
      }

      // 如果发现当前是在 /login 页面，强行设为未登录
      if (this.$route.path === '/login') {
        loggedIn = false
      }

      // 将检查结果赋给按钮开关
      this.isLoggedIn = loggedIn
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
            // 清理作案现场
            this.$store.commit('logout')
            window.localStorage.removeItem('user')

            // 手动把按钮掰成“登录”
            this.isLoggedIn = false

            this.$message.success('已安全退出')
            this.$router.replace('/login')
          }
        }).catch(() => {
           // 如果后端接口报错，前端也强制清理登出，防止卡死
           this.$store.commit('logout')
           window.localStorage.removeItem('user')
           this.isLoggedIn = false
           this.$router.replace('/login')
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
