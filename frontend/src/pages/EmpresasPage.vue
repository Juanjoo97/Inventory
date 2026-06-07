<template>
  <q-page class="q-pa-lg">
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="lt-page-title">Empresas</div>
        <div class="text-body2 text-grey-8">
          {{ auth.isAdmin ? 'Gestiona las empresas registradas' : 'Listado de empresas (solo lectura)' }}
        </div>
      </div>
      <q-btn v-if="auth.isAdmin" color="primary" :icon="matAdd" label="Nueva empresa" no-caps unelevated
        @click="abrirCrear" />
    </div>

    <q-card flat class="lt-card">
      <q-card-section class="q-pb-none">
        <q-input v-model="filtro" dense outlined placeholder="Buscar por nombre o NIT..." clearable
          style="max-width: 320px">
          <template #prepend><q-icon :name="matSearch" /></template>
        </q-input>
      </q-card-section>

      <q-card-section>
        <q-table :rows="store.items" :columns="columns" row-key="nit" :loading="store.loading" :filter="filtro" flat
          class="lt-table" :rows-per-page-options="[10, 20, 50]" no-data-label="No hay empresas registradas">
          <template #body-cell-nit="props">
            <q-td :props="props">
              <q-chip square dense color="grey-2" text-color="dark" class="lt-chip-mono">{{ props.value }}</q-chip>
            </q-td>
          </template>

          <template #body-cell-acciones="props">
            <q-td :props="props" class="text-right">
              <template v-if="auth.isAdmin">
                <q-btn flat round dense :icon="matEdit" color="primary" aria-label="Editar"
                  @click="abrirEditar(props.row)">
                  <q-tooltip>Editar</q-tooltip>
                </q-btn>
                <q-btn flat round dense :icon="matDelete" color="negative" aria-label="Eliminar"
                  @click="confirmarEliminar(props.row)">
                  <q-tooltip>Eliminar</q-tooltip>
                </q-btn>
              </template>
              <span v-else class="text-grey-6">—</span>
            </q-td>
          </template>
        </q-table>
      </q-card-section>
    </q-card>

    <EmpresaFormDialog v-model="showDialog" :empresa="empresaSeleccionada" @saved="onSaved" />
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { matAdd, matSearch, matEdit, matDelete } from '@quasar/extras/material-icons';
import { useEmpresasStore } from 'stores/empresas';
import { useAuthStore } from 'stores/auth';
import EmpresaFormDialog from 'components/EmpresaFormDialog.vue';

const store = useEmpresasStore();
const auth = useAuthStore();
const $q = useQuasar();

const filtro = ref('');
const showDialog = ref(false);
const empresaSeleccionada = ref(null);

const baseColumns = [
  { name: 'nit', label: 'NIT', field: 'nit', align: 'left', sortable: true },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left', sortable: true },
  { name: 'direccion', label: 'Direccion', field: 'direccion', align: 'left' },
  { name: 'telefono', label: 'Telefono', field: 'telefono', align: 'left' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'right' }
];
const columns = baseColumns;

function abrirCrear() {
  empresaSeleccionada.value = null;
  showDialog.value = true;
}

function abrirEditar(empresa) {
  empresaSeleccionada.value = { ...empresa };
  showDialog.value = true;
}

function onSaved() {
  showDialog.value = false;
}

function confirmarEliminar(empresa) {
  $q.dialog({
    title: 'Eliminar empresa',
    message: `Eliminar "${empresa.nombre}" (NIT ${empresa.nit})? Esta accion no se puede deshacer.`,
    cancel: { label: 'Cancelar', flat: true, noCaps: true },
    ok: { label: 'Eliminar', color: 'negative', unelevated: true, noCaps: true },
    persistent: true
  }).onOk(async () => {
    try {
      await store.remove(empresa.nit);
      $q.notify({ type: 'positive', message: 'Empresa eliminada' });
    } catch (err) {
      $q.notify({ type: 'negative', message: err?.response?.data?.message || 'No se pudo eliminar' });
    }
  });
}

onMounted(() => store.fetchAll());
</script>