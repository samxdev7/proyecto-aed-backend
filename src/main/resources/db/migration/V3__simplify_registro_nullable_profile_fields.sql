-- V3__simplify_registro_nullable_profile_fields.sql
-- Permite registro solo con correo + contraseña: los campos de perfil
-- que antes eran NOT NULL ahora aceptan NULL hasta que el usuario los complete.
-- La validación de completitud se mueve a la capa de servicio (al reservar).

ALTER TABLE usuario ALTER COLUMN primer_nombre       DROP NOT NULL;
ALTER TABLE usuario ALTER COLUMN primer_apellido      DROP NOT NULL;
ALTER TABLE usuario ALTER COLUMN sexo                 DROP NOT NULL;
ALTER TABLE usuario ALTER COLUMN sexo                 SET DEFAULT NULL;
ALTER TABLE usuario ALTER COLUMN nacionalidad         DROP NOT NULL;
ALTER TABLE usuario ALTER COLUMN nacionalidad         DROP DEFAULT;
ALTER TABLE usuario ALTER COLUMN tipo_identificacion  DROP NOT NULL;
ALTER TABLE usuario ALTER COLUMN tipo_identificacion  DROP DEFAULT;
ALTER TABLE usuario ALTER COLUMN numero_identificacion DROP NOT NULL;

-- La restricción UNIQUE en numero_identificacion puede tener conflictos con múltiples NULLs.
-- En PostgreSQL, múltiples NULLs son permitidos en índices UNIQUE, así que no hay problema.

-- Eliminar CHECK antiguo de sexo para permitir NULL
ALTER TABLE usuario DROP CONSTRAINT IF EXISTS usuario_sexo_check;
ALTER TABLE usuario ADD CONSTRAINT usuario_sexo_check CHECK (sexo IS NULL OR sexo IN ('M', 'F', 'Otro'));
