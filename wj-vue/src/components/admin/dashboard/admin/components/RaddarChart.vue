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
    setOptions ({ indicators, inventory, heat } = {}) {
      if (!indicators || indicators.length === 0) return
      this.chart.setOption({
        title: {
          text: '馆藏结构 vs 学习热度 评估模型',
          left: 'center',
          top: '0'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        radar: {
          radius: '60%',
          center: ['50%', '50%'],
          splitNumber: 8,
          splitArea: {
            areaStyle: {
              color: 'rgba(127,95,132,.3)',
              opacity: 1,
              shadowBlur: 45,
              shadowColor: 'rgba(0,0,0,.5)',
              shadowOffsetX: 0,
              shadowOffsetY: 15
            }
          },
          indicator: indicators
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['馆藏分配比重', '真实借阅热度']
        },
        series: [{
          type: 'radar',
          symbol: 'none',
          areaStyle: {
            normal: {
              shadowBlur: 13,
              shadowColor: 'rgba(0,0,0,.2)',
              shadowOffsetX: 0,
              shadowOffsetY: 10,
              opacity: 1
            }
          },
          data: [
            {
              value: inventory,
              name: '馆藏分配比重'
            },
            {
              value: heat,
              name: '真实借阅热度'
            }
          ],
          animationDuration: 3000
        }]
      })
    }
  }
}
</script>
