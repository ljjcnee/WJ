<template>
  <el-row :gutter="40" class="panel-group">

    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel">
        <div class="card-panel-icon-wrapper icon-people">
          <i class="el-icon-reading card-panel-icon"></i>
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            馆藏知识库总数
          </div>
          <count-to :start-val="0" :end-val="stats.bookCount || 0" :duration="2600" class="card-panel-num" />
        </div>
      </div>
    </el-col>

    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel">
        <div class="card-panel-icon-wrapper icon-message">
          <i class="el-icon-user-solid card-panel-icon"></i>
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            注册求知学者
          </div>
          <count-to :start-val="0" :end-val="stats.userCount || 0" :duration="3000" class="card-panel-num" />
        </div>
      </div>
    </el-col>

    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel">
        <div class="card-panel-icon-wrapper icon-money">
          <i class="el-icon-data-line card-panel-icon"></i>
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            当前外借/流转中
          </div>
          <count-to :start-val="0" :end-val="stats.borrowingCount || 0" :duration="3200" class="card-panel-num" />
        </div>
      </div>
    </el-col>

    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel">
        <div class="card-panel-icon-wrapper icon-shopping">
          <i class="el-icon-s-data card-panel-icon"></i>
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            累计借阅流水
          </div>
          <count-to :start-val="0" :end-val="stats.totalBorrowCount || 0" :duration="3600" class="card-panel-num" />
        </div>
      </div>
    </el-col>

  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  name: 'PanelGroup',
  components: {
    CountTo
  },
  // 👑 ESLint 格式已修复 (加了空格)
  data () {
    return {
      stats: {
        bookCount: 0,
        userCount: 0,
        borrowingCount: 0,
        totalBorrowCount: 0
      }
    }
  },
  // 👑 ESLint 格式已修复
  mounted () {
    this.loadStatistics()
  },
  methods: {
    // 👑 ESLint 格式已修复
    loadStatistics () {
      this.$axios.get('/admin/dashboard/statistics').then(resp => {
        if (resp && resp.data.code === 200 && resp.data.result) {
          this.stats = resp.data.result
        }
      }).catch(() => {
        console.log('数据拉取失败，显示默认0')
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 18px;

  .card-panel-col {
    margin-bottom: 32px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);
    border-radius: 10px;

    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }
      .icon-people { background: #40c9c6; }
      .icon-message { background: #36a3f7; }
      .icon-money { background: #f4516c; }
      .icon-shopping { background: #34bfa3; }
    }

    .icon-people { color: #40c9c6; }
    .icon-message { color: #36a3f7; }
    .icon-money { color: #f4516c; }
    .icon-shopping { color: #34bfa3; }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;

      .card-panel-text {
        line-height: 18px;
        color: #8caaac;
        font-size: 14px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}

@media (max-width:550px) {
  .card-panel-description {
    display: none;
  }
  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;

    .card-panel-icon {
      display: block;
      margin: 14px auto !important;
      float: none !important;
    }
  }
}
</style>
