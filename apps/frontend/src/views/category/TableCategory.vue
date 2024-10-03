<script setup lang="ts">
import { onMounted, ref, watch, inject } from "vue";
import { debounce } from "lodash";
import { Action, CategoryOverview, openMessage } from "@/views/category/index";

import viVN from "element-plus/es/locale/lang/vi";
import enUS from "element-plus/es/locale/lang/en";
import {
  Collection,
  Delete,
  Edit,
  Plus,
  Search,
  View,
} from "@element-plus/icons-vue";
import { ElMessageBox, FormInstance } from "element-plus";
import { instance } from "@/axios/customAxios";
import { useI18n } from "vue-i18n";
import { openMessageFail, openMessageSuccess } from "@/store/store";
import { createSearchRules } from "@/views/category/searchValidate";

const APIURL = process.env.VUE_APP_API_URL;
const langLocal = localStorage.getItem("lang");
const uiLocale = langLocal === "vi" ? viVN : enUS;

const childMethod = () => {
  console.log("Child method called!");
};

defineProps({
  addTabForm: {
    type: Function,
    required: true,
  },
});
const { t } = useI18n(); // Lấy hàm t và locale từ i18n

defineExpose({
  childMethod,
});
const emitter = inject<any>("emitter");
const totalElements = ref(100);
const objects = ref<CategoryOverview[]>([]);
const errorStartDate = ref<{ error: boolean; value: string }>({
  error: false,
  value: "",
});
const errorEndDate = ref<{ error: boolean; value: string }>({
  error: false,
  value: "",
});
const selectedObject = ref<CategoryOverview>({
  name: "",
  categoryCode: "",
  description: "",
});
const data = ref({
  pageIndex: 1,
  pageSize: 5,
  query: "",
  sort: {
    order: "",
    key: "",
  },
});
const param = ref({
  name: "",
  code: "",
  createdFrom: "",
  createdTo: "",
});
const selectedAction = ref<Action>(Action.DETAIL);

const isOpenForm = ref<boolean>(false);
const setIsOpenForm = (p: boolean, a: Action, idObject?: number) => {
  if (a === "CREATE") {
    selectedObject.value.file = undefined;
  }
  selectedAction.value = a;
  isOpenForm.value = p;
  if (idObject) {
    instance.get(`/category/${idObject}`).then(function (response) {
      selectedObject.value = response.data;
    });
  } else {
    selectedObject.value = {};
  }
};

const validateDates = () => {
  const from = new Date(param.value.createdFrom);
  const to = new Date(param.value.createdTo);

  errorStartDate.value = { error: false, value: "" };
  errorEndDate.value = { error: false, value: "" };

  if (param.value.createdFrom && param.value.createdTo) {
    if (to < from) {
      errorStartDate.value = { error: true, value: t("validateDate") };
      errorEndDate.value = { error: true, value: t("validateDate") };
      return false;
    } else {
      dateError.value = null; // Không có lỗi
      return true;
    }
  } else {
    dateError.value = null; // Không có lỗi
    return true;
  }
};

const isValidateFormSearch = async () => {
  if (!ruleSearchFormRef.value) return;
  return (
    validateDates() &&
    ruleSearchFormRef.value.validate((valid) => {
      if (valid) {
        console.log("Validate SUCCESS");
      } else {
        console.log("Validate FAIL");
      }
    })
  );
};

const fetchAll = async () => {
  const clonedParam = ref({ ...param.value });
  clonedParam.value.code = clonedParam.value.code.trim();
  clonedParam.value.name = clonedParam.value.name.trim();
  if (await isValidateFormSearch()) {
    instance
      .post("/category/overview", data.value, {
        params: clonedParam.value,
      })
      .then(function (response) {
        console.log(response.data);
        objects.value = response.data.content;
        totalElements.value = response.data.totalElements;
      });
  }
};

// Sử dụng debounce để trì hoãn việc gọi fetchAll
const debouncedFetchAll = debounce(fetchAll, 300);

const handleSizeChange = (val: number) => {
  console.log(`${val} items per page`);
};
const handleCurrentChange = (val: number) => {
  console.log(`current page: ${val}`);
};

onMounted(() => {
  fetchAll();
  emitter.on("custom-event", (payload: any) => {
    console.log("------------");
    fetchAll();
  });
});

watch(
  [param, data],
  ([newParam, newData], [oldParam, oldData]) => {
    debouncedFetchAll();
  },
  { deep: true }
);

