import { boot } from 'quasar/wrappers';
import axios from 'axios';

// Instancia central de Axios apuntando al backend

const api = axios.create({
  baseURL: process.env.API_URL
});

export default boot(({ app, router }) => {
  // Interceptor de request: adjunta el JWT si existe
  api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  // Interceptor de response: si el token expira (401), limpia sesion y redirige a login
  api.interceptors.response.use(
    (response) => response,
    (error) => {
      const status = error?.response?.status;
      if (status === 401 && router.currentRoute.value.path !== '/login') {
        localStorage.removeItem('token');
        localStorage.removeItem('email');
        localStorage.removeItem('rol');
        router.push('/login');
      }
      return Promise.reject(error);
    }
  );

  app.config.globalProperties.$api = api;
});

export { api };
