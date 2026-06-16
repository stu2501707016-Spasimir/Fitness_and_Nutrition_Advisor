package bg.uniplov.fitness.advisor.config;

import bg.uniplov.fitness.advisor.agents.RecommendationCoordinator;
import bg.uniplov.fitness.advisor.ontology.OntologyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean(destroyMethod = "close")
    public OntologyService ontologyService() {
        return OntologyService.loadDefault();
    }

    @Bean
    public RecommendationCoordinator recommendationCoordinator(OntologyService ontologyService) {
        return new RecommendationCoordinator(ontologyService);
    }
}
