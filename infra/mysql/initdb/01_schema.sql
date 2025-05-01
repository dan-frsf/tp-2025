-- Script para crear las tablas en el esquema `users`
CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE users.bancos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

CREATE TABLE users.cuentas_bancarias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta VARCHAR(255) NOT NULL,
    cbu VARCHAR(255) NOT NULL,
    alias VARCHAR(255),
    banco_id INT,
    FOREIGN KEY (banco_id) REFERENCES users.bancos(id)
);

CREATE TABLE users.usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefono VARCHAR(255) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE,
    cuenta_bancaria_id INT,
    hotel_id INT,
    FOREIGN KEY (cuenta_bancaria_id) REFERENCES users.cuentas_bancarias(id)
);


CREATE TABLE users.tarjetas_credito (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_tarjeta VARCHAR(22) NOT NULL,
    nombre_titular VARCHAR(255) NOT NULL,
    fecha_vencimiento VARCHAR(4) NOT NULL,
    codigo_seguridad VARCHAR(4) NOT NULL,
    es_principal BIT(1),
    usuario_id INT NOT NULL,
    banco_id INT NOT NULL,
    FOREIGN KEY (banco_id) REFERENCES users.bancos(id),
    FOREIGN KEY (usuario_id) REFERENCES users.usuarios(id)
);
