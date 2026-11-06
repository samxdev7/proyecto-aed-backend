-- =====================================================================
-- CNM — Llenado de Registros en Supabase
-- 1. Preguntas de evaluación (experiencia, condición física, salud y docs nicaragüenses)
-- 2. Activación de los 5 viajes de referencia (12, 13, 14, 15, 16)
-- 3. Inserción de 35 nuevos usuarios nicaragüenses
-- 4. Inserción de 35 nuevas reservas con respuestas de formulario y acompañantes
-- 5. Actualización proporcional de cupos disponibles
-- REGLA ESTRICTA: NO SE ELIMINA NINGUNA INFORMACIÓN EXISTENTE.
-- =====================================================================

BEGIN;

-- ---------------------------------------------------------------------
-- 1. INSERTAR PREGUNTAS DE EVALUACIÓN PARA LOS 5 VIAJES
-- ---------------------------------------------------------------------

-- Viaje 16: Cumbres de Centroamérica 🇬🇹 Guatemala — Volcán Tajumulco ⛰️ (Dificultad: Extrema)
INSERT INTO app.campo_formulario (id_viaje, tipo_campo, etiqueta_pregunta, opciones_respuesta, orden, obligatorio)
VALUES
(16, 'seleccion_unica', '¿Has ascendido previamente cumbres o volcanes superiores a 3,000 msnm (Acatenango, Fuego o similares)?', '["Sí, cuento con experiencia comprobada en alta montaña","He ascendido entre 1,500 y 3,000 msnm","No, sería mi primera cumbre de gran altitud"]', 1, true),
(16, 'seleccion_unica', '¿Cómo califica su resistencia aeróbica para caminatas de más de 8 horas con mochila técnica en alta montaña?', '["Excelente (entreno resistencia semanalmente)","Buena (actividad física regular)","Intermedia / Requiere preparación"]', 2, true),
(16, 'seleccion_unica', '¿Padece o ha padecido de afecciones cardíacas, asma o antecedentes de mal agudo de montaña (soroche)?', '["No padezco ninguna de estas condiciones","Sí, controlada bajo supervisión médica","Sí, padezco afecciones cardíacas/respiratorias"]', 3, true),
(16, 'archivo', 'Pasaporte nicaragüense vigente (mínimo 6 meses de vigencia para trámite migratorio en Guatemala)', NULL, 4, true),
(16, 'archivo', 'Certificado o constancia médica de aptitud física expedida por médico nicaragüense', NULL, 5, true),
(16, 'texto', 'Contacto de emergencia en Nicaragua (Nombre completo, cédula nicaragüense y teléfono)', NULL, 6, true);

-- Viaje 15: PROYECTO 7 CUMBRES DE CENTROAMÉRICA 🇸🇻 CERRO EL PITAL | EL SALVADOR (Dificultad: Alta)
INSERT INTO app.campo_formulario (id_viaje, tipo_campo, etiqueta_pregunta, opciones_respuesta, orden, obligatorio)
VALUES
(15, 'seleccion_unica', '¿Cuenta con experiencia en expediciones de montaña continuas de más de 5 horas?', '["Sí, frecuentemente","Sí, algunas veces","No, solo caminatas recreativas"]', 1, true),
(15, 'seleccion_unica', '¿Con qué frecuencia realiza ejercicios de fuerza o cardio que preparen sus articulaciones para pendientes pronunciadas?', '["3 o más veces por semana","1 a 2 veces por semana","Ocasional / Sedentario"]', 2, true),
(15, 'seleccion_unica', '¿Presenta lesiones actuales en rodillas, tobillos o espalda que limiten descensos técnicos?', '["No, libre de lesiones","Molestias leves ocasionales","Sí, lesión crónica diagnosticada"]', 3, true),
(15, 'archivo', 'Cédula de identidad nicaragüense o pasaporte nicaragüense vigente (para paso fronterizo CA-4)', NULL, 4, true),
(15, 'archivo', 'Constancia médica o formulario de salud deportiva', NULL, 5, true),
(15, 'texto', 'Nombre y teléfono de contacto de emergencia en Nicaragua', NULL, 6, true);

-- Viaje 14: Caminata de Entrenamiento – Cerro Apante, Matagalpa (Dificultad: Baja)
INSERT INTO app.campo_formulario (id_viaje, tipo_campo, etiqueta_pregunta, opciones_respuesta, orden, obligatorio)
VALUES
(14, 'seleccion_unica', '¿Ha participado anteriormente en senderismo o caminatas ecológicas?', '["Sí","No, es mi primera experiencia"]', 1, true),
(14, 'seleccion_unica', '¿Puede mantener una caminata sostenida de 3 a 4 horas por senderos naturales?', '["Sí, sin dificultad","Sí, requiriendo pausas breves","No estoy seguro/a"]', 2, true),
(14, 'texto', 'Alergias a picaduras, plantas o medicamentos que deba reportar a los guías', NULL, 3, false),
(14, 'archivo', 'Copia o foto legible de Cédula de Identidad Nicaragüense (o carnet de minoridad)', NULL, 4, true),
(14, 'texto', 'Contacto de emergencia (Nombre y teléfono nicaragüense)', NULL, 5, true);

