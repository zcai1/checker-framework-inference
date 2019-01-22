package checkers.inference.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.checkerframework.dataflow.util.HashCodeUtils;

/**
 * VariableSlot is a constraint variable which is always inserted into source.
 * After the Solver is run, each VariableSlot should have an assigned value
 * which is then written to the output Jaif file for later insertion into source
 * code.
 *
 * VariableSlots are the result of converting @VarAnnot( slot id ) annotations
 * on AnnotatedTypeMirrors.
 *
 * VariableSlots hold references to slots it is refined by, and slots it is
 * merged to.
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

    // Slots this variable has been merged to.
    private final Set<LUBVariableSlot> mergedToSlots = new HashSet<>();

    // Refinement variables that refine this slot.
    private final Set<RefinementVariableSlot> refinedToSlots = new HashSet<>();

    public boolean isMergedTo(VariableSlot other) {
        for (LUBVariableSlot mergedTo: mergedToSlots) {
            if (mergedTo.equals(other)) {
                return true;
            } else {
                if (mergedTo.isMergedTo(other)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<LUBVariableSlot> getMergedToSlots() {
        return Collections.unmodifiableSet(mergedToSlots);
    }

    public void addMergedToSlot(LUBVariableSlot mergedSlot) {
        this.mergedToSlots.add(mergedSlot);
    }

    public Set<RefinementVariableSlot> getRefinedToSlots() {
        return refinedToSlots;
    }

    @Override
    public <SlotEncodingT> SlotEncodingT serialize(Serializer<SlotEncodingT, ?> serializer) {
        return serializer.serialize(this);
    }
}
