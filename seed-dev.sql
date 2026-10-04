-- ============================================================
-- CNM — Semilla de datos de desarrollo (BD "cnm", PostgreSQL local)
-- Generado 2026-09-30. Ejecutar:
--   PGPASSWORD=cnm123 psql -h /tmp -U postgres -d cnm -f seed-dev.sql
-- Es IDEMPOTENTE: trunca y reinserta (FKs con ON DELETE CASCADE donde aplica).
-- Credenciales demo: admin@cnmontanismo.com / Admin1234  ·  carlos.gonzalez@example.com / Cliente1234
--   (ana.perez, luis.martinez, maria.torres @example.com / Cliente1234)
-- ============================================================

BEGIN;

TRUNCATE respuesta_formulario, acompanante, notificacion, reserva, campo_formulario, viaje, usuario RESTART IDENTITY CASCADE;

-- ---------- usuarios (BCrypt: Admin1234 / Cliente1234) ----------
INSERT INTO usuario (primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, correo, contrasena_hash, rol, telefono, sexo, nacionalidad, tipo_identificacion, numero_identificacion, notificaciones_habilitadas, fecha_registro) VALUES
('Sofía',  NULL, 'Ramírez', 'López', 'admin@cnmontanismo.com',       '$2a$10$kCuwPwqVYmxTXe8YxVuk6u9NupQvY6R.cHJfuhYwW8hi9Fr3PH2mG', 'administrador', '+505 8888-0001', 'F', 'Nicaragüense', 'cedula', '001-010180-0001A', true,  '2026-01-15T08:00:00-06:00'),
('Carlos', NULL, 'González', 'Ruiz', 'carlos.gonzalez@example.com',  '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente',       '+505 8888-9999', 'M', 'Nicaragüense', 'cedula', '001-150890-0001A', true,  '2026-02-10T10:30:00-06:00'),
('Ana',    'María','Pérez',  'Silva', 'ana.perez@example.com',       '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente',       '+505 8777-6655', 'F', 'Nicaragüense', 'cedula', '001-200195-0003B', true,  '2026-03-05T16:45:00-06:00'),
('Luis',   NULL, 'Martínez', 'Díaz', 'luis.martinez@example.com',     '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente',       '+505 8666-5544', 'M', 'Nicaragüense', 'cedula', '001-281093-0002C', false, '2026-04-22T09:15:00-06:00'),
('María',  'Fernanda','Torres','Chávez','maria.torres@example.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente',       '+505 8555-4433', 'F', 'Nicaragüense', 'cedula', '001-050797-0004D', true,  '2026-05-18T14:20:00-06:00');

-- ---------- viajes (creados por el admin, id_usuario 1) ----------
INSERT INTO viaje (id_administrador_creador, titulo, descripcion, itinerario, dificultad, fecha_hora_ida, fecha_hora_vuelta, punto_encuentro, inclusiones_adicionales, monto_total, monto_reserva, cupos_maximos, cupos_disponibles, enlace_whatsapp, estado, fecha_creacion, imagen_url, equipo) VALUES
(1, 'Volcán Mombacho',
 'Ascenso por la reserva natural del Volcán Mombacho con vistas panorámicas al lago.',
 E'Salida y traslado hacia senderos\nAscenso por senderos designados\nLlegada a la cumbre\nAlmuerzo y descanso\nRetorno al punto de encuentro',
 'Media', '2026-10-24T05:00:00-06:00', '2026-10-24T15:00:00-06:00',
 'Gasolinera UNO Metrocentro - 5:00 AM',
 E'Entrada a la reserva natural Mombacho\nAlmuerzo al finalizar la ruta',
 1285.00, 550.00, 15, 14, NULL, 'activo', '2026-09-10T09:00:00-06:00',
 '/Presentaciones/viajes/mombacho/VolcanMombacho.jpg',
 E'Botas de montaña\n2L de agua\nCapa de lluvia\nSnack energético'),
