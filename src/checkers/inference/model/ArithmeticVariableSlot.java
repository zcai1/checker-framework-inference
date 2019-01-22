package checkers.inference.model;

/**
 * ArithmeticVariableSlot represent the result of an arithmetic operation
 * between two other {@link Slot}s.
 *
 * ArithmeticVariableSlots are never inserted into source.
 *
 * Note that this slot should be serialized identically to a
 * {@link VariableSlot}.
 */
public class ArithmeticVariableSlot extends Slot {

    public ArithmeticVariableSlot(int id, AnnotationLocation location) {
        super(id, false, location);
    }

    @Override
    public <SlotEncodingT> SlotEncodingT serialize(Serializer<SlotEncodingT, ?> serializer) {
        return serializer.serialize(this);
    }
}

// Alternative design: useful for debug output to track the component slots
//
// package checkers.inference.model;
//
// import org.checkerframework.dataflow.util.HashCodeUtils;
//
// import checkers.inference.model.ArithmeticConstraint.ArithmeticOperationKind;
//
/// **
// * A ArithmeticVariableSlot represents the result of an arithmetic operation
// * between two other {@link Slot}s. Note that this slot should be serialized
// * identically to a {@link VariableSlot}.
// *
// * ArithmeticVariableSlots are hashed on the operation, and left and right
// * operand slots, as the result only depends on these three components. As the
// * SlotManager caches ArithmeticVariableSlots, subsequent calls to create a
// * ArithmeticVariableSlot for the same three components will give back the
// same
// * ArithmeticVariableSlot. Thus, the slot ID is effectively unique for each
// * Arithmetic result.
// *
// * Comparisons to other ArithmeticVariableSlots is performed by comparing the
// * operation and operand slots.
// *
// * Comparisons to other Slots is performed by comparing the slot ID.
// */
// public class ArithmeticVariableSlot extends Slot {
//
// private final ArithmeticOperationKind operation;
// private final Slot leftOperand;
// private final Slot rightOperand;
//
// public ArithmeticVariableSlot(int id, AnnotationLocation location,
// ArithmeticOperationKind operation, Slot leftOperand, Slot rightOperand) {
// super(id, false, location);
// this.operation = operation;
// this.leftOperand = leftOperand;
// this.rightOperand = rightOperand;
// }
//
// @Override public <SlotEncodingT> SlotEncodingT serialize(
// Serializer<SlotEncodingT, ?> serializer) {
// return serializer.serialize(this);
// }
//
// public ArithmeticOperationKind getOperation() {
// return operation;
// }
//
// public Slot getLeftOperand() {
// return leftOperand;
// }
//
// public Slot getRightOperand() {
// return rightOperand;
// }
//
// @Override public int hashCode() {
// return HashCodeUtils.hash(operation, leftOperand, rightOperand);
// }
//
// @Override public boolean equals(Object obj) {
// if (obj == null) {
// return false;
// }
// if (this == obj) {
// return true;
// }
// if (this.getClass() == obj.getClass()) {
// ArithmeticVariableSlot other = (ArithmeticVariableSlot) obj;
// return this.operation == other.operation &&
// this.leftOperand.equals(other.leftOperand)
// && this.rightOperand.equals(other.rightOperand);
// }
// return false;
// }
//
// @Override public int compareTo(Slot other) {
// if (other instanceof ArithmeticVariableSlot) {
// ArithmeticVariableSlot otherAVS = (ArithmeticVariableSlot) other;
// int operationCompare = this.operation.compareTo(otherAVS.operation);
// int leftOperandCompare = this.leftOperand.compareTo(otherAVS.leftOperand);
// int rightOperandCompare = this.rightOperand.compareTo(otherAVS.rightOperand);
//
// return operationCompare != 0 ? operationCompare
// : (leftOperandCompare != 0 ? leftOperandCompare : rightOperandCompare);
// } else {
// return super.compareTo(other);
// }
// }
// }
