package checkers.inference.solver.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;

import checkers.inference.InferenceMain;
import checkers.inference.model.*;
import checkers.inference.model.serialization.ToStringSerializer;
import checkers.inference.solver.util.StatisticRecorder.StatisticKey;

/**
 * PrintUtils contains methods for printing and writing the solved results.
 *
 * @author jianchu
 *
 */
public class PrintUtils {

    /**
     * Print the solved result out.
     *
     * @param result
     */
    public static void printResult(Map<Integer, AnnotationMirror> result) {

        final int maxLength = String.valueOf(InferenceMain.getInstance().getSlotManager().getNumberOfSlots()).length();
        StringBuilder printResult = new StringBuilder();

        System.out.println("/***********************Results****************************/");
        for (Integer j : result.keySet()) {
            printResult.append("SlotID: ");
            printResult.append(String.valueOf(j));
            for (int i = 0; i < maxLength + 2 - String.valueOf(j).length(); i++) {
                printResult.append(" ");
            }
            printResult.append("Annotation: ");
            printResult.append(result.get(j).toString());
            printResult.append("\n");
        }
        System.out.println(printResult.toString());
        System.out.flush();
        System.out.println("/**********************************************************/");
    }

    public static void writeResult(Map<Integer, AnnotationMirror> result) {

        final int maxLength = String.valueOf(InferenceMain.getInstance().getSlotManager().getNumberOfSlots()).length();
        StringBuilder printResult = new StringBuilder();

        for (Integer j : result.keySet()) {
            printResult.append("SlotID: ");
            printResult.append(String.valueOf(j));
            for (int i = 0; i < maxLength + 2 - String.valueOf(j).length(); i++) {
                printResult.append(" ");
            }
            printResult.append("Annotation: ");
            printResult.append(result.get(j).toString());
            printResult.append("\n");
        }

        File basePath = new File(new File("").getAbsolutePath());
        String writePath = basePath.getAbsolutePath() + "/result" + ".txt";
        File file = new File(writePath);
        PrintWriter pw;
        try {
            pw = new PrintWriter(file);
            pw.write(printResult.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Result has been written to: " + writePath);
    }

    private static StringBuilder buildStatistic(Map<StatisticKey, Long> statistic,
            Map<String, Integer> modelRecord) {

        StringBuilder statisticsText = new StringBuilder();
        // slot, constraint, constantslot, variableslot size
        String s1 = String.join("&", String.valueOf(statistic.get(StatisticKey.SLOTS_SIZE)),
                String.valueOf(statistic.get(StatisticKey.CONSTRAINT_SIZE)),
                String.valueOf(modelRecord.get(ConstantSlot.class.getSimpleName())),
                String.valueOf(modelRecord.get(VariableSlot.class.getSimpleName())));
        statisticsText.append(s1+"\n");

        // SATSolver related statistics
        String s2 = String.join("&", String.valueOf(statistic.get(StatisticKey.CNF_VARIABLE_SIZE)),
                String.valueOf(statistic.get(StatisticKey.CNF_CLAUSE_SIZE)),
                String.valueOf(statistic.get(StatisticKey.PARSING_TIME)),
                String.valueOf(statistic.get(StatisticKey.SAT_SERIALIZATION_TIME)),
                String.valueOf(statistic.get(StatisticKey.SAT_SOLVING_TIME)));
        statisticsText.append(s2+"\n");

        String s3 = String.join("&",
                String.valueOf(convertNumber(modelRecord, SubtypeConstraint.class.getSimpleName())),
                String.valueOf(convertNumber(modelRecord, EqualityConstraint.class.getSimpleName())),
                String.valueOf(convertNumber(modelRecord, InequalityConstraint.class.getSimpleName())),
                String.valueOf(convertNumber(modelRecord, PreferenceConstraint.class.getSimpleName())),
                String.valueOf(convertNumber(modelRecord, CombineConstraint.class.getSimpleName())),
                String.valueOf(convertNumber(modelRecord, ComparableConstraint.class.getSimpleName())),
                String.valueOf(convertNumber(modelRecord, ImplicationConstraint.class.getSimpleName())));
        statisticsText.append(s3+"\n");

        return statisticsText;
    }

    static int convertNumber(Map<String, Integer> modelRecord, String key) {
        return !modelRecord.containsKey(key) ? 0 : modelRecord.get(key);
    }

    /**
     * Print the statistics out.
     *
     * @param statistic
     * @param modelRecord
     * @param solverType
     * @param useGraph
     * @param solveInParallel
     */
    public static void printStatistic(Map<StatisticKey, Long> statistic,
            Map<String, Integer> modelRecord) {
        StringBuilder statisticsTest = buildStatistic(statistic, modelRecord);
        System.out.println("\n/***********************Statistic start*************************/");
        System.out.println(statisticsTest);
        System.out.flush();
        System.out.println("/**********************Statistic end****************************/");
    }

    public static void writeStatistic(Map<StatisticKey, Long> statistic,
            Map<String, Integer> modelRecord, String fileName) {
        StringBuilder statisticsTest = buildStatistic(statistic, modelRecord);
        String writePath = new File(new File("").getAbsolutePath()).toString() + "/" + fileName;
        try {
            PrintWriter pw = new PrintWriter(writePath);
            pw.write(statisticsTest.toString());
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Statistic has been written to: " + writePath);
    }

    private static void buildStatisticText(Map<StatisticKey, Long> statistic,
            StringBuilder statisticsText,
            StatisticKey key) {
        statisticsText.append(key.toString().toLowerCase());
        statisticsText.append(",");
        statisticsText.append(statistic.get(key));
        statisticsText.append("\n");
    }

    private static void buildStatisticText(String key, Integer value, StringBuilder statisticsText) {
        statisticsText.append(key.toLowerCase());
        statisticsText.append(",");
        statisticsText.append(value);
        statisticsText.append("\n");
    }

    public static void printUnsolvable(Collection<Constraint> mus) {
        if (mus == null) {
            System.out.println("The backend you used doesn't support explanation feature!");
            return;
        }

        ToStringSerializer toStringSerializer = new ToStringSerializer(false);
        SlotPrinter slotPrinter = new SlotPrinter(toStringSerializer);
        // Print constraints and related slots
        System.out.println("\n=================================== Explanation Starts=================================\n");
        System.out.println("------------------ Unsatisfactory Constraints ------------------\n");
        for (Constraint constraint : mus) {
            System.out.println("\t" + constraint.serialize(toStringSerializer) + " \n\t    " + constraint.getLocation().toString() + "\n");
        }
        System.out.println("------------- Related Slots -------------\n");
        for (Constraint c : mus) {
            c.serialize(slotPrinter);
        }
        System.out.println("=================================== Explanation Ends Here ================================");
    }

    /**
     * Created by mier on 04/08/17.
     * Transitively prints all non-constant slots in a constraint. Each slot is only
     * printed once.
     */
    public static final class SlotPrinter implements Serializer<Void, Void> {

        /**Delegatee that serializes slots to string representation.*/
        private final ToStringSerializer toStringSerializer;
        /**Stores already-printed slots so they won't be printed again.*/
        private final Set<Slot> printedSlots;


        public SlotPrinter(final ToStringSerializer toStringSerializer) {
            this.toStringSerializer = toStringSerializer;
            printedSlots = new HashSet<>();
        }

        private void printSlotIfNotPrinted(Slot slot) {
            if (printedSlots.add(slot) && !(slot instanceof ConstantSlot)) {
                System.out.println("\t" + slot.serialize(toStringSerializer) + " \n\t    " + slot.getLocation() + "\n");
            }
        }

        @Override
        public Void serialize(SubtypeConstraint constraint) {
            constraint.getSubtype().serialize(this);
            constraint.getSupertype().serialize(this);
            return null;
        }

        @Override
        public Void serialize(EqualityConstraint constraint) {
            constraint.getFirst().serialize(this);
            constraint.getSecond().serialize(this);
            return null;
        }

        @Override
        public Void serialize(ExistentialConstraint constraint) {
            constraint.getPotentialVariable().serialize(this);
            return null;
        }

        @Override
        public Void serialize(InequalityConstraint constraint) {
            constraint.getFirst().serialize(this);
            constraint.getSecond().serialize(this);
            return null;
        }

        @Override
        public Void serialize(ComparableConstraint comparableConstraint) {
            comparableConstraint.getFirst().serialize(this);
            comparableConstraint.getSecond().serialize(this);
            return null;
        }

        @Override
        public Void serialize(CombineConstraint combineConstraint) {
            combineConstraint.getResult().serialize(this);
            combineConstraint.getTarget().serialize(this);
            combineConstraint.getDeclared().serialize(this);
            return null;
        }

        @Override
        public Void serialize(PreferenceConstraint preferenceConstraint) {
            preferenceConstraint.getVariable().serialize(this);
            return null;
        }

        @Override
        public Void serialize(VariableSlot slot) {
            printSlotIfNotPrinted(slot);
            return null;
        }

        @Override
        public Void serialize(ConstantSlot slot) {
            return null;
        }

        @Override
        public Void serialize(ExistentialVariableSlot slot) {
            slot.getPotentialSlot().serialize(this);
            slot.getAlternativeSlot().serialize(this);
            printSlotIfNotPrinted(slot);
            return null;
        }

        @Override
        public Void serialize(RefinementVariableSlot slot) {
            slot.getRefined().serialize(this);
            printSlotIfNotPrinted(slot);
            return null;
        }

        @Override
        public Void serialize(CombVariableSlot slot) {
            slot.getFirst().serialize(this);
            slot.getSecond().serialize(this);
            printSlotIfNotPrinted(slot);
            return null;
        }

        @Override
        public Void serialize(LubVariableSlot slot) {
            return null;
        }

        @Override
        public Void serialize(ImplicationConstraint implicationConstraint) {
            for (Constraint a : implicationConstraint.getAssumptions()) {
                a.serialize(this);
            }
            implicationConstraint.getConclusion().serialize(this);
            return null;
        }
    }
}
