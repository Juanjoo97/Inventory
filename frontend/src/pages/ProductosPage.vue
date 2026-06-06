<template>
  <q-page class="q-pa-lg">
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="lt-page-title">Productos</div>
        <div class="text-body2 text-grey-6">Gestiona los productos y sus precios por moneda</div>
      </div>
      <q-btn color="primary" icon="add" label="Nuevo producto" no-caps unelevated @click="abrirCrear" />
    </div>

    <q-card flat class="lt-card">
      <q-card-section class="row q-col-gutter-md items-center">
        <div class="col-12 col-sm-5">
          <q-select
            v-model="empresaFiltro"
            :options="opcionesEmpresa"
            label="Filtrar por empresa"
            outlined
            dense
            clearable
            emit-value
            map-options
            @update:model-value="aplicarFiltro"
          />
        </div>
        <div class="col-12 col-sm-4">
          <q-input v-model="busqueda" dense outlined placeholder="Buscar producto..." clearable>
            <template #prepend><q-icon name="search" /></template>
          </q-input>
        </div>
      </q-card-section>

      <q-card-section class="q-pt-none">
        <q-table
          :rows="store.items"
          :columns="columns"
          row-key="id"
          :loading="store.loading"
          :filter="busqueda"
          flat
          class="lt-table"
          :rows-per-page-options="[10, 20, 50]"
          no-data-label="No hay productos"
        >
          <template #body-cell-codigo="props">
            <q-td :props="props">
              <q-chip square dense color="grey-2" text-color="dark" class="lt-chip-mono">{{ props.value }}</q-chip>
            </q-td>
          </template>

          <template #body-cell-categorias="props">
            <q-td :props="props">
              <q-chip
                v-for="cat in props.row.categorias"
                :key="cat"
                dense
                size="sm"
                color="teal-1"
                text-color="teal-9"
              >{{ cat }}</q-chip>
              <span v-if="!props.row.categorias.length" class="text-grey-5">—</span>
            </q-td>
          </template>

          <template #body-cell-precios="props">
            <q-td :props="props">
              <div v-for="p in props.row.precios" :key="p.moneda" class="lt-chip-mono">
                <span class="text-weight-medium">{{ p.moneda }}</span>
                <span class="q-ml-xs">{{ formatNumero(p.valor) }}</span>
              </div>
            </q-td>
          </template>

          <template #body-cell-acciones="props">
            <q-td :props="props" class="text-right">
              <q-btn flat round dense icon="edit" color="primary" @click="abrirEditar(props.row)">
                <q-tooltip>Editar</q-tooltip>
              </q-btn>
              <q-btn flat round dense icon="delete" color="negative" @click="confirmarEliminar(props.row)">
                <q-tooltip>Eliminar</q-tooltip>
              </q-btn>
            </q-td>
          </template>
        </q-table>
      </q-card-section>
    </q-card>

    <ProductoFormDialog
      v-model="showDialog"
      :producto="productoSeleccionado"
      :empresas="empresasStore.items"
      :categorias="store.categorias"
      @saved="onSaved"
    />
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { useProductosStore } from 'stores/productos';
import { useEmpresasStore } from 'stores/empresas';
import ProductoFormDialog from 'components/ProductoFormDialog.vue';

const store = useProductosStore();
const empresasStore = useEmpresasStore();
const $q = useQuasar();

const busqueda = ref('');
const empresaFiltro = ref(null);
const showDialog = ref(false);
const productoSeleccionado = ref(null);

const columns = [
  { name: 'codigo', label: 'Codigo', field: 'codigo', align: 'left', sortable: true },
  { name: 'nombre', label: 'Producto', field: 'nombre', align: 'left', sortable: true },
  { name: 'empresaNombre', label: 'Empresa', field: 'empresaNombre', align: 'left', sortable: true },
  { name: 'categorias', label: 'Categorias', field: 'categorias', align: 'left' },
  { name: 'precios', label: 'Precios', field: 'precios', align: 'left' },
  { name: 'acciones', label: '', field: 'acciones', align: 'right' }
];

const opcionesEmpresa = computed(() =>
  empresasStore.items.map((e) => ({ label: `${e.nombre} (${e.nit})`, value: e.nit }))
);

function formatNumero(valor) {
  return new Intl.NumberFormat('es-CO').format(valor);
}

function aplicarFiltro() {
  store.fetchAll(empresaFiltro.value || undefined);
}

function abrirCrear() {
  productoSeleccionado.value = null;
  showDialog.value = true;
}

function abrirEditar(producto) {
  productoSeleccionado.value = JSON.parse(JSON.stringify(producto));
  showDialog.value = true;
}

function onSaved() {
  showDialog.value = false;
  aplicarFiltro();
}

function confirmarEliminar(producto) {
  $q.dialog({
    title: 'Eliminar producto',
    message: `Eliminar "${producto.nombre}" (${producto.codigo})?`,
    cancel: { label: 'Cancelar', flat: true, noCaps: true },
    ok: { label: 'Eliminar', color: 'negative', unelevated: true, noCaps: true },
    persistent: true
  }).onOk(async () => {
    try {
      await store.remove(producto.id);
      $q.notify({ type: 'positive', message: 'Producto eliminado' });
    } catch (err) {
      $q.notify({ type: 'negative', message: err?.response?.data?.message || 'No se pudo eliminar' });
    }
  });
}

onMounted(async () => {
  await Promise.all([
    store.fetchAll(),
    store.fetchCategorias(),
    empresasStore.fetchAll()
  ]);
});
</script>
