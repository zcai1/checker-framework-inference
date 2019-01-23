package checkers.inference;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;

import org.checkerframework.framework.type.AnnotatedTypeMirror;

import checkers.inference.model.AnnotationLocation;
import checkers.inference.model.ArithmeticVariableSlot;
import checkers.inference.model.ConstantSlot;
import checkers.inference.model.ExistentialVariableSlot;
import checkers.inference.model.LUBVariableSlot;
import checkers.inference.model.PolyInvokeVariableSlot;
import checkers.inference.model.RefinementVariableSlot;
import checkers.inference.model.Slot;
import checkers.inference.model.VPAVariableSlot;
import checkers.inference.model.VariableSlot;

/**
 * SlotManager stores variables for later access, provides ids for creating
 * variables and provides helper method for converting back and forth between
 * Slots and the AnnotationMirrors that represent them.
 */
public interface SlotManager {

    /**
     * Return number of slots collected by this SlotManager
     *
     * @return number of slots collected by this SlotManager
     */
    int getNumberOfSlots();

    /**
     * Create new ConstrantSlot and returns the reference to it if no ConstantSlot
     * representing this AnnotationMirror exists. Otherwise, return the reference to
     * existing ConstantSlot. An AnnotationMirror uniquely identifies a ConstantSlot
     *
     * @param value
     *            The actual AnnotationMirror that this ConstantSlot represents.
     *            This AnnotationMirror should be valid within the type system for
     *            which we are inferring values.
     * @return the ConstantSlot that represents this AnnotationMirror
     */
    ConstantSlot createConstantSlot(AnnotationMirror value);

    /**
     * Create new VariableSlot and return the reference to it if no VariableSlot on
     * this location exists. Otherwise return the reference to existing VariableSlot
     * on this location. Each location uniquely identifies a VariableSlot
     *
     * @param location
     *            used to locate this variable in code
     * @return VariableSlot that corresponds to this location
     */
    VariableSlot createVariableSlot(AnnotationLocation location);

    /**
     * Create new RefinementVariableSlot and return the reference to it if no
     * RefinementVariableSlot on this location exists. Otherwise return the
     * reference to existing RefinementVariableSlot on this location. Each location
     * uniquely identifies a RefinementVariableSlot
     *
     * @param location
     *            used to locate this variable in code.
     * @param refined
     *            a potential downward refinement of an existing VariableSlot
     * @return RefinementVariableSlot that corresponds to this location
     */
    RefinementVariableSlot createRefinementVariableSlot(AnnotationLocation location, Slot refined);

    /**
     * Create new ExistentialVariableSlot using potential slot and alternative slot,
     * and return reference to it if no ExistentialVariableSlot that wraps this
     * potentialSlot and alternativeSlot exists. Otherwise, returns the existing
     * ExistentialVariableSlot. Potential slot and alternative slot can uniquely
     * identify an ExistentialVariableSlot
     *
     * @param potentialSlot
     *            a variable whose annotation may or may not exist in source code
     * @param alternativeSlot
     *            the variable which would take part in a constraint if
     *            potentialSlot does not exist
     * @return the ExistentialVariableSlot that wraps this potentialSlot and
     *         alternativeSlot
     */
    ExistentialVariableSlot createExistentialVariableSlot(Slot potentialSlot, Slot alternativeSlot);

    /**
     * Create new VPAVariableSlot using receiver slot and declared slot, and return
     * reference to it if no VPAVariableSlot representing result of adapting
     * declared slot to receiver slot exists. Otherwise, returns the existing
     * VPAVariableSlot. Receiver slot and declared slot can uniquely identify a
     * VPAVariableSlot
     *
     * @param receiver
     *            receiver slot
     * @param declared
     *            declared slot
     * @return VPAVariableSlot that represents the viewpoint adaptation result of
     *         adapting declared slot to receiver slot
     */
    VPAVariableSlot createVPAVariableSlot(Slot receiver, Slot declared);

