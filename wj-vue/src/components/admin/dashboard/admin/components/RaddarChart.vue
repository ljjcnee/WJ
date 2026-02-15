<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
// 👑 修复点：import 置顶
import echarts from 'echarts'
import resize from './mixins/resize'
require('echarts/theme/macarons') // echarts theme

const animationDuration = 3000

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
    }
  },
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
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        radar: {
          radius: '66%',
          center: ['50%', '42%'],
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
          indicator: [
            { name: '文学类', max: 10000 },
            { name: '科技类', max: 20000 },
            { name: '经管类', max: 20000 },
            { name: '生活类', max: 20000 },
            { name: '文化类', max: 20000 },
            { name: '流行类', max: 20000 }
          ]
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['图书存量', '预期采购', '实际采购']
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
              value: [5000, 7000, 12000, 11000, 15000, 14000],
              name: '图书存量'
            },
            {
              value: [4000, 9000, 15000, 15000, 13000, 11000],
              name: '预期采购'
            },
            {
              value: [5500, 11000, 12000, 15000, 12000, 12000],
              name: '实际采购'
            }
          ],
          animationDuration: animationDuration
        }]
      })
    }
  }
}
</script>
