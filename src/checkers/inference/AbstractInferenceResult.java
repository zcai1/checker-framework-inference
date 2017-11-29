package checkers.inference;

import checkers.inference.model.Constraint;

import javax.lang.model.element.AnnotationMirror;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class AbstractInferenceResult implements InferenceResult {

    protected final Map<Integer, AnnotationMirror> annotationResults;
    protected final Collection<Constraint> unsatisfiableConstraints;

    public AbstractInferenceResult(Map<Integer, AnnotationMirror> annotationResults,
                                   Collection<Constraint> unsatisfiableConstraints) {
        this.annotationResults = annotationResults;
        this.unsatisfiableConstraints = unsatisfiableConstraints;
    }

    @Override
    public boolean doesVariableExist(int varId) {
        return annotationResults.containsKey(varId);
    }

    @Override
    public AnnotationMirror getAnnotation(int varId) {
        return annotationResults.get(varId);
    }

    @Override
    public boolean isEmpty() {
        return annotationResults.isEmpty();
    }

    @Override
    public Collection<Constraint> getUnsatisfiableConstraints() {
        return unsatisfiableConstraints;
    }
}
