package checkers.inference;

import checkers.inference.model.Constraint;

import javax.lang.model.element.AnnotationMirror;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DefaultInferenceResult extends AbstractInferenceResult {
    public DefaultInferenceResult(Map<Integer, AnnotationMirror> annotationResults,
                                  Collection<Constraint> unsatisfiableConstraints) {
        super(annotationResults, unsatisfiableConstraints);
    }
}