(1, 'Cañón de Somoto',
 'Recorrido por las paredes y piscinas naturales del Cañón de Somoto.',
 E'Salida desde Managua\nSesión informativa de seguridad\nRecorrido por el cañón\nPausa para nadar y almuerzo\nRegreso a Managua',
 'Baja', '2026-10-29T05:30:00-06:00', '2026-10-29T17:30:00-06:00',
 'Plaza El Sol, Somoto - 5:30 AM',
 E'Guías locales certificados del cañón\nEquipo de flotación',
 1100.00, 450.00, 20, 17, NULL, 'activo', '2026-09-10T09:05:00-06:00',
 '/Presentaciones/viajes/somoto/canonsomoto.jpg',
 E'Zapatos antideslizantes\nProtector solar\nRopa de cambio\nSnack energético'),
(1, 'Volcán Cosigüina',
 'Senderismo por el volcán más occidental, laguna en su cráter y vista al golfo de Fonseca.',
 E'Salida temprano hacia Chinandega\nInicio del ascenso\nDescanso en el mirador\nLlegada al cráter y laguna\nDescenso y retorno',
 'Alta', '2026-11-05T04:00:00-06:00', '2026-11-05T18:00:00-06:00',
 'Gasolinera Uno Rubenia - 4:00 AM',
 E'Entrada al área protegida\nBotiquín y radios del club',
 1500.00, 650.00, 12, 11, NULL, 'activo', '2026-09-12T10:00:00-06:00',
 '/Presentaciones/viajes/cosiguina/VolcanCosiguina.png',
 E'Botas de montaña\n3L de agua\nLinterna frontal\nCapa de lluvia'),
(1, 'Cerro Negro',
 'Sandboarding nocturno en la arena volcánica del Cerro Negro, León.',
 E'Traslado a la base del volcán\nCharla de seguridad y equipo\nAscenso por la arena volcánica\nDescenso en tabla\nRegreso y merienda',
 'Media', '2026-11-08T14:00:00-06:00', '2026-11-08T22:00:00-06:00',
 'Oficina CNM León - 2:00 PM',
 E'Tablas de sandboarding incluidas\nMerienda al regreso',
 1400.00, 600.00, 15, 14, NULL, 'activo', '2026-09-12T10:05:00-06:00',
 '/Presentaciones/viajes/cerro-negro/CerroNegro.png',
 E'Ropa para lavar después\nPañuelo para cubrir nariz y boca\n2L de agua\nCambio de ropa'),
(1, 'Volcán Telica',
 'Campamento y ascenso nocturno para ver la lava y el amanecer desde la cima.',
 E'Juntanza en la hostería\nAscenso al atardecer\nObservación del cráter en la noche\nDescanso en la cima\nDescenso al amanecer',
 'Media', '2026-11-12T16:00:00-06:00', '2026-11-13T09:00:00-06:00',
 'Hostería San Jacinto - 4:00 PM',
 E'Campamento y equipo de cocina\nCena y desayuno en la cima',
 1350.00, 550.00, 14, 12, NULL, 'activo', '2026-09-15T11:00:00-06:00',
 '/Presentaciones/viajes/telica/Telica.png',
 E'Botas de montaña\n3L de agua\nChaqueta térmica\nLinterna frontal'),
(1, 'Volcán Concepción',
 'La cumbre más alta y exigente de la isla de Ometepe, con vistas espectaculares.',
 E'Travesía en ferry a Moyogalpa\nInicio del ascenso en Altagracia\nParadas técnicas por nivel\nCumbre y descenso\nRegreso en ferry',
 'Alta', '2026-11-18T03:30:00-06:00', '2026-11-18T17:30:00-06:00',
 'Muelle San Jorge - 3:30 AM',
 E'Tiquetes de ferry\nGuía local de la isla',
 1600.00, 700.00, 6, 2, NULL, 'activo', '2026-09-15T11:05:00-06:00',
 '/Presentaciones/viajes/concepcion/Concepción1.png',
 E'Botas de montaña\n3L de agua mínimo\nProtector solar\nComida energética'),
