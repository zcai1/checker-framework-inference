package checkers.inference;

import checkers.inference.model.Constraint;

import javax.lang.model.element.AnnotationMirror;
import java.util.Collection;
import java.util.Set;

/**
 * Returned by InferenceSolvers, InferenceResult represents the result of
 * inference.
 */
public interface InferenceResult {
    /**
     * Was a solution inferred for the given variable ID? Equivalent to
     * getAnnotation(id) != null.
     */
    boolean doesVariableExist(int varId);

    /**
     * Get the inferred solution for the given variable ID. Will return null if
     * doesVariableExist(id) is false.
     */
    AnnotationMirror getAnnotation(int varId);

    boolean hasSolution();

    Collection<Constraint> getUnsatisfiableConstraints();
}

