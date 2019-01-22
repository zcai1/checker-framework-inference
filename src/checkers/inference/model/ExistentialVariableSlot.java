package checkers.inference.model;

import org.checkerframework.dataflow.util.HashCodeUtils;

/**
 * Summary of Shorthand:
 * {@code
 * (@0 | @1) - if (@0 exists) use @0 else use @1
 * (@2 (@0 | @1) - the id of this existential variable is 2, if (@0 exists) use @0 else use @1
 * (@3) - This indicates that an annotation may or may not exist here and its id is @3
 *        IF we have a type parameter <@4 T exntends @5 Object> then
 *            (@3) T is equivalent to <(@3 | @4) T extends (@3 | 5) Object>
 * }
 * <p>
 * ExistentialVariableSlots represent variables that may or may not exist.  These slots
 * represent parametric locations, locations where there is no annotation you could
 * place that would result in an equivalent meaning to omitting the variable.
 * <p>
 * Any non-local use of a type variable is a parametric.  In these cases,
 * the type variable will be given an ExistentialVariableSlot
 * <p>
 * {@code
 * Often in comments, we abbreviate ExistentialVariable slots as either:
 * (@0 | @1) - indicating that if @0 exists then use that otherwise use @1
 * or
 * (@5 (@0 | @1)) - indicating that if @0 exists then use that otherwise use @1
 * and @5 is the identifier for this Existential Variable slot.
 *
 * Finally, if you see a variable alone in parentheses it means that variable may or may
 * not exist:
 * (@2) T  - indicates T may have a primary annotation of @2
 *     If T's declaration were <@0 T extends @1 Object> then
 *         (@2) T corresponds to a type:  <(@2 | @0) T extends (@2 | @1) Object></(@2>
 * }
 * <p>
 * When "normalizing" constraints, we replace ExistentialVariableSlots by translating
 * constraints that contain them into Existential constraints.
 * <p>
 * {@code
 * That is, if we have a constraint:
 *
 * (@0 | @1) <: @3
 *
 * This really states:
 * if (@0 exists) {
 *     @0 <: @3
 * } else {
 *     @1 <: @3
 * }
 * }
 */
public class ExistentialVariableSlot extends Slot {

    // a variable whose annotation may or may not exist in source code
    private final VariableSlot potentialSlot;

    // the variable which would take part in a constraint if potentialSlot does not
    // exist
    private final VariableSlot alternativeSlot;

    public ExistentialVariableSlot(int id, VariableSlot potentialSlot,
            VariableSlot alternativeSlot) {
        super(id, false);

        if (potentialSlot == null) {
            throw new IllegalArgumentException("PotentialSlot cannot be null\n" + "id=" + id + "\n"
                    + "alternativeSlot=" + alternativeSlot);
        }

        if (alternativeSlot == null) {
            throw new IllegalArgumentException("alternativeSlot cannot be null\n" + "id=" + id
                    + "\n" + "potentialSlot=" + potentialSlot);
        }

        this.potentialSlot = potentialSlot;
        this.alternativeSlot = alternativeSlot;
    }

    @Override
    public <SlotEncodingT> SlotEncodingT serialize(Serializer<SlotEncodingT, ?> serializer) {
        return serializer.serialize(this);
    }

    public VariableSlot getPotentialSlot() {
        return potentialSlot;
    }

    public VariableSlot getAlternativeSlot() {
        return alternativeSlot;
    }

    @Override
    public String toString() {
        return "ExistentialVariableSlot(" + this.getId() + ", (" + potentialSlot.getId() + " | "
                + alternativeSlot.getId() + ")";
    }

    // TODO: see if it is even necessary to have a specific hashcode, quals, and
    // compareTo; EVS are cached in slot manager on their component slots

    @Override
    public int hashCode() {
        return HashCodeUtils.hash(potentialSlot, alternativeSlot);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ExistentialVariableSlot)) {
            return false;
        }
        final ExistentialVariableSlot that = (ExistentialVariableSlot) obj;
        return this.potentialSlot.equals(that.potentialSlot)
                && this.alternativeSlot.equals(that.alternativeSlot);
    }

    // Comparisons to ExistentialVariableSlot done by the component slots
    // Comparisons to all other slots done by ID
    @Override
    public int compareTo(Slot other) {
        if (!(other instanceof ExistentialVariableSlot)) {
            return super.compareTo(other);
        }

        final ExistentialVariableSlot that = (ExistentialVariableSlot) other;
        int potentialSlotCompare = this.potentialSlot.compareTo(that.potentialSlot);
        int alternativeSlotCompare = this.alternativeSlot.compareTo(that.alternativeSlot);
        return potentialSlotCompare != 0 ? potentialSlotCompare : alternativeSlotCompare;
    }
}
