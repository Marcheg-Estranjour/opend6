package fr.opend6.dice.domain;

import java.util.ArrayDeque;
import java.util.Deque;

import fr.opend6.dice.domain.SourceAleatoire;

/** Source de dés déterministe pour les tests : renvoie les valeurs dans l'ordre donné. */
final class DesScriptes implements SourceAleatoire {

    private final Deque<Integer> valeurs = new ArrayDeque<>();

    DesScriptes(int... valeurs) {
        for (int v : valeurs) {
            this.valeurs.add(v);
        }
    }

    @Override
    public int lancerD6() {
        if (valeurs.isEmpty()) {
            throw new IllegalStateException("Plus de dés scriptés disponibles");
        }
        return valeurs.poll();
    }

    boolean toutConsomme() {
        return valeurs.isEmpty();
    }
}
