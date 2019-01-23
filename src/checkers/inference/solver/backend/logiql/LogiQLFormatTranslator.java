package checkers.inference.solver.backend.logiql;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;

import checkers.inference.model.ArithmeticVariableSlot;
import checkers.inference.model.ConstantSlot;
import checkers.inference.model.ExistentialVariableSlot;
import checkers.inference.model.LUBVariableSlot;
import checkers.inference.model.PolyInvokeVariableSlot;
import checkers.inference.model.RefinementVariableSlot;
import checkers.inference.model.VPAVariableSlot;
import checkers.inference.model.VariableSlot;
import checkers.inference.solver.backend.AbstractFormatTranslator;
import checkers.inference.solver.backend.encoder.ConstraintEncoderFactory;
import checkers.inference.solver.backend.logiql.encoder.LogiQLConstraintEncoderFactory;
import checkers.inference.solver.frontend.Lattice;

/**
 * LogiQLFormatTranslator converts constraint into string as logiQL data.
 *
 * @author jianchu
 *
 */
public class LogiQLFormatTranslator extends AbstractFormatTranslator<String, String, String> {

    public LogiQLFormatTranslator(Lattice lattice) {
        super(lattice);
        finishInitializingEncoders();
    }

    @Override
    protected ConstraintEncoderFactory<String> createConstraintEncoderFactory() {
        return new LogiQLConstraintEncoderFactory(lattice, this);
    }

    @Override
    public AnnotationMirror decodeSolution(String solution,
            ProcessingEnvironment processingEnvironment) {
        // TODO Refactor LogiQL backend to follow the design protocal.
        // https://github.com/opprop/checker-framework-inference/issues/108
        return null;
    }

    @Override
    public String serialize(VariableSlot slot) {
        return null;
    }

    @Override
    public String serialize(ConstantSlot slot) {
        return null;
    }

    @Override
    public String serialize(ExistentialVariableSlot slot) {
        return null;
    }

    @Override
    public String serialize(RefinementVariableSlot slot) {
        return null;
    }

    @Override
    public String serialize(VPAVariableSlot slot) {
        return null;
    }

    @Override
    public String serialize(LUBVariableSlot slot) {
        return null;
    }

    @Override
    public String serialize(ArithmeticVariableSlot slot) {
        return null;
    }

    @Override
    public String serialize(PolyInvokeVariableSlot slot) {
        return null;
    }
}
