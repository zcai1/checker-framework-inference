package checkers.inference.model;

/**
 * VPAVariableSlots are used store the viewpoint adaptation result of two slots.
 */
public class VPAVariableSlot extends Slot {

    public VPAVariableSlot(int id, AnnotationLocation location) {
        super(id, false, location);
    }

    @Override public <SlotEncodingT> SlotEncodingT serialize(
            Serializer<SlotEncodingT, ?> serializer) {
        return serializer.serialize(this);
    }
}

// Alternative design: useful for debug output to track the component slots
// 
//package checkers.inference.model;
//
//import org.checkerframework.dataflow.util.HashCodeUtils;
//
///**
// * VPAVariableSlots are used store the viewpoint adaptation result of two slots.
// */
//public class VPAVariableSlot extends Slot {
//
//    private final Slot receiver;
//    private final Slot declared;
//
//    public VPAVariableSlot(int id, AnnotationLocation location, Slot receiver, Slot declared) {
//        super(id, false, location);
//        this.receiver = receiver;
//        this.declared = declared;
//    }
//
//    @Override public <SlotEncodingT> SlotEncodingT serialize(
//            Serializer<SlotEncodingT, ?> serializer) {
//        return serializer.serialize(this);
//    }
//
//    public Slot getReceiver() {
//        return receiver;
//    }
//
//    public Slot getDeclared() {
//        return declared;
//    }
//
//    @Override public int hashCode() {
//        return HashCodeUtils.hash(receiver, declared);
//    }
//
//    @Override public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null || !(obj instanceof VPAVariableSlot)) {
//            return false;
//        }
//        final VPAVariableSlot that = (VPAVariableSlot) obj;
//        return this.receiver.equals(that.receiver) && this.declared.equals(that.declared);
//    }
//
//    @Override public int compareTo(Slot other) {
//        if (!(other instanceof VPAVariableSlot)) {
//            return super.compareTo(other);
//        }
//
//        final VPAVariableSlot that = (VPAVariableSlot) other;
//        int receiverCompare = this.receiver.compareTo(that.receiver);
//        int declaredCompare = this.declared.compareTo(that.declared);
//        return receiverCompare != 0 ? receiverCompare : declaredCompare;
//    }
//}
