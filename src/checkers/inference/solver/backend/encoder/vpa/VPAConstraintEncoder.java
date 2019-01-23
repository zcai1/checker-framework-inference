package checkers.inference.solver.backend.encoder.vpa;

import checkers.inference.model.ConstantSlot;
import checkers.inference.model.Slot;
import checkers.inference.model.VPAVariableSlot;

/**
 * Interface that defines operations to encode a {@link checkers.inference.model.VPAConstraint}. It has four methods
 * depending on the {@link checkers.inference.solver.backend.encoder.SlotSlotCombo} of {@code target} and {@code
 * declared} slots.
 *
 * <p>
 * {@code result} is always {@link checkers.inference.model.VPASlot}, which is essentially {@link Slot},
 * whose {@link Slot#id} is the only interesting knowledge in encoding phase. Therefore there don't exist
 * methods in which {@code result} is {@link ConstantSlot}.
 *
 * @see checkers.inference.model.VPAConstraint
 * @see checkers.inference.solver.backend.encoder.SlotSlotCombo
 */
public interface VPAConstraintEncoder<ConstraintEncodingT> {

    ConstraintEncodingT encodeVariable_Variable(Slot target, Slot declared, VPAVariableSlot result);

    ConstraintEncodingT encodeVariable_Constant(Slot target, ConstantSlot declared, VPAVariableSlot result);

    ConstraintEncodingT encodeConstant_Variable(ConstantSlot target, Slot declared, VPAVariableSlot result);

    ConstraintEncodingT encodeConstant_Constant(ConstantSlot target, ConstantSlot declared, VPAVariableSlot result);
}
