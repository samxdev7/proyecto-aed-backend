-- V6: Eliminar columnas redundantes (inclusiones_adicionales, equipo, itinerario)
-- Toda la información enriquecida del viaje ahora se consolida en el campo descripcion bajo formato Markdown.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'app' AND table_name = 'viaje') THEN
        ALTER TABLE app.viaje DROP COLUMN IF EXISTS inclusiones_adicionales;
        ALTER TABLE app.viaje DROP COLUMN IF EXISTS equipo;
        ALTER TABLE app.viaje DROP COLUMN IF EXISTS itinerario;
        ALTER TABLE app.viaje ADD COLUMN IF NOT EXISTS imagen_url TEXT;
    ELSIF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'viaje') THEN
        ALTER TABLE public.viaje DROP COLUMN IF EXISTS inclusiones_adicionales;
        ALTER TABLE public.viaje DROP COLUMN IF EXISTS equipo;
        ALTER TABLE public.viaje DROP COLUMN IF EXISTS itinerario;
        ALTER TABLE public.viaje ADD COLUMN IF NOT EXISTS imagen_url TEXT;
    END IF;
END $$;

