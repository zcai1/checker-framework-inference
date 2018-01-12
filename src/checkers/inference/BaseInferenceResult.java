package checkers.inference;

import checkers.inference.model.Constraint;

import javax.lang.model.element.AnnotationMirror;
import java.util.Collection;
import java.util.Map;

public class BaseInferenceResult implements InferenceResult {

    protected final Map<Integer, AnnotationMirror> inferredResults;
    protected final Collection<Constraint> unsatisfiableConstraints;

    public BaseInferenceResult(Map<Integer, AnnotationMirror> inferredResults,
                               Collection<Constraint> unsatisfiableConstraints) {
        this.inferredResults = inferredResults;
        this.unsatisfiableConstraints = unsatisfiableConstraints;
    }

    @Override
    public boolean doesVariableExist(int varId) {
        return inferredResults.containsKey(varId);
    }

    @Override
    public AnnotationMirror getAnnotation(int varId) {
        return inferredResults.get(varId);
    }

    @Override
    public boolean hasSolution() {
        return inferredResults != null;
    }

    @Override
    public Collection<Constraint> getUnsatisfiableConstraints() {
        return unsatisfiableConstraints;
    }
}
