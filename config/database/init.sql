-- ===================================
-- AIRSPHERE DATABASE INITIALIZATION
-- ===================================

CREATE DATABASE IF NOT EXISTS `air-sphere-connect`;
USE `air-sphere-connect`;

-- ===================================
-- TABLES DE BASE
-- ===================================

-- Table des utilisateurs
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role ENUM('USER','ADMIN','GUEST') DEFAULT 'USER',
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table des régions (focus Occitanie)
CREATE TABLE regions (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         code VARCHAR(10) NOT NULL,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table des départements
CREATE TABLE departments (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             code VARCHAR(10) NOT NULL,
                             name VARCHAR(100) NOT NULL,
                             region_id INT NOT NULL,
                             FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE CASCADE,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table des villes
CREATE TABLE cities (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(150) NOT NULL,
                        postal_code VARCHAR(10),
                        department_id INT NOT NULL,
                        region_id INT NOT NULL,
                        latitude DOUBLE NOT NULL,
                        longitude DOUBLE NOT NULL,
                        population INT,
                        FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
                        FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE CASCADE,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ===================================
-- DONNÉES DE TEST OCCITANIE
-- ===================================

-- Région Occitanie
INSERT INTO regions (name, code) VALUES ('Occitanie', '76');

-- Départements principaux d'Occitanie
INSERT INTO departments (code, name, region_id) VALUES
                                                    ('09', 'Ariège', 1),
                                                    ('11', 'Aude', 1),
                                                    ('12', 'Aveyron', 1),
                                                    ('30', 'Gard', 1),
                                                    ('31', 'Haute-Garonne', 1),
                                                    ('32', 'Gers', 1),
                                                    ('34', 'Hérault', 1),
                                                    ('46', 'Lot', 1),
                                                    ('48', 'Lozère', 1),
                                                    ('65', 'Hautes-Pyrénées', 1),
                                                    ('66', 'Pyrénées-Orientales', 1),
                                                    ('81', 'Tarn', 1),
                                                    ('82', 'Tarn-et-Garonne', 1);

-- Villes principales d'Occitanie
INSERT INTO cities (name, postal_code, department_id, region_id, latitude, longitude, population) VALUES
-- Haute-Garonne (31)
('Toulouse', '31000', 5, 1, 43.604652, 1.444209, 498003),
('Colomiers', '31770', 5, 1, 43.611546, 1.332524, 39968),

-- Hérault (34)
('Montpellier', '34000', 7, 1, 43.610769, 3.876716, 295542),
('Béziers', '34500', 7, 1, 43.344097, 3.215795, 79177),

-- Aude (11)
('Carcassonne', '11000', 2, 1, 43.212161, 2.353663, 47068),
('Narbonne', '11100', 2, 1, 43.183946, 3.004230, 55695),

-- Pyrénées-Orientales (66)
('Perpignan', '66000', 11, 1, 42.698611, 2.895556, 122032),
('Canet-en-Roussillon', '66140', 11, 1, 42.693989, 3.038056, 12751),

-- Gard (30)
('Nîmes', '30000', 4, 1, 43.836699, 4.360054, 151001),
('Alès', '30100', 4, 1, 44.125102, 4.081061, 40400),

-- Tarn (81)
('Albi', '81000', 12, 1, 43.929063, 2.147899, 51274),
('Castres', '81100', 12, 1, 43.605473, 2.240647, 41385);

-- Utilisateurs de test
INSERT INTO users (username, email, password, role) VALUES
                                                        ('admin', 'admin@airsphere.com', '$2a$10$DummyHashForTesting', 'ADMIN'),
                                                        ('sandrine', 'sandrine.alcazar@airsphere.com', '$2a$10$DummyHashForTesting', 'USER'),
                                                        ('cyril', 'cyril.schneidenbach@airsphere.com', '$2a$10$DummyHashForTesting', 'USER'),
                                                        ('nuno', 'nuno.esteves@airsphere.com', '$2a$10$DummyHashForTesting', 'USER');


-- VÉRIFICATIONS FINALES
SELECT 'Base de données air-sphere-connect initialisée avec succès !' as message;
SELECT COUNT(*) as nb_regions FROM regions;
SELECT COUNT(*) as nb_departments FROM departments;
SELECT COUNT(*) as nb_cities FROM cities;
SELECT COUNT(*) as nb_users FROM users;

SELECT 'Principales villes d\'Occitanie :' as info;
SELECT c.name as ville, d.name as departement, c.population
FROM cities c
JOIN departments d ON c.department_id = d.id
ORDER BY c.population DESC
LIMIT 5;