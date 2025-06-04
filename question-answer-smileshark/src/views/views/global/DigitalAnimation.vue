<template>
    <span ref="countUpEl" class="count-up">
    {{ animatedValue }}
    </span>
</template>
  
  <script>
  import { CountUp } from 'countup.js'
  
  export default {
    props:{
        orderAllLimit: {
          type: Number,
          default: 0
        },
        decimalPlaces:{
            type: Number,
            required: false,
            default: 2
        }
    },
    data() {
      return {
        animatedValue: 0,
        countUp: null
      }
    },
    watch: {
      orderAllLimit(newVal) {
        this.countUp.update(newVal)
      }
    },
    mounted() {
      this.countUp = new CountUp(this.$refs.countUpEl, this.orderAllLimit, {
        decimalPlaces: this.decimalPlaces,
        duration: 1.5,
        separator: ','
      })
      this.countUp.start()
    }
  }
  </script>
  
  <style>
  </style>