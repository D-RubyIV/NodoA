import { reactive, ref } from "vue";
import { ElNotification, FormInstance, FormRules } from "element-plus";

const patternCode = /^[A-Za-z0-9]+$/;
const patternName = /^[A-Za-z0-9À-ÿ ]+$/; // Cập nhật để cho phép chữ có dấu
const patternSpecialChars = /[!@#$%^&*(),.?":{}|<>]/; // Kiểm tra ký tự đặc biệt

type CategoryOverview = {
  createdDate?: string; // Dạng string cho ngày
  modifiedDate?: string; // Dạng string cho ngày
  createdBy?: string; // Người tạo
  modifiedBy?: string; // Người chỉnh sửa
  id?: number; // ID của danh mục
  name?: string; // Tên danh mục
  description?: string; // Mô tả danh mục
  categoryCode?: string; // Mã danh mục
  image?: string;
  status?: "ACTIVE" | "INACTIVE"; // Trạng thái có thể là ACTIVE hoặc INACTIVE
  file?: File;
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
export { Action, CategoryOverview, openMessage };
