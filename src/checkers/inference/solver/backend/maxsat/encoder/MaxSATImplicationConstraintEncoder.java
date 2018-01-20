package checkers.inference.solver.backend.maxsat.encoder;

import checkers.inference.model.Constraint;
import checkers.inference.model.ImplicationConstraint;
import checkers.inference.solver.backend.encoder.implication.ImplicationConstraintEncoder;
import checkers.inference.solver.backend.maxsat.MaxSatFormatTranslator;
import checkers.inference.solver.frontend.Lattice;
import checkers.inference.util.ConstraintVerifier;
import org.sat4j.core.VecInt;

import javax.lang.model.element.AnnotationMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MaxSATImplicationConstraintEncoder extends MaxSATAbstractConstraintEncoder implements ImplicationConstraintEncoder<VecInt[]> {

    private final MaxSatFormatTranslator formatTranslator;

    public MaxSATImplicationConstraintEncoder(Lattice lattice, ConstraintVerifier verifier,
                                              Map<AnnotationMirror, Integer> typeToInt, MaxSatFormatTranslator formatTranslator) {
        super(lattice, verifier, typeToInt);
        this.formatTranslator = formatTranslator;
    }

    // TODO Add high level documentation
    @Override
    public VecInt[] encode(ImplicationConstraint constraint) {
        // Concatenate with every pair at the end
        VecInt[] result = constraint.getConclusion().serialize(formatTranslator);
        VecInt rhs = new VecInt();
        for (VecInt r : result) {
            rhs.pushAll(r);
        }

        // VecInt corresponds to a cnf clause
        List<VecInt> assumptions = new LinkedList<>();
        for (Constraint a : constraint.getAssumptions()) {
            assumptions.addAll(Arrays.asList(a.serialize(formatTranslator)));
        }
        List<List<Integer>> l = new ArrayList<>();
        int size = 1;
        for (VecInt clause : assumptions) {
            int[] a = clause.toArray();
            List<Integer> list = new ArrayList<>();
            for (int each : a) {
                list.add(each);
            }
            l.add(list);
            size *= clause.size();
        }
        VecInt[] target = new VecInt[size];

        // Get the combinations of
        List<List<Integer>> cartesian = cartesianProduct(l);// should have the same length as target arrray
        for (int j=0; j<target.length; j++) {
            List<Integer> toNegate = cartesian.get(j);
            VecInt targetClause = new VecInt();
            for (Integer var : toNegate) {
                targetClause.push(-var);// Push the negation, of variable because it's from lhs assumption
            }
            targetClause.pushAll(rhs);
            target[j] = targetClause;
        }
        return target;
    }

    protected <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> resultLists = new ArrayList<List<T>>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<T>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<T>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }
}
