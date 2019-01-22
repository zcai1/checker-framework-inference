package checkers.inference.solver.backend.encoder;

import checkers.inference.model.BinaryConstraint;
import checkers.inference.model.VPAConstraint;
import checkers.inference.model.Slot;
import checkers.inference.solver.backend.encoder.binary.BinaryConstraintEncoder;
import checkers.inference.solver.backend.encoder.vpa.VPAConstraintEncoder;

/**
 * Enum that models combination of slots in a {@link BinaryConstraint} and {@link VPAConstraint}
 * (in this case, it's the combination of {@link VPAConstraint#target} and {@link VPAConstraint#decl}).
 * <p>
 * {@link BinaryConstraintEncoder} and {@link VPAConstraintEncoder} need to know the combination of
 * {@link Slot.Kind} to determine which encodeXXX() method in {@link BinaryConstraintEncoder} and
 * {@link VPAConstraintEncoder} should be called.
 * <p>
 * But the {@link Slot.Kind} that's needed here is coarser-grained than its original definition:
 * Only knowing if a {@code Slot} is variable or constant is enough in solver encoding. Because solver
 * treats every {@link checkers.inference.model.VariableSlot} and its subclasses essentially as having
 * unknown value that is to be inferred and only the {@link checkers.inference.model.VariableSlot#id} is
 * interesting; Solver treats {@link checkers.inference.model.ConstantSlot} as having a real qualifier
 * that no inference is needed.
 *
 * @see ConstraintEncoderCoordinator#dispatch(BinaryConstraint, BinaryConstraintEncoder)
 * @see ConstraintEncoderCoordinator#dispatch(VPAConstraint, VPAConstraintEncoder)
 */
public enum SlotSlotCombo {

    VARIABLE_VARIABLE(true, true),
    VARIABLE_CONSTANT(true, false),
    CONSTANT_VARIABLE(false, true),
    CONSTANT_CONSTANT(false, false);

    /**
     * A {@link java.util.Map} that caches the mapping between two booleans(in implementation, converted
     * to indecies according to the truth value) and {code SlotSlotCombo} that the two booleans represent.
     */
    static private final SlotSlotCombo[][] map = new SlotSlotCombo[2][2];

    static {
        for (SlotSlotCombo combo : SlotSlotCombo.values()) {
            map[index(combo.isFstVariableSlot)][index(combo.isSndVariableSlot)] = combo;
        }
    }

    private final boolean isFstVariableSlot;
    private final boolean isSndVariableSlot;

    SlotSlotCombo(boolean isFstVariableSlot, boolean isSndVariableSlot) {
        this.isFstVariableSlot = isFstVariableSlot;
        this.isSndVariableSlot = isSndVariableSlot;
    }

    public static SlotSlotCombo valueOf(Slot fst, Slot snd) {
        return map[index(fst.isVariable())][index(snd.isVariable())];
    }

    private static int index(boolean value) {
        return value ? 1 : 0;
    }
}
