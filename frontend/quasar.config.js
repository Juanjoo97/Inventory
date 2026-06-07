const { configure } = require('quasar/wrappers');

module.exports = configure(function (/* ctx */) {
  return {
    boot: ['axios'],

    css: ['app.scss'],

    // Se elimina 'material-icons' (font de ~128KB). Los iconos se cargan como SVG.
    extras: ['roboto-font'],

    build: {
      target: {
        browser: ['es2019', 'edge88', 'firefox78', 'chrome87', 'safari13.1'],
        node: 'node16'
      },
      vueRouterMode: 'history',
      env: {
        API_URL: process.env.API_URL || 'http://localhost:8080'
      }
    },

    devServer: {
      open: false,
      port: 9000
    },

    framework: {
      iconSet: 'svg-material-icons',
      plugins: ['Notify', 'Dialog', 'Loading']
    },

    animations: []
  };
});