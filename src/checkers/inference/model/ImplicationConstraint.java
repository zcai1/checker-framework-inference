package checkers.inference.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Constraint that models implication logic. If all the assumptions are satisfied, then
 * conclusion should also be satisfied.
 *
 * This is intended to express the restrictions between solutions for {@link Slot}s. For example,
 * if {@code @A} is inferred as on declaration of class {@code MyClass}, then every usage of class
 * {@code MyClass} should also be inferred to {@code @A}, but to nothing else. {@link ImplicationConstraint}
 * can express this "restriction":
 * <p>
 * {@code @1 == @A -> @2 == @A}, in which {@code @1} is the slot inserted on the class tree and
 * {@code @2} is the slot that represents one usage of {@code MyClass}.
 */
public class ImplicationConstraint extends Constraint {

    /**
     * A list of {@link Constraint}s that are conjuncted together.
     */
    private final List<Constraint> assumptions;

    /**
     * A single {@link Constraint} that is implicated by the {@link #assumptions}.
     */
    private final Constraint conclusion;

    public ImplicationConstraint(
            List<Constraint> assumptions, Constraint conclusion, AnnotationLocation location) {
        super(computeSlots(assumptions, conclusion), location);

        this.assumptions = assumptions;
        this.conclusion = conclusion;
    }

    private static List<Slot> computeSlots(List<Constraint> assumptions, Constraint conclusion) {
        List<Slot> slots = new ArrayList<>();
        for(Constraint a : assumptions) {
            slots.addAll(a.getSlots());
        }
        slots.addAll(conclusion.getSlots());
        return slots;
    }

    public List<Constraint> getAssumptions() {
        return assumptions;
    }

    public Constraint getConclusion() {
        return conclusion;
    }

    @Override
    public <S, T> T serialize(Serializer<S, T> serializer) {
        return serializer.serialize(this);
    }
}
