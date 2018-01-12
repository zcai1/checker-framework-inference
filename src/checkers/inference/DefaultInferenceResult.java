package checkers.inference;

import checkers.inference.model.Constraint;

import javax.lang.model.element.AnnotationMirror;
import java.util.Collection;
import java.util.Map;

public class DefaultInferenceResult extends BaseInferenceResult {
    public DefaultInferenceResult(Map<Integer, AnnotationMirror> annotationResults,
                                  Collection<Constraint> unsatisfiableConstraints) {
        super(annotationResults, unsatisfiableConstraints);
    }
}
