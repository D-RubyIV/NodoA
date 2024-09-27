const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  lintOnSave: false,
  transpileDependencies: true,
  devServer: {
    proxy: {
      "/api": {
        target: "https://f667-1-53-126-15.ngrok-free.app",
        changeOrigin: true,
        pathRewrite: { "^/api": "" },
      },
    },
    open: process.platform === "darwin",
    host: "0.0.0.0",
    port: 8085,
    https: false,
    client: {
      overlay: {
        runtimeErrors: (error) => {
          const ignoreErrors = [
            "ResizeObserver loop completed with undelivered notifications.",
          ];
          return !ignoreErrors.includes(error.message);
        },
      },
    },
  },
  chainWebpack: (config) => {
    config.plugin("define").tap((definitions) => {
      Object.assign(definitions[0], {
        VUE_OPTIONS_API: "true",
        VUE_PROD_DEVTOOLS: "false",
        VUE_PROD_HYDRATION_MISMATCH_DETAILS: "false",
      });
      return definitions;
    });
  },
});
