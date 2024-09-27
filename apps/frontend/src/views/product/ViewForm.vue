<script lang="ts" setup>
import { inject, onMounted, PropType, reactive, ref, watch } from "vue";
import { FormInstance, FormRules } from "element-plus";
import { ElMessage } from "element-plus";
import type { UploadProps } from "element-plus";
import { instance } from "@/axios/customAxios";
import { Action, ProductOverview } from "@/views/product/index";
import { CategoryOverview } from "@/views/category";
import { Plus } from "@element-plus/icons-vue";
import { useI18n } from "vue-i18n";
import { openMessageFail, openMessageSuccess } from "@/store/store";
const patternSpecialChars = /[!@#$%^&*(),.?":{}|<>]/; // Kiểm tra ký tự đặc biệt
const { t } = useI18n();

const productRules = reactive<FormRules<ProductOverview>>({
  name: [
    {
      required: true,
      validator(rule, value, callback, source, options) {
        if (value === "" || value === undefined) {
          callback(new Error(t("pleaseInput") + " " + t("name").toLowerCase()));
        } else if (patternSpecialChars.test(value)) {
          callback(new Error(t("noSpecialChars")));
        } else if (value.length < 5 || value.length > 25) {
          callback(new Error(t("name") + " " + t("from5To25").toLowerCase()));
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
          callback(
            new Error(t("pleaseInput") + " " + t("productCode").toLowerCase())
          );
        } else if (patternSpecialChars.test(value)) {
          callback(new Error(t("noSpecialChars")));
        } else if (value.length < 5 || value.length > 15) {
          callback(
            new Error(t("productCode") + " " + t("from5To15").toLowerCase())
          );
        } else {
          callback();
        }
      },
      trigger: ["change", "blur"],
    },
  ],
  description: [
    {
      required: true,
      validator(rule, value, callback, source, options) {
        if (value && value.length > 250) {
          callback(
            new Error(t("description") + " " + t("max250").toLowerCase())
          );
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
          callback(
            new Error(t("pleaseInput") + " " + t("price").toLowerCase())
          );
        } else if (value.length > 50) {
          callback(new Error(t("price") + " " + t("max50").toLowerCase()));
        } else if (
          isNaN(value) ||
          value <= 0 ||
          !Number.isInteger(Number(value))
        ) {
          callback(
            new Error(
              t("price") + " " + t("mustBePositivePositive").toLowerCase()
            )
          );
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
          callback(
            new Error(t("pleaseInput") + " " + t("quantity").toLowerCase())
          );
        } else if (value.length > 50) {
          callback(new Error(t("price") + " " + t("max50").toLowerCase()));
        } else if (
          isNaN(value) ||
          value < 0 ||
          !Number.isInteger(Number(value))
        ) {
          callback(
            new Error(
              t("quantity") + " " + t("mustBePositivePositive").toLowerCase()
            )
          );
        } else {
          callback();
        }
      },
      trigger: ["change", "blur"],
    },
  ],
  selectedCategoryOptions: [
    {
      required: true,
      validator(rule, value, callback) {
        console.log("OOOOOOO", value);
        console.log("OOOOOOO", selectedCategoryOptions.value);
        const valueCheck = selectedCategoryOptions.value;
        if (valueCheck.length === 0) {
          callback(
            new Error(t("pleaseInput") + " " + t("category").toLowerCase())
          );
        } else {
          // Validation passed
          callback();
        }
      },
      trigger: ["change", "blur"], // Specify when the validation should be triggered
    },
  ],
});

const props = defineProps({
  selectedObject: {
    type: Object as PropType<ProductOverview>,
    required: true,
  },
  action: {
    type: String as PropType<Action>,
    required: true,
  },
  comeBackMain: {
    type: Function,
    required: true,
  },
});

// init
const imgNotAvaiable =
  "https://th.bing.com/th/id/OIP.xHvCevAq3nc3vJv2UjgE2AHaHa?pid=ImgDet&w=205&h=205&c=7";
const categorySelectOptions = ref<{ id: number; categoryCode: string }[]>([]);
const emitter = inject<any>("emitter");
const ruleFormRef = ref<FormInstance>();
const previewImage = ref<string | null>(null);
const selectedCategoryOptions = ref<string[]>([]);
const productObject = ref<ProductOverview>({});
// func
const isDisabled = () => {
  if (props.action === "DETAIL") {
    return true;
  } else {
    return false;
  }
};

const beforeAvatarUpload: UploadProps["beforeUpload"] = (rawFile: any) => {
  // eslint-disable-next-line vue/no-mutating-props
  productObject.value.file = rawFile.raw;
  if (rawFile.raw.type !== "image/jpeg" && rawFile.raw.type !== "image/png") {
    ElMessage.error("Avatar picture must be JPG/PNG format!");
    return false;
  } else if (rawFile.raw.size / 1024 / 1024 > 2) {
    ElMessage.error("Avatar picture size can not exceed 2MB!");
    return false;
  }
  previewImage.value = URL.createObjectURL(rawFile.raw);
  return true;
};

const onSubmitCreate = async () => {
  console.log("------------------");
  console.log(productObject.value);
  const bool = await isValidateForm();
  if (bool) {
    await instance
      .post(`product`, productObject.value, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })
      .then(function (response) {
        console.log(response);
        openMessageSuccess(t("addSuccess"));
        emitter.emit("custom-event", "Hello from Component B");
      })
      .catch(function (error) {
        console.error(error);
      });
  }
};

