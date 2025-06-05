const { defineConfig } = require('@vue/cli-service')
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
module.exports = {
  lintOnSave: false, 
  devServer: {
    port: 8081,
    proxy: {
      '/javaSever': {
        // target: 'http://localhost:8080',
        target: 'http://192.168.131.5:18080',
        changeOrigin: true,
        pathRewrite: { '^/javaSever': '' },
      }
    }
  },
  publicPath: '/' ,
  chainWebpack: (config) => {
    // 仅在生产环境启用 CSS 压缩
    if (process.env.NODE_ENV === 'production') {
      config.optimization.minimizer('css')
        .use(CssMinimizerPlugin);
    }
  },
  
}
