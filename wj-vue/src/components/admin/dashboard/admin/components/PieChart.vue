<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize'
require('echarts/theme/macarons') // echarts theme

export default {
  name: 'PieChart',
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    // 👑 核心新增 1：接收从 index.vue 传过来的真实库存数组
    chartData: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      chart: null
    }
  },
  // 👑 核心新增 2：监听数据变化，一旦数据库里图书有变，立刻重绘饼图！
  watch: {
    chartData: {
      deep: true,
      handler (val) {
        this.setOptions(val)
      }
    }
  },
  mounted () {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy () {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart () {
      this.chart = echarts.init(this.$el, 'macarons')
      // 初始化时载入外部传来的真数据
      this.setOptions(this.chartData)
    },
    // 👑 核心新增 3：把死数据扒掉，换成动态绑定的 expectedData
    setOptions (expectedData) {
      // 动态提取所有的分类名字，用来渲染底部的图例
      const legendData = expectedData.map(item => item.name)

     this.chart.setOption({
        title: {
          text: '馆藏智慧知识画像',
          subtext: '真实库存分布情况',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c}本 ({d}%)'
        },
        legend: {
          type: 'scroll', // 👑 新增魔法：如果未来分类太多，允许图例左右滚动，绝不往上乱挤！
          left: 'center',
          bottom: '0', // 👑 把图例尽量往下压，贴紧底边
          data: legendData
        },
        series: [
          {
            name: '真实馆藏数量',
            type: 'pie',
            roseType: 'radius',
            // 👑 核心修复：把外半径从 95 缩小到 70！给外围的文字标签留出足够的“呼吸空间”
            radius: [15, 70],
            center: ['50%', '52%'], // 👑 稍微往下偏一点点，避开头部的副标题
            data: expectedData,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
