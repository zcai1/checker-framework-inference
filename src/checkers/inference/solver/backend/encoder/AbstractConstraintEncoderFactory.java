package checkers.inference.solver.backend.encoder;

import checkers.inference.solver.backend.FormatTranslator;
import checkers.inference.solver.frontend.Lattice;
import checkers.inference.util.ConstraintVerifier;
import checkers.inference.solver.backend.z3.encoder.Z3BitVectorSubtypeConstraintEncoder;
import checkers.inference.solver.backend.maxsat.encoder.MaxSATImplicationConstraintEncoder;

/**
 * Abstract base class for all concrete {@link ConstraintEncoderFactory}. Subclasses of {@code AbstractConstraintEncoderFactory}
 * should override corresponding createXXXEncoder methods to return concrete implementations if the
 * solver backend that the subclasses belong to support encoding such constraints.
 *
 * @see ConstraintEncoderFactory
 */
public abstract class AbstractConstraintEncoderFactory<FormatTranslatorT extends FormatTranslator<?, ConstraintEncodingT, ?>, ConstraintEncodingT>
            implements ConstraintEncoderFactory<ConstraintEncodingT>{

    /**
     * {@link Lattice} instance that every constraint encoder needs
     */
    protected final Lattice lattice;

    /**
     * {@link ConstraintVerifier} instance that every constraint encoder needs
     */
    protected final ConstraintVerifier verifier;

    /**
     * {@link FormatTranslator} instance that concrete subclass of {@link AbstractConstraintEncoder} might need.
     * For example, {@link Z3BitVectorSubtypeConstraintEncoder} needs it to format translate {@SubtypeConstraint}.
     * {@link MaxSATImplicationConstraintEncoder} needs it to delegate format translation task of non-{@code
     * ImplicationConstraint}s.
     */
    protected final FormatTranslatorT formatTranslator;

    public AbstractConstraintEncoderFactory(Lattice lattice, ConstraintVerifier verifier, FormatTranslatorT formatTranslator) {
        this.lattice = lattice;
        this.verifier = verifier;
        this.formatTranslator = formatTranslator;
    }
}
