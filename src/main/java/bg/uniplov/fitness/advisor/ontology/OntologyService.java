package bg.uniplov.fitness.advisor.ontology;

import bg.uniplov.fitness.advisor.model.UserProfile;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import org.semanticweb.owlapi.io.StreamDocumentSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public final class OntologyService implements AutoCloseable {
    public static final String DEFAULT_ONTOLOGY_RESOURCE = "/ontology/Fitness_Nutrition_Recommendation_Ontology.owx";

    private final OWLOntologyManager manager;
    private final OWLOntology ontology;
    private final OWLDataFactory dataFactory;
    private final OWLReasoner reasoner;
    private final String baseIri;

    private OntologyService(OWLOntologyManager manager, OWLOntology ontology, OWLReasoner reasoner) {
        this.manager = Objects.requireNonNull(manager);
        this.ontology = Objects.requireNonNull(ontology);
        this.reasoner = Objects.requireNonNull(reasoner);
        this.dataFactory = manager.getOWLDataFactory();
        this.baseIri = ontology.getOntologyID()
                .getOntologyIRI()
                .map(IRI::toString)
                .orElse("http://spas.bg/ontology/fitness");
    }

    public static OntologyService loadDefault() {
        try (InputStream stream = OntologyService.class.getResourceAsStream(DEFAULT_ONTOLOGY_RESOURCE)) {
            if (stream == null) {
                throw new OntologyLoadException("Ontology resource not found: " + DEFAULT_ONTOLOGY_RESOURCE);
            }
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new StreamDocumentSource(stream));
            OWLReasonerFactory factory = new ReasonerFactory();
            OWLReasoner reasoner = factory.createReasoner(ontology);
            if (!reasoner.isConsistent()) {
                reasoner.dispose();
                throw new OntologyLoadException("Loaded ontology is inconsistent. Open it in Protégé and run HermiT for details.");
            }
            reasoner.precomputeInferences(
                    InferenceType.CLASS_HIERARCHY,
                    InferenceType.CLASS_ASSERTIONS,
                    InferenceType.OBJECT_PROPERTY_ASSERTIONS
            );
            return new OntologyService(manager, ontology, reasoner);
        } catch (OWLOntologyCreationException | IOException ex) {
            throw new OntologyLoadException("Unable to load ontology", ex);
        }
    }

    public OWLNamedIndividual individual(String shortName) {
        return dataFactory.getOWLNamedIndividual(iri(shortName));
    }

    public OWLClass namedClass(String shortName) {
        return dataFactory.getOWLClass(iri(shortName));
    }

    public OWLObjectProperty objectProperty(String shortName) {
        return dataFactory.getOWLObjectProperty(iri(shortName));
    }

    public OWLDataProperty dataProperty(String shortName) {
        return dataFactory.getOWLDataProperty(iri(shortName));
    }

    public boolean hasIndividual(String shortName) {
        return ontology.containsIndividualInSignature(iri(shortName));
    }

    public Set<String> getIndividualsOfClass(String classShortName) {
        OWLClass cls = namedClass(classShortName);
        NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(cls, false);
        return instances.getFlattened().stream()
                .map(this::shortForm)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public Set<String> getInferredTypes(String individualShortName) {
        OWLNamedIndividual ind = individual(individualShortName);
        return reasoner.getTypes(ind, false).getFlattened().stream()
                .filter(c -> !c.isOWLThing() && !c.isOWLNothing())
                .map(this::shortForm)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public Set<String> getObjectValues(String subjectIndividualShortName, String propertyShortName) {
        OWLNamedIndividual subject = individual(subjectIndividualShortName);
        OWLObjectProperty property = objectProperty(propertyShortName);
        Set<String> inferred = reasoner.getObjectPropertyValues(subject, property).getFlattened().stream()
                .map(this::shortForm)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!inferred.isEmpty()) {
            return inferred;
        }

        return ontology.getObjectPropertyAssertionAxioms(subject).stream()
                .filter(ax -> !ax.getObject().isAnonymous())
                .filter(ax -> ax.getProperty().equals(property))
                .map(ax -> ax.getObject().asOWLNamedIndividual())
                .map(this::shortForm)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Optional<String> firstObjectValue(String subjectIndividualShortName, String propertyShortName) {
        return getObjectValues(subjectIndividualShortName, propertyShortName).stream().findFirst();
    }

    public Set<String> getDataValues(String subjectIndividualShortName, String propertyShortName) {
        OWLNamedIndividual subject = individual(subjectIndividualShortName);
        OWLDataProperty property = dataProperty(propertyShortName);
        return ontology.getDataPropertyAssertionAxioms(subject).stream()
                .filter(ax -> ax.getProperty().equals(property))
                .map(ax -> literalToString(ax.getObject()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Optional<String> firstDataValue(String subjectIndividualShortName, String propertyShortName) {
        return getDataValues(subjectIndividualShortName, propertyShortName).stream().findFirst();
    }

    public String displayName(String individualShortName) {
        return firstDataValue(individualShortName, "hasDisplayName")
                .orElseGet(() -> humanize(individualShortName));
    }

    public double dataDouble(String individualShortName, String propertyShortName, double defaultValue) {
        return firstDataValue(individualShortName, propertyShortName)
                .flatMap(OntologyService::tryParseDouble)
                .orElse(defaultValue);
    }

    public int dataInt(String individualShortName, String propertyShortName, int defaultValue) {
        return firstDataValue(individualShortName, propertyShortName)
                .flatMap(OntologyService::tryParseInt)
                .orElse(defaultValue);
    }

    public Set<String> getAllReferencedIndividuals(String subjectIndividualShortName, List<String> propertyShortNames) {
        Set<String> result = new LinkedHashSet<>();
        for (String property : propertyShortNames) {
            result.addAll(getObjectValues(subjectIndividualShortName, property));
        }
        return result;
    }

    public String createTemporaryUser(UserProfile profile) {
        String safeName = profile.displayName() == null || profile.displayName().isBlank()
                ? "CandidateUser"
                : profile.displayName().replaceAll("[^A-Za-z0-9]", "");
        String userName = safeName + "Runtime" + System.currentTimeMillis();
        OWLNamedIndividual user = individual(userName);
        List<OWLAxiom> axioms = new ArrayList<>();
        axioms.add(dataFactory.getOWLClassAssertionAxiom(namedClass("User"), user));
        axioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(objectProperty("hasGoal"), user, individual(profile.goalIndividual())));
        axioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(objectProperty("hasTrainingExperience"), user, individual(profile.trainingExperienceIndividual())));
        axioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(objectProperty("hasBmiCategory"), user, individual(profile.bmiCategoryIndividual())));

        for (String approach : profile.dietExperienceIndividuals()) {
            axioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(objectProperty("hasExperienceWithDietaryApproach"), user, individual(approach)));
        }
        for (String constraint : profile.dietaryConstraintIndividuals()) {
            axioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(objectProperty("hasDietaryConstraint"), user, individual(constraint)));
        }

        axioms.add(dataFactory.getOWLDataPropertyAssertionAxiom(dataProperty("hasAge"), user, profile.age()));
        axioms.add(decimalAssertion("hasHeightCm", user, profile.heightCm()));
        axioms.add(decimalAssertion("hasWeightKg", user, profile.weightKg()));
        axioms.add(decimalAssertion("hasBmi", user, profile.bmi()));
        axioms.add(dataFactory.getOWLDataPropertyAssertionAxiom(dataProperty("hasMealsPerDayPreference"), user, profile.mealsPerDay()));

        manager.addAxioms(ontology, new LinkedHashSet<>(axioms));
        reasoner.flush();
        return userName;
    }

    private OWLDataPropertyAssertionAxiom decimalAssertion(String propertyShortName, OWLNamedIndividual subject, double value) {
        String lexicalValue = String.format(Locale.US, "%.2f", value);
        OWLDatatype decimalDatatype = dataFactory.getOWLDatatype(OWL2Datatype.XSD_DECIMAL.getIRI());
        OWLLiteral literal = dataFactory.getOWLLiteral(lexicalValue, decimalDatatype);
        return dataFactory.getOWLDataPropertyAssertionAxiom(dataProperty(propertyShortName), subject, literal);
    }

    public String shortForm(OWLEntity entity) {
        return shortForm(entity.getIRI());
    }

    public String shortForm(IRI iri) {
        String fragment = iri.getFragment();
        if (fragment != null && !fragment.isBlank()) {
            return fragment;
        }
        String value = iri.toString();
        int hash = value.lastIndexOf('#');
        int slash = value.lastIndexOf('/');
        int index = Math.max(hash, slash);
        return index >= 0 ? value.substring(index + 1) : value;
    }

    public String humanize(String shortName) {
        if (shortName == null || shortName.isBlank()) {
            return "";
        }
        return shortName.replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    private IRI iri(String shortName) {
        return IRI.create(baseIri + "#" + shortName);
    }

    private static String literalToString(OWLLiteral literal) {
        return literal.getLiteral();
    }

    private static Optional<Double> tryParseDouble(String value) {
        try {
            return Optional.of(Double.parseDouble(value));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private static Optional<Integer> tryParseInt(String value) {
        try {
            return Optional.of((int) Math.round(Double.parseDouble(value)));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    @Override
    public void close() {
        reasoner.dispose();
    }
}
