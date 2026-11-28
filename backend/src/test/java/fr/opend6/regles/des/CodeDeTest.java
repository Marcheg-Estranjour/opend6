package fr.opend6.regles.des;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import fr.opend6.dice.domain.CodeDe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CodeDeTest {

    @ParameterizedTest
    @CsvSource({
            "3D,      3, 0",
            "3D+2,    3, 2",
            "5D+4,    6, 1",   // exemple du livre p.25
            "4d-1,    3, 2",
            "' 2D + 1 ', 2, 1"
    })
    void analyse_et_normalise(String notation, int des, int pips) {
        CodeDe code = CodeDe.parse(notation);
        assertThat(code.des()).isEqualTo(des);
        assertThat(code.pips()).isEqualTo(pips);
    }

    @Test
    void addition_exemple_medecine_p25() {
        // Savoir 3D+2 + 2D+2 dépensés en Médecine = 5D+4 = 6D+1
        assertThat(CodeDe.parse("3D+2").plus(CodeDe.parse("2D+2"))).isEqualTo(CodeDe.parse("6D+1"));
    }

    @Test
    void malus_borne_a_zero() {
        assertThat(CodeDe.parse("1D+1").malusDes(2)).isEqualTo(CodeDe.ZERO);
        assertThat(CodeDe.parse("5D+1").malusDes(2).toString()).isEqualTo("3D+1"); // exemple p.100
    }

    @Test
    void valeur_fixe_pnj_p101() {
        assertThat(CodeDe.parse("3D+1").valeurFixe()).isEqualTo(10);
    }

    @Test
    void affichage() {
        assertThat(CodeDe.de(4).toString()).isEqualTo("4D");
        assertThat(new CodeDe(2, 4).toString()).isEqualTo("3D+1");
    }

    @Test
    void notation_invalide() {
        assertThatThrownBy(() -> CodeDe.parse("trois dés")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CodeDe(0, -1)).isInstanceOf(IllegalArgumentException.class);
    }
}