-- Viaje 13: Ascenso al Cerro Musún. (Dificultad: Baja)
INSERT INTO app.campo_formulario (id_viaje, tipo_campo, etiqueta_pregunta, opciones_respuesta, orden, obligatorio)
VALUES
(13, 'seleccion_unica', '¿Tiene experiencia transitando por senderos lodosos o cruces de ríos?', '["Sí, tengo experiencia","No, pero cuento con el calzado y disposición"]', 1, true),
(13, 'seleccion_unica', '¿Cómo evalúa su condición para caminatas en clima cálido húmedo tropical?', '["Óptima","Buena","Básica"]', 2, true),
(13, 'texto', 'Condiciones médicas particulares o problemas de presión arterial', NULL, 3, false),
(13, 'archivo', 'Cédula de identidad nicaragüense del titular de la reserva', NULL, 4, true),
(13, 'texto', 'Contacto de emergencia en Nicaragua (Nombre, parentesco y teléfono)', NULL, 5, true);

-- Viaje 12: PEÑA LABRADA | SANTA LUCÍA, BOACO (Dificultad: Baja)
INSERT INTO app.campo_formulario (id_viaje, tipo_campo, etiqueta_pregunta, opciones_respuesta, orden, obligatorio)
VALUES
(12, 'seleccion_unica', '¿Ha realizado senderismo por formaciones rocosas en el centro del país?', '["Sí","No"]', 1, true),
(12, 'seleccion_unica', '¿Presenta vértigo severo en miradores naturales de roca?', '["No","Leve","Sí, severo"]', 2, true),
(12, 'archivo', 'Copia de Cédula de Identidad Nicaragüense', NULL, 3, true),
(12, 'texto', 'Contacto de emergencia (Nombre y número telefónico)', NULL, 4, true);

-- ---------------------------------------------------------------------
-- 2. ACTIVAR LOS 5 VIAJES DE REFERENCIA
-- ---------------------------------------------------------------------
UPDATE app.viaje
SET estado = 'activo'
WHERE id_viaje IN (12, 13, 14, 15, 16);

