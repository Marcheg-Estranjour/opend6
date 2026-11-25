package fr.opend6.regles.des;

/**
 * Échelle des niveaux de difficulté (D6 intégral p.93).
 */
public enum NiveauDifficulte {
    TRES_FACILE(1, 5, "Très facile", "Action que tout le monde peut réussir"),
    FACILE(6, 10, "Facile", "À la portée d'un pratiquant occasionnel"),
    MOYEN(11, 15, "Moyen", "À la portée d'un professionnel"),
    DIFFICILE(16, 20, "Difficile", "À la portée d'un très bon professionnel"),
    TRES_DIFFICILE(21, 25, "Très difficile", "Quelques personnes dans une région la réussissent régulièrement"),
    HEROIQUE(26, 30, "Héroïque", "Quelques personnes dans un pays la réussissent régulièrement"),
    LEGENDAIRE(31, Integer.MAX_VALUE, "Légendaire", "Les meilleurs experts mondiaux pourraient échouer");

    private final int min;
    private final int max;
    private final String libelle;
    private final String description;

    NiveauDifficulte(int min, int max, String libelle, String description) {
        this.min = min;
        this.max = max;
        this.libelle = libelle;
        this.description = description;
    }

    public static NiveauDifficulte pourValeur(int valeur) {
        if (valeur < 1) {
            return TRES_FACILE;
        }
        for (NiveauDifficulte n : values()) {
            if (valeur >= n.min && valeur <= n.max) {
                return n;
            }
        }
        return LEGENDAIRE;
    }

    public int min() { return min; }
    public int max() { return max; }
    public String libelle() { return libelle; }
    public String description() { return description; }
}
