<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize' // 👑 修复点：把它提到 require 的前面！
require('echarts/theme/macarons') // echarts 主题

export default {
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
    // 👑 核心新增 1：长出耳朵，接收来自父组件 index.vue 的真实排行榜数据
    chartData: {
      type: Object,
      default: () => {
        return {
          xAxis: [],
          heatData: []
        }
      }
    }
  },
  data () {
    return {
      chart: null
    }
  },
  // 👑 核心新增 2：一旦排行榜数据发生变化，立刻重新渲染！
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
      this.setOptions(this.chartData)
    },
    // 👑 核心新增 3：用传进来的书名和热度替换掉写死的假数据
    setOptions ({ xAxis, heatData } = {}) {
      this.chart.setOption({
        title: {
          text: '🔥 热门借阅风云榜 TOP 7',
          left: 'center',
          textStyle: {
            color: '#666',
            fontSize: 16,
            fontWeight: 'normal'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        grid: {
          top: 45,
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: xAxis, // 👑 动态绑定：前 7 名的书名！
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            interval: 0,
            rotate: 25 // 书名可能比较长，让文字倾斜 25 度防止重叠
          }
        }],
        yAxis: [{
          type: 'value',
          axisTick: {
            show: false
          }
        }],
        series: [{
          name: '借阅综合热度',
          type: 'bar',
          stack: 'vistors',
          barWidth: '40%', // 柱子的宽度
          data: heatData, // 👑 动态绑定：前 7 名对应的热度值！
          animationDuration: 2600,
          itemStyle: {
            // 给柱子加一点极其炫酷的圆角渐变效果
            normal: {
              barBorderRadius: [4, 4, 0, 0],
              color: new echarts.graphic.LinearGradient(
                0, 0, 0, 1,
                [
                  { offset: 0, color: '#83bff6' },
                  { offset: 0.5, color: '#188df0' },
                  { offset: 1, color: '#188df0' }
                ]
              )
            }
          }
        }]
      })
    }
  }
}
</script>
