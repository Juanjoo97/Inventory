const { configure } = require('quasar/wrappers');

module.exports = configure(function (/* ctx */) {
  return {
    boot: ['axios'],

    css: ['app.scss'],

    extras: ['material-icons', 'roboto-font'],

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
      plugins: ['Notify', 'Dialog', 'Loading']
    },

    animations: []
  };
});
