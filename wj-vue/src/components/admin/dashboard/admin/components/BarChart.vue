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
    setOptions ({ xAxis, inLibrary, emptyStock } = {}) {
      if (!xAxis) return
      this.chart.setOption({
        title: {
          text: '分类在馆状态',
          left: 'center',
          top: '10'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          top: 40,
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: xAxis,
          axisTick: { alignWithLabel: true }
        }],
        yAxis: [{
          type: 'value',
          axisTick: { show: false }
        }],
        series: [
          {
            name: '有货品种',
            type: 'bar',
            stack: 'vistors',
            barWidth: '40%',
            data: inLibrary,
            animationDuration: 3000
          },
          {
            name: '缺货品种',
            type: 'bar',
            stack: 'vistors',
            barWidth: '40%',
            data: emptyStock,
            animationDuration: 3000,
            itemStyle: { color: '#f4516c' }
          }
        ]
      })
    }
  }
}
</script>
