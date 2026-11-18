-- ========================================================
-- SCRIPT DE BASE DE DATOS: CLÍNICA VETERINARIA (PostgreSQL)
-- ========================================================

-- 1. Creación del usuario y base de datos
-- (Ejecutar conectado como superusuario postgres)
CREATE USER esteban WITH PASSWORD '123';
CREATE DATABASE veterinaria;
ALTER DATABASE veterinaria OWNER TO esteban;

-- Conectarse a la base de datos 'veterinaria':
\c veterinaria;

-- Permisos en el esquema público para el usuario esteban
GRANT ALL ON SCHEMA public TO esteban;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO esteban;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO esteban;

-- ========================================================
-- DATOS DE PRUEBA (5 registros por tabla)
-- Ejecuta estos INSERT después de arrancar la aplicación una vez,
-- cuando Hibernate ya haya creado las tablas con ddl-auto=update.
-- ========================================================

-- 1. TABLA: mascota
INSERT INTO mascota (nombre, especie, edad, dueno) VALUES
('Firulais', 'perro', 4, 'Ana Pérez'),
('Michi', 'gato', 1, 'Carlos Gómez'),
('Toby', 'perro', 0, 'Ana Pérez'),
('Luna', 'gato', 3, 'María Rodríguez'),
('Rocky', 'loro', 2, 'Pedro Salazar');

-- 2. TABLA: medicamento
INSERT INTO medicamento (nombre, precio, stock, requiere_receta) VALUES
('Amoxicilina 250 mg', 8.75, 30, true),
('Antiparasitario Canino', 5.50, 4, false),
('Vitaminas Complejo B', 12.00, 20, false),
('Meloxicam Antiinflamatorio', 15.30, 2, true),
('Champú Medicado Pulgas', 9.90, 15, false);

-- 3. TABLA: veterinario
INSERT INTO veterinario (nombre, especialidad, anios_experiencia, disponible) VALUES
('Dr. Luis Cevallos', 'cirugía', 7, true),
('Dra. Carolina Mora', 'dermatología', 4, true),
('Dr. Andrés Vega', 'cirugía', 12, false),
('Dra. Sofía Benítez', 'medicina general', 9, true),
('Dr. Fernando Ortiz', 'traumatología', 15, true);
