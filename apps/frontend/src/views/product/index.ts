import { reactive, ref } from "vue";
import { ElNotification, FormInstance, FormRules } from "element-plus";

type ProductOverview = {
  createdDate?: string;
  modifiedDate?: string;
  createdBy?: string;
  modifiedBy?: string;
  id?: number;
  name?: string;
  description?: string;
  price?: number;
  productCode?: string;
  quantity?: number;
  image?: string | null;
  status?: string;
  categoryCodes?: string | string[];
  file?: File;
  selectedCategoryOptions?: string[];
};

enum Action {
  CREATE = "CREATE",
  UPDATE = "UPDATE",
  DETAIL = "DETAIL",
  DELETE = "DELETE",
}
const openMessage = (message: string, delay?: number) => {
  ElNotification({
    title: "Title",
    message: message,
    duration: delay || 1000,
  });
};

const rules = reactive<FormRules<ProductOverview>>({
  name: [
    {
      required: true,
      validator(rule, value, callback, source, options) {
        if (value === "" || value === undefined) {
          callback(new Error("Vui lòng nhập tên"));
        } else if (value.length < 5 || value.length > 25) {
          callback(new Error(`Tên từ 5 => 25 kí tự`));
        }
        callback();
      },
      trigger: ["change", "blur"],
    },
  ],
  productCode: [
    {
      required: true,
      validator(rule, value, callback, source, options) {
        if (value === "" || value === undefined) {
          callback(new Error("Vui lòng nhập mã"));
        } else if (value.length < 5 || value.length > 15) {
          callback(new Error(`Tên từ 5 => 15 kí tự`));
        }
        callback();
      },
      trigger: ["change", "blur"],
    },
  ],
  description: [
    {
      required: true,
      validator(rule, value, callback, source, options) {
        if (value === "" || value === undefined) {
          callback(new Error("Vui lòng nhập mô tả"));
        } else if (value.length < 5 || value.length > 50) {
          callback(new Error(`Tên từ 5 => 50 kí tự`));
        } else {
          callback();
        }
      },
      trigger: ["change", "blur"],
    },
  ],
  price: [
    {
      required: true,
      validator(rule, value, callback, source, options) {
        if (value === "" || value === undefined) {
          callback(new Error("Vui lòng nhập giá"));
        } else if (isNaN(value) || value <= 0) {
          callback(new Error("Giá phải là số dương hợp lệ"));
        } else {
          callback();
        }
      },
      trigger: ["change", "blur"],
    },
  ],
  quantity: [
    {
      required: true,
      validator(rule, value, callback, source, options) {
        if (value === "" || value === undefined) {
          callback(new Error("Vui lòng nhập số lượng"));
        } else if (isNaN(value) || value < 0) {
          callback(new Error("Số lượng phải là số dương hợp lệ" + value));
        } else {
          callback();
        }
      },
      trigger: ["change", "blur"],
    },
  ],
});

export { Action, ProductOverview, rules, openMessage };
