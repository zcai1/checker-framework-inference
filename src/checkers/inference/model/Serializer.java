package checkers.inference.model;

/**
 * Interface for serializing constraints and variables.
 *
 * Serialization will occur for all variables and constraints before Constraint
 * solving.
 *
 * This allows us to avoid re-generating constraints for a piece of source code
 * every time we wish to solve (for instance when a new solver is written or an
 * existing one is modified).
 *
 * Type parameters SlotEncodingT and ConstraintEncodingT are used to adapt the
 * return type of the XXXSlot visitor methods (SlotEncodingT) and the
 * XXXConstraint visitor methods (ConstraintEncodingT). Implementing classes can
 * use the same or different types for these type parameters.
 */
public interface Serializer<SlotEncodingT, ConstraintEncodingT> {

    SlotEncodingT serialize(VariableSlot slot);

    SlotEncodingT serialize(ConstantSlot slot);

    SlotEncodingT serialize(ExistentialVariableSlot slot);

    SlotEncodingT serialize(RefinementVariableSlot slot);

    SlotEncodingT serialize(VPAVariableSlot slot);

    SlotEncodingT serialize(LUBVariableSlot slot);

    SlotEncodingT serialize(ArithmeticVariableSlot slot);

    SlotEncodingT serialize(PolyInvokeVariableSlot slot);

    ConstraintEncodingT serialize(SubtypeConstraint constraint);

    ConstraintEncodingT serialize(EqualityConstraint constraint);

    ConstraintEncodingT serialize(ExistentialConstraint constraint);

    ConstraintEncodingT serialize(InequalityConstraint constraint);

    ConstraintEncodingT serialize(ComparableConstraint comparableConstraint);

    ConstraintEncodingT serialize(CombineConstraint combineConstraint);

    ConstraintEncodingT serialize(PreferenceConstraint preferenceConstraint);

    ConstraintEncodingT serialize(ImplicationConstraint implicationConstraint);

    ConstraintEncodingT serialize(ArithmeticConstraint arithmeticConstraint);
}
