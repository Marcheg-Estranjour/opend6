package fr.opend6.regles.des;

import java.util.List;

/**
 * Résultat d'un Dé Libre (p.97-98).
 * <ul>
 *   <li>2 à 5 : s'ajoute normalement ;</li>
 *   <li>6 : s'ajoute et on relance, tant que l'on obtient des 6 (explosion) ;</li>
 *   <li>1 au premier lancer : échec critique (un 1 après un 6 n'en est pas un).</li>
 * </ul>
 *
 * @param lancers      les valeurs successives obtenues (plusieurs si explosion)
 * @param echecCritique vrai si le premier lancer vaut 1
 * @param compte        faux si la politique d'échec critique a retiré ce dé de la somme
 */
public record DeLibre(List<Integer> lancers, boolean echecCritique, boolean compte) {

    public DeLibre {
        lancers = List.copyOf(lancers);
    }

    public int somme() {
        return lancers.stream().mapToInt(Integer::intValue).sum();
    }

    public int valeurRetenue() {
        return compte ? somme() : 0;
    }

    public boolean aExplose() {
        return lancers.size() > 1;
    }
}