-- ---------------------------------------------------------------------
-- 3. INSERTAR 35 NUEVOS USUARIOS NICARAGÜENSES
-- Hash BCrypt para contraseña 'Cliente1234':
-- $2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G
-- ---------------------------------------------------------------------
INSERT INTO app.usuario (
    primer_nombre, segundo_nombre, primer_apellido, segundo_apellido,
    correo, contrasena_hash, rol, telefono, sexo, nacionalidad,
    tipo_identificacion, numero_identificacion, notificaciones_habilitadas, fecha_registro
) VALUES
('Marlon', 'José', 'Blandón', 'Zamora', 'marlon.blandon@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8421-5510', 'M', 'Nicaragüense', 'cedula', '001-140288-0002F', true, '2026-08-01 10:00:00+00'),
('Katherine', 'Elena', 'Gutiérrez', 'Orozco', 'katherine.gutierrez@outlook.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8632-1190', 'F', 'Nicaragüense', 'cedula', '001-220792-0004K', true, '2026-08-03 11:30:00+00'),
('Néstor', 'David', 'Chamorro', 'Silva', 'nestor.chamorro@yahoo.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8914-7723', 'M', 'Nicaragüense', 'cedula', '001-051185-0001B', true, '2026-08-05 09:15:00+00'),
('Brenda', 'Lucía', 'Argeñal', 'Mendoza', 'brenda.argenal@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8520-3344', 'F', 'Nicaragüense', 'cedula', '081-190396-0002A', true, '2026-08-08 14:20:00+00'),
('Jairo', 'Antonio', 'Picado', 'Rivas', 'jairo.picado@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8745-9988', 'M', 'Nicaragüense', 'cedula', '441-110889-0003P', true, '2026-08-10 16:45:00+00'),
('Claudia', 'Vanessa', 'Espinoza', 'Duarte', 'claudia.espinoza@hotmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8312-4467', 'F', 'Nicaragüense', 'cedula', '281-250991-0005M', true, '2026-08-12 18:10:00+00'),
('Gabriel', 'Alejandro', 'Sequeira', 'Jarquín', 'gabriel.sequeira@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8890-1234', 'M', 'Nicaragüense', 'cedula', '121-040687-0001S', true, '2026-08-15 08:30:00+00'),
('Xochilt', 'del Carmen', 'Mairena', 'Urbina', 'xochilt.mairena@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8654-7890', 'F', 'Nicaragüense', 'cedula', '601-300194-0002X', true, '2026-08-18 10:40:00+00'),
('Roberto', 'Carlos', 'Baldizón', 'Vega', 'roberto.baldizon@outlook.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8478-2211', 'M', 'Nicaragüense', 'cedula', '001-180490-0003R', true, '2026-08-20 15:00:00+00'),
('Tania', 'Marcela', 'Cardenal', 'Solís', 'tania.cardenal@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8234-5678', 'F', 'Nicaragüense', 'cedula', '001-091293-0004T', true, '2026-08-22 17:35:00+00'),
('Franklin', 'Eduardo', 'Centeno', 'Valle', 'franklin.centeno@yahoo.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8987-6543', 'M', 'Nicaragüense', 'cedula', '321-150586-0001C', true, '2026-08-25 09:20:00+00'),
('Deyanira', 'Auxiliadora', 'Moraga', 'Pavón', 'deyanira.moraga@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8712-3344', 'F', 'Nicaragüense', 'cedula', '401-270895-0002D', true, '2026-08-28 12:15:00+00'),
('Álvaro', 'José', 'Zeledón', 'Castro', 'alvaro.zeledon@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8567-8901', 'M', 'Nicaragüense', 'cedula', '161-030988-0003Z', true, '2026-09-01 11:00:00+00'),
('Iliana', 'Patricia', 'Somarriba', 'Guido', 'iliana.somarriba@hotmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8345-6789', 'F', 'Nicaragüense', 'cedula', '081-120297-0001I', true, '2026-09-03 14:50:00+00'),
('Walter', 'Enrique', 'Benavidez', 'Lazo', 'walter.benavidez@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8123-4567', 'M', 'Nicaragüense', 'cedula', '201-241190-0002B', true, '2026-09-05 16:30:00+00'),
('Yamileth', 'del Socorro', 'Téllez', 'Obando', 'yamileth.tellez@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8901-2345', 'F', 'Nicaragüense', 'cedula', '001-080392-0003Y', true, '2026-09-08 08:45:00+00'),
('Ronald', 'Mauricio', 'Salgado', 'Cano', 'ronald.salgado@outlook.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8789-0123', 'M', 'Nicaragüense', 'cedula', '561-170784-0004R', true, '2026-09-10 13:20:00+00'),
('Fátima', 'Beatriz', 'Quintero', 'Rocha', 'fatima.quintero@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8543-2109', 'F', 'Nicaragüense', 'cedula', '241-290596-0001F', true, '2026-09-12 10:10:00+00'),
('Silvio', 'Manuel', 'Calero', 'Velásquez', 'silvio.calero@yahoo.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8321-0987', 'M', 'Nicaragüense', 'cedula', '001-211089-0002S', true, '2026-09-14 15:40:00+00'),
('Heydi', 'Lorena', 'Sandino', 'Rugama', 'heydi.sandino@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8654-3210', 'F', 'Nicaragüense', 'cedula', '161-140493-0003H', true, '2026-09-16 09:30:00+00'),
('Lester', 'Francisco', 'Vivas', 'Amador', 'lester.vivas@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8876-5432', 'M', 'Nicaragüense', 'cedula', '001-061286-0001L', true, '2026-09-18 11:15:00+00'),
('Martha', 'Cecilia', 'Lacayo', 'Fonseca', 'martha.lacayo@hotmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8432-1098', 'F', 'Nicaragüense', 'cedula', '001-180894-0004M', true, '2026-09-20 14:00:00+00'),
('Guillermo', 'Antonio', 'Moncada', 'Cuadra', 'guillermo.moncada@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8210-9876', 'M', 'Nicaragüense', 'cedula', '041-020188-0002G', true, '2026-09-22 16:50:00+00'),
('Danelia', 'Esperanza', 'Borge', 'Pineda', 'danelia.borge@outlook.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8999-1122', 'F', 'Nicaragüense', 'cedula', '361-230995-0003D', true, '2026-09-24 10:20:00+00'),
('Noel', 'Alberto', 'Tercero', 'Gadea', 'noel.tercero@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8777-2233', 'M', 'Nicaragüense', 'cedula', '481-110685-0001N', true, '2026-09-26 12:45:00+00'),
('Scarleth', 'Karina', 'Palacios', 'Vado', 'scarleth.palacios@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8555-3344', 'F', 'Nicaragüense', 'cedula', '001-280296-0002S', true, '2026-09-28 15:10:00+00'),
('Denis', 'Javier', 'Arróliga', 'Rivas', 'denis.arroliga@yahoo.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8333-4455', 'M', 'Nicaragüense', 'cedula', '281-160790-0003D', true, '2026-09-30 09:00:00+00'),
('Mayling', 'del Rosario', 'Cruz', 'Hurtado', 'mayling.cruz@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8111-5566', 'F', 'Nicaragüense', 'cedula', '001-041193-0004M', true, '2026-10-02 11:40:00+00'),
('Bayardo', 'José', 'Chévez', 'Barquero', 'bayardo.chevez@hotmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8888-6677', 'M', 'Nicaragüense', 'cedula', '001-200587-0001B', true, '2026-10-04 14:15:00+00'),
('Indira', 'Tatiana', 'Zepeda', 'Mena', 'indira.zepeda@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8666-7788', 'F', 'Nicaragüense', 'cedula', '081-131092-0002I', true, '2026-10-06 17:00:00+00'),
('Mario', 'René', 'Tinoco', 'Loáisiga', 'mario.tinoco@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8444-8899', 'M', 'Nicaragüense', 'cedula', '321-070389-0003M', true, '2026-10-08 08:30:00+00'),
('Rosa', 'Emilia', 'Blandón', 'Sequeira', 'rosa.blandon@outlook.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8222-9900', 'F', 'Nicaragüense', 'cedula', '441-260894-0001R', true, '2026-10-10 10:50:00+00'),
('Cristian', 'David', 'Oporta', 'Reyes', 'cristian.oporta@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8900-1133', 'M', 'Nicaragüense', 'cedula', '121-151286-0002C', true, '2026-10-12 13:25:00+00'),
('Karen', 'Sofía', 'Potosme', 'Guadamuz', 'karen.potosme@yahoo.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8711-2244', 'F', 'Nicaragüense', 'cedula', '401-090497-0003K', true, '2026-10-14 16:00:00+00'),
('Héctor', 'Iván', 'Largaespada', 'Solano', 'hector.largaespada@gmail.com', '$2a$10$TqSwvkVFPqGVLrhDi/rn6uDNpzy6iWOhKk3p1S2QMblxeu8Xz6k5G', 'cliente', '+505 8522-3355', 'M', 'Nicaragüense', 'cedula', '001-300688-0001H', true, '2026-10-16 18:30:00+00');

