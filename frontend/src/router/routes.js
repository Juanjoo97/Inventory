const routes = [
  {
    path: '/login',
    component: () => import('pages/LoginPage.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/empresas' },
      {
        path: 'empresas',
        component: () => import('pages/EmpresasPage.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'productos',
        component: () => import('pages/ProductosPage.vue'),
        meta: { requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'inventario',
        component: () => import('pages/InventarioPage.vue'),
        meta: { requiresAuth: true, roles: ['ADMIN'] }
      }
    ]
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
];

export default routes;
