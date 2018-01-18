package checkers.inference.solver.backend.encoder.implication;

import checkers.inference.model.ImplicationConstraint;

public interface ImplicationConstraintEncoder<ConstraintEncodingT> {

    ConstraintEncodingT encode(ImplicationConstraint constraint);
}
