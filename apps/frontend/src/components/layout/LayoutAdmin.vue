<script lang="ts" setup>
import { markRaw, ref } from "vue";
import { Fries, House, Setting, TakeawayBox } from "@element-plus/icons-vue";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";

const { t, locale } = useI18n(); // Lấy hàm t và locale từ i18n
const selectNavbar = ref("");

const routeApp = useRoute();
console.log(routeApp.path);

const onChangeLang = (lang: string) => {
  localStorage.setItem("lang", lang);
  locale.value = lang;
  window.location.reload();
};

const items = [
  {
    label: t("homePage"),
    value: "/admin/dashboard",
    icon: markRaw(House),
  },
  {
    label: t("category"),
    value: "/admin/category",
    icon: markRaw(TakeawayBox),
  },
  {
    label: t("product"),
    value: "/admin/product",
    icon: markRaw(Fries),
  },
];
</script>
<template>
  <el-container class="layout-container-demo" style="height: 100vh">
    <el-aside width="200px" class="!bg-blue-950">
      <div class="!flex !flex-col !justify-between h-full">
        <div>
          <el-menu class="!bg-blue-950 text-white">
            <div class="text-[25px] py-5 font-semibold">NODO</div>
            <hr />
            <router-link
              :to="item.value"
              v-for="(item, index) in items"
              :key="index"
              :class="
                routeApp.path === item.value ? 'bg-blue-500 text-white' : ''
              "
              @click="selectNavbar = item.value"
              class="w-full !h-full p-8 inline-block font-semibold hover:bg-blue-300"
            >
              <div class="flex gap-3">
                <p>
                  <el-icon>
                    <component :is="item.icon"></component>
                  </el-icon>
                </p>
                <p>{{ item.label }}</p>
              </div>
            </router-link>
          </el-menu>
        </div>
        <div class="py-5 flex gap-5 justify-center text-white">
          <div>
            <button
              @click="onChangeLang('en')"
              :class="locale === 'en' ? 'underline underline-offset-2' : ''"
            >
              EN
            </button>
          </div>
          <div>
            <button
              @click="onChangeLang('vi')"
              :class="locale === 'vi' ? 'underline underline-offset-2' : ''"
            >
              VI
            </button>
          </div>
        </div>
      </div>
    </el-aside>

    <el-container>
      <el-header style="text-align: right; font-size: 12px" class="!bg-white">
        <div class="toolbar">
          <el-dropdown>
            <el-icon style="margin-right: 8px; margin-top: 1px">
              <setting />
            </el-icon>
            <template #dropdown>
              <el-dropdown-menu> </el-dropdown-menu>
            </template>
          </el-dropdown>
          <span>PHAH04</span>
        </div>
      </el-header>

      <el-main>
        <div class="p-10 bg-gray-100 h-full">
          <router-view></router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container-demo .el-header {
  position: relative;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}
.layout-container-demo .el-aside {
  color: var(--el-text-color-primary);
  background: var(--el-color-primary-light-8);
}
.layout-container-demo .el-menu {
  border-right: none;
}
.layout-container-demo .el-main {
  padding: 0;
}
.layout-container-demo .toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  right: 20px;
}

#navbar.is-active {
  background-color: #409eff; /* Màu nền khi mục menu đang hoạt động */
  color: #fff; /* Màu văn bản khi mục menu đang hoạt động */
}
</style>