(1, 'Cerro Mogotón',
 'Hasta el punto más alto del país (2,107 m) en la reserva de Dipilto, Nueva Segovia.',
 E'Traslado a Dipilto\nIngreso a la reserva\nAscenso por bosque nublado\nCumbre del Mogotón\nDescenso y retorno',
 'Alta', '2026-11-25T04:00:00-06:00', '2026-11-25T18:00:00-06:00',
 'Parque Central de Ocotal - 4:00 AM',
 E'Entrada a la reserva Dipilto\nAlmuerzo caliente en cumbre',
 1550.00, 650.00, 10, 9, NULL, 'activo', '2026-09-18T09:30:00-06:00',
 '/Presentaciones/viajes/mogoton/mogoton.png',
 E'Botas de montaña\n3L de agua\nChaqueta térmica\nCapa de lluvia'),
(1, 'Laguna de Apoyo',
 'Caminata de cerros a la laguna de origen volcánico más profunda del país.',
 E'Traslado a Catarina\nMirador de la Laguna\nDescenso a la orilla\nTiempo libre, nadar y almuerzo\nRetorno a Masaya',
 'Baja', '2026-12-02T07:00:00-06:00', '2026-12-02T16:00:00-06:00',
 'Mercado de Masaya - 7:00 AM',
 E'Entrada a la laguna\nAlmuerzo frente al agua',
 950.00, 400.00, 20, 18, NULL, 'activo', '2026-09-18T09:35:00-06:00',
 '/Presentaciones/viajes/laguna-apoyo/LagunaApoyo.jpg',
 E'Zapatos para caminar\nTraje de baño\nProtector solar\n2L de agua'),
(1, 'Volcán Acatenango',
 'Ascenso nocturno y amanecer sobre uno de los volcanes más emblemáticos de Centroamérica, con el Fuego lanzando lava a lo lejos.',
 E'Juntanza y traslado en Antigua\nAscenso nocturno con linternas\nCampamento a mitad de ruta\nAmanecer desde la cumbre\nDescenso y regreso',
 'Alta', '2027-01-15T04:00:00-06:00', '2027-01-16T13:00:00-06:00',
 'Antigua, Guatemala - por confirmar',
 E'Transporte terrestre en Guatemala\nEquipo de campamento compartido\nDos comidas calientes',
 8500.00, 3000.00, 10, 10, NULL, 'activo', '2026-09-20T15:00:00-06:00',
 '/Presentaciones/viajes/acatenango/exterior.jpg',
 E'Botas de montaña\n3L de agua\nChaqueta térmica\nLinterna frontal\nSaco de dormir'),
(1, 'Volcán Asososca',
 'Ascenso al Volcán Asososca (El Hoyo), en la cordillera de los Maribios, con su laguna interior y vistas a León.',
 E'Juntanza en León\nRuta por la cordillera de los Maribios\nAscenso al sendero del cráter\nDescanso en la laguna interior\nDescenso y regreso',
 'Media', '2027-02-06T07:00:00-06:00', '2027-02-06T17:00:00-06:00',
 'Parque San Juan de Dios, León - por confirmar',
 E'Guías locales de los Maribios\nFruta al regreso',
 9500.00, 3500.00, 12, 12, NULL, 'activo', '2026-09-25T16:00:00-06:00',
 NULL,
 E'Botas de montaña\n2L de agua\nChaqueta cortaviento\nProtector solar');

-- ---------- campos de formulario (viajes 1 Mombacho, 5 Telica, 7 Mogotón) ----------
INSERT INTO campo_formulario (id_viaje, tipo_campo, etiqueta_pregunta, opciones_respuesta, orden, obligatorio) VALUES
(1, 'seleccion_unica', '¿Tienes experiencia en montañismo?', '["Sí","No"]'::json, 1, true),
(1, 'texto',           'Alergias o condiciones médicas',     NULL,            2, false),
(5, 'seleccion_unica', '¿Has acampado en altura antes?',     '["Sí","No","No, es mi primera vez"]'::json, 1, true),
(5, 'texto',           'Contacto de emergencia (nombre y teléfono)', NULL, 2, true),
(5, 'texto',           'Alergias o condiciones médicas',     NULL,            3, false),
(7, 'seleccion_unica', '¿Cómo evalúas tu condición física actual?', '["Buena","Regular","Necesito mejorar"]'::json, 1, true),
(7, 'texto',           'Medicamentos que tomarás durante la ruta', NULL, 2, false);

