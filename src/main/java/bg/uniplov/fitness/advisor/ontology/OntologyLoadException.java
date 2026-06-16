package bg.uniplov.fitness.advisor.ontology;

public class OntologyLoadException extends RuntimeException {
    public OntologyLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public OntologyLoadException(String message) {
        super(message);
    }
}
