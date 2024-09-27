import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import LayoutAdmin from "@/components/layout/LayoutAdmin.vue";
import CategoryTab from "@/views/category/CategoryTab.vue";
import Dashboard from "@/views/home/HomeView.vue";
import ProductTab from "@/views/product/TabProduct.vue";
import DashboardView from "@/views/dashboard/DashboardView.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "home",
    component: Dashboard,
  },
  {
    path: "/admin",
    name: "admin",
    component: LayoutAdmin,
    children: [
      {
        path: "dashboard",
        component: DashboardView,
        name: "dashboardTab",
      },
      {
        path: "product",
        component: ProductTab,
        name: "ProductTab",
      },
      {
        path: "category",
        component: CategoryTab,
        name: "CategoryTab",
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
