package com.airsphereconnect.utils;

/**
 * Classe utilitaire pour la génération des messages d'alerte liés à la qualité de l'air.
 * Fournit des méthodes statiques pour créer des messages d'alerte en fonction de l'indice ATMO (1-6).
 * Les alertes sont graduées selon le niveau de pollution et incluent des recommandations spécifiques.
 */
public class AirQualityAlertUtils {
    /**
     * Constructeur par défaut.
     */
    private AirQualityAlertUtils() {

    }

    /**
     * Génère un message d'alerte approprié en fonction de l'indice ATMO de qualité de l'air.
     * L'indice ATMO est une échelle de 1 à 6 :
     * - 1-2 : Bon à très bon (pas d'alerte)
     * - 3 : Moyen (alerte pour personnes sensibles)
     * - 4 : Dégradé (recommandations pour personnes sensibles)
     * - 5 : Mauvais (alerte pour tous, consignes strictes pour sensibles)
     * - 6 : Très mauvais (alerte générale, réduction des activités)
     *
     * @param atmoIndex L'indice ATMO de qualité de l'air (1-6)
     * @return Le message d'alerte avec recommandations, ou null si la qualité est acceptable (indices 1-2)
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
     * Génère un message d'alerte contextuel incluant le nom de la zone géographique concernée.
     * Utilise {@link #determineAlertMessage(Integer)} pour générer le message de base,
     * puis ajoute le nom de la zone entre parenthèses.
     * Si aucun message d'alerte n'est généré (qualité acceptable) ou si le nom de zone est null,
     * retourne le message de base sans modification.
     *
     * @param atmoIndex L'indice ATMO de qualité de l'air (1-6)
     * @param areaName  Le nom de la zone géographique concernée par l'alerte
     * @return Le message d'alerte formaté avec le nom de la zone, ou le message de base si areaName est null
     */
    public static String determineAlertMessageWithArea(Integer atmoIndex, String areaName) {
        String baseMessage = determineAlertMessage(atmoIndex);

        if (baseMessage == null || areaName == null) {
            return baseMessage;
        }

        return String.format("%s (Zone : %s)", baseMessage, areaName);
    }
}
