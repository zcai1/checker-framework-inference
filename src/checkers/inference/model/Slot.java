package checkers.inference.model;

import org.checkerframework.dataflow.util.HashCodeUtils;

import checkers.inference.SlotManager;

/**
 * Slots represent constraint variables or known annotations over which
 * Constraints are generated.
 *
 * Each slot has a unique ID.
 * 
 * Constraint variable slots are attached to a code location.
 *
 * Slots for known annotations hold the annotation mirror for that annotation.
 * see {@link ConstantSlot}.
 */
public abstract class Slot implements Comparable<Slot>{

    /**
     * Uniquely identifies this Slot. id's are monotonically increasing in value by
     * the order they are generated. See {@link SlotManager}.
     */
    private final int id;

    /**
     * Should this VariableSlot be inserted back into the source code.
     */
    private final boolean insertable;

    /**
     * Used to locate this Slot in source code. ASTRecords are written to Jaif files
     * along with the Annotation determined for this slot by the Solver.
     */
    private AnnotationLocation location;

    /**
     * Create a Slot with the given annotation location.
     *
     * @param id
     *            Unique identifier for this variable
     * @param insertable
     *            whether this variable is insertable into source code or not
     * @param location
     *            an AnnotationLocation for which the slot is attached to
     */
    public Slot(int id, boolean insertable, AnnotationLocation location) {
        this.id = id;
        this.insertable = insertable;
        this.location = location;
    }

    /**
     * Create a slot with a default location of
     * {@link AnnotationLocation#MISSING_LOCATION}.
     * 
     * @param id
     *            Unique identifier for this variable
     * @param insertable
     *            whether this variable is insertable into source code or not
     */
    public Slot(int id, boolean insertable) {
        this(id, insertable, AnnotationLocation.MISSING_LOCATION);
    }

    public final int getId() {
        return id;
    }

    public final boolean isInsertable() {
        return insertable;
    }

    public final AnnotationLocation getLocation() {
        return location;
    }

    public final void setLocation(AnnotationLocation location) {
        this.location = location;
    }

    public final boolean isVariable() {
        return !isConstant();
    }

    public final boolean isConstant() {
        return this instanceof ConstantSlot;
    }

    public abstract <SlotEncodingT> SlotEncodingT serialize(
            Serializer<SlotEncodingT, ?> serializer);

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + id + ")";
    }

    @Override
    public int hashCode() {
        return HashCodeUtils.hash(id);
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Slot)) {
            return false;
        }
        final Slot that = (Slot) obj;
        // by construction, IDs are unique
        return this.id == that.id;
    }

    @Override
    public int compareTo(Slot other) {
        return Integer.compare(id, other.id);
    }
}
