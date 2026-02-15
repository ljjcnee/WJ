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
    chartData: { type: Array, required: true }
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
    setOptions (data) {
      if (!data || data.length === 0) return
      const legendData = data.map(item => item.name)
      this.chart.setOption({
        title: {
          text: '藏书品种占比',
          left: 'center',
          top: '10'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} 品种 ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: legendData
        },
        series: [{
          name: '藏书品种分布',
          type: 'pie',
          roseType: 'radius',
          radius: [15, 95],
          center: ['50%', '45%'],
          data: data,
          animationEasing: 'cubicInOut',
          animationDuration: 2600
        }]
      })
    }
  }
}
</script>
