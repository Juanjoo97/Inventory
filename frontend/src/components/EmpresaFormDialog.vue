<template>
  <q-dialog v-model="show" persistent>
    <q-card style="min-width: 440px; border-radius: 14px">
      <q-card-section class="row items-center q-pb-none">
        <q-icon
          :name="esEdicion ? 'edit' : 'add_business'"
          color="primary"
          size="24px"
          class="q-mr-sm"
        />
        <div class="text-display text-h6">
          {{ esEdicion ? "Editar empresa" : "Nueva empresa" }}
        </div>
      </q-card-section>

      <q-card-section>
        <q-form ref="formRef" class="q-gutter-md" @submit.prevent="guardar">
          <q-input
            v-model="form.nit"
            label="NIT"
            outlined
            dense
            :disable="esEdicion"
            :hint="esEdicion ? 'El NIT no se puede modificar' : 'Ejemplo: 901987654-3'"
            :rules="[
              (v) => !!v || 'El NIT es requerido',
              (v) => /^\d{9,10}-\d$/.test(v) || 'Formato válido: 901987654-3',
            ]"
          />
          <q-input
            v-model="form.nombre"
            label="Nombre"
            outlined
            dense
            :rules="[(v) => !!v || 'El nombre es requerido']"
          />
          <q-input v-model="form.direccion" label="Direccion" outlined dense />
          <q-input
            v-model="form.telefono"
            label="Teléfono"
            outlined
            dense
            :rules="[
              (v) =>
                !v ||
                /^\d{7,10}$/.test(v) ||
                'Ingrese un teléfono válido (7 a 10 dígitos)',
            ]"
          />
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
import { useEmpresasStore } from "stores/empresas";

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  empresa: { type: Object, default: null },
});
const emit = defineEmits(["update:modelValue", "saved"]);

const store = useEmpresasStore();
const $q = useQuasar();

const formRef = ref(null);
const guardando = ref(false);
const form = ref({ nit: "", nombre: "", direccion: "", telefono: "" });

const show = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});

const esEdicion = computed(() => !!props.empresa);

watch(
  () => props.modelValue,
  (abierto) => {
    if (abierto) {
      form.value = props.empresa
        ? { ...props.empresa }
        : { nit: "", nombre: "", direccion: "", telefono: "" };
    }
  }
);

async function guardar() {
  const valido = await formRef.value.validate();
  if (!valido) return;

  guardando.value = true;
  try {
    if (esEdicion.value) {
      await store.update(props.empresa.nit, {
        nit: form.value.nit,
        nombre: form.value.nombre,
        direccion: form.value.direccion,
        telefono: form.value.telefono,
      });
      $q.notify({ type: "positive", message: "Empresa actualizada" });
    } else {
      await store.create({ ...form.value });
      $q.notify({ type: "positive", message: "Empresa creada" });
    }
    emit("saved");
  } catch (err) {
    $q.notify({
      type: "negative",
      message: err?.response?.data?.message || "No se pudo guardar",
    });
  } finally {
    guardando.value = false;
  }
}
</script>
