package fr.opend6.dice.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Moteur de jet du Système D6 (chapitre 3).
 * <p>
 * Pour un Code-Dé XD+Y : on lance X dés dont exactement un Dé Libre, on additionne, on ajoute Y.
 * <ul>
 *   <li>Point de Destin : on lance 2X dés (toujours un seul Dé Libre) ;</li>
 *   <li>Point de Personnage : on lance un Dé Libre supplémentaire ;</li>
 *   <li>si le code vaut 1D, l'unique dé est le Dé Libre ;</li>
 *   <li>si le code vaut 0D (après malus), seuls les pips comptent.</li>
 * </ul>
 * Classe sans état ni dépendance à Spring : le domaine reste testable isolément.
 */
public class LanceurDeDes {

    private final SourceAleatoire source;
    private final PolitiqueEchecCritique politique;

    public LanceurDeDes(SourceAleatoire source, PolitiqueEchecCritique politique) {
        this.source = source;
        this.politique = politique;
    }

    public ResultatJet lancer(CodeDe code) {
        return lancer(code, OptionsJet.AUCUNE);
    }

    public ResultatJet lancer(CodeDe code, OptionsJet options) {
        int nombreDeDes = options.pointDeDestin() ? code.des() * 2 : code.des();
        CodeDe codeLance = new CodeDe(nombreDeDes, code.pips());

        if (nombreDeDes == 0) {
            return new ResultatJet(codeLance, List.of(), List.of(), List.of(), code.pips(), code.pips());
        }

        List<Integer> desNormaux = new ArrayList<>();
        for (int i = 0; i < nombreDeDes - 1; i++) {
            desNormaux.add(source.lancerD6());
        }

        int nombreDeDesLibres = options.pointDePersonnage() ? 2 : 1;
        List<DeLibre> desLibres = new ArrayList<>();
        List<Integer> desAnnules = new ArrayList<>();

        for (int i = 0; i < nombreDeDesLibres; i++) {
            DeLibre deLibre = lancerDeLibre();
            if (deLibre.echecCritique() && politique == PolitiqueEchecCritique.ANNULER_DE_LIBRE_ET_PLUS_HAUT) {
                deLibre = new DeLibre(deLibre.lancers(), true, false);
                desNormaux.stream()
                        .max(Comparator.naturalOrder())
                        .ifPresent(plusHaut -> {
                            desNormaux.remove(plusHaut); // retire une occurrence (Integer, pas index)
                            desAnnules.add(plusHaut);
                        });
            }
            desLibres.add(deLibre);
        }

        int total = desNormaux.stream().mapToInt(Integer::intValue).sum()
                + desLibres.stream().mapToInt(DeLibre::valeurRetenue).sum()
                + code.pips();

        return new ResultatJet(codeLance, desNormaux, desAnnules, desLibres, code.pips(), total);
    }

    private DeLibre lancerDeLibre() {
        List<Integer> lancers = new ArrayList<>();
        int valeur = source.lancerD6();
        lancers.add(valeur);
        boolean echecCritique = valeur == 1;
        while (valeur == 6) {
            valeur = source.lancerD6();
            lancers.add(valeur);
        }
        return new DeLibre(lancers, echecCritique, true);
    }
}
