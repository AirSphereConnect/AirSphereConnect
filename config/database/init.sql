-- ===================================
-- AIRSPHERE DATABASE INITIALIZATION - VERSION COMPLETE
-- ===================================

CREATE DATABASE IF NOT EXISTS `air-sphere-connect`;
USE `air-sphere-connect`;

-- ===================================
-- TABLES DE BASE GÉOGRAPHIQUE
-- ===================================

-- Table des régions
CREATE TABLE regions (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL UNIQUE,
                         code VARCHAR(10) NOT NULL UNIQUE,
);

-- Table des départements
CREATE TABLE departments (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             code VARCHAR(10) NOT NULL UNIQUE,
                             name VARCHAR(100) NOT NULL,
                             region_id BIGINT NOT NULL,
                             FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE CASCADE,
                             INDEX idx_dept_region (region_id)
);

-- Table des villes
CREATE TABLE cities (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        postal_code VARCHAR(10),
                        department_id BIGINT NOT NULL,
                        latitude DOUBLE NOT NULL,
                        longitude DOUBLE NOT NULL,
                        area_code VARCHAR(10),
                        FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
                        INDEX idx_city_dept (department_id),
                        INDEX idx_city_coords (latitude, longitude)
);

-- ===================================
-- TABLES UTILISATEURS ET AUTHENTIFICATION
-- ===================================

-- Table des utilisateurs
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role ENUM('USER','ADMIN','GUEST') DEFAULT 'USER',
                       login_count INTEGER DEFAULT 0,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       deleted_at DATETIME NULL,
                       INDEX idx_user_email (email),
                       INDEX idx_user_role (role)
);

-- Table des adresses
CREATE TABLE addresses (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           city_id BIGINT NOT NULL,
                           type ENUM('HOME','WORK','OTHER') DEFAULT 'HOME',
                           description VARCHAR(255),
                           street VARCHAR(255) NOT NULL,
                           postal_code VARCHAR(10) NOT NULL,
                           city_name VARCHAR(150) NOT NULL,
                           country VARCHAR(100) NOT NULL DEFAULT 'France',
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           FOREIGN KEY (city_id) REFERENCES cities(id),
                           INDEX idx_address_user (user_id)
);

-- ===================================
-- TABLES DONNÉES ENVIRONNEMENTALES
-- ===================================

-- Table population
CREATE TABLE populations (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             city_id BIGINT NOT NULL,
                             population INTEGER NOT NULL,
                             year INTEGER NOT NULL,
                             density DOUBLE,
                             source VARCHAR(100) NOT NULL,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                             updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE,
                             INDEX idx_pop_city_year (city_id, year),
                             UNIQUE KEY unique_city_year (city_id, year)
);

-- Table stations qualité de l'air
CREATE TABLE air_quality_stations (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      name VARCHAR(100) NOT NULL,
                                      code VARCHAR(50) NOT NULL UNIQUE,
                                      source VARCHAR(100) NOT NULL,
                                      latitude DOUBLE NOT NULL,
                                      longitude DOUBLE NOT NULL,
                                      city_id BIGINT NOT NULL,
                                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      FOREIGN KEY (city_id) REFERENCES cities(id),
                                      INDEX idx_station_city (city_id),
                                      INDEX idx_station_coords (latitude, longitude)
);

-- Table mesures qualité de l'air
CREATE TABLE air_quality_measurements (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          station_id BIGINT NOT NULL,
                                          datetime DATETIME NOT NULL,
                                          pm10 DOUBLE,
                                          pm25 DOUBLE,
                                          no2 DOUBLE,
                                          o3 DOUBLE,
                                          so2 DOUBLE,
                                          atmo INTEGER,
                                          message VARCHAR(50),
                                          source VARCHAR(100),
                                          measured_at DATETIME NOT NULL,
                                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          FOREIGN KEY (station_id) REFERENCES air_quality_stations(id) ON DELETE CASCADE,
                                          INDEX idx_measurement_station_date (station_id, measured_at),
                                          INDEX idx_measurement_date (measured_at)
);

-- Table mesures météo
CREATE TABLE weather_measurements (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      city_id BIGINT NOT NULL,
                                      temperature DOUBLE,
                                      humidity DOUBLE,
                                      pressure DOUBLE,
                                      wind_speed DOUBLE,
                                      wind_direction VARCHAR(50),
                                      message VARCHAR(50),
                                      source VARCHAR(100),
                                      measured_at DATETIME NOT NULL,
                                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE,
                                      INDEX idx_weather_city_date (city_id, measured_at)
);

-- ===================================
-- TABLES FAVORIS
-- ===================================

