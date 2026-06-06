<template>
  <q-layout view="lHh Lpr lFf">
    <q-header class="bg-white text-dark" bordered>
      <q-toolbar class="q-px-md" style="min-height: 64px">
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleDrawer" class="lt-muted" />

        <div class="row items-center q-ml-sm no-wrap">
          <div class="lt-brand-badge" style="width: 34px; height: 34px; border-radius: 9px; font-size: 13px">LT</div>
          <div class="q-ml-sm">
            <div class="text-display text-weight-bold" style="line-height: 1.1">Lite Thinking</div>
            <div class="text-caption text-grey-6" style="line-height: 1">Inventory</div>
          </div>
        </div>

        <q-space />

        <q-chip
          :color="auth.isAdmin ? 'primary' : 'secondary'"
          text-color="white"
          size="sm"
          class="text-weight-medium"
        >
          {{ auth.rol }}
        </q-chip>

        <q-btn flat round dense class="q-ml-sm">
          <q-avatar size="34px" color="secondary" text-color="white" class="text-weight-bold">
            {{ auth.iniciales }}
          </q-avatar>
          <q-menu anchor="bottom right" self="top right">
            <div class="q-pa-md" style="min-width: 220px">
              <div class="text-caption text-grey-6">Sesion iniciada como</div>
              <div class="text-weight-medium ellipsis">{{ auth.email }}</div>
            </div>
            <q-separator />
            <q-list>
              <q-item clickable v-close-popup @click="cerrarSesion">
                <q-item-section avatar><q-icon name="logout" color="negative" /></q-item-section>
                <q-item-section>Cerrar sesion</q-item-section>
              </q-item>
            </q-list>
          </q-menu>
        </q-btn>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="drawer" show-if-above bordered :width="260" class="bg-white">
      <q-list padding class="q-pt-md">
        <q-item-label header class="text-grey-6 text-uppercase" style="font-size: 11px; letter-spacing: 0.06em">
          Navegacion
        </q-item-label>

        <q-item
          v-for="link in visibleLinks"
          :key="link.to"
          :to="link.to"
          clickable
          active-class="lt-active-link"
          class="rounded-borders q-mx-sm q-mb-xs"
        >
          <q-item-section avatar><q-icon :name="link.icon" /></q-item-section>
          <q-item-section>{{ link.label }}</q-item-section>
        </q-item>
      </q-list>

      <template v-if="!auth.isAdmin">
        <q-separator class="q-my-md" />
        <div class="q-px-md text-caption text-grey-6">
          Tu rol <b>EXTERNO</b> permite visualizar empresas en modo lectura.
        </div>
      </template>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from 'stores/auth';

const auth = useAuthStore();
const router = useRouter();
const drawer = ref(false);

const allLinks = [
  { label: 'Empresas', icon: 'apartment', to: '/empresas', adminOnly: false },
  { label: 'Productos', icon: 'inventory_2', to: '/productos', adminOnly: true },
  { label: 'Inventario', icon: 'receipt_long', to: '/inventario', adminOnly: true }
];

const visibleLinks = computed(() =>
  allLinks.filter((l) => !l.adminOnly || auth.isAdmin)
);

function toggleDrawer() {
  drawer.value = !drawer.value;
}

function cerrarSesion() {
  auth.logout();
  router.push('/login');
}
</script>

<style scoped>
.lt-active-link {
  background: rgba(13, 122, 111, 0.10);
  color: #0d7a6f;
  font-weight: 600;
}
</style>
