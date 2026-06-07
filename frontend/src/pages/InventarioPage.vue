<template>
  <q-page class="q-pa-lg">
    <div class="row items-center justify-between q-mb-lg">
      <div>
        <div class="lt-page-title">Inventario</div>
        <div class="text-body2 text-grey-8">Reporte consolidado de productos por empresa</div>
      </div>
      <div class="row q-gutter-sm">
        <q-btn color="secondary" :icon="matMail" label="Enviar por correo" no-caps outline @click="abrirEnviar" />
        <q-btn color="primary" :icon="matPictureAsPdf" label="Descargar PDF" no-caps unelevated :loading="descargando"
          @click="descargar" />
      </div>
    </div>

    <q-card flat class="lt-card">
      <q-card-section>
        <q-table :rows="store.items" :columns="columns" row-key="codigo" :loading="store.loading" flat class="lt-table"
          :rows-per-page-options="[10, 20, 50]" no-data-label="No hay productos en el inventario">
          <template #body-cell-empresa="props">
            <q-td :props="props">
              <div class="text-weight-medium">{{ props.row.empresaNombre }}</div>
              <div class="text-caption text-grey-7 lt-chip-mono">{{ props.row.empresaNit }}</div>
            </q-td>
          </template>

          <template #body-cell-categorias="props">
            <q-td :props="props">
              <q-chip v-for="cat in props.row.categorias" :key="cat" dense size="sm" color="teal-1"
                text-color="teal-9">{{ cat }}</q-chip>
              <span v-if="!props.row.categorias.length" class="text-grey-6">—</span>
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
        </q-table>
      </q-card-section>
    </q-card>

    <q-dialog v-model="dialogEnviar">
      <q-card style="min-width: 380px; border-radius: 14px">
        <q-card-section class="row items-center q-pb-none">
          <q-icon :name="matMail" color="primary" size="24px" class="q-mr-sm" />
          <div class="text-display text-h6">Enviar inventario</div>
        </q-card-section>
        <q-card-section>
          <div class="text-body2 text-grey-7 q-mb-md">
            Se generara el PDF del inventario y se enviara al correo indicado.
          </div>
          <q-form ref="formEnviar" @submit.prevent="enviar">
            <q-input v-model="correoDestino" type="email" label="Correo destino" outlined dense
              :rules="[(v) => !!v || 'Requerido', (v) => /.+@.+\..+/.test(v) || 'Correo invalido']">
              <template #prepend><q-icon :name="matAlternateEmail" /></template>
            </q-input>
          </q-form>
        </q-card-section>
        <q-card-actions align="right" class="q-pa-md q-pt-none">
          <q-btn flat label="Cancelar" no-caps v-close-popup />
          <q-btn color="primary" label="Enviar" no-caps unelevated :loading="enviando" @click="enviar" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { matMail, matPictureAsPdf, matAlternateEmail } from '@quasar/extras/material-icons';
import { useInventarioStore } from 'stores/inventario';

const store = useInventarioStore();
const $q = useQuasar();

const descargando = ref(false);
const enviando = ref(false);
const dialogEnviar = ref(false);
const correoDestino = ref('');
const formEnviar = ref(null);

const columns = [
  { name: 'empresa', label: 'Empresa', field: 'empresaNombre', align: 'left', sortable: true },
  { name: 'codigo', label: 'Codigo', field: 'codigo', align: 'left', sortable: true },
  { name: 'nombre', label: 'Producto', field: 'nombre', align: 'left', sortable: true },
  { name: 'caracteristicas', label: 'Caracteristicas', field: 'caracteristicas', align: 'left' },
  { name: 'categorias', label: 'Categorias', field: 'categorias', align: 'left' },
  { name: 'precios', label: 'Precios', field: 'precios', align: 'left' }
];

function formatNumero(valor) {
  return new Intl.NumberFormat('es-CO').format(valor);
}

async function descargar() {
  descargando.value = true;
  try {
    await store.descargarPdf();
    $q.notify({ type: 'positive', message: 'PDF generado' });
  } catch (err) {
    $q.notify({ type: 'negative', message: 'No se pudo generar el PDF' });
  } finally {
    descargando.value = false;
  }
}

function abrirEnviar() {
  correoDestino.value = '';
  dialogEnviar.value = true;
}

async function enviar() {
  const ok = await formEnviar.value.validate();
  if (!ok) return;
  enviando.value = true;
  try {
    const res = await store.enviarPorCorreo(correoDestino.value);
    $q.notify({ type: 'positive', message: res?.mensaje || 'Inventario enviado' });
    dialogEnviar.value = false;
  } catch (err) {
    $q.notify({ type: 'negative', message: err?.response?.data?.message || 'No se pudo enviar el correo' });
  } finally {
    enviando.value = false;
  }
}

onMounted(() => store.fetchAll());
</script>