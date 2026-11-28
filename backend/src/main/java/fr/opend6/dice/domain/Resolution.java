package fr.opend6.dice.domain;

/**
 * Résolution d'un jet face à un niveau de difficulté, avec niveau de réussite / d'échec (p.101).
 *
 * @param reussite vrai si total >= difficulté
 * @param marge    total - difficulté (négative en cas d'échec)
 * @param degre    ampleur de la réussite ou de l'échec
 * @param egalite  vrai si total == difficulté (option p.97 : le MJ peut faire relancer)
 */
public record Resolution(boolean reussite, int marge, DegreResultat degre, boolean egalite) {

    public static Resolution de(int total, int difficulte) {
        int marge = total - difficulte;
        return new Resolution(marge >= 0, marge, DegreResultat.pourEcart(Math.abs(marge)), marge == 0);
    }
}