-- Table favoris
CREATE TABLE favorites (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           city_id BIGINT NOT NULL,
                           favorite_category ENUM('POPULATION','AIR_QUALITY','WEATHER','ALL') NOT NULL,
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           deleted_at DATETIME NULL,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           FOREIGN KEY (city_id) REFERENCES cities(id),
                           UNIQUE KEY unique_user_city_category (user_id, city_id, favorite_category),
                           INDEX idx_favorite_user (user_id)
);

-- ===================================
-- TABLES FORUM
-- ===================================

-- Table forum principal
CREATE TABLE forum (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(200) NOT NULL,
                       description TEXT,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       deleted_at DATETIME NULL
);

-- Table rubriques forum
CREATE TABLE forum_rubrics (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               forum_id BIGINT,
                               user_id BIGINT NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               description TEXT,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               deleted_at DATETIME NULL,
                               FOREIGN KEY (forum_id) REFERENCES forum(id),
                               FOREIGN KEY (user_id) REFERENCES users(id),
                               INDEX idx_rubric_forum (forum_id),
                               INDEX idx_rubric_user (user_id)
);

-- Table threads forum
CREATE TABLE forum_threads (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               rubric_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               deleted_at DATETIME NULL,
                               FOREIGN KEY (rubric_id) REFERENCES forum_rubrics(id) ON DELETE CASCADE,
                               FOREIGN KEY (user_id) REFERENCES users(id),
                               INDEX idx_thread_rubric (rubric_id),
                               INDEX idx_thread_user (user_id)
);

-- Table posts forum
CREATE TABLE forum_posts (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             thread_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             content TEXT NOT NULL,
                             likes_count INTEGER DEFAULT 0,
                             dislikes_count INTEGER DEFAULT 0,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                             updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             deleted_at DATETIME NULL,
                             FOREIGN KEY (thread_id) REFERENCES forum_threads(id) ON DELETE CASCADE,
                             FOREIGN KEY (user_id) REFERENCES users(id),
                             INDEX idx_post_thread (thread_id),
                             INDEX idx_post_user (user_id)
);

-- ===================================
-- TABLE NOTIFICATIONS
-- ===================================

-- Table notifications
CREATE TABLE notifications (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               message TEXT NOT NULL,
                               notification_type ENUM('WEATHER','AIR_QUALITY','POPULATION','FORUM','SYSTEM') NOT NULL,
                               sent_at DATETIME NOT NULL,
                               is_checked BOOLEAN DEFAULT FALSE,
                               user_id BIGINT,
                               city_id BIGINT,
                               department_id BIGINT,
                               region_id BIGINT,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               FOREIGN KEY (city_id) REFERENCES cities(id),
                               FOREIGN KEY (department_id) REFERENCES departments(id),
                               FOREIGN KEY (region_id) REFERENCES regions(id),
                               INDEX idx_notif_user (user_id),
                               INDEX idx_notif_type (notification_type),
                               INDEX idx_notif_sent_at (sent_at)
);

-- ===================================
-- TABLE POST REACTIONS
-- ===================================

CREATE TABLE post_reactions (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                post_id BIGINT NOT NULL,
                                reaction_type ENUM('LIKE','DISLIKE') NOT NULL,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                deleted_at DATETIME NULL,
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
                                UNIQUE KEY unique_user_post (user_id, post_id),
                                INDEX idx_reaction_user (user_id),
                                INDEX idx_reaction_post (post_id)
);


-- ===================================
-- DONNÉES DE TEST - OCCITANIE
-- ===================================

-- Région Occitanie
INSERT INTO regions (name, code) VALUES ('Occitanie', '76');

-- Départements d'Occitanie
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

-- Villes principales avec coordonnées GPS
INSERT INTO cities (name, postal_code, department_id, latitude, longitude, area_code) VALUES
-- Haute-Garonne (31) - ID department = 5
('Toulouse', '31000', 5, 43.604652, 1.444209, '31555'),
('Colomiers', '31770', 5, 43.611546, 1.332524, '31149'),
('Tournefeuille', '31170', 5, 43.586111, 1.343611, '31557'),

-- Hérault (34) - ID department = 7
('Montpellier', '34000', 7, 43.610769, 3.876716, '34172'),
('Béziers', '34500', 7, 43.344097, 3.215795, '34032'),
('Sète', '34200', 7, 43.403889, 3.696944, '34301'),

-- Aude (11) - ID department = 2
('Carcassonne', '11000', 2, 43.212161, 2.353663, '11069'),
('Narbonne', '11100', 2, 43.183946, 3.004230, '11262'),

-- Pyrénées-Orientales (66) - ID department = 11
('Perpignan', '66000', 11, 42.698611, 2.895556, '66136'),
('Canet-en-Roussillon', '66140', 11, 42.693989, 3.038056, '66037'),

