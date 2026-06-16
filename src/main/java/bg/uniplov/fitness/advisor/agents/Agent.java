package bg.uniplov.fitness.advisor.agents;

@FunctionalInterface
public interface Agent<I, O> {
    O execute(I input);
}