const isValidateForm = async () => {
  if (!ruleFormRef.value) return;
  return ruleFormRef.value.validate((valid) => {
    if (valid) {
      console.log("Validate SUCCESS");
    } else {
      console.log("Validate FAIL");
    }
  });
};

const onSubmitUpdate = async () => {
  console.log("data: ", productObject);
  const bool = await isValidateForm();
  if (bool) {
    await instance
      .put(`product/${props.selectedObject.id}`, productObject.value, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })
      .then(function (response) {
        console.log(response);
        emitter.emit("custom-event", "Hello from Component B");
        openMessageSuccess(t("updateSuccess"));
      })
      .catch(function (error) {
        console.error(error);
      });
  }
};
const fetchAllCategory = async () => {
  await instance.get("category").then(function (response) {
    console.log(response);
    categorySelectOptions.value = response.data;
  });
};
const fetchProductObject = async () => {
  await instance
    .get(`product/${props.selectedObject.id}`)
    .then(function (response) {
      productObject.value = response.data;
      selectedCategoryOptions.value = (
        productObject.value.categoryCodes as string
      )
        .split(", ")
        .map((code) => code.trim());
    });
};
//hook
watch(selectedCategoryOptions, (newVal, oldVal) => {
  console.log(newVal);
  console.log(oldVal);
  productObject.value.categoryCodes = newVal;
});

onMounted(() => {
  if (props.action === Action.DETAIL || props.action === Action.UPDATE) {
    fetchProductObject();
  }
  fetchAllCategory();
});

