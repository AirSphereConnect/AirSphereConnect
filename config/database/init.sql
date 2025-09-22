-- ===================================
-- AIRSPHERE DATABASE INITIALIZATION - VERSION COMPLETE
-- ===================================

CREATE DATABASE IF NOT EXISTS `air-sphere-connect`;
USE `air-sphere-connect`;

-- ===================================
-- TABLES DE BASE GÉOGRAPHIQUE
-- ===================================

CREATE TABLE regions
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(10)  NOT NULL UNIQUE
);

CREATE TABLE departments
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    code      VARCHAR(10)  NOT NULL UNIQUE,
    name      VARCHAR(100) NOT NULL,
    region_id BIGINT       NOT NULL,
    FOREIGN KEY (region_id) REFERENCES regions (id) ON DELETE CASCADE,
    INDEX idx_dept_region (region_id)
);

CREATE TABLE cities
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    postal_code   VARCHAR(10)  NOT NULL,
    department_id BIGINT       NOT NULL,
    population    INTEGER       NOT NULL,
    latitude      DOUBLE       NOT NULL,
    longitude     DOUBLE       NOT NULL,
    area_code     VARCHAR(10),
    FOREIGN KEY (department_id) REFERENCES departments (id) ON DELETE CASCADE,
    INDEX idx_city_dept (department_id),
    INDEX idx_city_coords (latitude, longitude)
);

-- ===================================
-- TABLES UTILISATEURS ET AUTHENTIFICATION
-- ===================================

CREATE TABLE users
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(100) NOT NULL UNIQUE,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM ('USER','ADMIN','GUEST') DEFAULT 'USER',
    login_count INTEGER                       DEFAULT 0,
    created_at  DATETIME                      DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME                      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME                      DEFAULT NULL,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
);

CREATE TABLE addresses
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    city_id    BIGINT       NOT NULL,
    street     VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (city_id) REFERENCES cities (id),
    INDEX idx_address_user (user_id)
);

-- ===================================
-- TABLES DONNÉES ENVIRONNEMENTALES
-- ===================================

CREATE TABLE populations
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    city_id    BIGINT       NOT NULL,
    population INTEGER       NOT NULL,
    year       INTEGER      NOT NULL,
    source     VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (city_id) REFERENCES cities (id) ON DELETE CASCADE,
    INDEX idx_pop_city_year (city_id, year),
    UNIQUE KEY unique_city_year (city_id, year)
);

CREATE TABLE air_quality_stations
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    code       VARCHAR(50)  NOT NULL UNIQUE,
    source     VARCHAR(100) NOT NULL,
    latitude   DOUBLE       NOT NULL,
    longitude  DOUBLE       NOT NULL,
    city_id    BIGINT       NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (city_id) REFERENCES cities (id),
    INDEX idx_station_city (city_id),
    INDEX idx_station_coords (latitude, longitude)
);

CREATE TABLE air_quality_measurements
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id  BIGINT   NOT NULL,
    pm10        DOUBLE,
    pm25        DOUBLE,
    no2         DOUBLE,
    o3          DOUBLE,
    so2         DOUBLE,
    atmo        INTEGER,
    area_code   VARCHAR(10),
    message     VARCHAR(50),
    source      VARCHAR(100),
    measured_at DATETIME NOT NULL,
    FOREIGN KEY (station_id) REFERENCES air_quality_stations (id) ON DELETE CASCADE,
    INDEX idx_measurement_station_date (station_id, measured_at),
    INDEX idx_measurement_date (measured_at)
);

CREATE TABLE weather_measurements
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    city_id        BIGINT   NOT NULL,
    temperature    DOUBLE,
    humidity       DOUBLE,
    pressure       DOUBLE,
    wind_speed     DOUBLE,
    wind_direction VARCHAR(50),
    message        VARCHAR(50),
    source         VARCHAR(100),
    measured_at    DATETIME NOT NULL,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (city_id) REFERENCES cities (id) ON DELETE CASCADE,
    INDEX idx_weather_city_date (city_id, measured_at)
);

-- ===================================
-- TABLES FAVORIS
-- ===================================

