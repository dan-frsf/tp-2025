-- Crear esquema
CREATE SCHEMA IF NOT EXISTS tp_dan;

-- Crear secuencia para el ID de la tabla HOTEL
do $$
begin
    if not exists (select 1 from pg_class where relname = 'hotel_id_seq') then
        create sequence tp_dan.hotel_id_seq;
    end if;
end$$;

-- Crear tabla HOTEL
CREATE TABLE IF NOT EXISTS tp_dan.hotel (
    id integer PRIMARY KEY DEFAULT nextval('tp_dan.hotel_id_seq'),
    nombre varchar(255) NOT NULL
);
