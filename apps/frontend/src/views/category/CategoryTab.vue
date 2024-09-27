<script lang="ts" setup>
import { markRaw, provide, ref } from "vue";
import TableCategory from "@/views/category/TableCategory.vue";
import ViewForm from "@/views/category/ViewForm.vue";
import { Action, CategoryOverview } from "@/views/category/index";
import mitt from "mitt";
import { useI18n } from "vue-i18n";
import { Custom } from "@/axios/customAxios";

type Tab = {
  title: string;
  name: string;
  content: any;
  props?: any;
  ref?: any;
  isClose: boolean;
};

const { t } = useI18n();
let tabIndex = 2;
const editableTabsValue = ref("1");

const emitter = mitt();
provide("emitter", emitter);

const addTabForm = (selectedObject: CategoryOverview, action: Action) => {
  const newTabName = `${++tabIndex}`;
  editableTabs.value.push({
    title:
      action === "CREATE"
        ? t("create") + " " + t("category").toLowerCase()
        : action === "UPDATE"
        ? t("update") + " " + t("category").toLowerCase()
        : t("detail") + " " + t("category").toLowerCase(),
    name: newTabName,
    content: markRaw(ViewForm),
    props: {
      selectedObject, // Truyền đối tượng được chọn từ tham số
      action, // Truyền action từ tham số
      comeBackMain,
    }, // Đảm bảo kiểu phù hợp với interface đã định nghĩa
    isClose: true,
  });
  editableTabsValue.value = newTabName;
};

const comeBackMain = () => {
  editableTabsValue.value = "1";
};

const editableTabs = ref<Tab[]>([
  {
    title: t("listCategory"),
    name: "1",
    content: markRaw(TableCategory),
    props: {
      addTabForm: markRaw(addTabForm),
    },
    isClose: false,
  },
]);

const removeTab = (targetName: string) => {
  const tabs = editableTabs.value;
  let activeName = editableTabsValue.value;
  console.log(activeName);
  if (activeName === targetName) {
    tabs.forEach((tab, index) => {
      if (tab.name === targetName) {
        const nextTab = tabs[index + 1] || tabs[index - 1];
        if (nextTab) {
          activeName = nextTab.name;
        }
      }
    });
  }

  editableTabsValue.value = activeName;
  editableTabs.value = tabs.filter((tab) => tab.name !== targetName);
};
</script>

<template>
  <el-tabs
    v-model="editableTabsValue"
    type="card"
    class="demo-tabs"
    @tab-remove="removeTab"
  >
    <el-tab-pane
      v-for="item in editableTabs"
      :key="item.name"
      :label="item.title"
      :name="item.name"
      :closable="item.isClose"
    >
      <component :is="item.content" v-bind="item.props" />
    </el-tab-pane>
  </el-tabs>
</template>

<style>
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}
</style>
