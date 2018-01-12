package checkers.inference.util;

import checkers.inference.InferenceMain;
import checkers.inference.SlotManager;
import checkers.inference.model.AnnotationLocation;
import checkers.inference.model.ConstraintManager;
import checkers.inference.model.Slot;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.util.ViewpointAdapter;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;

public class InferenceViewpointAdapter extends ViewpointAdapter<Slot>{

    private final SlotManager slotManager = InferenceMain.getInstance().getSlotManager();
    private final ConstraintManager constraintManager = InferenceMain.getInstance().getConstraintManager();
    private AnnotationLocation location;

    public void setLocation(AnnotationLocation location) {
        this.location = location;
    }

    @Override
    protected Slot combineModifierWithModifier(Slot recvModifier, Slot declModifier, AnnotatedTypeFactory f) {
        Slot combVariableSlot = slotManager.createCombVariableSlot(recvModifier, declModifier);
        constraintManager.addCombineConstraint(recvModifier, declModifier, combVariableSlot);
        return combVariableSlot;
    }

    @Override
    protected Slot getModifier(AnnotatedTypeMirror atm, AnnotatedTypeFactory f) {
        return slotManager.getVariableSlot(atm);
    }

    @Override
    protected AnnotationMirror getAnnotationFromModifier(Slot slot) {
        return slotManager.getAnnotation(slot);
    }

}
