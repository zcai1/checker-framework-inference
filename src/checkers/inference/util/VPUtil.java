package checkers.inference.util;

import javax.lang.model.type.TypeKind;

import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedArrayType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedNullType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedTypeVariable;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedWildcardType;

import checkers.inference.InferenceMain;
import checkers.inference.SlotManager;
import checkers.inference.model.CombVariableSlot;
import checkers.inference.model.ConstraintManager;
import checkers.inference.model.Slot;

public class VPUtil {

    static final SlotManager slotManager = InferenceMain.getInstance()
            .getSlotManager();
    static final ConstraintManager constraintManager = InferenceMain
            .getInstance().getConstraintManager();
    private static boolean isTypeVarExtends = false;

    // In this method, we only need to consider the upper bound of type
    // variable.
    // Other bounds are properly passed as arguments from the caller like in
    // typeVaraiblesFromUse
    public static void combineRecvTypeWithDeclType(
            AnnotatedTypeMirror recv, AnnotatedTypeMirror type) {
        // System.out.println("VPUtils: recv" + recv);
        // System.out.println("VPUtils: declType: " + declType);
        // System.out.println("VPUtils: type: " + type);
        if (recv.getKind() == TypeKind.TYPEVAR) {
            recv = ((AnnotatedTypeVariable) recv).getUpperBound();
            // System.out.println("VPUtils: typevar-recv:" + recv);
        }
        // System.out.println("VPUtils: recv is: " + recv);
        Slot recvSlot = slotManager.getVariableSlot(recv);
        /*System.out.println("VPUtils: recvSlot'id is: " + ((VariableSlot) recvSlot).getId());
        System.out.println("VPUtils: recvSlot is: " + recvSlot);*/
        // TODO figure out why sometime recvSlot is null
        if (recvSlot != null) {
            combineRecvSlotWithDeclType(recvSlot, type);
        }
        // TODO implement this in the end
        // Example?
        // combinedType = substituteTVars(recv, combinedType);
        // System.out.println("combTT comb: " + combinedType);
        return;
    }
    // declType is the copy of type, they both represent declared type.
    public static void combineRecvSlotWithDeclType(
            Slot recvSlot, AnnotatedTypeMirror type) {
        if (type.getKind().isPrimitive()) {
            // Original implementation replaces main modifier as bottom
            // Here, I just make the main modifier the one primitive has
            Slot declSlot = slotManager.getVariableSlot(type);
            // TODO @Bottom!
            type.replaceAnnotation(slotManager.getAnnotation(declSlot));
            return;
        } else if (type instanceof AnnotatedTypeVariable ) {
            // System.out.println("VPUtils: type is: " + type);
            // GUTQualifiersUtils has two scenerios: upper bound is again type variable, the other is other types
            // only upper bound processing like in typeVariablesFromUse
            // Actually, I never thought of if an annotated type mirror is type variable situation.
            // What I used to do is if typevar doesn't have varSlot, then I use varSlot!=null
            // to naturally omit it, but not actually implement it. Prof's code does consider this situation, without always 
            // skip as I did.
            // TODO Why only adpat upper bound for type variable?
            if (!isTypeVarExtends) {
                // TODO what's the purpose of this mechanism?
                isTypeVarExtends = true;
                AnnotatedTypeVariable atv = (AnnotatedTypeVariable) type;
                /*System.out.println("VPUtils: atv.getUpperBound is: "
                        + atv.getUpperBound());*/
                combineRecvSlotWithDeclType(recvSlot, atv.getUpperBound());
                /*System.out.println("VPUtils: atv.getLowerBound is: " + atv.getLowerBound());*/
                combineRecvSlotWithDeclType(recvSlot, atv.getLowerBound());
                isTypeVarExtends = false;
                return;
            }
            return;
        } else if (type instanceof AnnotatedDeclaredType) {
            // System.out.println("VPUtils: type is: " + type);
            // I think this is my implementation in InferenceATF, which only
            // considers declared type situation.
            // Get the combined main modifier
            AnnotatedDeclaredType declDeclaredType = (AnnotatedDeclaredType)type;
            Slot declSlot = slotManager.getVariableSlot(declDeclaredType);
            // Can recvSlot and declSlot be null?
            CombVariableSlot combSlot = slotManager.createCombVariableSlot(recvSlot, declSlot);
            constraintManager.addCombineConstraint(recvSlot, declSlot, combSlot);
            declDeclaredType.replaceAnnotation(slotManager.getAnnotation(combSlot));

            // Get the combined type arguments
            // This is only for parameterized type: also adapt type arguments
            // There is no relationship between typeVariablesFromUse. It's considering parameterized situatinon
            for (AnnotatedTypeMirror typeArgument : ((AnnotatedDeclaredType)type).getTypeArguments()) { 
                combineRecvSlotWithDeclType(recvSlot, typeArgument);
                // The following implementation is only one levl and duplicate to the recursive method
                // Recursive method's purpose is to remove this way of writing method
                /*Slot typeArgumentDeclSlot = slotManager.getVariableSlot(typeArgument);
                CombVariableSlot typeArgumentCombSlot = slotManager.addCombVariableSlot(null, recvSlot, typeArgumentDeclSlot);
                constraintManager.add(new CombineConstraint(recvSlot, typeArgumentDeclSlot, typeArgumentCombSlot));
                typeArgument.replaceAnnotation(slotManager.getAnnotation(typeArgumentCombSlot));*/
            }
            return;
        } else if (type instanceof AnnotatedArrayType) {
            // Use type, but not declType
            AnnotatedArrayType  declArrayType = (AnnotatedArrayType) type;
            // TODO: combine element type
            AnnotatedTypeMirror declComponentType = declArrayType.getComponentType();
            // Recursively call itself first.
            combineRecvSlotWithDeclType(recvSlot, declComponentType);
            // Then adapt the main modifier, in case the component type might affect the main modifier
            // Similar to getting universe modifier
            // !Always let component type get adapated first, in order to
            // ensure the inner parts get adapted first, like the GUTQualsUtils
            Slot declMainSlot = slotManager.getVariableSlot(declArrayType);
            CombVariableSlot combMainSlot = slotManager.createCombVariableSlot(recvSlot, declMainSlot);
            constraintManager.addCombineConstraint(recvSlot, declMainSlot, combMainSlot);
            // Construct result type
            declArrayType.replaceAnnotation(slotManager.getAnnotation(combMainSlot));
            return;
        } else if (type instanceof AnnotatedWildcardType) {

            AnnotatedWildcardType wildType = (AnnotatedWildcardType) type;

            // There is no main modifier for a wildcard
            // TODO Is the uppper bound and lower bound part of the AnnotatedWildcardType?
            // TODO or like type variable, separately pass bounds to incovation of combineRecvSlotWithDeclType?
            AnnotatedTypeMirror annotatedUpperBound = wildType.getExtendsBound();
            combineRecvSlotWithDeclType(recvSlot, annotatedUpperBound);

            AnnotatedTypeMirror annotatedLowerBound = wildType.getSuperBound();
            combineRecvSlotWithDeclType(recvSlot, annotatedLowerBound);
            return;
        } else if (type instanceof AnnotatedNullType) {
            // System.out.println("VPUtils: type is: " + type);
            // Viewpoint result is still type, nothing changes, so return
            return;
        } else {
            System.err.println("Unknown result.getKind(): " + type.getKind());
            assert false;
            return;
        }
    }
}
