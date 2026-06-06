import { route } from 'quasar/wrappers';
import {
  createRouter,
  createMemoryHistory,
  createWebHistory,
  createWebHashHistory
} from 'vue-router';
import routes from './routes';
import { useAuthStore } from 'stores/auth';

export default route(function () {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : process.env.VUE_ROUTER_MODE === 'history'
      ? createWebHistory
      : createWebHashHistory;

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,
    history: createHistory(process.env.VUE_ROUTER_BASE)
  });

  // Guard global: autenticacion + autorizacion por rol
  Router.beforeEach((to) => {
    const auth = useAuthStore();

    if (to.meta.requiresAuth && !auth.isAuthenticated) {
      return { path: '/login', query: { redirect: to.fullPath } };
    }

    if (to.meta.roles && !to.meta.roles.includes(auth.rol)) {
      // Sin permiso para esta ruta -> lo enviamos a la vista permitida
      return { path: '/empresas' };
    }

    if (to.path === '/login' && auth.isAuthenticated) {
      return { path: '/empresas' };
    }

    return true;
  });

  return Router;
});