-- Gard (30) - ID department = 4
('Nîmes', '30000', 4, 43.836699, 4.360054, '30189'),
('Alès', '30100', 4, 44.125102, 4.081061, '30007'),

-- Tarn (81) - ID department = 12
('Albi', '81000', 12, 43.929063, 2.147899, '81004'),
('Castres', '81100', 12, 43.605473, 2.240647, '81065');

-- Données population pour les principales villes (année 2023)
INSERT INTO populations (city_id, population, year, density, source) VALUES
                                                                         (1, 498003, 2023, 4167.0, 'INSEE'),  -- Toulouse
                                                                         (2, 39968, 2023, 1850.0, 'INSEE'),   -- Colomiers
                                                                         (3, 32456, 2023, 2100.0, 'INSEE'),   -- Tournefeuille
                                                                         (4, 295542, 2023, 5080.0, 'INSEE'),  -- Montpellier
                                                                         (5, 79177, 2023, 830.0, 'INSEE'),    -- Béziers
                                                                         (6, 46283, 2023, 2440.0, 'INSEE'),   -- Sète
                                                                         (7, 47068, 2023, 729.0, 'INSEE'),    -- Carcassonne
                                                                         (8, 55695, 2023, 325.0, 'INSEE'),    -- Narbonne
                                                                         (9, 122032, 2023, 1755.0, 'INSEE'),  -- Perpignan
                                                                         (10, 12751, 2023, 554.0, 'INSEE'),   -- Canet-en-Roussillon
                                                                         (11, 151001, 2023, 939.0, 'INSEE'),  -- Nîmes
                                                                         (12, 40400, 2023, 1712.0, 'INSEE'),  -- Alès
                                                                         (13, 51274, 2023, 1127.0, 'INSEE'),  -- Albi
                                                                         (14, 41385, 2023, 1280.0, 'INSEE');  -- Castres

-- Utilisateurs de test
INSERT INTO users (username, email, password, role, login_count) VALUES
                                                                     ('admin', 'admin@airsphere.com', '$2a$10$DummyHashForTestingPurposes123', 'ADMIN', 0),
                                                                     ('sandrine', 'sandrine.alcazar@airsphere.com', '$2a$10$DummyHashForTestingPurposes123', 'USER', 0),
                                                                     ('cyril', 'cyril.schneidenbach@airsphere.com', '$2a$10$DummyHashForTestingPurposes123', 'USER', 0),
                                                                     ('nuno', 'nuno.esteves@airsphere.com', '$2a$10$DummyHashForTestingPurposes123', 'USER', 0),
                                                                     ('testuser', 'test@example.com', '$2a$10$DummyHashForTestingPurposes123', 'USER', 0);

-- Adresses de test
INSERT INTO addresses (user_id, city_id, type, description, street, postal_code, city_name, country) VALUES
                                                                                                         (2, 1, 'HOME', 'Domicile principal', '123 Rue de la Paix', '31000', 'Toulouse', 'France'),
                                                                                                         (3, 4, 'HOME', 'Appartement centre-ville', '45 Avenue de la République', '34000', 'Montpellier', 'France'),
                                                                                                         (4, 9, 'WORK', 'Bureau', '78 Boulevard du Commerce', '66000', 'Perpignan', 'France');

-- Stations qualité de l'air de test
INSERT INTO air_quality_stations (name, code, source, latitude, longitude, city_id) VALUES
                                                                                        ('Toulouse - Chapitre', 'TLS-CHAP-001', 'ATMO Occitanie', 43.612, 1.445, 1),
                                                                                        ('Montpellier - Plan de Cabanes', 'MPL-PLAN-001', 'ATMO Occitanie', 43.615, 3.885, 4),
                                                                                        ('Nîmes - Université', 'NIM-UNIV-001', 'ATMO Occitanie', 43.835, 4.365, 11),
                                                                                        ('Perpignan - République', 'PER-REP-001', 'ATMO Occitanie', 42.700, 2.896, 9);

-- Mesures qualité de l'air de test (données récentes)
INSERT INTO air_quality_measurements (station_id, datetime, pm10, pm25, no2, o3, so2, atmo, source, measured_at) VALUES
                                                                                                                     (1, NOW(), 25.5, 15.2, 38.7, 85.3, 5.1, 3, 'ATMO Occitanie', NOW()),
                                                                                                                     (2, NOW(), 22.1, 12.8, 42.1, 92.5, 3.2, 2, 'ATMO Occitanie', NOW()),
                                                                                                                     (3, NOW(), 28.9, 18.4, 35.6, 78.9, 7.8, 4, 'ATMO Occitanie', NOW()),
                                                                                                                     (4, NOW(), 24.7, 14.1, 40.3, 88.2, 4.5, 3, 'ATMO Occitanie', NOW());

