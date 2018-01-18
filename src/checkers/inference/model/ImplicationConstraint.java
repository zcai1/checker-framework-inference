package checkers.inference.model;

import java.util.ArrayList;
import java.util.List;

public class ImplicationConstraint extends Constraint {

    private final List<Constraint> assumptions;
    private final Constraint conclusion;

    public ImplicationConstraint(
            List<Constraint> assumptions, Constraint conclusion, AnnotationLocation location) {
        super(new ArrayList<>(), location);
        for(Constraint a : assumptions) {
            getSlots().addAll(a.getSlots());
        }
        getSlots().addAll(conclusion.getSlots());

        this.assumptions = assumptions;
        this.conclusion = conclusion;
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
