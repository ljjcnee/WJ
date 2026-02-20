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
    }
  },
  // 👑 严格遵守 ESLint 规范，加了空格
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
          text: '馆藏智慧知识画像',
          subtext: '各分类图书占比情况',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c}本 ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['文学', '计算机', '理学', '工程', '哲学', '艺术']
        },
        series: [
          {
            name: '藏书数量',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '42%'],
            data: [
              { value: 320, name: '文学' },
              { value: 240, name: '计算机' },
              { value: 149, name: '理学' },
              { value: 100, name: '工程' },
              { value: 59, name: '哲学' },
              { value: 80, name: '艺术' }
            ],
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