    /**
     * Creates new LubVariableSlot using left slot and right slot, and returns
     * reference to it if no LubVariableSlot representing least upper bound of left
     * slot and right slot exists. Otherwise, returns the existing VPAVariableSlot.
     * Left slot and right slot can uniquely identify a LubVariableSlot
     *
     * @param left
     *            left side of least upper bound operation
     * @param right
     *            right side of least upper bound operation
     * @return LubVariableSlot that represents the least upper bound result of left
     *         slot and right slot
     */
    LUBVariableSlot createLubVariableSlot(Slot left, Slot right);

    /**
     * Create new ArithmeticVariableSlot at the given location and return a
     * reference to it if no ArithmeticVariableSlots exists for the location.
     * Otherwise, returns the existing ArithmeticVariableSlot.
     *
     * @param location
     *            an AnnotationLocation used to locate this variable in code
     * @return the ArithmeticVariableSlot for the given location
     */
    ArithmeticVariableSlot createArithmeticVariableSlot(AnnotationLocation location);

    /**
     * Retrieves the ArithmeticVariableSlot created for the given location if it has
     * been previously created, otherwise null is returned.
     *
     * This method allows faster retrieval of already created
     * ArithmeticVariableSlots during traversals of binary trees in an
     * InferenceVisitor subclass, which does not have direct access to the ATM
     * containing this slot.
     *
     * @param location
     *            an AnnotationLocation used to locate this variable in code
     * @return the ArithmeticVariableSlot for the given location, or null if none
     *         exists
     */
    ArithmeticVariableSlot getArithmeticVariableSlot(AnnotationLocation location);

    /**
     * Create new PolyInvokeVariableSlot and return the reference to it if no
     * PolyInvokeVariableSlot on this location exists. Otherwise return the
     * reference to existing PolyInvokeVariableSlot on this location. Each location
     * uniquely identifies a PolyInvokeVariableSlot
     *
     * @param location
     *            used to locate this variable in code
     * @return PolyInvokeVariableSlot that corresponds to this location
     */
    PolyInvokeVariableSlot createPolyInvokeVariableSlot(AnnotationLocation location);

    /**
     * Create a VarAnnot equivalent to the given realQualifier.
     *
     * @return a VarAnnot equivalent to the given realQualifier.
     *
     */
    AnnotationMirror createEquivalentVarAnno(final AnnotationMirror realQualifier);

    /**
     * Return the Slot identified by the given id or null if no such Slot has been
     * added
     */
    Slot getSlot(int id);

    /**
     * Return the Slot (or an equivalent Slot) that is represented by the given
     * AnnotationMirror. A RuntimeException is thrown if the annotation isn't a
     * VarAnnot or a member of one of the REAL_QUALIFIER set provided by
     * InferenceChecker.
     * 
     * @param am
     *            The annotationMirror representing a Slot
     * @return The Slot (on an equivalent Slot) represented by annotationMirror
     */
    Slot getSlot(AnnotationMirror am);

    /**
     * Return the Slot in the primary annotation location of annotated type mirror.
     * If there is no Slot this method throws an exception
     * 
     * @param atm
     *            An annotated type mirror with a VarAnnot in its primary
     *            annotations list
     */
    Slot getSlot(AnnotatedTypeMirror atm);

    /**
     * Given a slot return an annotation that represents the slot when added to an
     * AnnotatedTypeMirror. If A is the annotation returned by getAnnotation( S )
     * where S is a slot. Then getSlot( A ) will return S (or an equivalent Slot in
     * case of Constants ). For {@code ConstantSlot}, this method should return the
     * {@code VariableAnnotation} that represents the underlying constant, and one
     * should use {@link ConstantSlot#getAnnotation()} to get the real annotation.
     * 
     * @param slot
     *            A slot to convert to an annotation
     * @return An annotation representing the slot
     */
    AnnotationMirror getAnnotation(Slot slot);

    /**
     * Return all slots collected by this SlotManager
     * 
     * @return a list of slots
     */
    List<Slot> getSlots();

    /**
     * Return all non-constant slots collected by this SlotManager
     * 
     * @return a list of non-constant slots
     */
    List<Slot> getVariableSlots();

    /**
     * Return all ConstantSlots collected by this SlotManager
     * 
     * @return a list of ConstantSlots
     */
    List<ConstantSlot> getConstantSlots();
}
