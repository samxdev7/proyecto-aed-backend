-- V4: Eliminación lógica / archivado de viajes.
-- Reemplaza la eliminación física por un indicador booleano 'archivado'.
ALTER TABLE viaje ADD COLUMN archivado BOOLEAN NOT NULL DEFAULT FALSE;
