package fr.opend6.api;

import fr.opend6.regles.des.CodeDe;
import fr.opend6.regles.des.DeLibre;
import fr.opend6.regles.des.LanceurDeDes;
import fr.opend6.regles.des.NiveauDifficulte;
import fr.opend6.regles.des.OptionsJet;
import fr.opend6.regles.des.Resolution;
import fr.opend6.regles.des.ResultatJet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * POST /api/jets
 * { "code": "3D+2", "difficulte": 15, "pointDePersonnage": false, "pointDeDestin": false }
 */
@RestController
@RequestMapping("/api/jets")
public class JetController {

    private final LanceurDeDes lanceur;

    public JetController(LanceurDeDes lanceur) {
        this.lanceur = lanceur;
    }

    public record JetRequete(
            @NotBlank String code,
            @Min(1) Integer difficulte,
            boolean pointDePersonnage,
            boolean pointDeDestin) {
    }

    public record DeLibreDto(List<Integer> lancers, boolean echecCritique, boolean compte) {
        static DeLibreDto de(DeLibre d) {
            return new DeLibreDto(d.lancers(), d.echecCritique(), d.compte());
        }
    }

    public record ResolutionDto(boolean reussite, int marge, String degre, String libelleDegre,
                                boolean egalite, String niveauDifficulte) {
    }

    public record JetReponse(
            String codeLance,
            List<Integer> desNormaux,
            List<Integer> desAnnules,
            List<DeLibreDto> desLibres,
            int pips,
            int total,
            boolean echecCritique,
            ResolutionDto resolution) {
    }

    @PostMapping
    public JetReponse lancer(@Valid @RequestBody JetRequete requete) {
        CodeDe code = CodeDe.parse(requete.code());
        OptionsJet options = new OptionsJet(requete.pointDePersonnage(), requete.pointDeDestin());
        ResultatJet r = lanceur.lancer(code, options);

        ResolutionDto resolution = null;
        if (requete.difficulte() != null) {
            Resolution res = r.contre(requete.difficulte());
            resolution = new ResolutionDto(res.reussite(), res.marge(), res.degre().name(),
                    res.degre().libelle(), res.egalite(),
                    NiveauDifficulte.pourValeur(requete.difficulte()).libelle());
        }

        return new JetReponse(r.codeDe().toString(), r.desNormaux(), r.desAnnules(),
                r.desLibres().stream().map(DeLibreDto::de).toList(),
                r.pips(), r.total(), r.echecCritique(), resolution);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail requeteInvalide(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
