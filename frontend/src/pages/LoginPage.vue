<template>
  <main class="lt-auth-bg row items-center justify-center q-pa-md">
    <q-card class="lt-auth-card bg-white q-pa-lg">
      <div class="row items-center q-mb-lg">
        <div class="lt-brand-badge">LT</div>
        <div class="q-ml-md">
          <div class="text-display text-h6 text-weight-bold" style="line-height: 1.1">Lite Thinking</div>
          <div class="text-caption text-grey-7">Sistema de Inventario</div>
        </div>
      </div>

      <div class="text-display text-h5 q-mb-xs">Bienvenido</div>
      <div class="text-body2 text-grey-7 q-mb-lg">Ingresa tus credenciales para continuar</div>

      <q-form @submit.prevent="onSubmit" class="q-gutter-md">
        <q-input v-model="email" type="email" label="Correo electronico" outlined
          :rules="[(v) => !!v || 'El correo es requerido']" lazy-rules>
          <template #prepend><q-icon :name="matMailOutline" /></template>
        </q-input>

        <q-input v-model="password" :type="showPassword ? 'text' : 'password'" label="Contraseña" outlined
          :rules="[(v) => !!v || 'La contraseña es requerida']" lazy-rules>
          <template #prepend><q-icon :name="matLock" /></template>
          <template #append>
            <q-btn flat round dense :icon="showPassword ? matVisibility : matVisibilityOff"
              :aria-label="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
              @click="showPassword = !showPassword" />
          </template>
        </q-input>

        <q-btn type="submit" label="Iniciar sesion" color="primary" class="full-width q-py-sm text-weight-bold"
          unelevated no-caps :loading="loading" />
      </q-form>

      <q-separator class="q-my-md" />

      <div class="text-caption text-grey-7 q-mb-xs">Credenciales de prueba</div>
      <div class="row q-col-gutter-sm">
        <div class="col-6">
          <q-btn outline color="primary" no-caps size="sm" class="full-width" label="Admin"
            @click="usarDemo('admin')" />
        </div>
        <div class="col-6">
          <q-btn outline color="secondary" no-caps size="sm" class="full-width" label="Externo"
            @click="usarDemo('externo')" />
        </div>
      </div>
    </q-card>
  </main>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useQuasar } from 'quasar';
import { matMailOutline, matLock, matVisibility, matVisibilityOff } from '@quasar/extras/material-icons';
import { useAuthStore } from 'stores/auth';

const auth = useAuthStore();
const router = useRouter();
const route = useRoute();
const $q = useQuasar();

const email = ref('');
const password = ref('');
const showPassword = ref(false);
const loading = ref(false);

function usarDemo(tipo) {
  if (tipo === 'admin') {
    email.value = 'admin@litethinking.com';
    password.value = 'Admin123*';
  } else {
    email.value = 'externo@litethinking.com';
    password.value = 'Externo123*';
  }
}

async function onSubmit() {
  loading.value = true;
  try {
    await auth.login(email.value, password.value);
    $q.notify({ type: 'positive', message: 'Sesion iniciada correctamente' });
    const redirect = route.query.redirect || '/empresas';
    router.push(redirect);
  } catch (err) {
    const msg = err?.response?.data?.message || 'No fue posible iniciar sesion';
    $q.notify({ type: 'negative', message: msg });
  } finally {
    loading.value = false;
  }
}
</script>