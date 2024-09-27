import axios from "axios";
import { openMessageFail } from "@/store/store";
import { Composer } from "vue-i18n";
import i18n from "@/config/i18n";
class Translate {
  public t: Composer["t"];
  constructor() {
    this.t = i18n.global.t;
  }
  public getT(code: string) {
    return this.t(code);
  }
}
const translator = new Translate();

const instance = axios.create({
  baseURL: process.env.VUE_APP_API_URL,
  timeout: 10000,
});

instance.interceptors.request.use(
  function (config) {
    config.headers["Accept-Language"] = localStorage.getItem("lang") || "en";
    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

instance.interceptors.response.use(
  function (response) {
    return response;
  },
  function (error) {
    const lang = localStorage.getItem("lang");
    if (error.response) {
      const status = error.response.status;
      const data = error.response.data;

      if (status === 400) {
        console.log("Error 400:", data);
        if (data?.error) {
          openMessageFail(data.error);
        }
      }
    } else if (
      error.code === "ECONNABORTED" ||
      error.message === "Network Error"
    ) {
      openMessageFail(translator.getT("ECONNABORTED"));
    } else {
      openMessageFail(translator.getT("ECONNABORTED"));
    }

    return Promise.reject(error);
  }
);

export { instance };
