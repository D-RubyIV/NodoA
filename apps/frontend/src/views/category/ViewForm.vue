<script lang="ts" setup>
import { onMounted, PropType, reactive, ref } from "vue";
import { FormInstance, FormRules } from "element-plus";
import { Action, CategoryOverview } from "@/views/category/index";
import { ElMessage } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import type { UploadProps } from "element-plus";
import { instance } from "@/axios/customAxios";
const { t } = useI18n();
const patternSpecialChars = /[!@#$%^&*(),.?":{}|<>]/; // Kiểm tra ký tự đặc biệt

const imgNotAvaiable =
  "https://th.bing.com/th/id/OIP.xHvCevAq3nc3vJv2UjgE2AHaHa?pid=ImgDet&w=205&h=205&c=7";

const categoryObject = ref<CategoryOverview>({});
const emit = defineEmits(["editIsOpen:isOpen", "reloadTable"]);
import { inject } from "vue";
import { useI18n } from "vue-i18n";
import { openMessageFail, openMessageSuccess } from "@/store/store";
const emitter = inject<any>("emitter");
const ruleFormRef = ref<FormInstance>();
const previewImage = ref<string | null>(null);

const props = defineProps({
  selectedObject: {
    type: Object as PropType<CategoryOverview>,
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

const categoryRules = reactive<FormRules<CategoryOverview>>({
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
  categoryCode: [
    {
      required: true,
      validator(rule, value, callback, source, options) {
        if (value === "" || value === undefined) {
          callback(
            new Error(t("pleaseInput") + " " + t("description").toLowerCase())
          );
        } else if (patternSpecialChars.test(value)) {
          callback(new Error(t("noSpecialChars")));
        } else if (value.length < 5 || value.length > 15) {
          callback(
            new Error(t("categoryCode") + " " + t("from5To15").toLowerCase())
          );
        }
        callback();
      },
      trigger: ["change", "blur"],
    },
  ],
  // description: [
  //   {
  //     required: true,
  //     validator(rule, value, callback, source, options) {
  //       if (value === "" || value === undefined) {
  //         callback(
  //           new Error(t("pleaseInput") + " " + t("description").toLowerCase())
  //         );
  //       } else if (value.length < 5 || value.length > 250) {
  //         callback(
  //           new Error(t("description") + " " + t("from5To250").toLowerCase())
  //         );
  //       } else {
  //         callback();
  //       }
  //     },
  //     trigger: ["change", "blur"],
  //   },
  // ],
});

const isDisabled = () => {
  console.log(props.action);
  if (props.action === "DETAIL") {
    return true;
  } else {
    return false;
  }
};

const beforeAvatarUpload: UploadProps["beforeUpload"] = (rawFile: any) => {
  // eslint-disable-next-line vue/no-mutating-props
  categoryObject.value.file = rawFile.raw;

  if (rawFile.raw.type !== "image/jpeg" && rawFile.raw.type !== "image/png") {
    ElMessage.error("Avatar picture must be JPG/PNG format!");
    return false;
  } else if (rawFile.raw.size / 1024 / 1024 > 2) {
    ElMessage.error("Avatar picture size can not exceed 2MB!");
    return false;
  }

  // Tạo URL tạm thời từ tệp đã chọn
  previewImage.value = URL.createObjectURL(rawFile.raw);

  return true;
};

const onSubmitCreate = async () => {
  console.log(categoryObject);
  const bool = await isValidateForm();
  if (bool) {
    await instance
      .post(`category`, categoryObject.value, {
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
  console.log(categoryObject);
  const bool = await isValidateForm();
  if (bool) {
    await instance
      .put(`category/${props.selectedObject.id}`, categoryObject.value, {
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
        openMessageFail(t("updateFail"));
        console.error(error);
      });
  }
};
const fetchCategoryObject = async () => {
  await instance
    .get(`category/${props.selectedObject.id}`)
    .then(function (response) {
      categoryObject.value = response.data;
    });
};
const childMethod = () => {
  console.log("Hàm của component con đã được gọi!");
};

defineExpose({ childMethod });

onMounted(() => {
  if (props.action === Action.DETAIL || props.action === Action.UPDATE) {
    fetchCategoryObject();
  }
});
</script>
<template>
  <div class="">
    <div>
      <el-form
        ref="ruleFormRef"
        :model="categoryObject"
        :rules="categoryRules"
        label-width="auto"
        label-position="top"
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
                <el-icon v-if="!previewImage" class="avatar-uploader-icon"
                  ><Plus
                /></el-icon>
                <img
                  v-if="previewImage"
                  alt=""
                  :src="previewImage"
                  class="avatar"
                />
              </div>
              <div v-else>
                <img
                  v-if="previewImage"
                  alt=""
                  :src="previewImage"
                  class="avatar"
                />
                <img
                  class="avatar"
                  v-else-if="categoryObject.image"
                  alt=""
                  :src="`http://localhost:8080/api/v1/file/${categoryObject.image}`"
                />
                <img class="avatar" v-else :src="imgNotAvaiable" alt="" />
              </div>
            </el-upload>
            <div class="text-[13px] grid grid-cols-2 gap-x-5">
              <div class="text-right">
                <p>{{ t("categoryCode") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ categoryObject.categoryCode }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("name") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ categoryObject.name }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("description") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ categoryObject.description }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("status") }}:</p>
              </div>
              <div class="text-left">
                <p>
                  {{
                    categoryObject.status === "ACTIVE"
                      ? t("active")
                      : categoryObject.status === "INACTIVE"
                      ? t("inactive")
                      : ""
                  }}
                </p>
              </div>
              <div class="text-right">
                <p>{{ t("createdDate") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ categoryObject.createdDate }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("modifiedDate") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ categoryObject.modifiedDate }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("createdBy") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ categoryObject.createdBy }}</p>
              </div>
              <div class="text-right">
                <p>{{ t("modifiedBy") }}:</p>
              </div>
              <div class="text-left">
                <p>{{ categoryObject.modifiedBy }}</p>
              </div>
            </div>
          </div>
          <!--            Other-->
          <div class="col-span-2 bg-white rounded-b-md p-5 shadow-md">
            <div
              class="grid gap-5"
              :class="action === 'DETAIL' ? 'grid-cols-2' : ''"
            >
              <el-form-item prop="name" :label="t('name')">
                <el-input
                  :disabled="isDisabled()"
                  v-model="(categoryObject as CategoryOverview).name"
                ></el-input>
              </el-form-item>
              <el-form-item
                :label="t('categoryCode')"
                prop="categoryCode"
                v-if="action === 'CREATE' || action === 'DETAIL'"
              >
                <el-input
                  :disabled="isDisabled()"
                  v-model="(categoryObject as CategoryOverview).categoryCode"
                ></el-input>
              </el-form-item>
              <el-form-item :label="t('description')" prop="description">
                <el-input
                  :disabled="isDisabled()"
                  v-model="(categoryObject as CategoryOverview).description"
                ></el-input>
              </el-form-item>

              <el-form-item
                :label="t('status')"
                prop="status"
                v-if="action !== 'CREATE'"
              >
                <el-select
                  v-model="(categoryObject as CategoryOverview).status"
                  :disabled="isDisabled()"
                >
                  <el-option value="ACTIVE" :label="t('active')">{{
                    t("active")
                  }}</el-option>
                  <el-option value="INACTIVE" :label="t('inActive')">{{
                    t("inActive")
                  }}</el-option>
                </el-select>
              </el-form-item>
              <el-form-item
                :label="t('createdDate')"
                prop="createdDate"
                v-if="action === 'DETAIL'"
              >
                <el-input
                  :disabled="isDisabled()"
                  v-model="(categoryObject as CategoryOverview).createdDate"
                ></el-input>
              </el-form-item>
              <el-form-item
                :label="t('modifiedDate')"
                prop="modifiedDate"
                v-if="action === 'DETAIL'"
              >
                <el-input
                  :disabled="isDisabled()"
                  v-model="(categoryObject as CategoryOverview).modifiedDate"
                ></el-input>
              </el-form-item>
              <el-form-item
                :label="t('createdBy')"
                prop="createdBy"
                v-if="action === 'DETAIL'"
              >
                <el-input
                  :disabled="isDisabled()"
                  v-model="(categoryObject as CategoryOverview).createdBy"
                ></el-input>
              </el-form-item>
              <el-form-item
                :label="t('modifiedBy')"
                prop="modifiedBy"
                v-if="action === 'DETAIL'"
              >
                <el-input
                  :disabled="isDisabled()"
                  v-model="(categoryObject as CategoryOverview).modifiedBy"
                ></el-input>
              </el-form-item>
            </div>
            <div class="!flex !justify-end gap-5 py-5">
              <el-form-item>
                <el-button @click="comeBackMain">{{ t("back") }}</el-button>
              </el-form-item>
              <el-form-item>
                <el-button v-if="action === 'CREATE'" @click="onSubmitCreate">{{
                  t("create")
                }}</el-button>
                <el-button v-if="action === 'UPDATE'" @click="onSubmitUpdate">{{
                  t("update")
                }}</el-button>
              </el-form-item>
            </div>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>
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
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>
