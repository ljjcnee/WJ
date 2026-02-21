<template>
  <div class="dashboard-editor-container">
    <panel-group :stats="panelStats" />

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <raddar-chart :chart-data="radarChartData" />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart :chart-data="pieChartData" />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <bar-chart :chart-data="barChartData" />
        </div>
      </el-col>
    </el-row>

    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <line-chart :chart-data="lineChartData" />
    </el-row>

    <el-row>
      <el-col :span="24" style="margin-bottom:30px;">
        <transaction-table :list="transactionList" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import PanelGroup from './components/PanelGroup'
import LineChart from './components/LineChart'
import RaddarChart from './components/RaddarChart'
import PieChart from './components/PieChart'
import BarChart from './components/BarChart'
import TransactionTable from './components/TransactionTable'
// 👑 已彻底铲除 TodoList 的引入

export default {
  name: 'DashboardAdmin',
  components: {
    PanelGroup,
    LineChart,
    RaddarChart,
    PieChart,
    BarChart,
    TransactionTable
    // 👑 已彻底铲除 TodoList 的注册
  },
  data () {
    return {
      panelStats: { books: 0, borrows: 0, users: 0, active: 0 },
      lineChartData: { xAxisData: [], borrowData: [], returnData: [] },
      pieChartData: [],
      radarChartData: { indicators: [], inventory: [], heat: [] },
      barChartData: { xAxis: [], heatData: [] },
      transactionList: []
    }
  },
  mounted () {
    this.loadPanelStats()
    this.loadRealData()
    this.loadTrendAndTableData()
  },
  methods: {
    loadPanelStats () {
      this.$axios.get('/admin/dashboard/stats').then(resp => {
        if (resp && resp.data.code === 200) {
          this.panelStats = resp.data.result
        }
      }).catch(err => {
        console.log(err)
      })
    },

    loadRealData () {
      this.$axios.get('/admin/dashboard/chart-data').then(resp => {
        if (resp && resp.data.code === 200) {
          this.processData(resp.data.result)
        }
      }).catch(err => {
        console.log(err)
      })
    },

    loadTrendAndTableData () {
      this.$axios.get('/admin/dashboard/recent-records').then(resp => {
        if (resp && resp.data.code === 200) {
          const records = resp.data.result

          // 给底部的全宽表格赋值：截取最新的前 10 条流水
          this.transactionList = [...records].reverse().slice(0, 10)

          this.processLineChart(records)
        }
      }).catch(err => {
        console.log(err)
      })
    },

    processLineChart (records) {
      const xAxisData = []
      const borrowData = [0, 0, 0, 0, 0, 0, 0]
      const returnData = [0, 0, 0, 0, 0, 0, 0]

      for (let i = 6; i >= 0; i--) {
        const d = new Date()
        d.setDate(d.getDate() - i)
        let month = '' + (d.getMonth() + 1)
        let day = '' + d.getDate()
        if (month.length < 2) month = '0' + month
        if (day.length < 2) day = '0' + day
        xAxisData.push(`${d.getFullYear()}-${month}-${day}`)
      }

      records.forEach(record => {
        if (record.borrowTime) {
          const bDate = record.borrowTime.substring(0, 10)
          const bIdx = xAxisData.indexOf(bDate)
          if (bIdx > -1) borrowData[bIdx]++
        }
        if (record.returnTime) {
          const rDate = record.returnTime.substring(0, 10)
          const rIdx = xAxisData.indexOf(rDate)
          if (rIdx > -1) returnData[rIdx]++
        }
      })

      this.lineChartData = { xAxisData, borrowData, returnData }
    },

    processData (books) {
      const categoryMap = {}
      const bookHeatList = []

      books.forEach(book => {
        let borrowHeat = book.heat + 5

        bookHeatList.push({
          name: book.title || '未知图书',
          heat: borrowHeat
        })

        const catName = book.category || '未分类'
        if (!categoryMap[catName]) {
          categoryMap[catName] = { inventory: 0, borrowHeat: 0 }
        }
        categoryMap[catName].inventory += (book.nums || 1)
        categoryMap[catName].borrowHeat += borrowHeat
      })

      bookHeatList.sort((a, b) => b.heat - a.heat)
      const top7 = bookHeatList.slice(0, 7).reverse()
      this.barChartData = {
        xAxis: top7.map(item => item.name.substring(0, 8) + (item.name.length > 8 ? '..' : '')),
        heatData: top7.map(item => item.heat)
      }

      const categories = Object.keys(categoryMap)
      this.pieChartData = categories.map(key => ({ name: key, value: categoryMap[key].inventory }))

      this.radarChartData = {
        indicators: categories.map(key => ({ name: key, max: Math.max(categoryMap[key].inventory * 2, categoryMap[key].borrowHeat * 1.5) })),
        inventory: categories.map(key => categoryMap[key].inventory * 2),
        heat: categories.map(key => categoryMap[key].borrowHeat)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;
}
.chart-wrapper {
  background: #fff;
  padding: 16px 16px 0;
  margin-bottom: 32px;
}
@media (max-width:1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
