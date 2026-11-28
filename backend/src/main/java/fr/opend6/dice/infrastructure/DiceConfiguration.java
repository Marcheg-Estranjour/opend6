package fr.opend6.dice.infrastructure;

import fr.opend6.dice.domain.LanceurDeDes;
import fr.opend6.dice.domain.PolitiqueEchecCritique;
import fr.opend6.dice.domain.SourceAleatoire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiceConfiguration {

    @Bean
    public LanceurDeDes lanceurDeDes(
            @Value("${opend6.dice.critical-failure-rule:ANNULER_DE_LIBRE_ET_PLUS_HAUT}")
            PolitiqueEchecCritique politique) {
        return new LanceurDeDes(SourceAleatoire.standard(), politique);
    }
}