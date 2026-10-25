-- V7: Asegurar que captura_comprobante_url y motivo_rechazo sean de tipo TEXT en reserva
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'app' AND table_name = 'reserva') THEN
        ALTER TABLE app.reserva ALTER COLUMN captura_comprobante_url TYPE TEXT;
        ALTER TABLE app.reserva ALTER COLUMN motivo_rechazo TYPE TEXT;
    ELSIF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'reserva') THEN
        ALTER TABLE public.reserva ALTER COLUMN captura_comprobante_url TYPE TEXT;
        ALTER TABLE public.reserva ALTER COLUMN motivo_rechazo TYPE TEXT;
    END IF;
END $$;
