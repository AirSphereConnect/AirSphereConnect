-- ============================================================================
-- Script de génération de données historiques pour 10 villes d'Occitanie
-- Villes: Toulouse, Montpellier, Béziers, Narbonne, Nîmes, Perpignan,
--         Montauban, Albi, Carcassonne, Sète
--
-- Contenu:
-- - Populations historiques 2022-2024 (30 lignes)
-- - Air Quality Index actuel (10 zones, 1 mesure par zone)
-- - Air Quality Measurements historiques 7 jours (840 mesures stations)
-- - Weather Measurements historiques 7 jours (280 mesures météo)
-- ============================================================================

USE `air-sphere-connect`;

-- ============================================================================
-- 2. POPULATIONS - Données historiques 2022, 2023, 2024
-- ============================================================================

-- D'abord, on récupère les city_id des 10 villes via leurs codes INSEE
-- Puis on insère les populations pour 2022, 2023, 2024

INSERT INTO `populations` (`population`, `source`, `year`, `city_id`)
SELECT population, 'INSEE', year, city_id
FROM (
    -- Toulouse (31555)
    SELECT 504078 AS population, 2024 AS year, (SELECT id FROM cities WHERE insee_code = '31555' LIMIT 1) AS city_id UNION ALL
    SELECT 502037, 2023, (SELECT id FROM cities WHERE insee_code = '31555' LIMIT 1) UNION ALL
    SELECT 498003, 2022, (SELECT id FROM cities WHERE insee_code = '31555' LIMIT 1) UNION ALL

    -- Montpellier (34172)
    SELECT 307101, 2024, (SELECT id FROM cities WHERE insee_code = '34172' LIMIT 1) UNION ALL
    SELECT 305211, 2023, (SELECT id FROM cities WHERE insee_code = '34172' LIMIT 1) UNION ALL
    SELECT 302454, 2022, (SELECT id FROM cities WHERE insee_code = '34172' LIMIT 1) UNION ALL

    -- Béziers (34032)
    SELECT 79177, 2024, (SELECT id FROM cities WHERE insee_code = '34032' LIMIT 1) UNION ALL
    SELECT 78950, 2023, (SELECT id FROM cities WHERE insee_code = '34032' LIMIT 1) UNION ALL
    SELECT 78723, 2022, (SELECT id FROM cities WHERE insee_code = '34032' LIMIT 1) UNION ALL

    -- Narbonne (11262)
    SELECT 57084, 2024, (SELECT id FROM cities WHERE insee_code = '11262' LIMIT 1) UNION ALL
    SELECT 56923, 2023, (SELECT id FROM cities WHERE insee_code = '11262' LIMIT 1) UNION ALL
    SELECT 56762, 2022, (SELECT id FROM cities WHERE insee_code = '11262' LIMIT 1) UNION ALL

    -- Nîmes (30189)
    SELECT 152703, 2024, (SELECT id FROM cities WHERE insee_code = '30189' LIMIT 1) UNION ALL
    SELECT 151833, 2023, (SELECT id FROM cities WHERE insee_code = '30189' LIMIT 1) UNION ALL
    SELECT 150962, 2022, (SELECT id FROM cities WHERE insee_code = '30189' LIMIT 1) UNION ALL

    -- Perpignan (66136)
    SELECT 123784, 2024, (SELECT id FROM cities WHERE insee_code = '66136' LIMIT 1) UNION ALL
    SELECT 122996, 2023, (SELECT id FROM cities WHERE insee_code = '66136' LIMIT 1) UNION ALL
    SELECT 122209, 2022, (SELECT id FROM cities WHERE insee_code = '66136' LIMIT 1) UNION ALL

    -- Montauban (82121)
    SELECT 62206, 2024, (SELECT id FROM cities WHERE insee_code = '82121' LIMIT 1) UNION ALL
    SELECT 61885, 2023, (SELECT id FROM cities WHERE insee_code = '82121' LIMIT 1) UNION ALL
    SELECT 61563, 2022, (SELECT id FROM cities WHERE insee_code = '82121' LIMIT 1) UNION ALL

    -- Albi (81004)
    SELECT 52109, 2024, (SELECT id FROM cities WHERE insee_code = '81004' LIMIT 1) UNION ALL
    SELECT 51843, 2023, (SELECT id FROM cities WHERE insee_code = '81004' LIMIT 1) UNION ALL
    SELECT 51577, 2022, (SELECT id FROM cities WHERE insee_code = '81004' LIMIT 1) UNION ALL

    -- Carcassonne (11069)
    SELECT 47419, 2024, (SELECT id FROM cities WHERE insee_code = '11069' LIMIT 1) UNION ALL
    SELECT 47167, 2023, (SELECT id FROM cities WHERE insee_code = '11069' LIMIT 1) UNION ALL
    SELECT 46916, 2022, (SELECT id FROM cities WHERE insee_code = '11069' LIMIT 1) UNION ALL

    -- Sète (34301)
    SELECT 45285, 2024, (SELECT id FROM cities WHERE insee_code = '34301' LIMIT 1) UNION ALL
    SELECT 45026, 2023, (SELECT id FROM cities WHERE insee_code = '34301' LIMIT 1) UNION ALL
    SELECT 44767, 2022, (SELECT id FROM cities WHERE insee_code = '34301' LIMIT 1)
) AS pop_data
WHERE city_id IS NOT NULL;


