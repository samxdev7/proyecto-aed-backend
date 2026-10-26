-- V9__create_token_recuperacion.sql
-- Creación de la tabla para almacenar tokens temporales de recuperación de contraseña
DO $$
DECLARE
    target_schema text;
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'app' AND table_name = 'usuario') THEN
        target_schema := 'app';
    ELSE
        target_schema := 'public';
    END IF;

    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I.token_recuperacion (
            id_token            BIGSERIAL PRIMARY KEY,
            id_usuario          BIGINT NOT NULL REFERENCES %I.usuario(id_usuario) ON DELETE CASCADE,
            token               VARCHAR(255) NOT NULL UNIQUE,
            fecha_expiracion    TIMESTAMPTZ NOT NULL,
            usado               BOOLEAN NOT NULL DEFAULT FALSE,
            fecha_creacion      TIMESTAMPTZ NOT NULL DEFAULT NOW()
        )
    ', target_schema, target_schema);

    EXECUTE format('CREATE INDEX IF NOT EXISTS idx_token_recuperacion_token ON %I.token_recuperacion(token)', target_schema);
    EXECUTE format('CREATE INDEX IF NOT EXISTS idx_token_recuperacion_usuario ON %I.token_recuperacion(id_usuario)', target_schema);
END $$;
