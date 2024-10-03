<script setup lang="ts">
import { inject, onMounted, ref, watch } from "vue";
import axios from "axios";
import { debounce } from "lodash";
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
import { Action, openMessage, ProductOverview } from "@/views/product/index";
import { useI18n } from "vue-i18n";
import { openMessageFail, openMessageSuccess } from "@/store/store";

import viVN from "element-plus/es/locale/lang/vi";
import enUS from "element-plus/es/locale/lang/en";
import { createSearchProductRules } from "@/views/product/searchValidate";

const APIURL = process.env.VUE_APP_API_URL;
const langLocal = localStorage.getItem("lang");
const uiLocale = langLocal === "vi" ? viVN : enUS;
const errorStartDate = ref<{ error: boolean; value: string }>({
  error: false,
  value: "",
});
const errorEndDate = ref<{ error: boolean; value: string }>({
  error: false,
  value: "",
});

const childMethod = () => {
  console.log("Child method called!");
};

const { t, locale } = useI18n(); // Lấy hàm t và locale từ i18n

const props = defineProps({
  addTabForm: {
    type: Function,
    required: true,
  },
});
const emitter = inject<any>("emitter");

defineExpose({
  childMethod,
});

const totalElements = ref(100);

const objects = ref<ProductOverview[]>([]);

const selectedObject = ref<ProductOverview>({
  name: "",
  productCode: "",
  description: "",
});

// validate search
const ruleSearchFormRef = ref<FormInstance>();

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
  productCode: "",
  categoryCode: "",
  createdFrom: "",
  createdTo: "",
});
watch(
  param,
  (newVal, oldVal) => {
    console.log(param?.value);
    param.value.productCode = param.value.productCode.trim();
    param.value.categoryCode = param.value.categoryCode.trim();
  },
  { deep: true }
);

const selectedAction = ref<Action>(Action.DETAIL);

const setIsOpenForm = (p: boolean, a: Action, idObject?: number) => {
  if (a === "CREATE") {
    selectedObject.value.file = undefined;
  }
  selectedAction.value = a;
  if (idObject) {
    instance.get(`/product/${idObject}`).then(function (response) {
      selectedObject.value = response.data;
    });
  } else {
    selectedObject.value = {};
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
  clonedParam.value.categoryCode = clonedParam.value.categoryCode.trim();
  clonedParam.value.productCode = clonedParam.value.productCode.trim();
  clonedParam.value.name = clonedParam.value.name.trim();
  if (await isValidateFormSearch()) {
    instance
      .post("/product/overview", data.value, {
        params: clonedParam.value,
      })
      .then(function (response) {
        console.log(response.data);
        objects.value = response.data.content;
        totalElements.value = response.data.totalElements;
      });
  }
};

const debouncedFetchAll = debounce(fetchAll, 300);

const handleSizeChange = (val: number) => {
  console.log(`${val} items per page`);
};

const handleCurrentChange = (val: number) => {
  console.log(`current page: ${val}`);
};

const defaultTime = ref<[Date, Date]>([
  new Date(2000, 1, 1, 0, 0, 0),
  new Date(2000, 2, 1, 23, 59, 59),
]);

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
  try {
    const response = await instance.get("/product/export/excel", {
      params: param.value,
      responseType: "blob",
    });

    // Tạo URL cho dữ liệu blob
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "DanhSachSanPham.xlsx"); // Tên file khi tải về

    // Append link to body and click to trigger download
    document.body.appendChild(link);
    link.click();

    // Cleanup
    link.parentNode?.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Error downloading the file:", error);
  }
};

const openConfirmDelete = async (objetcId: number) => {
  await ElMessageBox.confirm(t("confirmDelete"), t("warning"), {
    confirmButtonText: t("ok"),
    cancelButtonText: t("cancel"),
    type: "warning",
  })
    .then(() => {
      instance.delete(`product/${objetcId}`).then(function () {
        fetchAll();
        openMessageSuccess(t("deleteSuccess"));
      });
    })
    .catch((error: any) => {
      console.log(error);
      if (error?.response?.status === 400) {
        openMessageFail(error.response.data.error);
      } else if (error?.response?.status !== 400 && error !== "cancel") {
        openMessageFail(t("deleteFail"));
      }
      console.error("Error deleting product:", error);
    });
};

const dateError = ref<string | null>(null);

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
</script>

<template>
  <div>
    <div class="">
      <el-form
        ref="ruleSearchFormRef"
        :model="param"
        :rules="createSearchProductRules(t)"
        class="flex gap-2 py-2 justify-between"
      >
        <div class="flex gap-2">
          <el-form-item prop="name" class="!w-[250px]">
            <el-input
              v-model="param.name"
              :placeholder="t('searchByProductName')"
              :suffix-icon="Search"
            />
          </el-form-item>
          <el-form-item prop="productCode" class="!w-[250px]">
            <el-input
              v-model="param.productCode"
              :placeholder="t('searchByProductCode')"
              :suffix-icon="Search"
            />
          </el-form-item>
          <el-form-item prop="categoryCode" class="!w-[250px]">
            <el-input
              v-model="param.categoryCode"
              :placeholder="t('searchByCategoryCode')"
              :suffix-icon="Search"
            />
          </el-form-item>
          <div class="flex items-start flex-col">
            <el-config-provider :locale="uiLocale">
              <el-date-picker
                v-model="param.createdFrom"
                type="date"
                :placeholder="t('fromDate')"
                format="YYYY/MM/DD"
                value-format="YYYY-MM-DD"
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
                format="YYYY/MM/DD"
                value-format="YYYY-MM-DD"
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
            width="200"
            sortable
          />
          <el-table-column
            prop="productCode"
            :label="t('productCode')"
            width="150"
            sortable
          />
          <el-table-column
            prop="categoryCodes"
            :label="t('categoryCode')"
            sortable
            width="150"
          />
          <el-table-column
            prop="price"
            :label="t('price')"
            sortable
            width="100"
            align="right"
          />
          <el-table-column
            prop="quantity"
            :label="t('quantity')"
            width="150"
            sortable
            align="right"
          />
          <el-table-column
            prop="createdDate"
            :label="t('createdDate')"
            width="150"
            sortable
          />
          <el-table-column
            prop="modifiedDate"
            :label="t('modifiedDate')"
            width="150"
            sortable
          />
          <el-table-column
            prop="createdBy"
            :label="t('createdBy')"
            width="150"
            sortable
          />
          <el-table-column
            prop="modifiedBy"
            :label="t('modifiedBy')"
            width="150"
            sortable
          />
          <el-table-column
            prop="status"
            :label="t('status')"
            width="150"
            sortable
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
