import { defineStore } from 'pinia';
import { api } from 'boot/axios';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    email: localStorage.getItem('email') || null,
    rol: localStorage.getItem('rol') || null
  }),

  getters: {
    isAuthenticated: (s) => !!s.token,
    isAdmin: (s) => s.rol === 'ADMIN',
    iniciales: (s) => (s.email ? s.email.substring(0, 2).toUpperCase() : '?')
  },

  actions: {
    async login(email, password) {
      const { data } = await api.post('/api/auth/login', { email, password });
      this.token = data.token;
      this.email = data.email;
      this.rol = data.rol;
      localStorage.setItem('token', data.token);
      localStorage.setItem('email', data.email);
      localStorage.setItem('rol', data.rol);
      return data;
    },

    logout() {
      this.token = null;
      this.email = null;
      this.rol = null;
      localStorage.removeItem('token');
      localStorage.removeItem('email');
      localStorage.removeItem('rol');
    }
  }
});
