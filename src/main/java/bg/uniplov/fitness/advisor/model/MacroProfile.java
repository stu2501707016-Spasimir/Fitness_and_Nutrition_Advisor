package bg.uniplov.fitness.advisor.model;

public record MacroProfile(
        double calories,
        double proteinG,
        double carbsG,
        double sugarsG,
        double fatG,
        double saturatedFatG,
        double fiberG,
        double saltG
) { }
