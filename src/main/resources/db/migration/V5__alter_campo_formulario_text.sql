-- V5: Ajuste de tipos en campo_formulario y reserva
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'app' AND table_name = 'campo_formulario') THEN
        ALTER TABLE app.campo_formulario ALTER COLUMN opciones_respuesta TYPE TEXT USING opciones_respuesta::text;
        ALTER TABLE app.campo_formulario ALTER COLUMN etiqueta_pregunta TYPE VARCHAR(300);
    ELSIF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'campo_formulario') THEN
        ALTER TABLE public.campo_formulario ALTER COLUMN opciones_respuesta TYPE TEXT USING opciones_respuesta::text;
        ALTER TABLE public.campo_formulario ALTER COLUMN etiqueta_pregunta TYPE VARCHAR(300);
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'app' AND table_name = 'reserva') THEN
        ALTER TABLE app.reserva ALTER COLUMN captura_comprobante_url TYPE TEXT;
        ALTER TABLE app.reserva ALTER COLUMN motivo_rechazo TYPE TEXT;
    ELSIF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'reserva') THEN
        ALTER TABLE public.reserva ALTER COLUMN captura_comprobante_url TYPE TEXT;
        ALTER TABLE public.reserva ALTER COLUMN motivo_rechazo TYPE TEXT;
    END IF;
END $$;
