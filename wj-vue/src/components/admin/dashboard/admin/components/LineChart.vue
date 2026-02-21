<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
import resize from './mixins/resize' // 👑 修复点：把它提到 require 的前面！
require('echarts/theme/macarons') // echarts theme

export default {
  mixins: [resize],
  props: {
    className: { type: String, default: 'chart' },
    width: { type: String, default: '100%' },
    height: { type: String, default: '350px' },
    autoResize: { type: Boolean, default: true },
    // 👑 接收真实近七日数据
    chartData: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      chart: null
    }
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
    setOptions ({ xAxisData, borrowData, returnData } = {}) {
      this.chart.setOption({
        title: { text: '全馆近七日借阅/归还活跃趋势', left: 'center', textStyle: { color: '#666', fontSize: 16, fontWeight: 'normal' } },
        xAxis: {
          data: xAxisData, // 👑 真实的近七天日期
          boundaryGap: false,
          axisTick: { show: false }
        },
        grid: { left: 10, right: 10, bottom: 20, top: 40, containLabel: true },
        tooltip: { trigger: 'axis', axisPointer: { type: 'cross' }, padding: [5, 10] },
        yAxis: { axisTick: { show: false } },
        legend: { data: ['每日借出', '每日归还'], bottom: 0 },
        series: [
          {
            name: '每日借出',
            smooth: true,
            type: 'line',
            itemStyle: { normal: { color: '#FF005A', lineStyle: { color: '#FF005A', width: 2 } } },
            data: borrowData, // 👑 真实的借出量
            animationDuration: 2800,
            animationEasing: 'cubicInOut'
          },
          {
            name: '每日归还',
            smooth: true,
            type: 'line',
            itemStyle: { normal: { color: '#3888fa', lineStyle: { color: '#3888fa', width: 2 }, areaStyle: { color: '#f3f8ff' } } },
            data: returnData, // 👑 真实的归还量
            animationDuration: 2800,
            animationEasing: 'quadraticOut'
          }
        ]
      })
    }
  }
}
</script>
