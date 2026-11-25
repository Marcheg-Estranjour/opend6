package fr.opend6.regles.des;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

/**
 * Abstraction du lancer d'un d6. Permet d'injecter des résultats prédéfinis dans les tests.
 */
@FunctionalInterface
public interface SourceAleatoire {

    /** @return une valeur entre 1 et 6 inclus */
    int lancerD6();

    static SourceAleatoire standard() {
        RandomGenerator generateur = new SecureRandom();
        return () -> generateur.nextInt(1, 7);
    }
}
