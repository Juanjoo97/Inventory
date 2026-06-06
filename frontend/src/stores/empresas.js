import { defineStore } from 'pinia';
import { api } from 'boot/axios';

export const useEmpresasStore = defineStore('empresas', {
  state: () => ({
    items: [],
    loading: false
  }),

  actions: {
    async fetchAll() {
      this.loading = true;
      try {
        const { data } = await api.get('/api/empresas');
        this.items = data;
      } finally {
        this.loading = false;
      }
    },

    async create(payload) {
      const { data } = await api.post('/api/empresas', payload);
      this.items.push(data);
      return data;
    },

    async update(nit, payload) {
      const { data } = await api.put(`/api/empresas/${nit}`, payload);
      const idx = this.items.findIndex((e) => e.nit === nit);
      if (idx !== -1) this.items.splice(idx, 1, data);
      return data;
    },

    async remove(nit) {
      await api.delete(`/api/empresas/${nit}`);
      this.items = this.items.filter((e) => e.nit !== nit);
    }
  }
});
