<template>
  <body id="paper">
    <el-form :model="loginForm" :rules="rules" ref="loginForm" class="login-container" label-position="left" label-width="0px" v-loading="loading">
      <h3 class="login_title">用户注册</h3>
      <el-form-item prop="username">
        <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="账号"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码"></el-input>
      </el-form-item>
      <el-form-item prop="name">
        <el-input type="text" v-model="loginForm.name" auto-complete="off" placeholder="真实姓名"></el-input>
      </el-form-item>
      <el-form-item prop="phone">
        <el-input type="text" v-model="loginForm.phone" auto-complete="off" placeholder="电话号码"></el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input type="text" v-model="loginForm.email" auto-complete="off" placeholder="E-Mail"></el-input>
      </el-form-item>
      <el-form-item style="width: 100%">
        <el-button type="primary" style="width: 100%;background: #505458;border: none" v-on:click="register">注册</el-button>
      </el-form-item>
    </el-form>
  </body>
</template>

<script>
export default {
  name: 'Register',
  data () {
    return {
      rules: {
        username: [{required: true, message: '用户名不能为空', trigger: 'blur'}],
        password: [{required: true, message: '密码不能为空', trigger: 'blur'}]
      },
      loginForm: {
        username: '',
        password: '',
        name: '',
        phone: '',
        email: ''
      },
      loading: false
    }
  },
  methods: {
    register () {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true
          this.$axios
            .post('/register', {
              username: this.loginForm.username,
              password: this.loginForm.password,
              name: this.loginForm.name,
              phone: this.loginForm.phone,
              email: this.loginForm.email
            })
            .then(resp => {
              this.loading = false
              if (resp && resp.data.code === 200) {
                this.$alert('注册成功，请前往登录！', '提示', {
                  confirmButtonText: '确定'
                }).then(() => {
                  this.$router.replace('/login')
                })
              } else {
                let errorMsg = resp.data.message ? resp.data.message : '注册失败：用户名已存在或格式不正确'
                this.$alert(errorMsg, '提示', {
                  confirmButtonText: '确定',
                  type: 'warning'
                })
              }
            })
            // 👑 核心修复：将 catch(error => ...) 改为 catch(() => ...) ，完美符合 ESLint 规范
            .catch(() => {
              this.loading = false
              this.$alert('网络请求失败或服务器异常，请确保后端已启动！', '错误', {
                confirmButtonText: '确定',
                type: 'error'
              })
            })
        } else {
          return false
        }
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
