-- V2: imagen y equipo del viaje.
-- El catálogo público es visual (imagen por viaje) y la ficha muestra "Equipo requerido";
-- sin estas columnas el frontend tendría que hardcodear el mapeo id→imagen.
ALTER TABLE viaje ADD COLUMN imagen_url TEXT;
ALTER TABLE viaje ADD COLUMN equipo TEXT;