//done js
</script>
<template>
  <div class="">
    <div>
      <el-form
        label-position="top"
        ref="ruleFormRef"
        :model="productObject"
        :rules="productRules"
        label-width="auto"
      >
        <div class="grid grid-cols-3 gap-5">
          <!--          Image-->
          <div class="col-span-1 bg-white rounded-b-md p-5 shadow-md">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :on-change="beforeAvatarUpload"
              :auto-upload="false"
              :disabled="isDisabled()"
            >
              <div v-if="action === 'CREATE'">
                <el-icon v-if="!previewImage" class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
                <img
                  class="avatar"
                  v-if="previewImage"
                  alt=""
                  :src="previewImage"
                />
              </div>
              <div v-else>
                <img
                  class="avatar"
                  v-if="previewImage"
                  alt=""
                  :src="previewImage"
                />
                <img
                  class="avatar"
                  v-else-if="productObject.image"
                  alt=""
                  :src="`http://localhost:8080/api/v1/file/${productObject.image}`"
                />
                <img class="avatar" v-else :src="imgNotAvaiable" alt="" />
              </div>
            </el-upload>
            <div class="text-[13px] grid grid-cols-2 gap-x-5">
              <div class="text-right">
                <p>{{ t("productCode") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ productObject.productCode }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("categoryCode") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ productObject.categoryCodes }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("name") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ productObject.name }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("description") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ productObject.description }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("price") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ productObject.price }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("quantity") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ productObject.quantity }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("status") }}:</p>
              </div>
              <div class="text-left">
                <p>
                  {{
                    productObject.status === "ACTIVE"
                      ? t("active")
                      : productObject.status === "INACTIVE"
                      ? t("inactive")
                      : ""
                  }}
                </p>
              </div>
              <div class="text-right">
                <p>{{ t("createdDate") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ productObject.createdDate }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("modifiedDate") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ productObject.modifiedDate }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("createdBy") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ productObject.createdBy }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("modifiedBy") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ productObject.modifiedBy }}</p>
              </div>
            </div>
          </div>
          <!--            Other-->
          <div class="col-span-2 bg-white rounded-b-md p-5 shadow-md">
            <div class="grid grid-cols-2 gap-2">
              <el-form-item :label="t('name')" prop="name">
                <el-input
                  :disabled="isDisabled()"
                  v-model="(productObject as ProductOverview).name"
                ></el-input>
              </el-form-item>
              <el-form-item :label="t('productCode')" prop="productCode">
                <el-input
                  :disabled="isDisabled()"
                  v-model="(productObject as ProductOverview).productCode"
                ></el-input>
              </el-form-item>
              <el-form-item :label="t('description')" prop="description">
                <el-input
                  :disabled="isDisabled()"
                  v-model="(productObject as ProductOverview).description"
                ></el-input>
              </el-form-item>
              <el-form-item :label="t('quantity')" prop="quantity">
                <el-input
                  type="number"
                  :disabled="isDisabled()"
                  v-model="(productObject as ProductOverview).quantity"
                ></el-input>
              </el-form-item>
              <el-form-item :label="t('price')" prop="price">
                <el-input
                  type="number"
                  :disabled="isDisabled()"
                  v-model="(productObject as ProductOverview).price"
                ></el-input>
              </el-form-item>

              <el-form-item
                :label="t('status')"
                prop="status"
                :disabled="isDisabled()"
                v-if="action !== 'CREATE'"
              >
                <el-select
                  v-model="(productObject as CategoryOverview).status"
                  :disabled="isDisabled()"
                >
                  <el-option value="ACTIVE" :label="t('active')"
                    >{{ t("active") }}
                  </el-option>
                  <el-option value="INACTIVE" :label="t('inActive')"
                    >{{ t("inActive") }}
                  </el-option>
                </el-select>
              </el-form-item>

              <el-form-item
                :label="t('createdDate')"
                prop="createdDate"
                v-if="action === 'DETAIL'"
              >
                <el-input
                  :disabled="isDisabled()"
                  v-model="(productObject as ProductOverview).createdDate"
                ></el-input>
              </el-form-item>

              <el-form-item
                :label="t('modifiedDate')"
                prop="modifiedDate"
                v-if="action === 'DETAIL'"
              >
                <el-input
                  :disabled="isDisabled()"
                  v-model="(productObject as ProductOverview).modifiedDate"
                ></el-input>
              </el-form-item>

              <el-form-item
                :label="t('createdBy')"
                prop="createdBy"
                v-if="action === 'DETAIL'"
              >
                <el-input
                  :disabled="isDisabled()"
                  v-model="(productObject as ProductOverview).createdBy"
                ></el-input>
              </el-form-item>

              <el-form-item
                :label="t('modifiedBy')"
                prop="modifiedBy"
                v-if="action === 'DETAIL'"
              >
                <el-input
                  :disabled="isDisabled()"
                  v-model="(productObject as ProductOverview).modifiedBy"
                ></el-input>
              </el-form-item>

              <el-form-item
                class="col-span-2"
                :label="t('category')"
                prop="selectedCategoryOptions"
              >
                <el-select
                  class="!w-full"
                  v-model="selectedCategoryOptions"
                  multiple
                  :placeholder="t('select')"
                  style="width: 240px"
                  :disabled="isDisabled()"
                  filterable
                >
                  <el-option
                    v-for="(item, index) in categorySelectOptions"
                    :key="index"
                    :label="item.categoryCode"
                    :value="item.categoryCode"
                  />
                </el-select>
              </el-form-item>
            </div>
            <div class="!flex !justify-end gap-5 py-5">
              <el-form-item>
                <el-button @click="comeBackMain">{{ t("back") }}</el-button>
              </el-form-item>
              <el-form-item>
                <el-button v-if="action === 'CREATE'" @click="onSubmitCreate"
                  >{{ t("create") }}
                </el-button>
                <el-button v-if="action === 'UPDATE'" @click="onSubmitUpdate"
                  >{{ t("update") }}
                </el-button>
              </el-form-item>
            </div>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>
<style scoped>
.avatar-uploader .avatar {
  width: 278px;
  height: 278px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 278px;
  height: 278px;
  text-align: center;
}
</style>
