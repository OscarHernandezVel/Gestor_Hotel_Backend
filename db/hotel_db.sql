-- Creación de la base de datos
CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

-- 1. Tabla de Habitaciones
CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type ENUM('SINGLE', 'DOUBLE', 'SUITE') NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE'
);

-- 2. Tabla de Reservas
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_nights INT NOT NULL,
    season ENUM('HIGH', 'LOW') NOT NULL,
    digital_key_uuid VARCHAR(36) UNIQUE, -- Almacena el UUID simulado de la llave digital
    reservation_status VARCHAR(30) DEFAULT 'CREATED', -- CREATED, CHECKED_IN, CHECKED_OUT, CANCELLED
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- 3. Tabla de Catálogo de Servicios Adicionales
CREATE TABLE additional_services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    cost DECIMAL(10, 2) NOT NULL
);

-- 4. Tabla de Asociación entre Reservas y Servicios Adicionales
CREATE TABLE reservation_services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL,
    service_id INT NOT NULL,
    quantity INT DEFAULT 1,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES additional_services(id)
);

-- 5. Tabla de Facturación
CREATE TABLE invoices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL UNIQUE,
    lodging_subtotal DECIMAL(10, 2) NOT NULL,
    services_total DECIMAL(10, 2) NOT NULL,
    grand_total DECIMAL(10, 2) NOT NULL,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

-- Servicios del taller
INSERT INTO additional_services (name, cost) VALUES 
('Spa', 50.00),
('Breakfast', 15.00),
('Transport', 30.00);

-- 15 habitaciones (Single, Double, Suite)
INSERT INTO rooms (room_number, room_type, base_price, status) VALUES
('101', 'SINGLE', 80.00, 'AVAILABLE'),
('102', 'SINGLE', 80.00, 'AVAILABLE'),
('103', 'SINGLE', 80.00, 'AVAILABLE'),
('104', 'SINGLE', 80.00, 'AVAILABLE'),
('105', 'SINGLE', 80.00, 'AVAILABLE'),
('201', 'DOUBLE', 130.00, 'AVAILABLE'),
('202', 'DOUBLE', 130.00, 'AVAILABLE'),
('203', 'DOUBLE', 130.00, 'AVAILABLE'),
('204', 'DOUBLE', 130.00, 'AVAILABLE'),
('205', 'DOUBLE', 130.00, 'AVAILABLE'),
('301', 'SUITE', 220.00, 'AVAILABLE'),
('302', 'SUITE', 220.00, 'AVAILABLE'),
('303', 'SUITE', 220.00, 'AVAILABLE'),
('304', 'SUITE', 220.00, 'AVAILABLE'),
('305', 'SUITE', 220.00, 'AVAILABLE');
