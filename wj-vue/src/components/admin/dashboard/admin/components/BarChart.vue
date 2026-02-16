<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize'
require('echarts/theme/macarons')

export default {
  mixins: [resize],
  props: {
    className: { type: String, default: 'chart' },
    width: { type: String, default: '100%' },
    height: { type: String, default: '300px' },
    chartData: { type: Object, required: true }
  },
  data () {
    return { chart: null }
  },
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
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    })
  },
  beforeDestroy () {
    if (!this.chart) return
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    setOptions ({ xAxis, heatData } = {}) {
      if (!xAxis) return
      this.chart.setOption({
        title: {
          text: '智慧学习：借阅热度 TOP 7 排行榜',
          left: 'center',
          top: '0'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          top: 35,
          left: '2%',
          right: '5%',
          bottom: '3%',
          containLabel: true
        },
        // 横向柱状图，X轴Y轴对调
        xAxis: [{
          type: 'value',
          axisTick: { show: false }
        }],
        yAxis: [{
          type: 'category',
          data: xAxis,
          axisTick: { alignWithLabel: true }
        }],
        series: [
          {
            name: '借阅热度(次)',
            type: 'bar',
            barWidth: '50%',
            data: heatData,
            animationDuration: 3000,
            itemStyle: {
              color: '#36a3f7',
              barBorderRadius: [0, 5, 5, 0]
            }
          }
        ]
      })
    }
  }
}
</script>
