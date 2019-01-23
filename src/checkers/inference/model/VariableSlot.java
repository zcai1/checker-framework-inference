package checkers.inference.model;

/**
 * VariableSlot is a constraint variable which is always inserted into source.
 * After the Solver is run, each VariableSlot should have an assigned value
 * which is then written to the output Jaif file for later insertion into source
 * code.
 *
 * VariableSlots are the result of converting @VarAnnot( slot id ) annotations
 * on AnnotatedTypeMirrors.
 */
public class VariableSlot extends Slot {

    /**
     * Create a VariableSlot with the given annotation location.
     * 
     * @param id
     *            Unique identifier for this variable
     * @param location
     *            Used to locate this variable in code, see @AnnotationLocation
     */
    public VariableSlot(int id, AnnotationLocation location) {
        super(id, true, location);
    }

    /**
     * Create a VariableSlot with a default location of
     * {@link AnnotationLocation#MISSING_LOCATION}.
     * 
     * @param id
     *            Unique identifier for this variable
     */
    public VariableSlot(int id) {
        super(id, true);
    }

    @Override
    public <SlotEncodingT> SlotEncodingT serialize(Serializer<SlotEncodingT, ?> serializer) {
        return serializer.serialize(this);
    }
}
