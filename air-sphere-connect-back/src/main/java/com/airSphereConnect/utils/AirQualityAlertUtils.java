package com.airSphereConnect.utils;

public class AirQualityAlertUtils {
    public AirQualityAlertUtils() {

    }

    /**
     * Génère un message d'alerte selon l'indice ATMO (1-6)
     *
     * @param atmoIndex Indice de qualité de l'air (1-6)
     * @return Message d'alerte ou null si qualité acceptable
     */
    public static String determineAlertMessage(Integer atmoIndex) {
        if (atmoIndex == null) return null;

        return switch (atmoIndex) {
            case 1, 2 -> null; // Qualité bonne à très bonne
            case 3 -> "Qualité de l'air moyenne - Personnes sensibles : soyez vigilants";
            case 4 -> "⚠️ Qualité de l'air dégradée - Personnes sensibles : limitez les activités physiques intenses";
            case 5 -> "🚨 Qualité de l'air mauvaise - Évitez les efforts prolongés. Personnes sensibles : restez à l'intérieur";
            case 6 -> "🔴 Qualité de l'air très mauvaise - Population générale : réduisez les activités physiques";
            default -> null;
        };
    }

    /**
     * Génère un message d'alerte avec nom de zone
     *
     * @param atmoIndex Indice de qualité de l'air (1-6)
     * @param areaName Nom de la zone concernée
     * @return Message d'alerte formaté avec le nom de la zone
     */
    public static String determineAlertMessageWithArea(Integer atmoIndex, String areaName) {
        String baseMessage = determineAlertMessage(atmoIndex);

        if (baseMessage == null || areaName == null) {
            return baseMessage;
        }

        return String.format("%s (Zone : %s)", baseMessage, areaName);
    }
}
