<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize'
require('echarts/theme/macarons') // echarts theme

export default {
  name: 'BarChart',
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
    }
  },
  // 👑 严格遵守 ESLint 规范
  data () {
    return {
      chart: null
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

      this.chart.setOption({
        title: {
          text: '智慧推荐底座：热门借阅 TOP 榜单',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          top: 50,
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          // 替换成咱们耳熟能详的书名
          data: ['三体', 'Java编程思想', '百年孤独', '活着', '红楼梦', 'Vue实战'],
          axisTick: { alignWithLabel: true },
          axisLabel: {
            interval: 0,
            rotate: 20 // 倾斜一下书名，防止名字太长挤在一起
          }
        }],
        yAxis: [{
          type: 'value',
          axisTick: { show: false }
        }],
        series: [{
          name: '累计借阅次数',
          type: 'bar',
          barWidth: '40%',
          data: [89, 72, 55, 43, 30, 24],
          animationDuration: 3000,
          itemStyle: { color: '#36a3f7' }
        }]
      })
    }
  }
}
</script>
