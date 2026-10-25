-- V8: Permitir id_viaje en notificacion y flexibilizar o expandir tipo de notificacion
DO $$
DECLARE
    target_schema text;
    r record;
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'app' AND table_name = 'notificacion') THEN
        target_schema := 'app';
    ELSIF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'notificacion') THEN
        target_schema := 'public';
    ELSE
        target_schema := 'app';
    END IF;

    -- 1. Agregar columna id_viaje si no existe
    EXECUTE format('ALTER TABLE %I.notificacion ADD COLUMN IF NOT EXISTS id_viaje BIGINT REFERENCES %I.viaje(id_viaje) ON DELETE CASCADE', target_schema, target_schema);

    -- 2. Eliminar restricciones CHECK sobre la columna tipo para permitir nuevos tipos (e.g. nueva_reserva)
    FOR r IN (
        SELECT conname
        FROM pg_constraint con
        JOIN pg_class rel ON rel.oid = con.conrelid
        JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
        WHERE nsp.nspname = target_schema
          AND rel.relname = 'notificacion'
          AND con.contype = 'c'
    ) LOOP
        EXECUTE format('ALTER TABLE %I.notificacion DROP CONSTRAINT %I', target_schema, r.conname);
    END LOOP;

    -- 3. Crear indice para busqueda rapida por id_usuario, id_viaje y tipo
    EXECUTE format('CREATE INDEX IF NOT EXISTS idx_notificacion_usuario_viaje ON %I.notificacion(id_usuario, id_viaje)', target_schema);
END $$;
