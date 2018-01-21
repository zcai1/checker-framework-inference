package checkers.inference.model;

import java.util.ArrayList;
import java.util.List;

public class ImplicationConstraint extends Constraint {

    private final List<Constraint> assumptions;
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