CREATE TABLE favorites
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT                                            NOT NULL,
    city_id           BIGINT                                            NOT NULL,
    favorite_category ENUM ('POPULATION','AIR_QUALITY','WEATHER','ALL') NOT NULL,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at        DATETIME DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (city_id) REFERENCES cities (id),
    UNIQUE KEY unique_user_city_category (user_id, city_id, favorite_category),
    INDEX idx_favorite_user (user_id)
);

-- ===================================
-- TABLES FORUM
-- ===================================

CREATE TABLE forum
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME DEFAULT NULL
);

CREATE TABLE forum_rubrics
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    forum_id    BIGINT,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME DEFAULT NULL,
    FOREIGN KEY (forum_id) REFERENCES forum (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_rubric_forum (forum_id),
    INDEX idx_rubric_user (user_id)
);

CREATE TABLE forum_threads
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    rubric_id  BIGINT       NOT NULL,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (rubric_id) REFERENCES forum_rubrics (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_thread_rubric (rubric_id),
    INDEX idx_thread_user (user_id)
);

CREATE TABLE forum_posts
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    thread_id  BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    content    TEXT   NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (thread_id) REFERENCES forum_threads (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_post_thread (thread_id),
    INDEX idx_post_user (user_id)
);

-- ==============================
-- TABLE FAVORITE_ALERTS (Paramètres d'alertes par utilisateur)
-- ==============================
CREATE TABLE favorites_alerts
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    city_id       INT    NOT NULL,
    department_id BIGINT,
    region_id     BIGINT,
    is_enabled    BOOLEAN  DEFAULT TRUE,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (city_id) REFERENCES cities (id),
    FOREIGN KEY (department_id) REFERENCES departments (id),
    FOREIGN KEY (region_id) REFERENCES regions (id),

    INDEX idx_alert_user_enabled (user_id, is_enabled),
    INDEX idx_alert_city (city_id)
);

-- ==============================
-- TABLE ALERTE (Historique des notifications envoyées)
-- ==============================
CREATE TABLE alerts
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT                          NOT NULL,
    city_id       INT                             NOT NULL,
    department_id BIGINT,
    region_id     BIGINT,
    alert_type    ENUM ('AIR_QUALITY', 'WEATHER') NOT NULL,
    message       TEXT                            NOT NULL,
    sent_at       DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (city_id) REFERENCES cities (id),
    FOREIGN KEY (department_id) REFERENCES departments (id),
    FOREIGN KEY (region_id) REFERENCES regions (id),

    INDEX idx_sent_date (sent_at),
    INDEX idx_city_type (city_id, alert_type),
    INDEX idx_user (user_id)
);


-- ===================================
-- TABLE POST REACTIONS
-- ===================================

CREATE TABLE post_reactions
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT                  NOT NULL,
    post_id       BIGINT                  NOT NULL,
    reaction_type ENUM ('LIKE','DISLIKE') NOT NULL,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    DATETIME DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES forum_posts (id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_post (user_id, post_id),
    INDEX idx_reaction_user (user_id),
    INDEX idx_reaction_post (post_id)
);


-- ===================================
-- DONNÉES DE TEST - OCCITANIE (VERSION CORRIGÉE)
-- ===================================

-- 1️⃣ Région Occitanie
INSERT INTO regions (name, code)
VALUES ('Occitanie', '76');
SET @region_id := LAST_INSERT_ID();

-- 2️⃣ Départements d'Occitanie
INSERT INTO departments (code, name, region_id)
VALUES ('09', 'Ariège', @region_id),
       ('11', 'Aude', @region_id),
       ('12', 'Aveyron', @region_id),
       ('30', 'Gard', @region_id),
       ('31', 'Haute-Garonne', @region_id),
       ('32', 'Gers', @region_id),
       ('34', 'Hérault', @region_id),
       ('46', 'Lot', @region_id),
       ('48', 'Lozère', @region_id),
       ('65', 'Hautes-Pyrénées', @region_id),
       ('66', 'Pyrénées-Orientales', @region_id),
       ('81', 'Tarn', @region_id),
       ('82', 'Tarn-et-Garonne', @region_id);