const downloadFile = async () => {
  console.log("chạy xuất excel");
  try {
    const response = await instance.get("/category/export/excel", {
      params: param.value,
      responseType: "blob",
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "DanhSachDanhMuc.xlsx");
    document.body.appendChild(link);
    link.click();

    link.parentNode?.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Error downloading the file:", error);
  }
};

const openConfirmDelete = async (objetcId: number) => {
  try {
    await ElMessageBox.confirm(t("confirmDelete"), t("warning"), {
      confirmButtonText: t("ok"),
      cancelButtonText: t("cancel"),
      type: "warning",
    });
    await instance.delete(`category/${objetcId}`);
    await fetchAll(); // Gọi lại dữ liệu sau khi xóa thành công
    openMessageSuccess(t("deleteSuccess")); // Hiển thị thông báo thành công
  } catch (error: any) {
    console.log(error);
    if (error?.response?.status === 400) {
      openMessageFail(error.response.data.error);
    } else if (error?.response?.status !== 400 && error !== "cancel") {
      openMessageFail(t("deleteFail"));
    }
    console.error("Error deleting product:", error);
  }
};
const dateError = ref<string | null>(null);

const emits = defineEmits(["addTabCreate:object:action"]);

// validate search
const ruleSearchFormRef = ref<FormInstance>();
</script>

<template>
  <div>
    <div>
      <el-form
        ref="ruleSearchFormRef"
        :model="param"
        :rules="createSearchRules(t)"
        class="flex gap-2 py-2 justify-between"
      >
        <div class="flex gap-2">
          <el-form-item prop="name" class="!w-[300px]">
            <el-input
              v-model="param.name"
              :placeholder="t('searchByCategoryName')"
              :suffix-icon="Search"
            />
          </el-form-item>
          <el-form-item prop="code" class="!w-[300px]">
            <el-input
              v-model="param.code"
              class="!min-w-[150px]"
              :placeholder="t('searchByCategoryCode')"
              :suffix-icon="Search"
              :formatter="
          (value: any) => {
            return value.trim();
          }
        "
            />
          </el-form-item>
          <div class="flex items-start flex-col">
            <el-config-provider :locale="uiLocale">
              <el-date-picker
                v-model="param.createdFrom"
                type="date"
                :placeholder="t('fromDate')"
                class="!min-w-[150px]"
                format="YYYY/MM/DD"
                value-format="YYYY-MM-DD"
                @change="validateDates"
              />
            </el-config-provider>
            <div>
              <p
                v-show="errorStartDate.error === true"
                class="text-[12px] text-red-400"
              >
                {{ errorStartDate.value }}
              </p>
            </div>
          </div>
          <div class="flex items-start flex-col">
            <el-config-provider :locale="uiLocale">
              <el-date-picker
                v-model="param.createdTo"
                type="date"
                :placeholder="t('toDate')"
                class="!min-w-[150px]"
                format="YYYY/MM/DD"
                value-format="YYYY-MM-DD"
                @change="validateDates"
              />
            </el-config-provider>
            <div>
              <p
                v-show="errorEndDate.error === true"
                class="text-[12px] text-red-400"
              >
                {{ errorEndDate.value }}
              </p>
            </div>
          </div>
        </div>
        <div class="flex gap-2">
          <el-form-item>
            <el-button
              @click="downloadFile"
              :disabled="totalElements <= 0"
              class="bg-blue-500 text-white hover:bg-blue-300"
            >
              <Collection style="width: 1em; height: 1em; margin-right: 8px" />
              {{ t("exportExcel") }}
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-button
              @click="addTabForm({}, Action.CREATE)"
              class="bg-blue-500 text-white hover:bg-blue-300"
            >
              <Plus style="width: 1em; height: 1em; margin-right: 8px" />
              {{ t("create") }}
            </el-button>
          </el-form-item>
        </div>
      </el-form>
    </div>
    <div>
      <el-config-provider :locale="uiLocale">
        <el-table :data="objects">
          <el-table-column label="STT" width="70">
            <template #default="scope">
              {{
                ((data.pageIndex ?? 1) - 1) * (data.pageSize ?? 10) +
                scope.$index +
                1
              }}
            </template>
          </el-table-column>
          <el-table-column prop="image" :label="t('image')" width="120">
            <template #default="scope">
              <img
                v-if="!scope.row.image"
                src="https://th.bing.com/th/id/OIP.xHvCevAq3nc3vJv2UjgE2AHaHa?pid=ImgDet&w=205&h=205&c=7"
                alt="Image"
                class="object-cover w-16"
              />
              <img
                v-if="scope.row.image"
                :src="`${APIURL}/file/${scope.row.image}`"
                alt="Image"
                class="object-cover w-16"
              />
            </template>
          </el-table-column>
          <el-table-column
            prop="name"
            :label="t('name')"
            sortable
            width="150"
          />
          <el-table-column
            prop="categoryCode"
            :label="t('categoryCode')"
            sortable
            width="150"
          />
          <el-table-column
            width="250"
            prop="description"
            :label="t('description')"
            sortable
          />
          <el-table-column
            prop="createdDate"
            :label="t('createdDate')"
            sortable
            width="125"
          />
          <el-table-column
            prop="modifiedDate"
            :label="t('modifiedDate')"
            sortable
            width="140"
          />
          <el-table-column
            prop="createdBy"
            :label="t('createdBy')"
            sortable
            width="140"
          />
          <el-table-column
            width="130"
            prop="modifiedBy"
            :label="t('modifiedBy')"
            sortable
          />
          <el-table-column
            prop="status"
            :label="t('status')"
            sortable
            width="140"
          >
            <template #default="scope">
              <p>
                {{
                  scope.row.status === "ACTIVE" ? t("active") : t("inActive")
                }}
              </p>
            </template>
          </el-table-column>
          <el-table-column :label="t('action')" width="200" fixed="right">
            <template #default="scope">
              <div class="flex">
                <el-tooltip :content="t('detail')" placement="top">
                  <el-button @click="addTabForm(scope.row, Action.DETAIL)">
                    <View style="width: 1em; height: 1em" />
                  </el-button>
                </el-tooltip>
                <el-tooltip :content="t('update')" placement="top">
                  <el-button @click="addTabForm(scope.row, Action.UPDATE)">
                    <Edit style="width: 1em; height: 1em" />
                  </el-button>
                </el-tooltip>
                <el-tooltip :content="t('delete')" placement="top">
                  <el-button @click="openConfirmDelete(scope.row.id)">
                    <Delete style="width: 1em; height: 1em" />
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-config-provider>
    </div>
    <div class="flex justify-end py-2">
      <div class="demo-pagination-block">
        <el-config-provider :locale="uiLocale">
          <el-pagination
            v-model:current-page="data.pageIndex"
            v-model:page-size="data.pageSize"
            :page-sizes="[1, 2, 3, 4, 5, 10, 20]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalElements"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </el-config-provider>
      </div>
    </div>
  </div>
</template>
