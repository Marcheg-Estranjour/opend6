package fr.opend6.regles.des;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Code-Dé du Système D6 : un nombre de dés à six faces plus des « pips » (bonus fixes).
 * <p>
 * Règle (D6 intégral p.25) : 1D = 3 pips. Une valeur de +3 ou plus n'apparaît jamais
 * sur une fiche : elle est convertie en dés. Ex. 5D+4 devient 6D+1.
 * La forme est donc toujours normalisée : 0 &lt;= pips &lt;= 2.
 */
public record CodeDe(int des, int pips) implements Comparable<CodeDe> {

    public static final int PIPS_PAR_DE = 3;
    public static final CodeDe ZERO = new CodeDe(0, 0);

    private static final Pattern FORMAT =
            Pattern.compile("^\\s*(\\d+)\\s*[dD]\\s*(?:([+-])\\s*(\\d+))?\\s*$");

    public CodeDe {
        int total = des * PIPS_PAR_DE + pips;
        if (total < 0) {
            throw new IllegalArgumentException("Un Code-Dé ne peut pas être négatif : " + des + "D" + pips);
        }
        des = total / PIPS_PAR_DE;
        pips = total % PIPS_PAR_DE;
    }

    public static CodeDe de(int des) {
        return new CodeDe(des, 0);
    }

    /** Construit un Code-Dé à partir d'un nombre total de pips (1D = 3 pips). */
    public static CodeDe depuisPips(int totalPips) {
        return new CodeDe(0, totalPips);
    }

    /** Analyse une notation du type "3D", "3D+2", "4d-1". */
    public static CodeDe parse(String notation) {
        if (notation == null) {
            throw new IllegalArgumentException("Notation de Code-Dé absente");
        }
        Matcher m = FORMAT.matcher(notation);
        if (!m.matches()) {
            throw new IllegalArgumentException("Notation de Code-Dé invalide : '" + notation + "'");
        }
        int des = Integer.parseInt(m.group(1));
        int pips = m.group(3) == null ? 0 : Integer.parseInt(m.group(3));
        if ("-".equals(m.group(2))) {
            pips = -pips;
        }
        return new CodeDe(des, pips);
    }

    public int totalPips() {
        return des * PIPS_PAR_DE + pips;
    }

    public CodeDe plus(CodeDe autre) {
        return depuisPips(totalPips() + autre.totalPips());
    }

    /** Soustraction bornée à 0D (un malus ne peut pas rendre un code négatif). */
    public CodeDe moins(CodeDe autre) {
        return depuisPips(Math.max(0, totalPips() - autre.totalPips()));
    }

    /** Malus de N dés (actions multiples, compétence approchante...), borné à 0D. */
    public CodeDe malusDes(int nombreDeDes) {
        return moins(de(nombreDeDes));
    }

    /**
     * Valeur fixe d'un PNJ pour éviter un jet en opposition (p.101) :
     * dés x 3 + pips. Ex. 3D+1 donne 10.
     */
    public int valeurFixe() {
        return des * 3 + pips;
    }

    @Override
    public int compareTo(CodeDe autre) {
        return Integer.compare(totalPips(), autre.totalPips());
    }

    @Override
    public String toString() {
        return pips == 0 ? des + "D" : des + "D+" + pips;
    }
}