-- ---------- reservas ----------
-- Estados: cupos descontados por (1 + acompañantes) en pendiente/aprobada.
-- monto_reserva = abono por persona del viaje (igual que hace el backend).
-- captura_comprobante_url = data-URI SVG (no hay endpoint de upload aún).
INSERT INTO reserva (id_reserva, id_usuario, id_viaje, id_administrador_revisor, estado, fecha_reserva, fecha_limite_pago, fecha_pago, numero_referencia_pago, captura_comprobante_url, motivo_rechazo, fecha_revision, editable) VALUES
-- 1: Carlos, Mogotón (v7), aprobada
(1, 2, 7, 1, 'aprobada', '2026-09-20T08:05:00-06:00', '2026-09-20T08:35:00-06:00', '2026-09-20T08:05:00-06:00', 'TRF-20260920-118',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BAC%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260920-118%20%C2%B7%20C%24%20650.00%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22172%22%20font-family%3D%22sans-serif%22%20font-size%3D%2214%22%20fill%3D%22%2364748b%22%3EA%20nombre%20de%3A%20Club%20Nicarag%C3%BCense%20de%20Monta%C3%B1ismo%3C%2Ftext%3E%3C%2Fsvg%3E',
 NULL, '2026-09-21T10:12:00-06:00', false),
-- 2: Carlos, Mombacho (v1), pendiente
(2, 2, 1, NULL, 'pendiente', '2026-09-28T17:40:00-06:00', '2026-09-28T18:10:00-06:00', '2026-09-28T17:40:00-06:00', 'TRF-20260928-884',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BanPro%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260928-884%20%C2%B7%20C%24%20550.00%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22172%22%20font-family%3D%22sans-serif%22%20font-size%3D%2214%22%20fill%3D%22%2364748b%22%3ECuenta%20100-022-000123-4%20%C2%B7%20C%C3%B3rdobas%3C%2Ftext%3E%3C%2Fsvg%3E',
 NULL, NULL, false),
-- 3: Ana, Somoto (v2) con 2 acompañantes, aprobada
(3, 3, 2, 1, 'aprobada', '2026-09-18T11:00:00-06:00', '2026-09-18T11:30:00-06:00', '2026-09-18T11:00:00-06:00', 'TRF-20260918-332',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BDF%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260918-332%20%C2%B7%20C%24%201350.00%20(3%20personas)%3C%2Ftext%3E%3C%2Fsvg%3E',
 NULL, '2026-09-18T15:45:00-06:00', false),
-- 4: Luis, Mombacho (v1), rechazada (cupos reintegrados)
(4, 4, 1, 1, 'rechazada', '2026-09-22T14:10:00-06:00', '2026-09-22T14:40:00-06:00', '2026-09-22T14:10:00-06:00', 'TRF-20260922-501',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BanPro%20(ilegible)%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260922-501%3C%2Ftext%3E%3C%2Fsvg%3E',
 'Comprobante ilegible: no se distingue el número de referencia.', '2026-09-23T09:00:00-06:00', false),
-- 5: María, Telica (v5) con 1 acompañante, pendiente
(5, 5, 5, NULL, 'pendiente', '2026-09-29T19:22:00-06:00', '2026-09-29T19:52:00-06:00', '2026-09-29T19:22:00-06:00', 'TRF-20260929-776',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BAC%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260929-776%20%C2%B7%20C%24%201100.00%20(2%20personas)%3C%2Ftext%3E%3C%2Fsvg%3E',
 NULL, NULL, false),
-- 6: Ana, Laguna de Apoyo (v8), aprobada
(6, 3, 8, 1, 'aprobada', '2026-09-25T10:00:00-06:00', '2026-09-25T10:30:00-06:00', '2026-09-25T10:00:00-06:00', 'TRF-20260925-210',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BanPro%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260925-210%20%C2%B7%20C%24%20400.00%3C%2Ftext%3E%3C%2Fsvg%3E',
 NULL, '2026-09-25T13:30:00-06:00', false),
