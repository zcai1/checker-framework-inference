package checkers.inference.solver.backend.encoder;

import checkers.inference.solver.backend.FormatTranslator;
import checkers.inference.solver.frontend.Lattice;
import checkers.inference.util.ConstraintVerifier;

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

    protected final FormatTranslatorT formatTranslator;

    public AbstractConstraintEncoderFactory(Lattice lattice, ConstraintVerifier verifier, FormatTranslatorT formatTranslator) {
        this.lattice = lattice;
        this.verifier = verifier;
        this.formatTranslator = formatTranslator;
    }
}
