import { ElNotification } from "element-plus";
import { h, Ref } from "vue";

const openMessageSuccess = (message: string, delay?: number) => {
  ElNotification({
    title: "",
    message: h("p", { style: "color: green" }, message),
    duration: delay || 2000,
  });
};

const openMessageFail = (message: string, delay?: number) => {
  ElNotification({
    title: "",
    message: h("p", { style: "color: teal" }, message),
    duration: delay || 4000,
  });
};

const handlePaste = (text: string) => {
  return text.replace(/[!@#$%^&*(),.?":{}|<>]/g, "");
};

export { openMessageSuccess, openMessageFail, handlePaste };