-- ---------------------------------------------------------------------
-- 4. INSERTAR 35 RESERVAS UTILIZANDO SUBCONSULTAS EXACTAS POR CORREO
-- ---------------------------------------------------------------------

-- Reservas para Viaje 16 (Tajumulco - Guatemala): 5 Aprobadas, 2 Pendientes
INSERT INTO app.reserva (
    id_usuario, id_viaje, id_administrador_revisor, estado,
    fecha_reserva, fecha_limite_pago, fecha_pago, numero_referencia_pago,
    captura_comprobante_url, motivo_rechazo, fecha_revision, editable
) VALUES
-- Gabriel Sequeira -> Tajumulco (Aprobada)
((SELECT id_usuario FROM app.usuario WHERE correo = 'gabriel.sequeira@gmail.com'), 16, 1, 'aprobada', '2026-10-18 10:00:00+00', '2026-10-18 10:30:00+00', '2026-10-18 10:18:00+00', 'BAC-7712390', '/comprobantes/bac_7712390.pdf', NULL, '2026-10-18 14:00:00+00', false),
-- Roberto Baldizón -> Tajumulco (Aprobada, con 1 acompañante)
((SELECT id_usuario FROM app.usuario WHERE correo = 'roberto.baldizon@outlook.com'), 16, 1, 'aprobada', '2026-10-19 11:20:00+00', '2026-10-19 11:50:00+00', '2026-10-19 11:35:00+00', 'LAFISE-991204', '/comprobantes/lafise_991204.jpg', NULL, '2026-10-19 15:30:00+00', false),
-- Franklin Centeno -> Tajumulco (Aprobada)
((SELECT id_usuario FROM app.usuario WHERE correo = 'franklin.centeno@yahoo.com'), 16, 1, 'aprobada', '2026-10-20 09:10:00+00', '2026-10-20 09:40:00+00', '2026-10-20 09:22:00+00', 'BANPRO-441092', '/comprobantes/banpro_441092.png', NULL, '2026-10-20 12:00:00+00', false),
-- Álvaro Zeledón -> Tajumulco (Aprobada)
((SELECT id_usuario FROM app.usuario WHERE correo = 'alvaro.zeledon@gmail.com'), 16, 1, 'aprobada', '2026-10-21 14:00:00+00', '2026-10-21 14:30:00+00', '2026-10-21 14:15:00+00', 'BAC-6623190', '/comprobantes/bac_6623190.pdf', NULL, '2026-10-21 17:00:00+00', false),
-- Walter Benavidez -> Tajumulco (Aprobada)
((SELECT id_usuario FROM app.usuario WHERE correo = 'walter.benavidez@gmail.com'), 16, 1, 'aprobada', '2026-10-22 16:30:00+00', '2026-10-22 17:00:00+00', '2026-10-22 16:42:00+00', 'BDF-331209', '/comprobantes/bdf_331209.jpg', NULL, '2026-10-22 18:30:00+00', false),
-- Ronald Salgado -> Tajumulco (Pendiente)
((SELECT id_usuario FROM app.usuario WHERE correo = 'ronald.salgado@outlook.com'), 16, NULL, 'pendiente', '2026-11-01 08:30:00+00', '2026-11-01 09:00:00+00', '2026-11-01 08:48:00+00', 'BAC-1123489', '/comprobantes/bac_1123489.png', NULL, NULL, false),
-- Lester Vivas -> Tajumulco (Pendiente)
((SELECT id_usuario FROM app.usuario WHERE correo = 'lester.vivas@gmail.com'), 16, NULL, 'pendiente', '2026-11-02 11:00:00+00', '2026-11-02 11:30:00+00', '2026-11-02 11:14:00+00', 'LAFISE-554411', '/comprobantes/lafise_554411.jpg', NULL, NULL, false),