-- 7: Luis, Cerro Negro (v4), pendiente
(7, 4, 4, NULL, 'pendiente', '2026-09-29T21:05:00-06:00', '2026-09-29T21:35:00-06:00', '2026-09-29T21:05:00-06:00', 'TRF-20260929-909',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BAC%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260929-909%20%C2%B7%20C%24%20600.00%3C%2Ftext%3E%3C%2Fsvg%3E',
 NULL, NULL, false),
-- 8: María, Cosigüina (v3), rechazada (cupos reintegrados)
(8, 5, 3, 1, 'rechazada', '2026-09-24T12:00:00-06:00', '2026-09-24T12:30:00-06:00', '2026-09-24T12:00:00-06:00', 'TRF-20260924-645',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BDF%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260924-645%20%C2%B7%20monto%20no%20coincide%3C%2Ftext%3E%3C%2Fsvg%3E',
 'El monto del comprobante no corresponde al abono del viaje (C$ 650.00).', '2026-09-24T16:20:00-06:00', false),
-- 9: Carlos, Laguna de Apoyo (v8), aprobada
(9, 2, 8, 1, 'aprobada', '2026-09-26T09:30:00-06:00', '2026-09-26T10:00:00-06:00', '2026-09-26T09:30:00-06:00', 'TRF-20260926-428',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BanPro%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260926-428%20%C2%B7%20C%24%20400.00%3C%2Ftext%3E%3C%2Fsvg%3E',
 NULL, '2026-09-26T14:00:00-06:00', false),
-- 10: Ana, Concepción (v6) con 1 acompañante, pendiente
(10, 3, 6, NULL, 'pendiente', '2026-09-30T08:12:00-06:00', '2026-09-30T08:42:00-06:00', '2026-09-30T08:12:00-06:00', 'TRF-20260930-318',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BAC%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260930-318%20%C2%B7%20C%24%201400.00%20(2%20personas)%3C%2Ftext%3E%3C%2Fsvg%3E',
 NULL, NULL, false),
-- 11: Ana, Cosigüina (v3), expirada (cupo retenido, sin flujo de expiración aún)
(11, 3, 3, NULL, 'expirada', '2026-09-15T18:30:00-06:00', '2026-09-15T19:00:00-06:00', '2026-09-15T18:30:00-06:00', NULL, NULL, NULL, NULL, false),
-- 12: Luis, Concepción (v6), aprobada
(12, 4, 6, 1, 'aprobada', '2026-09-27T11:20:00-06:00', '2026-09-27T11:50:00-06:00', '2026-09-27T11:20:00-06:00', 'TRF-20260927-542',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BDF%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260927-542%20%C2%B7%20C%24%20700.00%3C%2Ftext%3E%3C%2Fsvg%3E',
 NULL, '2026-09-27T17:05:00-06:00', false),
-- 13: María, Concepción (v6), aprobada
(13, 5, 6, 1, 'aprobada', '2026-09-28T16:44:00-06:00', '2026-09-28T17:14:00-06:00', '2026-09-28T16:44:00-06:00', 'TRF-20260928-660',
 'data:image/svg+xml;utf8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22640%22%20height%3D%22400%22%3E%3Crect%20width%3D%22640%22%20height%3D%22400%22%20fill%3D%22%23f1f5f9%22%2F%3E%3Crect%20x%3D%2240%22%20y%3D%2240%22%20width%3D%22560%22%20height%3D%22320%22%20fill%3D%22%23ffffff%22%20stroke%3D%22%23cbd5e1%22%2F%3E%3Ctext%20x%3D%2264%22%20y%3D%2296%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20font-weight%3D%22bold%22%20fill%3D%22%230f172a%22%3EComprobante%20BanPro%3C%2Ftext%3E%3Ctext%20x%3D%2264%22%20y%3D%22140%22%20font-family%3D%22monospace%22%20font-size%3D%2216%22%20fill%3D%22%23334155%22%3EREF%3A%20TRF-20260928-660%20%C2%B7%20C%24%20700.00%3C%2Ftext%3E%3C%2Fsvg%3E',
 NULL, '2026-09-29T08:30:00-06:00', false);

