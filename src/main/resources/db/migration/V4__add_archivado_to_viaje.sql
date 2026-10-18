-- V4: Eliminación lógica / archivado de viajes.
-- Reemplaza la eliminación física por un indicador booleano 'archivado'.
-- Soporta dinámicamente tanto el schema 'app' (usado en Supabase / Hibernate default_schema) como 'public'.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'app' AND table_name = 'viaje') THEN
        ALTER TABLE app.viaje ADD COLUMN IF NOT EXISTS archivado BOOLEAN NOT NULL DEFAULT FALSE;
    ELSIF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'viaje') THEN
        ALTER TABLE public.viaje ADD COLUMN IF NOT EXISTS archivado BOOLEAN NOT NULL DEFAULT FALSE;
    ELSE
        RAISE EXCEPTION 'No se encontró la tabla viaje ni en el schema app ni en public';
    END IF;
END $$;
