-- V1__create_schema.sql
-- Creación del esquema de base de datos para el Club Nicaragüense de Montañismo (CNM)
-- Sincronizado rigurosamente con diagrama_er_cnm.mmd (Normalización 1FN, 2FN, 3FN y tipos temporales TIMESTAMPTZ)
-- Compatible con Flyway, PostgreSQL 15+ y Supabase SQL Editor

-- 1. Tabla USUARIO
CREATE TABLE usuario (
    id_usuario                  BIGSERIAL PRIMARY KEY,
    primer_nombre               VARCHAR(50) NOT NULL,
    segundo_nombre              VARCHAR(50),
    primer_apellido             VARCHAR(50) NOT NULL,
    segundo_apellido            VARCHAR(50),
    correo                      VARCHAR(150) NOT NULL UNIQUE,
    contrasena_hash             VARCHAR(255) NOT NULL,
    rol                         VARCHAR(20) NOT NULL DEFAULT 'cliente'
                                  CHECK (rol IN ('administrador', 'cliente')),
    telefono                    VARCHAR(20),
    sexo                        VARCHAR(10) NOT NULL DEFAULT 'M'
                                  CHECK (sexo IN ('M', 'F', 'Otro')),
    nacionalidad                VARCHAR(50) NOT NULL DEFAULT 'Nicaragüense',
    tipo_identificacion         VARCHAR(20) NOT NULL DEFAULT 'cedula'
                                  CHECK (tipo_identificacion IN ('cedula', 'pasaporte')),
    numero_identificacion       VARCHAR(50) NOT NULL UNIQUE,
    notificaciones_habilitadas  BOOLEAN NOT NULL DEFAULT true,
    fecha_registro              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2. Tabla VIAJE (creada y administrada por un usuario con rol 'administrador')
CREATE TABLE viaje (
    id_viaje                    BIGSERIAL PRIMARY KEY,
    id_administrador_creador    BIGINT NOT NULL REFERENCES usuario(id_usuario),
    titulo                      VARCHAR(200) NOT NULL,
    descripcion                 TEXT,
    itinerario                  TEXT,
    dificultad                  VARCHAR(20) NOT NULL
                                  CHECK (dificultad IN ('Baja', 'Media', 'Alta', 'Extrema')),
    fecha_hora_ida              TIMESTAMPTZ NOT NULL,
    fecha_hora_vuelta           TIMESTAMPTZ NOT NULL,
    punto_encuentro             TEXT NOT NULL,
    inclusiones_adicionales     TEXT,
    monto_total                 NUMERIC(12,2) NOT NULL CHECK (monto_total > 0),
    monto_reserva               NUMERIC(12,2) NOT NULL CHECK (monto_reserva >= 0),
    cupos_maximos               INTEGER NOT NULL CHECK (cupos_maximos > 0),
    cupos_disponibles           INTEGER NOT NULL CHECK (cupos_disponibles >= 0),
    enlace_whatsapp             TEXT,
    estado                      VARCHAR(10) NOT NULL DEFAULT 'activo'
                                  CHECK (estado IN ('activo', 'cerrado')),
    fecha_creacion              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. Tabla CAMPO_FORMULARIO (campos personalizados de inscripción definidos por viaje [RF7])
CREATE TABLE campo_formulario (
    id_campo                    BIGSERIAL PRIMARY KEY,
    id_viaje                    BIGINT NOT NULL REFERENCES viaje(id_viaje) ON DELETE CASCADE,
    tipo_campo                  VARCHAR(20) NOT NULL DEFAULT 'texto'
                                  CHECK (tipo_campo IN ('texto', 'seleccion_unica', 'seleccion_multiple', 'fecha', 'archivo')),
    etiqueta_pregunta           VARCHAR(100) NOT NULL,
    opciones_respuesta          JSON,
    orden                       INTEGER NOT NULL DEFAULT 0,
    obligatorio                 BOOLEAN NOT NULL DEFAULT false
);

-- 4. Tabla RESERVA (reservas realizadas por clientes [RF1, RF4, RF9])
CREATE TABLE reserva (
    id_reserva                  BIGSERIAL PRIMARY KEY,
    id_usuario                  BIGINT NOT NULL REFERENCES usuario(id_usuario),
    id_viaje                    BIGINT NOT NULL REFERENCES viaje(id_viaje),
    id_administrador_revisor    BIGINT REFERENCES usuario(id_usuario),
    estado                      VARCHAR(15) NOT NULL DEFAULT 'pendiente'
                                  CHECK (estado IN ('pendiente', 'aprobada', 'rechazada', 'expirada')),
    fecha_reserva               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    fecha_limite_pago           TIMESTAMPTZ NOT NULL, -- fecha_reserva + 30 min [RF1]
    fecha_pago                  TIMESTAMPTZ,
    numero_referencia_pago      VARCHAR(50),
    captura_comprobante_url     TEXT,
    motivo_rechazo              TEXT,
    fecha_revision              TIMESTAMPTZ,
    editable                    BOOLEAN NOT NULL DEFAULT false
);

-- 5. Tabla ACOMPANANTE (compañeros incluidos en una reserva [RF1, RF9, 1FN])
CREATE TABLE acompanante (
    id_acompanante              BIGSERIAL PRIMARY KEY,
    id_reserva                  BIGINT NOT NULL REFERENCES reserva(id_reserva) ON DELETE CASCADE,
    primer_nombre               VARCHAR(50) NOT NULL,
    segundo_nombre              VARCHAR(50),
    primer_apellido             VARCHAR(50) NOT NULL,
    segundo_apellido            VARCHAR(50),
    tipo_identificacion         VARCHAR(20) NOT NULL DEFAULT 'cedula'
                                  CHECK (tipo_identificacion IN ('cedula', 'pasaporte')),
    numero_identificacion       VARCHAR(50) NOT NULL
);

-- 6. Tabla RESPUESTA_FORMULARIO (respuestas a los campos dinámicos del formulario [RF7])
CREATE TABLE respuesta_formulario (
    id_respuesta                BIGSERIAL PRIMARY KEY,
    id_reserva                  BIGINT NOT NULL REFERENCES reserva(id_reserva) ON DELETE CASCADE,
    id_campo                    BIGINT NOT NULL REFERENCES campo_formulario(id_campo),
    valor_respuesta             TEXT
);

-- 7. Tabla NOTIFICACION (notificaciones al usuario [RF8, RF9])
CREATE TABLE notificacion (
    id_notificacion             BIGSERIAL PRIMARY KEY,
    id_usuario                  BIGINT NOT NULL REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    tipo                        VARCHAR(30) NOT NULL DEFAULT 'nuevo_viaje'
                                  CHECK (tipo IN ('nuevo_viaje', 'pocos_cupos', 'reserva_aprobada', 'reserva_rechazada')),
    mensaje                     TEXT NOT NULL,
    fecha_envio                 TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    leida                       BOOLEAN NOT NULL DEFAULT false
);

-- Índices estratégicos para rendimiento y reglas de negocio
CREATE INDEX idx_viaje_estado ON viaje(estado);
CREATE INDEX idx_viaje_fecha_ida ON viaje(fecha_hora_ida);
CREATE INDEX idx_reserva_viaje ON reserva(id_viaje);
CREATE INDEX idx_reserva_usuario ON reserva(id_usuario);
CREATE INDEX idx_reserva_estado ON reserva(estado);
CREATE INDEX idx_campo_viaje ON campo_formulario(id_viaje);
CREATE INDEX idx_acompanante_reserva ON acompanante(id_reserva);
CREATE INDEX idx_acompanante_identificacion ON acompanante(numero_identificacion); -- Crítico para regla RF9 de rechazo
CREATE INDEX idx_respuesta_campo ON respuesta_formulario(id_campo);
CREATE INDEX idx_notificacion_usuario ON notificacion(id_usuario);
