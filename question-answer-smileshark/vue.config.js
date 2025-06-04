const { defineConfig } = require('@vue/cli-service')
module.exports = {
  lintOnSave: false, 
  devServer: {
    port: 8081,
    proxy: {
      '/javaSever': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: { '^/javaSever': '' },
      }
    }
  },
  // devServer: {
  //   port: 8081,
  //   proxy: {
  //     '/javaSever': {
  //       target: 'http://192.168.24.89:8080',
  //       changeOrigin: true,
  //       pathRewrite: { '^/javaSever': '' },
  //     }
  //   }
  // }
}