-- Récupérer les IDs des départements
SELECT id
INTO @dep_ariege
FROM departments
WHERE code = '09';
SELECT id
INTO @dep_aude
FROM departments
WHERE code = '11';
SELECT id
INTO @dep_aveyron
FROM departments
WHERE code = '12';
SELECT id
INTO @dep_gard
FROM departments
WHERE code = '30';
SELECT id
INTO @dep_haute_garonne
FROM departments
WHERE code = '31';
SELECT id
INTO @dep_gers
FROM departments
WHERE code = '32';
SELECT id
INTO @dep_herault
FROM departments
WHERE code = '34';
SELECT id
INTO @dep_lot
FROM departments
WHERE code = '46';
SELECT id
INTO @dep_lozere
FROM departments
WHERE code = '48';
SELECT id
INTO @dep_haut_py
FROM departments
WHERE code = '65';
SELECT id
INTO @dep_pyr_or
FROM departments
WHERE code = '66';
SELECT id
INTO @dep_tarn
FROM departments
WHERE code = '81';
SELECT id
INTO @dep_tarn_garonne
FROM departments
WHERE code = '82';

-- 3️⃣ Villes principales avec coordonnées GPS
INSERT INTO cities (name, postal_code, department_id, population, latitude, longitude, area_code)
VALUES ('Toulouse', '31000', @dep_haute_garonne, 493000, 43.604652, 1.444209, '31555'),
       ('Colomiers', '31770', @dep_haute_garonne, 39000, 43.611546, 1.332524, '31149'),
       ('Tournefeuille', '31170', @dep_haute_garonne, 26000, 43.586111, 1.343611, '31557'),
       ('Montpellier', '34000', @dep_herault, 290000, 43.610769, 3.876716, '34172'),
       ('Béziers', '34500', @dep_herault, 77000, 43.344097, 3.215795, '34032'),
       ('Sète', '34200', @dep_herault, 43000, 43.403889, 3.696944, '34301'),
       ('Carcassonne', '11000', @dep_aude, 47000, 43.212161, 2.353663, '11069'),
       ('Narbonne', '11100', @dep_aude, 54000, 43.183946, 3.004230, '11262'),
       ('Perpignan', '66000', @dep_pyr_or, 122000, 42.698611, 2.895556, '66136'),
       ('Canet-en-Roussillon', '66140', @dep_pyr_or, 14000, 42.693989, 3.038056, '66037'),
       ('Nîmes', '30000', @dep_gard, 150000, 43.836699, 4.360054, '30189'),
       ('Alès', '30100', @dep_gard, 40000, 44.125102, 4.081061, '30007'),
       ('Albi', '81000', @dep_tarn, 50000, 43.929063, 2.147899, '81004'),
       ('Castres', '81100', @dep_tarn, 42000, 43.605473, 2.240647, '81065');

-- Récupérer les IDs des villes
SELECT id
INTO @city_toulouse
FROM cities
WHERE name = 'Toulouse';
SELECT id
INTO @city_colomiers
FROM cities
WHERE name = 'Colomiers';
SELECT id
INTO @city_tournefeuille
FROM cities
WHERE name = 'Tournefeuille';
SELECT id
INTO @city_montpellier
FROM cities
WHERE name = 'Montpellier';
SELECT id
INTO @city_beziers
FROM cities
WHERE name = 'Béziers';
SELECT id
INTO @city_sete
FROM cities
WHERE name = 'Sète';
SELECT id
INTO @city_carcassonne
FROM cities
WHERE name = 'Carcassonne';
SELECT id
INTO @city_narbonne
FROM cities
WHERE name = 'Narbonne';
SELECT id
INTO @city_perpignan
FROM cities
WHERE name = 'Perpignan';
SELECT id
INTO @city_canet
FROM cities
WHERE name = 'Canet-en-Roussillon';
SELECT id
INTO @city_nimes
FROM cities
WHERE name = 'Nîmes';
SELECT id
INTO @city_ales
FROM cities
WHERE name = 'Alès';
SELECT id
INTO @city_albi
FROM cities
WHERE name = 'Albi';
SELECT id
INTO @city_castres
FROM cities
WHERE name = 'Castres';

