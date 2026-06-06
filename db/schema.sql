-- Esquema de referencia (en runtime lo genera Hibernate con ddl-auto=update).
-- Para produccion se recomienda gestionarlo con Flyway/Liquibase.

CREATE TABLE empresa (
    nit       VARCHAR(40) PRIMARY KEY,
    nombre    VARCHAR(150) NOT NULL,
    direccion VARCHAR(255),
    telefono  VARCHAR(40)
);

CREATE TABLE categoria (
    id     BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE producto (
    id              BIGSERIAL PRIMARY KEY,
    codigo          VARCHAR(60) NOT NULL UNIQUE,
    nombre          VARCHAR(150) NOT NULL,
    caracteristicas VARCHAR(2000),
    empresa_nit     VARCHAR(40) NOT NULL REFERENCES empresa(nit)
);

CREATE TABLE producto_precio (
    id          BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
    moneda      VARCHAR(3) NOT NULL,
    valor       NUMERIC(19,4) NOT NULL
);

CREATE TABLE producto_categoria (
    producto_id  BIGINT NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
    categoria_id BIGINT NOT NULL REFERENCES categoria(id) ON DELETE CASCADE,
    PRIMARY KEY (producto_id, categoria_id)
);

CREATE TABLE cliente (
    id     BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email  VARCHAR(150)
);

CREATE TABLE orden (
    id         BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES cliente(id),
    fecha      TIMESTAMP NOT NULL
);

CREATE TABLE orden_detalle (
    id              BIGSERIAL PRIMARY KEY,
    orden_id        BIGINT NOT NULL REFERENCES orden(id) ON DELETE CASCADE,
    producto_id     BIGINT NOT NULL REFERENCES producto(id),
    cantidad        INT NOT NULL,
    precio_unitario NUMERIC(19,4) NOT NULL
);

CREATE TABLE usuario (
    id       BIGSERIAL PRIMARY KEY,
    email    VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    rol      VARCHAR(20) NOT NULL
);
