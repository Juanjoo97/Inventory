<template>
  <q-dialog v-model="show" persistent>
    <q-card style="min-width: 520px; max-width: 96vw; border-radius: 14px">
      <q-card-section class="row items-center q-pb-none">
        <q-icon
          :name="esEdicion ? 'edit' : 'add_box'"
          color="primary"
          size="24px"
          class="q-mr-sm"
        />
        <div class="text-display text-h6">
          {{ esEdicion ? "Editar producto" : "Nuevo producto" }}
        </div>
      </q-card-section>

      <q-card-section style="max-height: 70vh; overflow-y: auto">
        <q-form ref="formRef" class="q-gutter-md" @submit.prevent="guardar">
          <div class="row q-col-gutter-md">
            <div class="col-12 col-sm-5">
              <q-input
                v-model="form.codigo"
                label="Codigo"
                outlined
                dense
                :rules="[(v) => !!v || 'Requerido']"
              />
            </div>
            <div class="col-12 col-sm-7">
              <q-input
                v-model="form.nombre"
                label="Nombre"
                outlined
                dense
                :rules="[(v) => !!v || 'Requerido']"
              />
            </div>
          </div>

          <q-input
            v-model="form.caracteristicas"
            label="Caracteristicas"
            type="textarea"
            outlined
            dense
            autogrow
          />

          <q-select
            v-model="form.empresaNit"
            :options="opcionesEmpresa"
            label="Empresa"
            outlined
            dense
            emit-value
            map-options
            :rules="[(v) => !!v || 'Selecciona una empresa']"
          />

          <q-select
            v-model="form.categoriaIds"
            :options="opcionesCategoria"
            label="Categorias"
            outlined
            dense
            multiple
            emit-value
            map-options
            use-chips
          />

          <!-- Precios multimoneda -->
          <div>
            <div class="row items-center justify-between q-mb-xs">
              <div class="text-subtitle2 text-weight-medium">Precios por moneda</div>
              <q-btn
                flat
                dense
                color="primary"
                icon="add"
                label="Agregar"
                no-caps
                size="sm"
                @click="agregarPrecio"
              />
            </div>

            <div
              v-for="(precio, idx) in form.precios"
              :key="idx"
              class="row q-col-gutter-sm items-start q-mb-xs"
            >
              <div class="col-4">
                <q-input
                  v-model="precio.moneda"
                  label="Moneda"
                  outlined
                  dense
                  maxlength="3"
                  @update:model-value="
                    (val) => (precio.moneda = val.replace(/[^a-zA-Z]/g, '').toUpperCase())
                  "
                  :rules="[
                    (v) => !!v || 'La moneda es requerida',
                    (v) => /^[A-Z]{3}$/.test(v) || 'Debe contener 3 letras',
                    (v) => esMonedaValida(v) || 'Código ISO 4217 no válido',
                  ]"
                />
              </div>
              <div class="col">
                <q-input
                  v-model.number="precio.valor"
                  label="Valor"
                  type="number"
                  outlined
                  dense
                  :rules="[
                    (v) => (v !== null && v !== '' && Number(v) > 0) || 'Mayor a 0',
                  ]"
                />
              </div>
              <div class="col-auto q-pt-sm">
                <q-btn
                  flat
                  round
                  dense
                  icon="delete"
                  color="negative"
                  :disable="form.precios.length === 1"
                  @click="quitarPrecio(idx)"
                />
              </div>
            </div>
            <div v-if="errorPrecios" class="text-negative text-caption">
              {{ errorPrecios }}
            </div>
          </div>
        </q-form>
      </q-card-section>

      <q-card-actions align="right" class="q-pa-md q-pt-none">
        <q-btn flat label="Cancelar" no-caps v-close-popup />
        <q-btn
          color="primary"
          :label="esEdicion ? 'Guardar cambios' : 'Crear'"
          no-caps
          unelevated
          :loading="guardando"
          @click="guardar"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useQuasar } from "quasar";
import { useProductosStore } from "stores/productos";

const esMonedaValida = (codigo) => {
  if (!codigo) return false;

  try {
    new Intl.NumberFormat("es-CO", {
      style: "currency",
      currency: codigo.toUpperCase(),
    });

    return true;
  } catch {
    return false;
  }
};

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  producto: { type: Object, default: null },
  empresas: { type: Array, default: () => [] },
  categorias: { type: Array, default: () => [] },
});
const emit = defineEmits(["update:modelValue", "saved"]);

const store = useProductosStore();
const $q = useQuasar();

const formRef = ref(null);
const guardando = ref(false);
const errorPrecios = ref("");

const form = ref(estadoInicial());

function estadoInicial() {
  return {
    codigo: "",
    nombre: "",
    caracteristicas: "",
    empresaNit: null,
    categoriaIds: [],
    precios: [{ moneda: "", valor: null }],
  };
}

const show = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});

const esEdicion = computed(() => !!props.producto);

const opcionesEmpresa = computed(() =>
  props.empresas.map((e) => ({ label: `${e.nombre} (${e.nit})`, value: e.nit }))
);

const opcionesCategoria = computed(() =>
  props.categorias.map((c) => ({ label: c.nombre, value: c.id }))
);

watch(
  () => props.modelValue,
  (abierto) => {
    errorPrecios.value = "";
    if (!abierto) return;

    if (props.producto) {
      // Mapear nombres de categoria del response de vuelta a ids
      const ids = props.categorias
        .filter((c) => props.producto.categorias.includes(c.nombre))
        .map((c) => c.id);

      form.value = {
        codigo: props.producto.codigo,
        nombre: props.producto.nombre,
        caracteristicas: props.producto.caracteristicas || "",
        empresaNit: props.producto.empresaNit,
        categoriaIds: ids,
        precios: props.producto.precios.length
          ? props.producto.precios.map((p) => ({
              moneda: p.moneda,
              valor: Number(p.valor),
            }))
          : [{ moneda: "", valor: null }],
      };
    } else {
      form.value = estadoInicial();
    }
  }
);

function agregarPrecio() {
  form.value.precios.push({ moneda: "", valor: null });
}

function quitarPrecio(idx) {
  form.value.precios.splice(idx, 1);
}

function validarPrecios() {
  const monedas = form.value.precios.map((p) => p.moneda);
  if (new Set(monedas).size !== monedas.length) {
    errorPrecios.value = "No repitas la misma moneda";
    return false;
  }
  errorPrecios.value = "";
  return true;
}

async function guardar() {
  const valido = await formRef.value.validate();
  if (!valido || !validarPrecios()) return;

  const payload = {
    codigo: form.value.codigo,
    nombre: form.value.nombre,
    caracteristicas: form.value.caracteristicas,
    empresaNit: form.value.empresaNit,
    categoriaIds: form.value.categoriaIds,
    precios: form.value.precios.map((p) => ({
      moneda: p.moneda,
      valor: Number(p.valor),
    })),
  };

  guardando.value = true;
  try {
    if (esEdicion.value) {
      await store.update(props.producto.id, payload);
      $q.notify({ type: "positive", message: "Producto actualizado" });
    } else {
      await store.create(payload);
      $q.notify({ type: "positive", message: "Producto creado" });
    }
    emit("saved");
  } catch (err) {
    const data = err?.response?.data;

    let mensaje = data?.message || "No se pudo guardar";

    if (data?.validaciones) {
      mensaje = Object.values(data.validaciones).join("\n");
    }

    $q.notify({
      type: "negative",
      message: mensaje,
      timeout: 5000,
    });
  } finally {
    guardando.value = false;
  }
}
</script>
