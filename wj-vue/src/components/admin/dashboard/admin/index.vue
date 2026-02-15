<template>
  <div class="dashboard-editor-container">
    <panel-group :panel-data="panelData" />

    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <line-chart :chart-data="lineChartData" />
    </el-row>

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
  </div>
</template>

<script>
import PanelGroup from './components/PanelGroup'
import LineChart from './components/LineChart'
import RaddarChart from './components/RaddarChart'
import PieChart from './components/PieChart'
import BarChart from './components/BarChart'

export default {
  name: 'DashboardAdmin',
  components: {
    PanelGroup, LineChart, RaddarChart, PieChart, BarChart
  },
  data () {
    return {
      panelData: { totalBooks: 0, totalTypes: 0, borrowRate: 0, outOfStock: 0 },
      pieChartData: [],
      radarChartData: { indicators: [], inventory: [] },
      lineChartData: { xAxis: [], expectedData: [], actualData: [] },
      barChartData: { xAxis: [], inLibrary: [], emptyStock: [] }
    }
  },
  mounted () {
    this.loadRealData()
  },
  methods: {
    loadRealData () {
      this.$axios.get('/books').then(resp => {
        if (resp && resp.data.code === 200) {
          this.processData(resp.data.result)
        }
      })
    },
    processData (books) {
      let totalInventory = 0 // 当前总馆藏
      let outOfStock = 0 // 缺货品种
      const categoryMap = {}

      books.forEach(book => {
        const nums = book.nums || 0
        totalInventory += nums
        if (nums <= 0) outOfStock++

        const catName = book.category ? book.category.name : '未分类'
        if (!categoryMap[catName]) {
          categoryMap[catName] = { count: 0, inventory: 0, empty: 0, maxBook: 0 }
        }
        categoryMap[catName].count++
        categoryMap[catName].inventory += nums
        if (nums <= 0) categoryMap[catName].empty++
        if (nums > categoryMap[catName].maxBook) categoryMap[catName].maxBook = nums
      })

      // 假设每种书原本有 100 本作为系统的“理想总容量”（用于计算借出率）
      const idealTotal = books.length * 100
      const borrowedCount = idealTotal > totalInventory ? idealTotal - totalInventory : 0
      const borrowRate = idealTotal === 0 ? 0 : Math.round((borrowedCount / idealTotal) * 100)

      // 1. 顶部真实指标
      this.panelData = {
        totalBooks: totalInventory,
        totalTypes: books.length,
        borrowRate: borrowRate,
        outOfStock: outOfStock
      }

      const categories = Object.keys(categoryMap)

      // 2. 饼图：品种分布
      this.pieChartData = categories.map(key => ({ name: key, value: categoryMap[key].count }))

      // 3. 雷达图：分类总库存健康度
      this.radarChartData = {
        indicators: categories.map(key => ({ name: key, max: Math.max(20, categoryMap[key].inventory * 1.5) })),
        inventory: categories.map(key => categoryMap[key].inventory)
      }

      // 4. 折线图：各分类“库存最高”与“平均库存”对比，寻找短板
      this.lineChartData = {
        xAxis: categories,
        expectedData: categories.map(key => categoryMap[key].maxBook), // 标杆库存
        actualData: categories.map(key => Math.round(categoryMap[key].inventory / categoryMap[key].count)) // 平均库存
      }

      // 5. 柱状图：各分类“有货品种”与“缺货品种”对比
      this.barChartData = {
        xAxis: categories,
        inLibrary: categories.map(key => categoryMap[key].count - categoryMap[key].empty),
        emptyStock: categories.map(key => categoryMap[key].empty)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.dashboard-editor-container { padding: 32px; background-color: rgb(240, 242, 245); position: relative; }
.chart-wrapper { background: #fff; padding: 16px 16px 0; margin-bottom: 32px; }
@media (max-width:1024px) { .chart-wrapper { padding: 8px; } }
</style>
