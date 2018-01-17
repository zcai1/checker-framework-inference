package checkers.inference;

import checkers.inference.model.Constraint;

import javax.lang.model.element.AnnotationMirror;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class DefaultInferenceResult implements InferenceResult {

    protected final Map<Integer, AnnotationMirror> varIdToAnnotation;
    protected final Collection<Constraint> unsatisfiableConstraints;

    public DefaultInferenceResult(Map<Integer, AnnotationMirror> varIdToAnnotation) {
        this(varIdToAnnotation, new HashSet<>());
    }

    public DefaultInferenceResult(Map<Integer, AnnotationMirror> varIdToAnnotation,
                                  Collection<Constraint> unsatisfiableConstraints) {
        this.varIdToAnnotation = varIdToAnnotation;
        this.unsatisfiableConstraints = unsatisfiableConstraints;
    }

    @Override
    public boolean hasSolution() {
        return varIdToAnnotation != null;
    }

    @Override
    public Map<Integer, AnnotationMirror> getSolutions() {
        return varIdToAnnotation;
    }

    @Override
    public boolean containsSolutionForVariable(int varId) {
        if (!hasSolution()) return false;
        return varIdToAnnotation.containsKey(varId);
    }

    @Override
    public AnnotationMirror getSolutionForVariable(int varId) {
        if (!hasSolution()) return null;
        return varIdToAnnotation.get(varId);
    }

    @Override
    public Collection<Constraint> getUnsatisfiableConstraints() {
        assert !hasSolution() : "There is solution, calling `getUnsatisfiableConstraints()` is forbidden!";
        return unsatisfiableConstraints;
    }
}
