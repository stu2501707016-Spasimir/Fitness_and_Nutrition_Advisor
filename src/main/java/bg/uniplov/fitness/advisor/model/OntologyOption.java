package bg.uniplov.fitness.advisor.model;

import java.util.Objects;

public record OntologyOption(String label, String individualShortName) {
    public OntologyOption {
        Objects.requireNonNull(label, "label");
        Objects.requireNonNull(individualShortName, "individualShortName");
    }

    @Override
    public String toString() {
        return label;
    }
}
