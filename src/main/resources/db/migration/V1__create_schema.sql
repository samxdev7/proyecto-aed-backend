-- V1__create_schema.sql
-- Creación de la base de datos para el sistema de gestión de viajes del CNM
-- Compatible con Flyway y SQL Editor de Supabase

-- 1. Tabla USUARIO
CREATE TABLE usuario (
    id_usuario    BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol           VARCHAR(10) NOT NULL DEFAULT 'comun'
                    CHECK (rol IN ('administrador', 'cliente')),
    telefono      VARCHAR(20),
    sexo          VARCHAR(8) NOT NULL DEFAULT 'M',
    nacionalidad  VARCHAR(20) NOT NULL DEFAULT 'M',
    tipo_identificacion VARCHAR(20) NOT NULL DEFAULT 'cedula',
    numero_identificacion VARCHAR(50) NOT NULL,
    notificaciones_habilitadas BOOLEAN NOT NULL DEFAULT true,
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 2. Tabla VIAJE (creada por un administrador)
CREATE TABLE viaje (
    id_viaje    BIGSERIAL PRIMARY KEY,
    id_administrador_creador BIGINT NOT NULL REFERENCES usuario(id_usuario),
    titulo       VARCHAR(200) NOT NULL,
    descripcion  TEXT,
    itinerario   TEXT,
    dificultad    VARCHAR(20) NOT NULL,
    fecha_hora_ida TIMESTAMP NOT NULL,
    fecha_hora_vuelta TIMESTAMP,
    punto_encuentro TEXT,
    inclusiones_adicionales TEXT,
    monto_total NUMERIC(12,2),
    monto_reserva NUMERIC(12,2),
    cupos_maximos INTEGER,
    cupos_disponibles INTEGER,
    enlace_whatsapp TEXT,
    estado        VARCHAR(10) NOT NULL DEFAULT 'activo',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 3. Tabla CAMPO_FORMULARIO (campos personalizados del formulario de reserva)
CREATE TABLE campo_formulario (
    id_campo    BIGSERIAL PRIMARY KEY,
    id_viaje    BIGINT NOT NULL REFERENCES viaje(id_viaje),
    tipo_campo  VARCHAR(20) NOT NULL DEFAULT 'texto'
                    CHECK (tipo_campo IN ('texto', 'seleccion_unica', 'seleccion_multiple', 'fecha', 'archivo')),
    etiqueta_pregunta VARCHAR(100),
    opciones_respuesta JSON,
    orden INTEGER,
    obligatorio BOOLEAN NOT NULL DEFAULT false
);

-- 4. Tabla RESERVA (reservas realizadas por clientes)
CREATE TABLE reserva (
    id_reserva    BIGSERIAL PRIMARY KEY,
    id_usuario    BIGINT NOT NULL REFERENCES usuario(id_usuario),
    id_viaje      BIGINT NOT NULL REFERENCES viaje(id_viaje),
    id_administrador_revisor BIGINT REFERENCES usuario(id_usuario),
    estado        VARCHAR(10) NOT NULL DEFAULT 'pendiente'
                    CHECK (estado IN ('pendiente', 'aprobada', 'rechazada', 'expirada')),
    fecha_reserva TIMESTAMP NOT NULL,
    fecha_limite_pago TIMESTAMP NOT NULL,
    fecha_pago TIMESTAMP,
    numero_referencia_pago VARCHAR(50),
    captura_comprobante_url TEXT,
    motivo_rechazo TEXT,
    fecha_revision TIMESTAMP,
    editable BOOLEAN NOT NULL DEFAULT false,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 5. Tabla ACOMPANANTE (compañeros incluidos en una reserva)
CREATE TABLE acompanante (
    id_acompanante BIGSERIAL PRIMARY KEY,
    id_reserva    BIGINT NOT NULL REFERENCES reserva(id_reserva),
    nombre_completo VARCHAR(100) NOT NULL,
    tipo_identificacion VARCHAR(20) NOT NULL DEFAULT 'cedula',
    numero_identificacion VARCHAR(50) NOT NULL
);

-- 6. Tabla RESPUESTA_FORMULARIO (respuestas a los campos del formulario)
CREATE TABLE respuesta_formulario (
    id_respuesta    BIGSERIAL PRIMARY KEY,
    id_reserva      BIGINT NOT NULL REFERENCES reserva(id_reserva),
    id_campo        BIGINT NOT NULL REFERENCES campo_formulario(id_campo),
    valor_respuesta TEXT
);

-- 7. Tabla NOTIFICACION (notificaciones al usuario)
CREATE TABLE notificacion (
    id_notificacion BIGSERIAL PRIMARY KEY,
    id_usuario      BIGINT NOT NULL REFERENCES usuario(id_usuario),
    tipo            VARCHAR(30) NOT NULL DEFAULT 'nuevo_viaje',
    mensaje         TEXT NOT NULL,
    fecha_envio     TIMESTAMP NOT NULL,
    leida           BOOLEAN NOT NULL DEFAULT false
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_viaje_estado ON viaje(estado);
CREATE INDEX idx_reserva_viaje ON reserva(id_viaje);
CREATE INDEX idx_reserva_usuario ON reserva(id_usuario);
CREATE INDEX idx_reserva_estado ON reserva(estado);
CREATE INDEX idx_campo_viaje ON campo_formulario(id_viaje);
CREATE INDEX idx_acompanante_reserva ON acompanante(id_reserva);
CREATE INDEX idx_respuesta_campo ON respuesta_formulario(id_campo);
CREATE INDEX idx_notificacion_usuario ON notificacion(id_usuario);
