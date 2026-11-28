package fr.opend6.dice.domain;

import java.util.List;

/**
 * Détail complet d'un jet, pour pouvoir l'afficher (animation, historique de partie...).
 *
 * @param codeDe     le Code-Dé effectivement lancé (après doublement éventuel par un PD)
 * @param desNormaux valeurs des dés normaux retenus
 * @param desAnnules valeurs des dés normaux annulés par un échec critique
 * @param desLibres  les Dés Libres (1 en temps normal, 2 avec un Point de Personnage)
 * @param pips       bonus fixe ajouté à la somme
 * @param total      somme finale
 */
public record ResultatJet(
        CodeDe codeDe,
        List<Integer> desNormaux,
        List<Integer> desAnnules,
        List<DeLibre> desLibres,
        int pips,
        int total) {

    public ResultatJet {
        desNormaux = List.copyOf(desNormaux);
        desAnnules = List.copyOf(desAnnules);
        desLibres = List.copyOf(desLibres);
    }

    public boolean echecCritique() {
        return desLibres.stream().anyMatch(DeLibre::echecCritique);
    }

    /** Confronte le total à un niveau de difficulté (réussite si total >= difficulté, p.96). */
    public Resolution contre(int niveauDeDifficulte) {
        return Resolution.de(total, niveauDeDifficulte);
    }
}