SELECT setval('reserva_id_reserva_seq', 13);

-- ---------- acompañantes ----------
INSERT INTO acompanante (id_reserva, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, tipo_identificacion, numero_identificacion) VALUES
(3,  'Javier',  NULL,   'López',   'Herrera', 'cedula', '001-120485-0005E'),
(3,  'Camila',  NULL,   'López',   'Herrera', 'cedula', '001-180720-0006F'),
(5,  'Gabriel', 'Ernesto','Novoa', 'Rosales', 'cedula', '001-220386-0007G'),
(10, 'Marta',   NULL,   'Pérez',   'Silva',   'cedula', '001-190995-0008H');

-- ---------- respuestas de formulario ----------
-- Campos: 1-2 → viaje 1 (Mombacho); 3-5 → viaje 5 (Telica); 6-7 → viaje 7 (Mogotón)
INSERT INTO respuesta_formulario (id_reserva, id_campo, valor_respuesta) VALUES
(2, 1, 'Sí'), (2, 2, 'Ninguna'),
(5, 3, 'Sí, he acampado antes'), (5, 4, 'Ernesto Novoa - +505 8123-4567'), (5, 5, 'Alergia al polen'),
(1, 6, 'Buena'), (1, 7, 'Ninguno');

-- ---------- notificaciones (tipos: nuevo_viaje, pocos_cupos, reserva_aprobada, reserva_rechazada) ----------
INSERT INTO notificacion (id_usuario, tipo, mensaje, fecha_envio, leida) VALUES
(2, 'reserva_aprobada',  '¡Tu reserva #1 ha sido aprobada!', '2026-09-21T10:12:00-06:00', true),
(2, 'reserva_aprobada',  '¡Tu reserva #9 ha sido aprobada!', '2026-09-26T14:00:00-06:00', false),
(2, 'nuevo_viaje',       '¡Nuevo viaje publicado: Volcán Asososca! Revisa el catálogo.', '2026-09-25T16:00:00-06:00', true),
(3, 'reserva_aprobada',  '¡Tu reserva #3 ha sido aprobada!', '2026-09-18T15:45:00-06:00', true),
(3, 'reserva_aprobada',  '¡Tu reserva #6 ha sido aprobada!', '2026-09-25T13:30:00-06:00', false),
(3, 'pocos_cupos',       '¡Últimos cupos! El Volcán Concepción tiene 2 cupos disponibles.', '2026-09-29T12:00:00-06:00', false),
(3, 'nuevo_viaje',       '¡Nuevo viaje publicado: Volcán Acatenango! Febrero 2027, cupos limitados.', '2026-09-20T15:00:00-06:00', true),
(4, 'reserva_rechazada', 'Tu reserva #4 ha sido rechazada. Motivo: Comprobante ilegible: no se distingue el número de referencia.', '2026-09-23T09:00:00-06:00', true),
(5, 'reserva_rechazada', 'Tu reserva #8 ha sido rechazada. Motivo: El monto del comprobante no corresponde al abono del viaje (C$ 650.00).', '2026-09-24T16:20:00-06:00', false),
(5, 'nuevo_viaje',       '¡Nuevo viaje publicado: Cañón de Somoto! Cupos abiertos.', '2026-09-10T09:05:00-06:00', true);

-- ---------- verificación ----------
SELECT 'usuarios' t, count(*) FROM usuario
UNION ALL SELECT 'viajes', count(*) FROM viaje
UNION ALL SELECT 'campos', count(*) FROM campo_formulario
UNION ALL SELECT 'reservas', count(*) FROM reserva
UNION ALL SELECT 'acompañantes', count(*) FROM acompanante
UNION ALL SELECT 'respuestas', count(*) FROM respuesta_formulario
UNION ALL SELECT 'notificaciones', count(*) FROM notificacion;

COMMIT;
