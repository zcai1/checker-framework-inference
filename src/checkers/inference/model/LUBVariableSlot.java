package checkers.inference.model;

import org.checkerframework.dataflow.util.HashCodeUtils;

/**
 * LubVariableSlot stores the least-upper-bounds of two other slots.
 */
public class LUBVariableSlot extends Slot {

    private final Slot left;
    private final Slot right;

    public LUBVariableSlot(int id, AnnotationLocation location, Slot left, Slot right) {
        super(id, false, location);
        this.left = left;
        this.right = right;
    }

    @Override public <SlotEncodingT> SlotEncodingT serialize(
            Serializer<SlotEncodingT, ?> serializer) {
        return serializer.serialize(this);
    }

    public Slot getLeft() {
        return left;
    }

    public Slot getRight() {
        return right;
    }

    @Override public int hashCode() {
        return HashCodeUtils.hash(left, right);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LUBVariableSlot)) {
            return false;
        }
        final LUBVariableSlot that = (LUBVariableSlot) obj;
        return this.left.equals(that.left) && this.right.equals(that.right);
    }

    @Override public int compareTo(Slot other) {
        if (!(other instanceof LUBVariableSlot)) {
            return super.compareTo(other);
        }

        final LUBVariableSlot that = (LUBVariableSlot) other;
        int leftCompare = this.left.compareTo(that.left);
        int rightCompare = this.right.compareTo(that.right);
        return leftCompare != 0 ? leftCompare : rightCompare;
    }
}