-- Mesures météo de test
INSERT INTO weather_measurements (city_id, temperature, humidity, pressure, wind_speed, wind_direction, source, measured_at) VALUES
                                                                                                                                 (1, 18.5, 65.2, 1013.2, 12.5, 'NO', 'OpenWeatherMap', NOW()),
                                                                                                                                 (4, 22.1, 58.7, 1015.8, 8.3, 'NE', 'OpenWeatherMap', NOW()),
                                                                                                                                 (9, 19.8, 72.1, 1012.5, 15.7, 'SO', 'OpenWeatherMap', NOW()),
                                                                                                                                 (11, 21.3, 55.9, 1014.7, 10.2, 'N', 'OpenWeatherMap', NOW());

-- Forum de test
INSERT INTO forum (title, description) VALUES
    ('Forum AirSphere Connect', 'Plateforme de discussion sur les enjeux environnementaux en Occitanie');

-- Rubriques forum de test
INSERT INTO forum_rubrics (forum_id, user_id, title, description) VALUES
                                                                      (1, 1, 'Qualité de l\'air', 'Discussions sur la pollution atmosphérique'),
                                                                      (1, 1, 'Changement climatique', 'Débats sur les impacts climatiques'),
                                                                      (1, 1, 'Environnement urbain', 'Écologie en ville et solutions durables');

-- Threads forum de test
INSERT INTO forum_threads (rubric_id, user_id, title) VALUES
                                                          (1, 2, 'Pic de pollution à Toulouse - que faire ?'),
                                                          (2, 3, 'Solutions pour réduire notre empreinte carbone'),
                                                          (3, 4, 'Jardins partagés à Montpellier');

-- Posts forum de test
INSERT INTO forum_posts (thread_id, user_id, content, likes_count) VALUES
                                                                       (1, 2, 'J\'ai remarqué une forte pollution aujourd\'hui. Quelles sont les mesures à prendre ?', 3),
                                                                       (1, 3, 'Il faut éviter les activités extérieures et fermer les fenêtres.', 5),
                                                                       (2, 4, 'Utiliser les transports en commun est un bon début !', 2),
                                                                       (3, 2, 'Super initiative ! Y a-t-il des places disponibles ?', 1);

-- Favoris de test
INSERT INTO favorites (user_id, city_id, favorite_category) VALUES
                                                                (2, 1, 'AIR_QUALITY'),  -- Sandrine suit la qualité de l'air à Toulouse
                                                                (2, 1, 'WEATHER'),      -- Sandrine suit aussi la météo à Toulouse
                                                                (3, 4, 'ALL'),          -- Cyril suit tout à Montpellier
                                                                (4, 9, 'POPULATION');   -- Nuno suit la démographie à Perpignan

-- Notifications de test
INSERT INTO notifications (message, notification_type, sent_at, user_id, city_id, is_checked) VALUES
                                                                                                  ('Alerte pollution : indice ATMO élevé à Toulouse', 'AIR_QUALITY', NOW(), 2, 1, FALSE),
                                                                                                  ('Forte chaleur prévue demain à Montpellier', 'WEATHER', NOW(), 3, 4, FALSE),
                                                                                                  ('Nouveau message dans le forum', 'FORUM', NOW(), 4, NULL, TRUE);

-- ===================================
-- VÉRIFICATIONS ET INFORMATIONS
-- ===================================

-- Messages de confirmation
SELECT 'Base de données AirSphere Connect initialisée avec succès !' as message;
SELECT COUNT(*) as nb_regions FROM regions;
SELECT COUNT(*) as nb_departments FROM departments;
SELECT COUNT(*) as nb_cities FROM cities;
SELECT COUNT(*) as nb_users FROM users;
SELECT COUNT(*) as nb_populations FROM populations;
SELECT COUNT(*) as nb_air_stations FROM air_quality_stations;
SELECT COUNT(*) as nb_forum_posts FROM forum_posts;
SELECT COUNT(*) as nb_favorites FROM favorites;

-- Principales villes avec leur population
SELECT
    c.name as ville,
    d.name as departement,
    p.population,
    CONCAT(c.latitude, ', ', c.longitude) as coordonnees
FROM cities c
         JOIN departments d ON c.department_id = d.id
         LEFT JOIN populations p ON c.id = p.city_id AND p.year = 2023
ORDER BY p.population DESC
LIMIT 10;

-- Statistiques forum
SELECT
    'Forum' as section,
    (SELECT COUNT(*) FROM forum_rubrics WHERE deleted_at IS NULL) as rubriques,
    (SELECT COUNT(*) FROM forum_threads WHERE deleted_at IS NULL) as threads,
    (SELECT COUNT(*) FROM forum_posts WHERE deleted_at IS NULL) as posts;