package fr.opend6.regles.des;

/**
 * Options décidées par le joueur AVANT le jet (p.98).
 *
 * @param pointDePersonnage dépenser un PP : lance un Dé Libre supplémentaire
 * @param pointDeDestin     dépenser un PD : lance le double du nombre habituel de dés
 */
public record OptionsJet(boolean pointDePersonnage, boolean pointDeDestin) {

    public static final OptionsJet AUCUNE = new OptionsJet(false, false);

    public OptionsJet {
        if (pointDePersonnage && pointDeDestin) {
            throw new IllegalArgumentException(
                    "Impossible de dépenser à la fois un Point de Personnage et un Point de Destin sur un même jet");
        }
    }
}
