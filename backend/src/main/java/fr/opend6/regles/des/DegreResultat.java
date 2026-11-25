package fr.opend6.regles.des;

/**
 * Niveaux de réussite et d'échec (D6 intégral p.101), selon l'écart entre le jet et la difficulté.
 */
public enum DegreResultat {
    MINIMAL(0, 0, "Réussite / échec minimal"),
    NORMAL(1, 4, "Réussite / échec normal"),
    BEAU(5, 8, "Belle réussite / bel échec"),
    HAUT_NIVEAU(9, 12, "Réussite de haut niveau / grand échec"),
    SPECTACULAIRE(13, 16, "Réussite / échec spectaculaire"),
    MEMORABLE(17, Integer.MAX_VALUE, "Restera dans les mémoires");

    private final int ecartMin;
    private final int ecartMax;
    private final String libelle;

    DegreResultat(int ecartMin, int ecartMax, String libelle) {
        this.ecartMin = ecartMin;
        this.ecartMax = ecartMax;
        this.libelle = libelle;
    }

    public static DegreResultat pourEcart(int ecart) {
        for (DegreResultat d : values()) {
            if (ecart >= d.ecartMin && ecart <= d.ecartMax) {
                return d;
            }
        }
        throw new IllegalArgumentException("Écart négatif : " + ecart);
    }

    public String libelle() {
        return libelle;
    }
}
