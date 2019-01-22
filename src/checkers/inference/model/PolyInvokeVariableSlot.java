package checkers.inference.model;

/**
 * PolyInvokeVariableSlot represent the annotation computed as the replacement
 * of a {@code @PolyXXX} qualifier at the invocation of a qualifier-polymorphic
 * method.
 *
 * PolyInvokeVariableSlots are never inserted into source.
 *
 * Note that this slot should be serialized identically to a
 * {@link VariableSlot}.
 */
public class PolyInvokeVariableSlot extends Slot {

    public PolyInvokeVariableSlot(int id, AnnotationLocation location) {
        super(id, false, location);
    }

    @Override
    public <SlotEncodingT> SlotEncodingT serialize(Serializer<SlotEncodingT, ?> serializer) {
        return serializer.serialize(this);
    }
}
