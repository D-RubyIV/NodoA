import { reactive } from "vue";
import { FormRules } from "element-plus";

type CategoryParamForm = {
  name: string;
  code: string;
  createdFrom: string;
  createdTo: string;
};

const createSearchRules = (
  t: (key: string) => string
): FormRules<CategoryParamForm> => {
  return reactive<FormRules<CategoryParamForm>>({
    name: [
      {
        required: true,
        validator(rule, value, callback) {
          if (value && /[!@#$%^&*(),.?":{}|<>]/.test(value)) {
            callback(new Error(t("noSpecialChars")));
          } else if (value && value.length > 25) {
            callback(new Error(t("max25")));
          }
          callback();
        },
        trigger: ["change"],
      },
    ],
    code: [
      {
        required: true,
        validator(rule, value, callback) {
          if (value && /[!@#$%^&*(),.?":{}|<>]/.test(value)) {
            callback(new Error(t("noSpecialChars")));
          } else if (value && value.length > 15) {
            callback(new Error(t("max15")));
          }
          callback();
        },
        trigger: ["change"],
      },
    ],
  });
};

export { CategoryParamForm, createSearchRules };
