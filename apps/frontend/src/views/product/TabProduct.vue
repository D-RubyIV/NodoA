<script lang="ts" setup>
import { markRaw, provide, ref } from "vue";
import { Action, ProductOverview } from "@/views/product/index";
import TableProduct from "@/views/product/TableProduct.vue";
import ViewForm from "@/views/product/ViewForm.vue";
import { useI18n } from "vue-i18n";
import mitt from "mitt";

type Tab = {
  title: string;
  name: string;
  content: any;
  props?: any;
  ref?: any;
  isClose: boolean;
};

let tabIndex = 2;
const editableTabsValue = ref("1");

const emitter = mitt();
provide("emitter", emitter);

const addTabForm = (selectedObject: ProductOverview, action: Action) => {
  const newTabName = `${++tabIndex}`;
  editableTabs.value.push({
    title:
      action === "CREATE"
        ? t("create") + " " + t("product").toLowerCase()
        : action === "UPDATE"
        ? t("update") + " " + t("product").toLowerCase()
        : t("detail") + " " + t("product").toLowerCase(),
    name: newTabName,
    content: markRaw(ViewForm),
    props: {
      selectedObject,
      action,
      comeBackMain,
    },
    isClose: true,
  });
  editableTabsValue.value = newTabName;
};

const comeBackMain = () => {
  editableTabsValue.value = "1";
};

const { t, locale } = useI18n(); // Lấy hàm t và locale từ i18n
const editableTabs = ref<Tab[]>([
  {
    title: t("listProduct"),
    name: "1",
    content: markRaw(TableProduct),
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