-- 4️⃣ Utilisateurs de test
INSERT INTO users (username, email, password, role, login_count)
VALUES ('admin', 'admin@airsphere.com', '$2a$10$DummyHashForTestingPurposes123', 'ADMIN', 0),
       ('sandrine', 'sandrine.alcazar@airsphere.com', '$2a$10$DummyHashForTestingPurposes123', 'USER', 0),
       ('cyril', 'cyril.schneidenbach@airsphere.com', '$2a$10$DummyHashForTestingPurposes123', 'USER', 0),
       ('nuno', 'nuno.esteves@airsphere.com', '$2a$10$DummyHashForTestingPurposes123', 'USER', 0),
       ('testuser', 'test@example.com', '$2a$10$DummyHashForTestingPurposes123', 'USER', 0);

-- Récupérer les IDs des utilisateurs
SELECT id
INTO @user_admin
FROM users
WHERE username = 'admin';
SELECT id
INTO @user_sandrine
FROM users
WHERE username = 'sandrine';
SELECT id
INTO @user_cyril
FROM users
WHERE username = 'cyril';
SELECT id
INTO @user_nuno
FROM users
WHERE username = 'nuno';
SELECT id
INTO @user_test
FROM users
WHERE username = 'testuser';

-- 5️⃣ Adresses de test
INSERT INTO addresses (user_id, city_id, street)
VALUES (@user_sandrine, @city_toulouse, '123 Rue de la Paix'),
       (@user_cyril, @city_montpellier, '45 Avenue de la République');

-- Stations qualité de l'air de test
INSERT INTO air_quality_stations (name, code, source, latitude, longitude, city_id)
VALUES ('Toulouse - Chapitre', 'TLS-CHAP-001', 'ATMO Occitanie', 43.612, 1.445, 1),
       ('Montpellier - Plan de Cabanes', 'MPL-PLAN-001', 'ATMO Occitanie', 43.615, 3.885, 4),
       ('Nîmes - Université', 'NIM-UNIV-001', 'ATMO Occitanie', 43.835, 4.365, 11),
       ('Perpignan - République', 'PER-REP-001', 'ATMO Occitanie', 42.700, 2.896, 9);

-- Mesures qualité de l'air de test (données récentes)
INSERT INTO air_quality_measurements (station_id, pm10, pm25, no2, o3, so2, atmo, source, measured_at)
VALUES (1, 25.5, 15.2, 38.7, 85.3, 5.1, 3, 'ATMO Occitanie', NOW()),
       (2, 22.1, 12.8, 42.1, 92.5, 3.2, 2, 'ATMO Occitanie', NOW()),
       (3, 28.9, 18.4, 35.6, 78.9, 7.8, 4, 'ATMO Occitanie', NOW()),
       (4, 24.7, 14.1, 40.3, 88.2, 4.5, 3, 'ATMO Occitanie', NOW());

-- Mesures météo de test
INSERT INTO weather_measurements (city_id, temperature, humidity, pressure, wind_speed, wind_direction, source,
                                  measured_at)
VALUES (1, 18.5, 65.2, 1013.2, 12.5, 'NO', 'OpenWeatherMap', NOW()),
       (4, 22.1, 58.7, 1015.8, 8.3, 'NE', 'OpenWeatherMap', NOW()),
       (9, 19.8, 72.1, 1012.5, 15.7, 'SO', 'OpenWeatherMap', NOW()),
       (11, 21.3, 55.9, 1014.7, 10.2, 'N', 'OpenWeatherMap', NOW());

-- Forum de test
INSERT INTO forum (title, description)
VALUES ('Forum AirSphere Connect', 'Plateforme de discussion sur les enjeux environnementaux en Occitanie');

-- Rubriques forum de test
INSERT INTO forum_rubrics (forum_id, user_id, title, description)
VALUES (1, 1, 'Qualité de l\'air', 'Discussions sur la pollution atmosphérique'),
       (1, 1, 'Changement climatique', 'Débats sur les impacts climatiques'),
       (1, 1, 'Environnement urbain', 'Écologie en ville et solutions durables');

-- Threads forum de test
INSERT INTO forum_threads (rubric_id, user_id, title)
VALUES (1, 2, 'Pic de pollution à Toulouse - que faire ?'),
       (2, 3, 'Solutions pour réduire notre empreinte carbone'),
       (3, 4, 'Jardins partagés à Montpellier');