-- ============================================================================
-- 3. AIR QUALITY INDEX - Historique sur 7 jours pour les 10 zones
-- ============================================================================

-- ✅ Génération de 7 jours d'historique (7 jours × 10 zones = 70 mesures)
-- Les indices varient de façon réaliste entre Bon (1), Moyen (2), Dégradé (3)

INSERT INTO `air_quality_index` (`alert`, `alert_message`, `area_code`, `area_name`, `measured_at`, `quality_color`, `quality_index`, `quality_label`, `source`)
SELECT
    b'0' AS alert,
    NULL AS alert_message,
    zone.area_code,
    zone.area_name,
    DATE_SUB(CURDATE(), INTERVAL (7 - day_num) DAY) + INTERVAL 12 HOUR AS measured_at,
    CASE
        WHEN (day_num + zone.base_quality) % 3 = 0 THEN '#50F0E6'  -- Bon
        WHEN (day_num + zone.base_quality) % 3 = 1 THEN '#50CCAA'  -- Moyen
        ELSE '#FEE440'  -- Dégradé
    END AS quality_color,
    ((day_num + zone.base_quality) % 3) + 1 AS quality_index,
    CASE
        WHEN (day_num + zone.base_quality) % 3 = 0 THEN 'Bon'
        WHEN (day_num + zone.base_quality) % 3 = 1 THEN 'Moyen'
        ELSE 'Dégradé'
    END AS quality_label,
    'Atmo-Occitanie' AS source
FROM (
    SELECT '243100518' AS area_code, 'Toulouse Métropole' AS area_name, 1 AS base_quality UNION ALL
    SELECT '243400017', 'Montpellier Méditerranée Métropole', 0 UNION ALL
    SELECT '243400769', 'CA Béziers Méditerranée', 0 UNION ALL
    SELECT '241100593', 'CA Le Grand Narbonne', 1 UNION ALL
    SELECT '243000643', 'Nîmes Métropole', 1 UNION ALL
    SELECT '200027183', 'CU Perpignan Méditerranée Métropole', 0 UNION ALL
    SELECT '248200099', 'CA du Grand Montauban', 0 UNION ALL
    SELECT '248100737', 'CA de l\'Albigeois (C2a)', 1 UNION ALL
    SELECT '200035715', 'CA Carcassonne Agglo', 0 UNION ALL
    SELECT '200066355', 'CA Sète Agglopôle Méditerranée', 0
) AS zone
CROSS JOIN (
    SELECT 1 AS day_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL
    SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7
) AS days;


-- ============================================================================
-- 4. AIR QUALITY MEASUREMENTS - Mesures des polluants sur 7 jours
-- ============================================================================

-- Pour chaque station, 28 mesures (7 jours × 4 mesures/jour)

