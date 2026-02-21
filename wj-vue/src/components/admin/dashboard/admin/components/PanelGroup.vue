<template>
  <el-row :gutter="40" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('newVisits')">
        <div class="card-panel-icon-wrapper icon-books">
          <i class="el-icon-reading card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            馆藏图书总量
          </div>
          <count-to :start-val="0" :end-val="stats.books" :duration="2600" class="card-panel-num" />
        </div>
      </div>
    </el-col>

    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('messages')">
        <div class="card-panel-icon-wrapper icon-users">
          <i class="el-icon-user card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            注册读者总数
          </div>
          <count-to :start-val="0" :end-val="stats.users" :duration="3000" class="card-panel-num" />
        </div>
      </div>
    </el-col>

    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('purchases')">
        <div class="card-panel-icon-wrapper icon-history">
          <i class="el-icon-document-copy card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            历史借阅流水
          </div>
          <count-to :start-val="0" :end-val="stats.borrows" :duration="3200" class="card-panel-num" />
        </div>
      </div>
    </el-col>

    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('shoppings')">
        <div class="card-panel-icon-wrapper icon-active">
          <i class="el-icon-data-line card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            今日活跃动作
          </div>
          <count-to :start-val="0" :end-val="stats.active" :duration="3600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  components: {
    CountTo
  },
  // 👑 新增：接收从 index.vue 传过来的 stats 对象
  props: {
    stats: {
      type: Object,
      default: () => {
        return {
          books: 0,
          users: 0,
          borrows: 0,
          active: 0
        }
      }
    }
  },
  methods: {
    handleSetLineChartData (type) {
      this.$emit('handleSetLineChartData', type)
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

    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }

      .icon-books { background: #40c9c6; }
      .icon-users { background: #36a3f7; }
      .icon-history { background: #f4516c; }
      .icon-active { background: #34bfa3 }
    }

    .icon-books { color: #40c9c6; }
    .icon-users { color: #36a3f7; }
    .icon-history { color: #f4516c; }
    .icon-active { color: #34bfa3 }

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
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
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

    .svg-icon {
      display: block;
      margin: 14px auto !important;
      float: none !important;
    }
  }
}
</style>