-- Posts forum de test
INSERT INTO forum_posts (thread_id, user_id, content)
VALUES (1, 2, 'J\'ai remarqué une forte pollution aujourd\'hui. Quelles sont les mesures à prendre ?'),
       (1, 3, 'Il faut éviter les activités extérieures et fermer les fenêtres.'),
       (2, 4, 'Utiliser les transports en commun est un bon début !'),
       (3, 2, 'Super initiative ! Y a-t-il des places disponibles ?');

INSERT INTO post_reactions (user_id, post_id, reaction_type)
VALUES (@user_cyril, 1, 'LIKE'),
       (@user_sandrine, 2, 'LIKE');

-- Favoris de test
INSERT INTO favorites (user_id, city_id, favorite_category)
VALUES (2, 1, 'AIR_QUALITY'), -- Sandrine suit la qualité de l'air à Toulouse
       (2, 1, 'WEATHER'),     -- Sandrine suit aussi la météo à Toulouse
       (3, 4, 'ALL'),         -- Cyril suit tout à Montpellier
       (4, 9, 'POPULATION');
-- Nuno suit la démographie à Perpignan

-- Alertes favorites
INSERT INTO favorites_alerts (user_id, city_id, is_enabled) VALUES
                                                                (1, 1, TRUE),
                                                                (2, 2, TRUE);

-- Historique des alertes
INSERT INTO alerts (user_id, city_id, alert_type, message) VALUES
                                                               (1, 1, 'AIR_QUALITY', 'PM10 élevé aujourd’hui'),
                                                               (2, 2, 'WEATHER', 'Vent fort prévu');
-- ===================================
-- VÉRIFICATIONS ET INFORMATIONS
-- ===================================

-- Message de confirmation
SELECT 'Base de données AirSphere Connect initialisée avec succès !' AS message;

-- Comptage global
SELECT COUNT(*) AS nb_regions
FROM regions;
SELECT COUNT(*) AS nb_departments
FROM departments;
SELECT COUNT(*) AS nb_cities
FROM cities;
SELECT COUNT(*) AS nb_users
FROM users;
SELECT COUNT(*) AS nb_populations
FROM populations;
SELECT COUNT(*) AS nb_air_stations
FROM air_quality_stations;
SELECT COUNT(*) AS nb_forum_posts
FROM forum_posts;
SELECT COUNT(*) AS nb_favorites
FROM favorites;

-- Principales villes avec population et coordonnées
SELECT c.name                                AS ville,
       d.name                                AS departement,
       p.population,
       CONCAT(c.latitude, ', ', c.longitude) AS coordonnees
FROM cities c
         JOIN departments d ON c.department_id = d.id
         LEFT JOIN populations p ON c.id = p.city_id AND p.year = 2023
ORDER BY p.population DESC
LIMIT 10;

-- Statistiques forum
SELECT 'Forum'                                                       AS section,
       (SELECT COUNT(*) FROM forum_rubrics WHERE deleted_at IS NULL) AS rubriques,
       (SELECT COUNT(*) FROM forum_threads WHERE deleted_at IS NULL) AS threads,
       (SELECT COUNT(*) FROM forum_posts WHERE deleted_at IS NULL)   AS posts;

-- Liste des utilisateurs et leurs adresses
SELECT u.username, u.email, a.street, c.name AS city, d.name AS department, r.name AS region
FROM users u
         JOIN addresses a ON a.user_id = u.id
         JOIN cities c ON c.id = a.city_id
         JOIN departments d ON d.id = c.department_id
         JOIN regions r ON r.id = d.region_id;

-- Mesures qualité de l’air récentes
SELECT s.name AS station, c.name AS city, m.pm10, m.pm25, m.no2, m.o3, m.so2, m.atmo, m.measured_at
FROM air_quality_measurements m
         JOIN air_quality_stations s ON s.id = m.station_id
         JOIN cities c ON c.id = s.city_id
ORDER BY m.measured_at DESC;

-- Posts et réactions
SELECT p.content, u.username, r.reaction_type
FROM forum_posts p
         LEFT JOIN post_reactions r ON r.post_id = p.id
         LEFT JOIN users u ON u.id = r.user_id;

-- Favoris par utilisateur
SELECT u.username, c.name AS city, f.favorite_category
FROM favorites f
         JOIN users u ON u.id = f.user_id
         JOIN cities c ON c.id = f.city_id;
