<template>
  <body id="paper">
    <el-form class="login-container" label-position="left" label-width="0px">
      <h3 class="login_title">系统登录</h3>
      <el-form-item>
        <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="账号"></el-input>
      </el-form-item>
      <el-form-item>
        <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码"></el-input>
      </el-form-item>
      <el-form-item style="width: 100%">
        <el-button type="primary" style="width: 100%;background: #505458;border: none" v-on:click="login">登录</el-button>
        <router-link to="register">
          <el-button type="primary" style="width: 100%;background: #505458;border: none">注册</el-button>
        </router-link>
      </el-form-item>
    </el-form>
  </body>
</template>

<script>
export default {
  name: 'Login',
  data () {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      responseResult: []
    }
  },
  methods: {
    login () {
      var _this = this
      console.log(this.$store.state)
      this.$axios
        .post('/login', {
          username: this.loginForm.username,
          password: this.loginForm.password
        })
        .then(resp => {
          if (resp.data.code === 200) {
            var data = resp.data.result
            _this.$store.commit('login', data)
            var path = _this.$route.query.redirect
            // 👑 核心修复：将默认跳转路径从 /index 彻底改为正确的首页 /library
            _this.$router.replace({path: path === '/' || path === undefined ? '/library' : path})
          } else {
            this.$alert(resp.data.message, '提示', {
              confirmButtonText: '确定'
            })
          }
        })
        .catch(() => {
        })
    }
  }
}
</script>

<style scoped>
#paper {
  background: url("../assets/img/bg/eva1.jpg") no-repeat center;
  height: 100%;
  width: 100%;
  background-size: cover;
  position: fixed;
}
body {
  margin: 0;
}
.login-container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 90px auto;
  width: 350px;
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}
.login_title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
}
</style>