INSERT INTO `air_quality_measurements` (`measured_at`, `no2`, `o3`, `pm10`, `pm25`, `so2`, `unit`, `station_id`)
SELECT
    DATE_SUB(CURDATE(), INTERVAL (7 - day_num) DAY) + INTERVAL hour_num HOUR AS measured_at,
    ROUND(8 + (RAND() * 35), 1) AS no2,      -- NO2: 8-43 µg/m³
    ROUND(25 + (RAND() * 60), 1) AS o3,      -- O3: 25-85 µg/m³
    ROUND(10 + (RAND() * 40), 1) AS pm10,    -- PM10: 10-50 µg/m³
    ROUND(4 + (RAND() * 15), 1) AS pm25,     -- PM2.5: 4-19 µg/m³
    IF(RAND() > 0.8, ROUND(1 + (RAND() * 8), 1), NULL) AS so2,  -- SO2: rare
    'ug.m-3' AS unit,
    station.id AS station_id
FROM
    `air_quality_stations` AS station
CROSS JOIN (
    SELECT 1 AS day_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL
    SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7
) AS days
CROSS JOIN (
    SELECT 0 AS hour_num UNION ALL SELECT 6 UNION ALL SELECT 12 UNION ALL SELECT 18
) AS hours
WHERE station.id <= 30  -- Limite aux 30 premières stations
LIMIT 840;  -- 30 stations × 7 jours × 4 heures = 840


-- ============================================================================
-- 5. WEATHER MEASUREMENTS - Météo sur 7 jours
-- ============================================================================

-- Pour chaque ville, 28 mesures (7 jours × 4 mesures/jour)

INSERT INTO `weather_measurements` (`alert`, `alert_message`, `humidity`, `measured_at`, `message`, `pressure`, `source`, `temperature`, `wind_direction`, `wind_speed`, `city_id`)
SELECT
    b'0' AS alert,
    NULL AS alert_message,
    ROUND(65 + (RAND() * 25), 0) AS humidity,  -- Humidité: 65-90%
    DATE_SUB(CURDATE(), INTERVAL (7 - day_num) DAY) + INTERVAL hour_num HOUR AS measured_at,
    CASE (day_num + hour_num) % 4
        WHEN 0 THEN '[{"main":"Clear","description":"clear sky","icon":"01d"}]'
        WHEN 1 THEN '[{"main":"Clouds","description":"scattered clouds","icon":"03d"}]'
        WHEN 2 THEN '[{"main":"Clouds","description":"broken clouds","icon":"04d"}]'
        ELSE '[{"main":"Clouds","description":"few clouds","icon":"02d"}]'
    END AS message,
    ROUND(1008 + (RAND() * 12), 0) AS pressure,  -- Pression: 1008-1020 hPa
    'openweathermap.org' AS source,
    ROUND(
        12 + (RAND() * 8) +
        (CASE WHEN hour_num = 12 THEN 4 WHEN hour_num = 18 THEN 2 ELSE -2 END),
        2
    ) AS temperature,  -- Température: 10-24°C (plus chaud à midi)
    ROUND(RAND() * 360, 0) AS wind_direction,  -- Direction: 0-360°
    ROUND(0.8 + (RAND() * 4), 2) AS wind_speed,  -- Vent: 0.8-4.8 m/s
    city.id AS city_id
FROM (
    SELECT id FROM cities WHERE insee_code IN (
        '31555', '34172', '34032', '11262', '30189',
        '66136', '82121', '81004', '11069', '34301'
    )
) AS city
CROSS JOIN (
    SELECT 1 AS day_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL
    SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7
) AS days
CROSS JOIN (
    SELECT 0 AS hour_num UNION ALL SELECT 6 UNION ALL SELECT 12 UNION ALL SELECT 18
) AS hours;


-- ============================================================================
-- Vérification des insertions
-- ============================================================================

SELECT '✅ Données historiques générées avec succès !' AS statut;

SELECT 'Populations' AS table_name, COUNT(*) AS nb_rows FROM `populations` WHERE year IN (2022, 2023, 2024) UNION ALL
SELECT 'Air Quality Index', COUNT(*) FROM `air_quality_index` UNION ALL
SELECT 'Air Quality Measurements', COUNT(*) FROM `air_quality_measurements` WHERE measured_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) UNION ALL
SELECT 'Weather Measurements', COUNT(*) FROM `weather_measurements` WHERE measured_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY);
