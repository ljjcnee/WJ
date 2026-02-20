<template>
  <div>
    <el-dialog
      title="修改用户信息"
      :visible.sync="dialogFormVisible">
      <el-form v-model="selectedUser" style="text-align: left" ref="dataForm">
        <el-form-item label="用户名" label-width="120px" prop="username">
          <label>{{selectedUser.username}}</label>
        </el-form-item>
        <el-form-item label="真实姓名" label-width="120px" prop="name">
          <el-input v-model="selectedUser.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="手机号" label-width="120px" prop="phone">
          <el-input v-model="selectedUser.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" label-width="120px" prop="email">
          <el-input v-model="selectedUser.email" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="密码" label-width="120px" prop="password">
          <el-button type="warning" @click="resetPassword(selectedUser.username)">重置密码</el-button>
        </el-form-item>
        <el-form-item label="角色分配" label-width="120px" prop="roles">
          <el-checkbox-group v-model="selectedRolesIds">
              <el-checkbox v-for="(role,i) in roles" :key="i" :label="role.id">{{role.nameZh}}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit(selectedUser)">确 定</el-button>
      </div>
    </el-dialog>
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理中心</el-breadcrumb-item>
        <el-breadcrumb-item>用户管理</el-breadcrumb-item>
        <el-breadcrumb-item>用户信息</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <bulk-registration @onSubmit="listUsers()"></bulk-registration>
    <el-card style="margin: 18px 2%;width: 95%">
      <el-table
        :data="users"
        stripe
        :default-sort = "{prop: 'id', order: 'ascending'}"
        style="width: 100%"
        :max-height="tableHeight">
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
        <el-table-column
          prop="id"
          label="id"
          sortable
          width="100">
        </el-table-column>
        <el-table-column
          prop="username"
          label="用户名"
          fit>
        </el-table-column>

        <el-table-column
          prop="name"
          label="真实姓名"
          fit>
        </el-table-column>
        <el-table-column
          prop="phone"
          label="电话号码"
          width="120">
        </el-table-column>
        <el-table-column
          prop="email"
          label="邮箱"
          show-overflow-tooltip
          fit>
        </el-table-column>

        <el-table-column
          label="状态"
          sortable
          width="100">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.enabled"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="(value) => commitStatusChange(value, scope.row)">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="120">
          <template slot-scope="scope">
            <el-button
              @click="editUser(scope.row)"
              type="text"
              size="small">
              编辑
            </el-button>
            <el-button
              @click="deleteUser(scope.row.id)"
              type="text"
              size="small">
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin: 20px 0 20px 0;float: left">
        <el-button>取消选择</el-button>
        <el-button>批量删除</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
  import BulkRegistration from './BulkRegistration'
  export default {
    name: 'UserProfile',
    components: {BulkRegistration},
    data () {
      return {
        users: [],
        roles: [],
        dialogFormVisible: false,
        selectedUser: [],
        selectedRolesIds: []
      }
    },
    mounted () {
      this.listUsers()
      this.listRoles()
    },
    computed: {
      tableHeight () {
        return window.innerHeight - 320
      }
    },
    methods: {
      listUsers () {
        var _this = this
        this.$axios.get('/admin/user').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.users = resp.data.result
          }
        })
      },
      listRoles () {
        var _this = this
        this.$axios.get('/admin/role').then(resp => {
          if (resp && resp.data.code === 200) {
            _this.roles = resp.data.result
          }
        })
      },
      commitStatusChange (value, user) {
        if (user.username !== 'admin') {
          this.$axios.put('/admin/user/status', {
            enabled: value,
            username: user.username
          }).then(resp => {
            if (resp && resp.data.code === 200) {
              if (value) {
                this.$message('用户 [' + user.username + '] 已启用')
              } else {
                this.$message('用户 [' + user.username + '] 已禁用')
              }
            }
          })
        } else {
          user.enabled = true
          this.$alert('不能禁用管理员账户')
        }
      },
      onSubmit (user) {
        let _this = this
        // 根据视图绑定的角色 id 向后端传送角色信息
        let roles = []
        for (let i = 0; i < _this.selectedRolesIds.length; i++) {
          for (let j = 0; j < _this.roles.length; j++) {
            if (_this.selectedRolesIds[i] === _this.roles[j].id) {
              roles.push(_this.roles[j])
            }
          }
        }
        this.$axios.put('/admin/user', {
          username: user.username,
          // 👑 确保在提交修改时把这三个字段也发送给后端保存
          name: user.name,
          phone: user.phone,
          email: user.email,
          roles: roles
        }).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$alert('用户信息修改成功')
            this.dialogFormVisible = false
            // 修改角色后重新请求用户信息，实现视图更新
            this.listUsers()
          } else {
            this.$alert(resp.data.message)
          }
        })
      },
      editUser (user) {
        this.dialogFormVisible = true
        // 为了防止直接修改表格里的数据而没有点保存，这里最好做个浅拷贝
        this.selectedUser = Object.assign({}, user)
        let roleIds = []
        for (let i = 0; i < user.roles.length; i++) {
          roleIds.push(user.roles[i].id)
        }
        this.selectedRolesIds = roleIds
      },
      resetPassword (username) {
        this.$axios.put('/admin/user/password', {
          username: username
        }).then(resp => {
          if (resp && resp.data.code === 200) {
            this.$alert('密码已重置为 123')
          }
        })
      },
      // 👑 核心新增：向后端发送删除请求
      deleteUser (id) {
        this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$axios.delete('/admin/user/' + id).then(resp => {
            if (resp && resp.data.code === 200) {
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
              // 删除成功后，重新拉取一次数据刷新表格
              this.listUsers()
            } else {
              this.$message.error('删除失败')
            }
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
      }
    }
  }
</script>

<style scoped>

</style>
