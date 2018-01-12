package checkers.inference.solver.backend.maxsat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import javax.lang.model.element.AnnotationMirror;

import org.checkerframework.javacutil.ErrorReporter;
import org.sat4j.core.VecInt;
import org.sat4j.maxsat.SolverFactory;
import org.sat4j.maxsat.WeightedMaxSatDecorator;

import checkers.inference.InferenceMain;
import checkers.inference.SlotManager;
import checkers.inference.model.Constraint;
import checkers.inference.model.PreferenceConstraint;
import checkers.inference.model.Slot;
import checkers.inference.solver.backend.Solver;
import checkers.inference.solver.frontend.Lattice;
import checkers.inference.solver.util.SolverArg;
import checkers.inference.solver.util.SolverEnvironment;
import checkers.inference.solver.util.StatisticRecorder;
import checkers.inference.solver.util.StatisticRecorder.StatisticKey;
import org.sat4j.pb.IPBSolver;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.xplain.DeletionStrategy;
import org.sat4j.tools.xplain.Xplain;

/**
 * MaxSatSolver calls MaxSatFormatTranslator that converts constraint into a list of
 * VecInt, then invoke Sat4j lib to solve the clauses, and decode the result.
 *
 * @author jianchu
 *
 */
public class MaxSatSolver extends Solver<MaxSatFormatTranslator> {

    protected enum MaxSatSolverArg implements SolverArg {
        /**
         * Whether should print the CNF formulas.
         */
        outputCNF;
    }

    protected final SlotManager slotManager;
    protected final List<VecInt> hardClauses = new LinkedList<>();
    private List<VecInt> wellFormdnessClauses = new LinkedList<>();
    protected final List<VecInt> softClauses = new LinkedList<>();
    private final MaxSATUnsatisfiableConstraintExplainer unsatisfiableConstraintExplainer;
    protected final File CNFData = new File(new File("").getAbsolutePath() + "/cnfData");
    protected StringBuilder CNFInput = new StringBuilder();

    private long serializationStart;
    private long serializationEnd;
    protected long solvingStart;
    protected long solvingEnd;

    public MaxSatSolver(SolverEnvironment solverEnvironment, Collection<Slot> slots,
            Collection<Constraint> constraints, MaxSatFormatTranslator formatTranslator, Lattice lattice) {
        super(solverEnvironment, slots, constraints, formatTranslator,
                lattice);
        this.slotManager = InferenceMain.getInstance().getSlotManager();
        this.unsatisfiableConstraintExplainer = new MaxSATUnsatisfiableConstraintExplainer();

        if (shouldOutputCNF()) {
            CNFData.mkdir();
        }
    }

    @Override
    public Map<Integer, AnnotationMirror> solve() {

        Map<Integer, AnnotationMirror> result = new HashMap<>();
        final WeightedMaxSatDecorator solver = new WeightedMaxSatDecorator(
                org.sat4j.pb.SolverFactory.newBoth());

        this.serializationStart = System.currentTimeMillis();
        // Serialization step:
        encodeAllConstraints();
        encodeWellFormdnessRestriction();
        this.serializationEnd = System.currentTimeMillis();

        if (shouldOutputCNF()) {
            buildCNF();
            writeCNFInput();
        }
        // printClauses();
        configureSatSolver(solver);

        try {
            addClausesToSolver(solver);

            this.solvingStart = System.currentTimeMillis();
            boolean isSatisfiable = solver.isSatisfiable();
            this.solvingEnd = System.currentTimeMillis();

            long solvingTime = solvingEnd - solvingStart;
            long serializationTime = serializationEnd - serializationStart;

            StatisticRecorder.recordSingleSerializationTime(serializationTime);
            StatisticRecorder.recordSingleSolvingTime(solvingTime);

            if (isSatisfiable) {
                result = decode(solver.model());
                // PrintUtils.printResult(result);
            } else {
                System.out.println("Not solvable!");
                result = null;
            }

        } catch (ContradictionException e) {
            InferenceMain.getInstance().logger.warning("Contradiction exceptin: ");
            // This case indicates that constraints are not solvable, too. This is normal so continue
            // execution and let solver strategy to explain why there is no solution
            result = null;
        } catch (Exception e) {
            ErrorReporter.errorAbort("Unexpected error occurred!", e);
        }
        return result;
    }

