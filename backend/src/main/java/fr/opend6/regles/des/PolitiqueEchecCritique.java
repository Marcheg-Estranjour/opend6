package fr.opend6.regles.des;

/**
 * Les deux options proposées par le D6 intégral (p.98) quand le Dé Libre fait 1 au premier lancer.
 */
public enum PolitiqueEchecCritique {
    /** Le Dé Libre ne compte pas et annule en plus le dé normal le plus élevé. */
    ANNULER_DE_LIBRE_ET_PLUS_HAUT,
    /** Le Dé Libre compte normalement, mais un incident imprévu survient (à la charge du MJ). */
    COMPTER_ET_INCIDENT
}
