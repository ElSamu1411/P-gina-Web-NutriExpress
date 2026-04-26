-- ==========================================
-- TABLA DE USUARIOS
-- ==========================================

CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL -- 'ADMIN' o 'CLIENTE'
);

-- Insertamos los usuarios iniciales
INSERT INTO usuario (username, password, rol) VALUES 
('administrador', 'admin123', 'ADMIN'),
('cliente', 'cliente123', 'CLIENTE');

-- ==========================================
-- 1. TABLAS DE ESTADO Y MUNICIPIO
-- ==========================================

CREATE TABLE estado (
    id_estado SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL unique
    
);

CREATE TABLE municipio (
    id_municipio SERIAL PRIMARY KEY,
    id_estado INT REFERENCES estado(id_estado),
    nombre VARCHAR(50) NOT null
    
);

-- ==========================================
-- 2. TABLAS DE LA TIENDA
-- ==========================================

CREATE TABLE suplemento (
    id_suplemento SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    marca VARCHAR(50),
    categoria VARCHAR(50),
    precio DECIMAL(10, 2),
    url_imagen TEXT,
    -- 'activo' permite la BAJA GLOBAL (si es false, no aparece en ninguna sucursal)
    activo BOOLEAN DEFAULT TRUE 
    
);

CREATE TABLE sucursal (
    id_sucursal SERIAL PRIMARY KEY,
    id_municipio INT REFERENCES municipio(id_municipio),
    nombre_sucursal VARCHAR(100) -- Ej: "NutriExpress Centro"
    
);

CREATE TABLE inventario (
    id_inventario SERIAL PRIMARY KEY,
    id_sucursal INT REFERENCES sucursal(id_sucursal),
    id_suplemento INT REFERENCES suplemento(id_suplemento),
    stock INT DEFAULT 0,
    -- Eliminar aquí permite la BAJA POR SUCURSAL
    UNIQUE(id_sucursal, id_suplemento)
    
);

-- ==========================================
-- 3. INSERCIÓN DE DATOS (6 ESTADOS, 10 MUNICIPIOS C/U)
-- ==========================================

-- Insertar los 6 estados seleccionados
INSERT INTO estado (nombre) VALUES 
('Jalisco'),            -- ID 1
('Nuevo León'),         -- ID 2
('Estado de México'),   -- ID 3
('Chihuahua'),          -- ID 4
('Sinaloa'),            -- ID 5
('Guanajuato');         -- ID 6

-- 1. Municipios de JALISCO (ID 1)
INSERT INTO municipio (id_estado, nombre) VALUES 
(1, 'Guadalajara'), (1, 'Zapopan'), (1, 'Tlaquepaque'), (1, 'Tonalá'), 
(1, 'Zapotlanejo'), (1, 'Tlajomulco'), (1, 'Ayotlán'), (1, 'Tequila'), 
(1, 'Ocotlán'), (1, 'Puerto Vallarta');

-- 2. Municipios de NUEVO LEÓN (ID 2)
INSERT INTO municipio (id_estado, nombre) VALUES 
(2, 'Guadalupe'), (2, 'Abasolo'), (2, 'Apodaca'), (2, 'Ciénega de Flores'), 
(2, 'General Zaragoza'), (2, 'Iturbide'), (2, 'Juárez'), (2, 'Monterrey'), 
(2, 'Salinas Victoria'), (2, 'General Zuazua');

-- 3. Municipios de ESTADO DE MÉXICO (ID 3)
INSERT INTO municipio (id_estado, nombre) VALUES 
(3, 'Cuautitlán Izcalli'), (3, 'Chalco'), (3, 'Aculco'), (3, 'Atizapán'), 
(3, 'Chapultepec'), (3, 'Ecatepec de Morelos'), (3, 'Naucalpan de Juárez'), 
(3, 'Morelos'), (3, 'Texcoco'), (3, 'Toluca');

-- 4. Municipios de CHIHUAHUA (ID 4)
INSERT INTO municipio (id_estado, nombre) VALUES 
(4, 'Ignacio Zaragoza'), (4, 'Allende'), (4, 'Valle de Zaragoza'), (4, 'Rosario'), 
(4, 'Nonoava'), (4, 'Matamoros'), (4, 'Guadalupe y Calvo'), (4, 'Coronado'), 
(4, 'Delicias'), (4, 'Galeana');

-- 5. Municipios de SINALOA (ID 5)
INSERT INTO municipio (id_estado, nombre) VALUES 
(5, 'Guasave'), (5, 'Navolato'), (5, 'Cosalá'), (5, 'Angostura'), 
(5, 'Mocorito'), (5, 'Culiacán'), (5, 'Mazatlán'), (5, 'Ahome'), 
(5, 'El Fuerte'), (5, 'Escuinapa');

-- 6. Municipios de GUANAJUATO (ID 6)
INSERT INTO municipio (id_estado, nombre) VALUES 
(6, 'León'), (6, 'Irapuato'), (6, 'Celaya'), (6, 'Salamanca'), 
(6, 'Silao'), (6, 'Guanajuato'), (6, 'San Miguel de Allende'), 
(6, 'Dolores Hidalgo'), (6, 'Valle de Santiago'), (6, 'Cortazar');