    /**
     * Convert constraints to list of VecInt.
     */
    @Override
    public void encodeAllConstraints() {
        for (Constraint constraint : constraints) {
            collectVarSlots(constraint);
            VecInt[] encoding = constraint.serialize(formatTranslator);
            if (encoding == null) {
                InferenceMain.getInstance().logger.warning(getClass()
                        + "doesn't support encoding constraint: " + constraint
                        + "of class: " + constraint.getClass());
                continue;
            }
            for (VecInt res : encoding) {
                if (res != null && res.size() != 0) {
                    if (constraint instanceof PreferenceConstraint) {
                        softClauses.add(res);
                    } else {
                        hardClauses.add(res);
                        //System.out.println("Generated hard clause: " + res);
                        // Add here to avoid second round of iteration over constraints if there's no solution
                        unsatisfiableConstraintExplainer.addVecIntToConstraintMapping(res, constraint);
                    }
                }
            }
        }
    }

    private void encodeWellFormdnessRestriction() {
        for (Integer varSlotId : varSlotIds) {
            List<VecInt> result = formatTranslator.generateWellFormednessClauses(varSlotId);
//            for (VecInt v : result) {
//                System.out.println("VariableId: " + varSlotId + " well formdness clause: " + v);
//            }
            wellFormdnessClauses.addAll(result);
        }
    }

    /**
     * sat solver configuration Configure
     *
     * @param solver
     */
    private void configureSatSolver(WeightedMaxSatDecorator solver) {

        final int totalVars = (slotManager.getNumberOfSlots() * lattice.numTypes);
        final int totalClauses = hardClauses.size() + wellFormdnessClauses.size() + softClauses.size();

        solver.newVar(totalVars);
        solver.setExpectedNumberOfClauses(totalClauses);
        StatisticRecorder.record(StatisticKey.CNF_CLAUSE_SIZE, (long) totalClauses);
        countVariables();
        solver.setTimeoutMs(1000000);
    }

    private void addClausesToSolver(WeightedMaxSatDecorator solver) throws ContradictionException {
        for (VecInt hardClause : hardClauses) {
            solver.addHardClause(hardClause);
        }

        for (VecInt wellFormdnessClause: wellFormdnessClauses) {
            solver.addHardClause(wellFormdnessClause);
        }

        for (VecInt softclause : softClauses) {
            solver.addSoftClause(softclause);
        }
    }

    protected Map<Integer, AnnotationMirror> decode(int[] solution) {
        Map<Integer, AnnotationMirror> result = new HashMap<>();
        for (Integer var : solution) {
            if (var > 0) {
                var = var - 1;
                int slotId = MathUtils.getSlotId(var, lattice);
                AnnotationMirror type = formatTranslator.decodeSolution(var, solverEnvironment.processingEnvironment);
                result.put(slotId, type);
            }
        }
        return result;
    }

    protected void countVariables() {

        Set<Integer> vars = new HashSet<Integer>();

        for (VecInt vi : hardClauses) {
            for (int i : vi.toArray()) {
                vars.add(i);
            }
        }
        StatisticRecorder.record(StatisticKey.CNF_VARIABLE_SIZE, (long) vars.size());
    }

    protected boolean shouldOutputCNF() {
        return solverEnvironment.getBoolArg(MaxSatSolverArg.outputCNF);
    }

    /**
     * Write CNF clauses into a string.
     */
    protected void buildCNF() {

        final int totalClauses = hardClauses.size();
        final int totalVars = slotManager.getNumberOfSlots() * lattice.numTypes;

        CNFInput.append("c This is the CNF input\n");
        CNFInput.append("p cnf ");
        CNFInput.append(totalVars);
        CNFInput.append(" ");
        CNFInput.append(totalClauses);
        CNFInput.append("\n");

        for (VecInt clause : hardClauses) {
            int[] literals = clause.toArray();
            for (int i = 0; i < literals.length; i++) {
                CNFInput.append(literals[i]);
                CNFInput.append(" ");
            }
            CNFInput.append("0\n");
        }
    }

    protected void writeCNFInput() {
        writeCNFInput("cnfdata.txt");
    }