-- Reservas para Viaje 15 (Cerro El Pital - El Salvador): 5 Aprobadas, 2 Pendientes
-- Xochilt Mairena -> El Pital (Aprobada)
((SELECT id_usuario FROM app.usuario WHERE correo = 'xochilt.mairena@gmail.com'), 15, 1, 'aprobada', '2026-10-18 14:00:00+00', '2026-10-18 14:30:00+00', '2026-10-18 14:12:00+00', 'BAC-5510293', '/comprobantes/bac_5510293.pdf', NULL, '2026-10-18 16:00:00+00', false),
-- Tania Cardenal -> El Pital (Aprobada, con 1 acompañante)
((SELECT id_usuario FROM app.usuario WHERE correo = 'tania.cardenal@gmail.com'), 15, 1, 'aprobada', '2026-10-19 15:40:00+00', '2026-10-19 16:10:00+00', '2026-10-19 15:55:00+00', 'LAFISE-883012', '/comprobantes/lafise_883012.jpg', NULL, '2026-10-19 18:00:00+00', false),
-- Deyanira Moraga -> El Pital (Aprobada)
((SELECT id_usuario FROM app.usuario WHERE correo = 'deyanira.moraga@gmail.com'), 15, 1, 'aprobada', '2026-10-20 10:20:00+00', '2026-10-20 10:50:00+00', '2026-10-20 10:33:00+00', 'BANPRO-992014', '/comprobantes/banpro_992014.png', NULL, '2026-10-20 14:30:00+00', false),
-- Iliana Somarriba -> El Pital (Aprobada)
((SELECT id_usuario FROM app.usuario WHERE correo = 'iliana.somarriba@hotmail.com'), 15, 1, 'aprobada', '2026-10-21 16:10:00+00', '2026-10-21 16:40:00+00', '2026-10-21 16:25:00+00', 'BAC-1129304', '/comprobantes/bac_1129304.pdf', NULL, '2026-10-21 18:30:00+00', false),
-- Yamileth Téllez -> El Pital (Aprobada)
((SELECT id_usuario FROM app.usuario WHERE correo = 'yamileth.tellez@gmail.com'), 15, 1, 'aprobada', '2026-10-22 17:00:00+00', '2026-10-22 17:30:00+00', '2026-10-22 17:15:00+00', 'BDF-772910', '/comprobantes/bdf_772910.jpg', NULL, '2026-10-22 19:00:00+00', false),
-- Fátima Quintero -> El Pital (Pendiente)
((SELECT id_usuario FROM app.usuario WHERE correo = 'fatima.quintero@gmail.com'), 15, NULL, 'pendiente', '2026-11-01 10:15:00+00', '2026-11-01 10:45:00+00', '2026-11-01 10:28:00+00', 'BAC-3344122', '/comprobantes/bac_3344122.jpg', NULL, NULL, false),
-- Martha Lacayo -> El Pital (Pendiente)
((SELECT id_usuario FROM app.usuario WHERE correo = 'martha.lacayo@hotmail.com'), 15, NULL, 'pendiente', '2026-11-02 14:30:00+00', '2026-11-02 15:00:00+00', '2026-11-02 14:46:00+00', 'LAFISE-776655', '/comprobantes/lafise_776655.pdf', NULL, NULL, false),

