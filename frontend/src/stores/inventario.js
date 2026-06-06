import { defineStore } from 'pinia';
import { api } from 'boot/axios';

export const useInventarioStore = defineStore('inventario', {
  state: () => ({
    items: [],
    loading: false
  }),

  actions: {
    async fetchAll() {
      this.loading = true;
      try {
        const { data } = await api.get('/api/inventario');
        this.items = data;
      } finally {
        this.loading = false;
      }
    },

    async descargarPdf() {
      const response = await api.get('/api/inventario/pdf', { responseType: 'blob' });
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'inventario.pdf';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    },

    async enviarPorCorreo(email) {
      const { data } = await api.post('/api/inventario/enviar', { email });
      return data;
    }
  }
});
