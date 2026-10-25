-- V6: Eliminar columnas redundantes (inclusiones_adicionales, equipo, itinerario)
-- Toda la información enriquecida del viaje ahora se consolida en el campo descripcion bajo formato Markdown.
ALTER TABLE viaje DROP COLUMN IF EXISTS inclusiones_adicionales;
ALTER TABLE viaje DROP COLUMN IF EXISTS equipo;
ALTER TABLE viaje DROP COLUMN IF EXISTS itinerario;

-- Garantizar que imagen_url exista (si no fue creada en V1 o V2)
ALTER TABLE viaje ADD COLUMN IF NOT EXISTS imagen_url TEXT;
