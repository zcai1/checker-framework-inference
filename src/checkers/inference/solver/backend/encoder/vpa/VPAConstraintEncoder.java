package checkers.inference.solver.backend.encoder.vpa;

import checkers.inference.model.ConstantSlot;
import checkers.inference.model.VariableSlot;

/**
 * Interface that defines operations to encode a {@link checkers.inference.model.VPAConstraint}. It has four methods
 * depending on the {@link checkers.inference.solver.backend.encoder.SlotSlotCombo} of {@code target} and {@code
 * declared} slots.
 *
 * <p>
 * {@code result} is always {@link checkers.inference.model.VPAVariableSlot}, which is essentially {@link VariableSlot},
 * whose {@link VariableSlot#id} is the only interesting knowledge in encoding phase. Therefore there don't exist
 * methods in which {@code result} is {@link ConstantSlot}.
 *
 * @see checkers.inference.model.VPAConstraint
 * @see checkers.inference.solver.backend.encoder.SlotSlotCombo
 */
public interface VPAConstraintEncoder<ConstraintEncodingT> {

    ConstraintEncodingT encodeVariable_Variable(VariableSlot target, VariableSlot declared, VariableSlot result);

    ConstraintEncodingT encodeVariable_Constant(VariableSlot target, ConstantSlot declared, VariableSlot result);

    ConstraintEncodingT encodeConstant_Variable(ConstantSlot target, VariableSlot declared, VariableSlot result);

    ConstraintEncodingT encodeConstant_Constant(ConstantSlot target, ConstantSlot declared, VariableSlot result);
}