-- Reservas para Viaje 14 (Cerro Apante - Matagalpa): 7 Aprobadas, 2 Pendientes, 1 Rechazada
((SELECT id_usuario FROM app.usuario WHERE correo = 'silvio.calero@yahoo.com'), 14, 1, 'aprobada', '2026-10-23 09:00:00+00', '2026-10-23 09:30:00+00', '2026-10-23 09:12:00+00', 'BAC-1234567', '/comprobantes/bac_1234567.jpg', NULL, '2026-10-23 11:00:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'heydi.sandino@gmail.com'), 14, 1, 'aprobada', '2026-10-23 10:30:00+00', '2026-10-23 11:00:00+00', '2026-10-23 10:45:00+00', 'LAFISE-2345678', '/comprobantes/lafise_2345678.png', NULL, '2026-10-23 12:30:00+00', false),
-- Guillermo Moncada -> Cerro Apante (Aprobada, con 1 acompañante)
((SELECT id_usuario FROM app.usuario WHERE correo = 'guillermo.moncada@gmail.com'), 14, 1, 'aprobada', '2026-10-24 11:15:00+00', '2026-10-24 11:45:00+00', '2026-10-24 11:29:00+00', 'BANPRO-3456789', '/comprobantes/banpro_3456789.pdf', NULL, '2026-10-24 15:00:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'danelia.borge@outlook.com'), 14, 1, 'aprobada', '2026-10-24 14:00:00+00', '2026-10-24 14:30:00+00', '2026-10-24 14:18:00+00', 'BDF-4567890', '/comprobantes/bdf_4567890.jpg', NULL, '2026-10-24 16:30:00+00', false),
-- Noel Tercero -> Cerro Apante (Aprobada, con 1 acompañante)
((SELECT id_usuario FROM app.usuario WHERE correo = 'noel.tercero@gmail.com'), 14, 1, 'aprobada', '2026-10-25 08:45:00+00', '2026-10-25 09:15:00+00', '2026-10-25 08:58:00+00', 'BAC-5678901', '/comprobantes/bac_5678901.pdf', NULL, '2026-10-25 10:30:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'scarleth.palacios@gmail.com'), 14, 1, 'aprobada', '2026-10-25 15:20:00+00', '2026-10-25 15:50:00+00', '2026-10-25 15:36:00+00', 'LAFISE-6789012', '/comprobantes/lafise_6789012.png', NULL, '2026-10-25 17:00:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'denis.arroliga@yahoo.com'), 14, 1, 'aprobada', '2026-10-26 12:10:00+00', '2026-10-26 12:40:00+00', '2026-10-26 12:22:00+00', 'BANPRO-7890123', '/comprobantes/banpro_7890123.jpg', NULL, '2026-10-26 14:00:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'mayling.cruz@gmail.com'), 14, NULL, 'pendiente', '2026-11-03 09:30:00+00', '2026-11-03 10:00:00+00', '2026-11-03 09:44:00+00', 'BAC-8901234', '/comprobantes/bac_8901234.pdf', NULL, NULL, false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'bayardo.chevez@hotmail.com'), 14, NULL, 'pendiente', '2026-11-03 13:00:00+00', '2026-11-03 13:30:00+00', '2026-11-03 13:17:00+00', 'LAFISE-9012345', '/comprobantes/lafise_9012345.png', NULL, NULL, false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'indira.zepeda@gmail.com'), 14, 1, 'rechazada', '2026-10-27 16:00:00+00', '2026-10-27 16:30:00+00', '2026-10-27 16:15:00+00', 'BANPRO-0123456', '/comprobantes/banpro_0123456.jpg', 'Comprobante de transferencia bancaria ilegible y número de referencia no coincide.', '2026-10-28 09:00:00+00', false),

-- Reservas para Viaje 13 (Cerro Musún): 5 Aprobadas, 2 Pendientes
((SELECT id_usuario FROM app.usuario WHERE correo = 'mario.tinoco@gmail.com'), 13, 1, 'aprobada', '2026-10-26 10:00:00+00', '2026-10-26 10:30:00+00', '2026-10-26 10:14:00+00', 'BAC-2233445', '/comprobantes/bac_2233445.pdf', NULL, '2026-10-26 13:00:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'rosa.blandon@outlook.com'), 13, 1, 'aprobada', '2026-10-27 11:30:00+00', '2026-10-27 12:00:00+00', '2026-10-27 11:46:00+00', 'LAFISE-3344556', '/comprobantes/lafise_3344556.png', NULL, '2026-10-27 14:30:00+00', false),
-- Cristian Oporta -> Cerro Musún (Aprobada, con 1 acompañante)
((SELECT id_usuario FROM app.usuario WHERE correo = 'cristian.oporta@gmail.com'), 13, 1, 'aprobada', '2026-10-28 14:15:00+00', '2026-10-28 14:45:00+00', '2026-10-28 14:30:00+00', 'BANPRO-4455667', '/comprobantes/banpro_4455667.jpg', NULL, '2026-10-28 16:00:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'karen.potosme@yahoo.com'), 13, 1, 'aprobada', '2026-10-29 09:40:00+00', '2026-10-29 10:10:00+00', '2026-10-29 09:55:00+00', 'BDF-5566778', '/comprobantes/bdf_5566778.pdf', NULL, '2026-10-29 12:00:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'hector.largaespada@gmail.com'), 13, 1, 'aprobada', '2026-10-29 16:20:00+00', '2026-10-29 16:50:00+00', '2026-10-29 16:34:00+00', 'BAC-6677889', '/comprobantes/bac_6677889.png', NULL, '2026-10-29 18:30:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'marlon.blandon@gmail.com'), 13, NULL, 'pendiente', '2026-11-04 10:00:00+00', '2026-11-04 10:30:00+00', '2026-11-04 10:19:00+00', 'LAFISE-7788990', '/comprobantes/lafise_7788990.jpg', NULL, NULL, false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'katherine.gutierrez@outlook.com'), 13, NULL, 'pendiente', '2026-11-04 15:30:00+00', '2026-11-04 16:00:00+00', '2026-11-04 15:48:00+00', 'BANPRO-8899001', '/comprobantes/banpro_8899001.pdf', NULL, NULL, false),

-- Reservas para Viaje 12 (Peña Labrada - Boaco): 4 Aprobadas, 1 Pendiente, 1 Expirada
-- Néstor Chamorro -> Peña Labrada (Aprobada, con 1 acompañante)
((SELECT id_usuario FROM app.usuario WHERE correo = 'nestor.chamorro@yahoo.com'), 12, 1, 'aprobada', '2026-10-26 14:00:00+00', '2026-10-26 14:30:00+00', '2026-10-26 14:18:00+00', 'BAC-9900112', '/comprobantes/bac_9900112.png', NULL, '2026-10-26 16:30:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'brenda.argenal@gmail.com'), 12, 1, 'aprobada', '2026-10-27 10:15:00+00', '2026-10-27 10:45:00+00', '2026-10-27 10:29:00+00', 'LAFISE-0011223', '/comprobantes/lafise_0011223.pdf', NULL, '2026-10-27 12:00:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'jairo.picado@gmail.com'), 12, 1, 'aprobada', '2026-10-28 09:30:00+00', '2026-10-28 10:00:00+00', '2026-10-28 09:44:00+00', 'BANPRO-1122334', '/comprobantes/banpro_1122334.jpg', NULL, '2026-10-28 11:30:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'claudia.espinoza@hotmail.com'), 12, 1, 'aprobada', '2026-10-28 15:50:00+00', '2026-10-28 16:20:00+00', '2026-10-28 16:05:00+00', 'BDF-2233445', '/comprobantes/bdf_2233445.png', NULL, '2026-10-28 17:30:00+00', false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'gabriel.sequeira@gmail.com'), 12, NULL, 'pendiente', '2026-11-04 11:10:00+00', '2026-11-04 11:40:00+00', '2026-11-04 11:25:00+00', 'BAC-3344556', '/comprobantes/bac_3344556.pdf', NULL, NULL, false),
((SELECT id_usuario FROM app.usuario WHERE correo = 'xochilt.mairena@gmail.com'), 12, NULL, 'expirada', '2026-10-15 08:00:00+00', '2026-10-15 08:30:00+00', NULL, NULL, NULL, 'No se completó el reporte de pago dentro del tiempo límite de 30 minutos.', NULL, false);

-- ---------------------------------------------------------------------
-- 5. INSERTAR ACOMPAÑANTES PARA RESERVAS CON MÁS DE 1 CUPO
-- ---------------------------------------------------------------------
INSERT INTO app.acompanante (id_reserva, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, tipo_identificacion, numero_identificacion)
SELECT r.id_reserva, 'Camila', 'Sofía', 'Baldizón', 'Gómez', 'cedula', '001-190696-0005C'
FROM app.reserva r 
JOIN app.usuario u ON u.id_usuario = r.id_usuario
WHERE u.correo = 'roberto.baldizon@outlook.com' AND r.id_viaje = 16 
LIMIT 1;

INSERT INTO app.acompanante (id_reserva, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, tipo_identificacion, numero_identificacion)
SELECT r.id_reserva, 'Alejandro', 'José', 'Cardenal', 'Reyes', 'cedula', '001-140590-0003A'
FROM app.reserva r 
JOIN app.usuario u ON u.id_usuario = r.id_usuario
WHERE u.correo = 'tania.cardenal@gmail.com' AND r.id_viaje = 15 
LIMIT 1;

INSERT INTO app.acompanante (id_reserva, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, tipo_identificacion, numero_identificacion)
SELECT r.id_reserva, 'Verónica', 'Lucía', 'Moncada', 'Rivas', 'cedula', '041-230891-0002V'
FROM app.reserva r 
JOIN app.usuario u ON u.id_usuario = r.id_usuario
WHERE u.correo = 'guillermo.moncada@gmail.com' AND r.id_viaje = 14 
LIMIT 1;

INSERT INTO app.acompanante (id_reserva, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, tipo_identificacion, numero_identificacion)
SELECT r.id_reserva, 'Gonzalo', 'Alberto', 'Tercero', 'Solís', 'cedula', '481-050487-0001G'
FROM app.reserva r 
JOIN app.usuario u ON u.id_usuario = r.id_usuario
WHERE u.correo = 'noel.tercero@gmail.com' AND r.id_viaje = 14 
LIMIT 1;

INSERT INTO app.acompanante (id_reserva, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, tipo_identificacion, numero_identificacion)
SELECT r.id_reserva, 'Marcela', 'del Carmen', 'Oporta', 'Vargas', 'cedula', '121-180989-0004M'
FROM app.reserva r 
JOIN app.usuario u ON u.id_usuario = r.id_usuario
WHERE u.correo = 'cristian.oporta@gmail.com' AND r.id_viaje = 13 
LIMIT 1;

INSERT INTO app.acompanante (id_reserva, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, tipo_identificacion, numero_identificacion)
SELECT r.id_reserva, 'Ernesto', 'Antonio', 'Chamorro', 'Pérez', 'cedula', '001-081192-0002E'
FROM app.reserva r 
JOIN app.usuario u ON u.id_usuario = r.id_usuario
WHERE u.correo = 'nestor.chamorro@yahoo.com' AND r.id_viaje = 12 
LIMIT 1;

-- ---------------------------------------------------------------------
-- 6. INSERTAR RESPUESTAS A CAMPOS DE FORMULARIO
-- ---------------------------------------------------------------------
INSERT INTO app.respuesta_formulario (id_reserva, id_campo, valor_respuesta)
SELECT r.id_reserva, c.id_campo,
  CASE 
    WHEN c.tipo_campo = 'seleccion_unica' AND c.opciones_respuesta IS NOT NULL 
      THEN c.opciones_respuesta::json ->> 0
    WHEN c.tipo_campo = 'archivo' 
      THEN '/expedientes/doc_' || r.id_reserva || '_' || c.id_campo || '.pdf'
    WHEN c.tipo_campo = 'texto' AND lower(c.etiqueta_pregunta) LIKE '%emergencia%' 
      THEN 'Contacto en Nicaragua: Dr. Roberto Blandón - Tel: +505 8899-0011 - Cédula: 001-100265-0001Z'
    ELSE 'Sin antecedentes médicos adversos ni observaciones adicionales.'
  END
FROM app.reserva r
JOIN app.campo_formulario c ON c.id_viaje = r.id_viaje
WHERE r.id_reserva NOT IN (SELECT id_reserva FROM app.respuesta_formulario);

-- ---------------------------------------------------------------------
-- 7. RECALCULAR Y ACTUALIZAR CUPOS DISPONIBLES EN APP.VIAJE
-- ---------------------------------------------------------------------
WITH ocupacion AS (
  SELECT 
    r.id_viaje,
    COUNT(r.id_reserva) + COALESCE(SUM((SELECT COUNT(*) FROM app.acompanante a WHERE a.id_reserva = r.id_reserva)), 0) AS personas_reservadas
  FROM app.reserva r
  WHERE r.estado IN ('aprobada', 'pendiente')
  GROUP BY r.id_viaje
)
UPDATE app.viaje v
SET cupos_disponibles = GREATEST(0, v.cupos_maximos - o.personas_reservadas)
FROM ocupacion o
WHERE v.id_viaje = o.id_viaje;

-- ---------------------------------------------------------------------
-- 8. SINCRONIZAR SECUENCIAS POSTGRESQL
-- ---------------------------------------------------------------------
SELECT setval('app.usuario_id_usuario_seq', (SELECT MAX(id_usuario) FROM app.usuario));
SELECT setval('app.campo_formulario_id_campo_seq', (SELECT MAX(id_campo) FROM app.campo_formulario));
SELECT setval('app.reserva_id_reserva_seq', (SELECT MAX(id_reserva) FROM app.reserva));
SELECT setval('app.acompanante_id_acompanante_seq', (SELECT MAX(id_acompanante) FROM app.acompanante));
SELECT setval('app.respuesta_formulario_id_respuesta_seq', (SELECT MAX(id_respuesta) FROM app.respuesta_formulario));

COMMIT;
