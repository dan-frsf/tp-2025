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
    nombre varchar(255) NOT NULL,
    cuit varchar(20) NOT NULL,
    domicilio varchar(255) NOT NULL,
    latitud decimal(10,7),
    longitud decimal(10,7),
    telefono varchar(30),
    correo_contacto varchar(100),
    categoria integer NOT NULL
);

-- Tabla tipo_habitacion
CREATE TABLE IF NOT EXISTS tp_dan.tipo_habitacion (
    id integer PRIMARY KEY,
    nombre varchar(50) NOT NULL,
    descripcion varchar(255) NOT NULL,
    capacidad integer NOT NULL
);

INSERT INTO tp_dan.tipo_habitacion (id, nombre, descripcion, capacidad) VALUES
    (1, 'SINGLE', 'Single', 1),
    (2, 'DOBLE INDIVIDUAL', 'Dos camas individuales', 2),
    (3, 'DOBLE', 'Doble una cama doble', 2),
    (4, 'TRIPLE INDIVIDUAL', 'Triple tres camas single', 3),
    (5, 'TRIPLE', 'Triple una cama doble y una single', 3),
    (6, 'CUADRUPLE', 'Dos camas individuales', 4),
    (7, 'DOBLE SUPERIOR', 'Dos camas individuales', 2),
    (8, 'TRIPLE SUPERIOR', 'Dos camas individuales', 3),
    (9, 'CUADRUPLE SUPERIOR', 'Dos camas individuales', 4)
ON CONFLICT (id) DO NOTHING;

-- Tabla tarifa
CREATE TABLE IF NOT EXISTS tp_dan.tarifa (
    id serial PRIMARY KEY,
    fecha_inicio date NOT NULL,
    fecha_fin date NOT NULL,
    id_tipo_habitacion integer NOT NULL REFERENCES tp_dan.tipo_habitacion(id),
    precio_noche decimal(10,2) NOT NULL
);

-- Secuencia para habitacion
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'habitacion_id_seq') THEN
        CREATE SEQUENCE tp_dan.habitacion_id_seq;
    END IF;
END$$;

-- Tabla habitacion
CREATE TABLE IF NOT EXISTS tp_dan.habitacion (
    id integer PRIMARY KEY DEFAULT nextval('tp_dan.habitacion_id_seq'),
    numero integer NOT NULL,
    piso integer NOT NULL,
    id_tipo integer NOT NULL REFERENCES tp_dan.tipo_habitacion(id),
    id_hotel integer NOT NULL REFERENCES tp_dan.hotel(id)
);

-- Secuencia para amenity_hotel_id_seq
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'amenity_hotel_id_seq') THEN
        CREATE SEQUENCE tp_dan.amenity_hotel_id_seq;
    END IF;
END$$;

-- Tabla amenity_hotel
CREATE TABLE IF NOT EXISTS tp_dan.amenity_hotel (
    id integer PRIMARY KEY DEFAULT nextval('tp_dan.amenity_hotel_id_seq'),
    id_hotel integer NOT NULL REFERENCES tp_dan.hotel(id),
    amenity varchar(250) NOT NULL
);
