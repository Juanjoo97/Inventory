import { defineStore } from 'pinia';
import { api } from 'boot/axios';

export const useProductosStore = defineStore('productos', {
  state: () => ({
    items: [],
    categorias: [],
    loading: false
  }),

  actions: {
    async fetchAll(empresaNit) {
      this.loading = true;
      try {
        const params = empresaNit ? { empresaNit } : {};
        const { data } = await api.get('/api/productos', { params });
        this.items = data;
      } finally {
        this.loading = false;
      }
    },

    async fetchCategorias() {
      const { data } = await api.get('/api/categorias');
      this.categorias = data;
    },

    async create(payload) {
      const { data } = await api.post('/api/productos', payload);
      this.items.push(data);
      return data;
    },

    async update(id, payload) {
      const { data } = await api.put(`/api/productos/${id}`, payload);
      const idx = this.items.findIndex((p) => p.id === id);
      if (idx !== -1) this.items.splice(idx, 1, data);
      return data;
    },

    async remove(id) {
      await api.delete(`/api/productos/${id}`);
      this.items = this.items.filter((p) => p.id !== id);
    }
  }
});
