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
      default: '350px'
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
          subtext: '真实库存分布情况', // 修正了副标题
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c}本 ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: legendData // 👑 动态绑定真实的分类名
        },
        series: [
          {
            name: '真实馆藏数量',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '42%'],
            data: expectedData, // 👑 动态绑定数据库里实打实的库存数据！
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