    protected void writeCNFInput(String file) {
        String writePath = CNFData.getAbsolutePath() + "/" + file;
        File f = new File(writePath);
        PrintWriter pw;
        try {
            pw = new PrintWriter(f);
            pw.write(CNFInput.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * print all soft and hard clauses for testing.
     */
    protected void printClauses() {
        System.out.println("Hard clauses: ");
        for (VecInt hardClause : hardClauses) {
            System.out.println(hardClause);
        }
        System.out.println();
        System.out.println("Soft clauses: ");
        for (VecInt softClause : softClauses) {
            System.out.println(softClause);
        }
    }

    @Override
    public Collection<Constraint> explainUnsatisfiable() {
        return unsatisfiableConstraintExplainer.minimumUnsatisfiableConstraints();
    }

    class MaxSATUnsatisfiableConstraintExplainer {

        /**
         * A mapping from VecInt to Constraint.
         * */
        private final Map<VecInt, Constraint> vecIntConstraintMap;

        private final Map<IConstr, VecInt> iConstrVecIntMap;


        private MaxSATUnsatisfiableConstraintExplainer() {
            // Using IdentityHashMap because different VecInts can share the same hash code,
            // but VecInt has one-to-one relation to Constraint
            vecIntConstraintMap = new IdentityHashMap<>();
            iConstrVecIntMap = new IdentityHashMap<>();
        }

        private void addVecIntToConstraintMapping(VecInt encoding, Constraint sourceConstraint) {
            vecIntConstraintMap.put(encoding, sourceConstraint);
        }

        private Collection<Constraint> minimumUnsatisfiableConstraints() {
            Set<Constraint> mus = new HashSet<>();
            // Explainer solver that is used
            Xplain<IPBSolver> explanationSolver = new Xplain<>(SolverFactory.newDefault());
            configureExplanationSolver(hardClauses, wellFormdnessClauses, slotManager, lattice, explanationSolver);
            try {
//                System.out.println("Hard Clauses");
                for (VecInt clause : hardClauses) {
//                    System.out.println("Adding hard clause: " + clause + " hashCode: " + clause.hashCode());
//                    System.out.println("Before Constraint: " + vecIntConstraintMap.get(clause));
                    IConstr iConstr = explanationSolver.addClause(clause);
                    iConstrVecIntMap.put(iConstr, clause);
//                    System.out.println("Added hard clause: " + clause + " hashCode: " + clause.hashCode());
//                    System.out.println("After Constraint: " + vecIntConstraintMap.get(clause));

                }
//                System.out.println("Well Form Clauses");
                for (VecInt clause : wellFormdnessClauses) {
//                    System.out.println("Adding Well Form: " + clause + " hashCode: " + clause.hashCode());
                    IConstr iConstr = explanationSolver.addClause(clause);
                    iConstrVecIntMap.put(iConstr, clause);
//                    System.out.println("Added Well Form: " + clause + " hashCode: " + clause.hashCode());
                }
                assert !explanationSolver.isSatisfiable();
                // Get collection of unsatisfiable constraints
                Collection<IConstr> explanation = explanationSolver.explain();
//                System.out.println("Explanation starts:");
                for (IConstr i : explanation) {
                    VecInt vecInt = iConstrVecIntMap.get(i);
                    if (vecIntConstraintMap.get(vecInt) != null) {
                        // It's ok to use HashSet since Constraint has a reliable hashCode implementation
                        mus.add(vecIntConstraintMap.get(vecInt));
                    } else {
                        System.out.println("Explanation hits well-formedness restriction: " + i);
                    }
                }
//                int[] indicies = explanationSolver.minimalExplanation();
//                for (int clauseIndex : indicies) {
//                    if (clauseIndex > constraints.size()) {
//                        System.out.println("Wellformdness mixed in: " + );
//                        continue;
//                    }
//                    // Solver gives 1-based index. Decrement by 1 here to get stored constraint
//                    Constraint constraint = hardConstraints.get(clauseIndex - 1);
//                    musSet.add(constraint);
//                    System.out.println(hardClauses.get(clauseIndex - 1));
//                }
            } catch (Exception e) {
                ErrorReporter.errorAbort("Explanation solver encountered not-expected exception: ", e);
            }
            return mus;
        }

        private void configureExplanationSolver(final List<VecInt> hardClauses, final List<VecInt> wellformdness,
                final SlotManager slotManager,
                                                final Lattice lattice, final Xplain<IPBSolver> explainer) {
            int numberOfNewVars = slotManager.getNumberOfSlots() * lattice.numTypes;
            System.out.println("Number of variables: " + numberOfNewVars);
            int numberOfClauses = hardClauses.size() + wellformdness.size();
            System.out.println("Number of clauses: " + numberOfClauses);
            explainer.setMinimizationStrategy(new DeletionStrategy());
            explainer.newVar(numberOfNewVars);
            explainer.setExpectedNumberOfClauses(numberOfClauses);
        }
    }
}
