package fr.opend6.regles.des;

import org.junit.jupiter.api.Test;

import fr.opend6.dice.domain.CodeDe;
import fr.opend6.dice.domain.DegreResultat;
import fr.opend6.dice.domain.LanceurDeDes;
import fr.opend6.dice.domain.NiveauDifficulte;
import fr.opend6.dice.domain.OptionsJet;
import fr.opend6.dice.domain.PolitiqueEchecCritique;
import fr.opend6.dice.domain.Resolution;
import fr.opend6.dice.domain.ResultatJet;

import static fr.opend6.dice.domain.PolitiqueEchecCritique.ANNULER_DE_LIBRE_ET_PLUS_HAUT;
import static fr.opend6.dice.domain.PolitiqueEchecCritique.COMPTER_ET_INCIDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LanceurDeDesTest {

    private static ResultatJet jet(String code, OptionsJet options, PolitiqueEchecCritique politique, int... des) {
        DesScriptes source = new DesScriptes(des);
        ResultatJet r = new LanceurDeDes(source, politique).lancer(CodeDe.parse(code), options);
        assertThat(source.toutConsomme()).as("tous les dés scriptés doivent être utilisés").isTrue();
        return r;
    }

    @Test
    void jet_simple_3D_plus_2() {
        // 2 dés normaux (4, 3) + Dé Libre (5) + 2 pips
        ResultatJet r = jet("3D+2", OptionsJet.AUCUNE, ANNULER_DE_LIBRE_ET_PLUS_HAUT, 4, 3, 5);
        assertThat(r.total()).isEqualTo(14);
        assertThat(r.desNormaux()).containsExactly(4, 3);
        assertThat(r.desLibres()).hasSize(1);
        assertThat(r.echecCritique()).isFalse();
    }

    @Test
    void de_libre_explose_sur_6_et_un_1_apres_un_6_n_est_pas_critique() {
        ResultatJet r = jet("2D", OptionsJet.AUCUNE, ANNULER_DE_LIBRE_ET_PLUS_HAUT, 2, 6, 6, 1);
        assertThat(r.desLibres().getFirst().lancers()).containsExactly(6, 6, 1);
        assertThat(r.echecCritique()).isFalse();
        assertThat(r.total()).isEqualTo(2 + 13);
    }

    @Test
    void echec_critique_annule_le_de_libre_et_le_plus_haut() {
        ResultatJet r = jet("4D", OptionsJet.AUCUNE, ANNULER_DE_LIBRE_ET_PLUS_HAUT, 5, 6, 2, 1);
        assertThat(r.echecCritique()).isTrue();
        assertThat(r.desAnnules()).containsExactly(6);
        assertThat(r.desNormaux()).containsExactlyInAnyOrder(5, 2);
        assertThat(r.total()).isEqualTo(7);
    }

    @Test
    void echec_critique_compte_normalement_en_mode_incident() {
        ResultatJet r = jet("4D", OptionsJet.AUCUNE, COMPTER_ET_INCIDENT, 5, 6, 2, 1);
        assertThat(r.echecCritique()).isTrue();
        assertThat(r.desAnnules()).isEmpty();
        assertThat(r.total()).isEqualTo(14);
    }

    @Test
    void code_1D_le_seul_de_est_le_de_libre() {
        ResultatJet r = jet("1D+1", OptionsJet.AUCUNE, ANNULER_DE_LIBRE_ET_PLUS_HAUT, 1);
        assertThat(r.desNormaux()).isEmpty();
        assertThat(r.echecCritique()).isTrue();
        assertThat(r.total()).isEqualTo(1); // seuls les pips restent
    }

    @Test
    void code_0D_seuls_les_pips_comptent() {
        ResultatJet r = jet("0D+2", OptionsJet.AUCUNE, ANNULER_DE_LIBRE_ET_PLUS_HAUT);
        assertThat(r.total()).isEqualTo(2);
    }

    @Test
    void point_de_personnage_ajoute_un_de_libre() {
        ResultatJet r = jet("2D", new OptionsJet(true, false), ANNULER_DE_LIBRE_ET_PLUS_HAUT, 3, 4, 5);
        assertThat(r.desLibres()).hasSize(2);
        assertThat(r.total()).isEqualTo(12);
    }

    @Test
    void point_de_destin_double_les_des_mais_pas_les_pips() {
        // 3D+1 -> 6D+1 : 5 normaux + 1 libre
        ResultatJet r = jet("3D+1", new OptionsJet(false, true), ANNULER_DE_LIBRE_ET_PLUS_HAUT, 1, 2, 3, 4, 5, 2);
        assertThat(r.codeDe().toString()).isEqualTo("6D+1");
        assertThat(r.desLibres()).hasSize(1);
        assertThat(r.total()).isEqualTo(15 + 2 + 1);
    }

    @Test
    void pp_et_pd_sont_incompatibles() {
        assertThatThrownBy(() -> new OptionsJet(true, true)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void resolution_et_niveaux_de_reussite() {
        assertThat(Resolution.de(15, 15).reussite()).isTrue();
        assertThat(Resolution.de(15, 15).egalite()).isTrue();
        assertThat(Resolution.de(15, 15).degre()).isEqualTo(DegreResultat.MINIMAL);
        assertThat(Resolution.de(21, 15).degre()).isEqualTo(DegreResultat.BEAU);
        assertThat(Resolution.de(5, 20).reussite()).isFalse();
        assertThat(Resolution.de(5, 20).degre()).isEqualTo(DegreResultat.SPECTACULAIRE);
        assertThat(Resolution.de(40, 20).degre()).isEqualTo(DegreResultat.MEMORABLE);
    }

    @Test
    void niveaux_de_difficulte() {
        assertThat(NiveauDifficulte.pourValeur(12)).isEqualTo(NiveauDifficulte.MOYEN);
        assertThat(NiveauDifficulte.pourValeur(35)).isEqualTo(NiveauDifficulte.LEGENDAIRE);
    }
}
