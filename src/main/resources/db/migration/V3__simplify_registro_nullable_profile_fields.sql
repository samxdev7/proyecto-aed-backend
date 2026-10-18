-- V3__simplify_registro_nullable_profile_fields.sql
-- Permite registro solo con correo + contraseña: los campos de perfil
-- que antes eran NOT NULL ahora aceptan NULL hasta que el usuario los complete.
-- La validación de completitud se mueve a la capa de servicio (al reservar).
--
-- Las tablas del negocio viven en el schema "app" (creado por V1).

ALTER TABLE app.usuario ALTER COLUMN primer_nombre        DROP NOT NULL;
ALTER TABLE app.usuario ALTER COLUMN primer_apellido       DROP NOT NULL;
ALTER TABLE app.usuario ALTER COLUMN sexo                  DROP NOT NULL;
ALTER TABLE app.usuario ALTER COLUMN sexo                  SET DEFAULT NULL;
ALTER TABLE app.usuario ALTER COLUMN nacionalidad          DROP NOT NULL;
ALTER TABLE app.usuario ALTER COLUMN nacionalidad          DROP DEFAULT;
ALTER TABLE app.usuario ALTER COLUMN tipo_identificacion   DROP NOT NULL;
ALTER TABLE app.usuario ALTER COLUMN tipo_identificacion   DROP DEFAULT;
ALTER TABLE app.usuario ALTER COLUMN numero_identificacion DROP NOT NULL;

-- En PostgreSQL, múltiples NULLs en una columna UNIQUE son permitidos (no hay conflicto).

-- Actualizar CHECK de sexo para permitir NULL.
ALTER TABLE app.usuario DROP CONSTRAINT IF EXISTS usuario_sexo_check;
ALTER TABLE app.usuario ADD CONSTRAINT usuario_sexo_check
    CHECK (sexo IS NULL OR sexo IN ('M', 'F', 'Otro'));
