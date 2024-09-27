import { reactive } from "vue";
import { FormRules } from "element-plus";

type ProductParamForm = {
  name: string;
  productCode: string;
  categoryCode: string;
  createdFrom: string;
  createdTo: string;
};

const createSearchProductRules = (
  t: (key: string) => string
): FormRules<ProductParamForm> => {
  return reactive<FormRules<ProductParamForm>>({
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
    productCode: [
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
    categoryCode: [
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

export { ProductParamForm, createSearchProductRules };