-- ==========================================
-- DATOS DE PRUEBA: NUTRIEXPRESS
-- ==========================================

-- 1. Insertar Catalogo de Suplementos
INSERT INTO suplemento (nombre, marca, categoria, precio, url_imagen, activo) VALUES 
('Whey Protein Gold Standard 5lbs', 'Optimum Nutrition', 'Proteina', 1850.00, 'https://placehold.co/400x400/2c2e33/e31837?text=ON+Whey+Protein', true),
('Creatina Monohidratada 300g', 'Mutant', 'Creatina', 550.00, 'https://placehold.co/400x400/2c2e33/ffc107?text=Mutant+Creatina', true),
('C4 Original Pre-Workout 30 serv', 'Cellucor', 'Pre-entreno', 620.00, 'https://placehold.co/400x400/2c2e33/ffff00?text=C4+Pre-Workout', true),
('Super Mass Gainer 12lbs', 'Dymatize', 'Ganador de peso', 2100.00, 'https://placehold.co/400x400/2c2e33/007bff?text=Dymatize+Mass', true),
('BCAA Energy 30 serv', 'Evlution Nutrition', 'Aminoacidos', 480.00, 'https://placehold.co/400x400/2c2e33/28a745?text=BCAA+Energy', true),
('Platinum Omega 3 Fish Oil', 'Muscletech', 'Vitaminas', 350.00, 'https://placehold.co/400x400/2c2e33/17a2b8?text=Omega+3', true),
('Isopure Zero Carb 3lbs', 'Natures Best', 'Proteina', 2250.00, 'https://placehold.co/400x400/2c2e33/ffffff?text=Isopure+Zero', true),
('L-Carnitina Liquida 3000', 'GAT Sport', 'Quemador', 420.00, 'https://placehold.co/400x400/2c2e33/fd7e14?text=L-Carnitina', true),
('Nitrotech Ripped 4lbs', 'Muscletech', 'Proteina', 1980.00, 'https://placehold.co/400x400/2c2e33/dc3545?text=Nitrotech', true),
('Opti-Men Multivitaminico 90 tabs', 'Optimum Nutrition', 'Vitaminas', 680.00, 'https://placehold.co/400x400/2c2e33/20c997?text=Opti-Men', true);

-- 2. Asignar Inventario (Distribucion entre sucursales)

-- Inventario para Sucursal 1 (Guadalajara)
INSERT INTO inventario (id_sucursal, id_suplemento, stock) VALUES 
(1, 1, 12), -- ON Whey
(1, 2, 20), -- Creatina
(1, 3, 15), -- C4
(1, 4, 5),  -- Mass Gainer
(1, 5, 0),  -- BCAA (Agotado para probar validaciones)
(1, 6, 30), -- Omega 3
(1, 7, 8),  -- Isopure
(1, 8, 10), -- Carnitina
(1, 9, 14), -- Nitrotech
(1, 10, 25); -- Opti-Men

-- Inventario para Sucursal 2 (Zapopan)
INSERT INTO inventario (id_sucursal, id_suplemento, stock) VALUES 
(2, 1, 8),  
(2, 2, 10), 
(2, 3, 20), 
(2, 4, 3),  
(2, 5, 15), -- Aqui si hay BCAA
(2, 6, 25), 
(2, 7, 12), 
(2, 8, 5),  
(2, 9, 9),  
(2, 10, 18);

-- ==========================================
-- SCRIPT DE LIMPIEZA DE DATOS
-- ==========================================

-- 1. Eliminar existencias en las sucursales
DELETE FROM inventario;

-- 2. Eliminar el catalogo de productos
DELETE FROM suplemento;

-- Opcional: Reiniciar los contadores de ID para que el proximo producto sea el ID 1
ALTER SEQUENCE suplemento_id_suplemento_seq RESTART WITH 1;
ALTER SEQUENCE inventario_id_inventario_seq RESTART WITH 1;

-- ==========================================
-- SCRIPT PARA INGRESAR SUCURSALES
-- ==========================================

-- JALISCO (Municipios 1 y 2: Guadalajara y Zapopan)
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (1, 'NutriExpress GDL Centro');
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (2, 'NutriExpress Zapopan Andares');

-- NUEVO LEÓN (Municipios 11 y 18: Guadalupe y Monterrey)
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (11, 'NutriExpress Estadio BBVA');
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (18, 'NutriExpress MTY Centro');

-- ESTADO DE MÉXICO (Municipios 26 y 30: Ecatepec y Toluca)
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (26, 'NutriExpress Ecatepec Plaza');
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (30, 'NutriExpress Toluca Centro');

-- CHIHUAHUA (Municipios 39 y 40: Delicias y Galeana)
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (39, 'NutriExpress Delicias Norte');
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (40, 'NutriExpress Galeana Sur');

-- SINALOA (Municipios 46 y 47: Culiacán y Mazatlán)
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (46, 'NutriExpress Tres Ríos');
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (47, 'NutriExpress Malecón');

-- GUANAJUATO (Municipios 51 y 52: León e Irapuato)
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (51, 'NutriExpress León Bajío');
INSERT INTO sucursal (id_municipio, nombre_sucursal) VALUES (52, 'NutriExpress Irapuato Centro');

select * from suplemento;




