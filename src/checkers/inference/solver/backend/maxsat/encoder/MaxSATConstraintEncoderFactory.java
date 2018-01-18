package checkers.inference.solver.backend.maxsat.encoder;

import checkers.inference.solver.backend.encoder.AbstractConstraintEncoderFactory;
import checkers.inference.solver.backend.encoder.combine.CombineConstraintEncoder;
import checkers.inference.solver.backend.encoder.existential.ExistentialConstraintEncoder;
import checkers.inference.solver.backend.encoder.implication.ImplicationConstraintEncoder;
import checkers.inference.solver.backend.maxsat.MaxSatFormatTranslator;
import checkers.inference.solver.frontend.Lattice;
import checkers.inference.util.ConstraintVerifier;
import org.sat4j.core.VecInt;

import javax.lang.model.element.AnnotationMirror;
import java.util.Map;

/**
 * MaxSAT implementation of {@link checkers.inference.solver.backend.encoder.ConstraintEncoderFactory}.
 *
 * @see checkers.inference.solver.backend.encoder.ConstraintEncoderFactory
 */
public class MaxSATConstraintEncoderFactory extends AbstractConstraintEncoderFactory<MaxSatFormatTranslator, VecInt[]> {

    private final Map<AnnotationMirror, Integer> typeToInt;

    public MaxSATConstraintEncoderFactory(Lattice lattice, ConstraintVerifier verifier,
                                          Map<AnnotationMirror, Integer> typeToInt, MaxSatFormatTranslator formatTranslator) {
        super(lattice, verifier, formatTranslator);
        this.typeToInt = typeToInt;
    }

    @Override
    public MaxSATSubtypeConstraintEncoder createSubtypeConstraintEncoder() {
        return new MaxSATSubtypeConstraintEncoder(lattice, verifier, typeToInt);
    }

    @Override
    public MaxSATEqualityConstraintEncoder createEqualityConstraintEncoder() {
        return new MaxSATEqualityConstraintEncoder(lattice, verifier, typeToInt);
    }

    @Override
    public MaxSATInequalityConstraintEncoder createInequalityConstraintEncoder() {
        return new MaxSATInequalityConstraintEncoder(lattice, verifier, typeToInt);
    }

    @Override
    public MaxSATComparableConstraintEncoder createComparableConstraintEncoder() {
        return new MaxSATComparableConstraintEncoder(lattice, verifier, typeToInt);
    }

    @Override
    public MaxSATPreferenceConstraintEncoder createPreferenceConstraintEncoder() {
        return new MaxSATPreferenceConstraintEncoder(lattice, verifier, typeToInt);
    }

    @Override
    public MaxSATImplicationConstraintEncoder createImplicationConstraintEncoder() {
        return new MaxSATImplicationConstraintEncoder(lattice, verifier, typeToInt, formatTranslator);
    }

    @Override
    public CombineConstraintEncoder<VecInt[]> createCombineConstraintEncoder() {
        return null;
    }

    @Override
    public ExistentialConstraintEncoder<VecInt[]> createExistentialConstraintEncoder() {
        return null;
    }
}
