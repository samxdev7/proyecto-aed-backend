-- V3__simplify_registro_nullable_profile_fields.sql
-- Permite registro solo con correo + contraseña: los campos de perfil
-- que antes eran NOT NULL ahora aceptan NULL hasta que el usuario los complete.
-- La validación de completitud se mueve a la capa de servicio (al reservar).
--
-- NOTA: Se usa schema-qualified (public.usuario) para compatibilidad con el
-- pooler de Supabase, donde el search_path no incluye "public" por defecto.

SET search_path TO public;

ALTER TABLE public.usuario ALTER COLUMN primer_nombre        DROP NOT NULL;
ALTER TABLE public.usuario ALTER COLUMN primer_apellido       DROP NOT NULL;
ALTER TABLE public.usuario ALTER COLUMN sexo                  DROP NOT NULL;
ALTER TABLE public.usuario ALTER COLUMN sexo                  SET DEFAULT NULL;
ALTER TABLE public.usuario ALTER COLUMN nacionalidad          DROP NOT NULL;
ALTER TABLE public.usuario ALTER COLUMN nacionalidad          DROP DEFAULT;
ALTER TABLE public.usuario ALTER COLUMN tipo_identificacion   DROP NOT NULL;
ALTER TABLE public.usuario ALTER COLUMN tipo_identificacion   DROP DEFAULT;
ALTER TABLE public.usuario ALTER COLUMN numero_identificacion DROP NOT NULL;

-- En PostgreSQL, múltiples NULLs en una columna UNIQUE son permitidos (no hay conflicto).

-- Actualizar CHECK de sexo para permitir NULL.
-- Usamos DROP CONSTRAINT IF EXISTS con el nombre estándar generado por PostgreSQL.
-- Si el nombre difiere en el entorno, el IF EXISTS evita errores y la restricción
-- queda sin redefinir (los valores existentes siguen siendo válidos).
ALTER TABLE public.usuario DROP CONSTRAINT IF EXISTS usuario_sexo_check;
ALTER TABLE public.usuario ADD CONSTRAINT usuario_sexo_check
    CHECK (sexo IS NULL OR sexo IN ('M', 'F', 'Otro'));
