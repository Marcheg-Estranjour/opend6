package fr.opend6.api;

import fr.opend6.regles.des.LanceurDeDes;
import fr.opend6.regles.des.PolitiqueEchecCritique;
import fr.opend6.regles.des.SourceAleatoire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Branche le domaine (Java pur) dans Spring.
 */
@Configuration
public class ConfigurationRegles {

    @Bean
    public LanceurDeDes lanceurDeDes(
            @Value("${opend6.echec-critique:ANNULER_DE_LIBRE_ET_PLUS_HAUT}") PolitiqueEchecCritique politique) {
        return new LanceurDeDes(SourceAleatoire.standard(), politique);
    }

    @Bean
    public WebMvcConfigurer cors(@Value("${opend6.cors-origine:http://localhost:4200}") String origine) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**").allowedOrigins(origine).allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}
