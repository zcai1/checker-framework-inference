package checkers.inference;

import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.BugInCF;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;

import checkers.inference.model.LUBVariableSlot;

import com.sun.tools.javac.util.Pair;

import checkers.inference.model.AnnotationLocation;
import checkers.inference.model.ArithmeticVariableSlot;
import checkers.inference.model.VPAVariableSlot;
import checkers.inference.model.ConstantSlot;
import checkers.inference.model.ExistentialVariableSlot;
import checkers.inference.model.RefinementVariableSlot;
import checkers.inference.model.Slot;
import checkers.inference.model.VariableSlot;
import checkers.inference.qual.VarAnnot;

/**
 * The default implementation of SlotManager.
 * @see checkers.inference.SlotManager
 */
public class DefaultSlotManager implements SlotManager {

    /**
     * Instance of {@link VarAnnot}.
     */
    private final AnnotationMirror varAnnot;

    /**
     * Monotonically increasing id for all Slots. This id starts at 1 because in
     * some serializer's (CnfSerializer) 0 is used as line delimiters.
     */
    private int nextId = 1;

    /**
     * A map for storing all the slots encountered by this slot manager. Key is an
     * {@link Integer}, representing a slot id. Value is a {@link Slot} that
     * corresponds to this slot id. Note that ConstantSlots are also stored in this
     * map.
     */
    private final Map<Integer, Slot> slots;

    /**
     * A map of {@link AnnotationMirror} to {@link ConstantSlot} for caching
     * ConstantSlot. Each {@link AnnotationMirror} uniquely identify a ConstantSlot.
     */
    private final Map<AnnotationMirror, ConstantSlot> constantCache;

    /**
     * A map of {@link AnnotationLocation} to {@link Slot} for caching VariableSlot
     * and RefinementVariableSlot. Those two kinds of slots can be uniquely
     * identified by their {@link AnnotationLocation}(Except MissingLocation).
     */
    private final Map<AnnotationLocation, Slot> locationCache;

    /**
     * A map of {@link Pair} of {@link Slot} to {@link ExistentialVariableSlot} for
     * caching ExistentialVariableSlot. Each ExistentialVariableSlot can be uniquely
     * identified by its potential and alternative Slots.
     */
    private final Map<Pair<Slot, Slot>, ExistentialVariableSlot> existentialSlotCache;

    /**
     * A map of {@link Pair} of {@link Slot} to {@link VPAVariableSlot} for caching
     * VPAVariableSlot. Each pair of receiver slot and declared slot uniquely
     * identifies a VPAVariableSlot.
     */
    private final Map<Pair<Slot, Slot>, VPAVariableSlot> vpaSlotCache;

    /**
     * A map of {@link Pair} of {@link Slot} to {@link LUBVariableSlot} for caching
     * LUBVariableSlot. Each pair of receiver slot and declared slot uniquely
     * identifies a LUBVariableSlot.
     */
    private final Map<Pair<Slot, Slot>, LUBVariableSlot> lubSlotCache;

    /**
     * A map of {@link AnnotationLocation} to {@link ArithmeticVariableSlot} for
     * caching {@link ArithmeticVariableSlot}s. The annotation location uniquely
     * identifies an {@link ArithmeticVariableSlot}.
     */
    private final Map<AnnotationLocation, ArithmeticVariableSlot> arithmeticSlotCache;

    private final Set<Class<? extends Annotation>> realQualifiers;
    private final ProcessingEnvironment processingEnvironment;

    public DefaultSlotManager( final ProcessingEnvironment processingEnvironment,
                               final Set<Class<? extends Annotation>> realQualifiers,
                               boolean storeConstants) {
        this.processingEnvironment = processingEnvironment;
        // sort the qualifiers so that they are always assigned the same varId
        this.realQualifiers = sortAnnotationClasses(realQualifiers);
        slots = new LinkedHashMap<>();

        // TODO: share the instance of VarAnnot created in InferenceATF
        AnnotationBuilder builder = new AnnotationBuilder(processingEnvironment, VarAnnot.class);
        builder.setValue("value", -1);
        this.varAnnot = builder.build();

        // Construct empty caches
        constantCache = AnnotationUtils.createAnnotationMap();
        locationCache = new LinkedHashMap<>();
        existentialSlotCache = new LinkedHashMap<>();
        vpaSlotCache = new LinkedHashMap<>();
        lubSlotCache = new LinkedHashMap<>();
        arithmeticSlotCache = new LinkedHashMap<>();

        if (storeConstants) {
            Set<? extends AnnotationMirror> mirrors = InferenceMain.getInstance().getRealTypeFactory().getQualifierHierarchy().getTypeQualifiers();
            for (AnnotationMirror am : mirrors) {
                ConstantSlot constantSlot = new ConstantSlot(nextId(), am);
                addToSlots(constantSlot);
                constantCache.put(am, constantSlot);
            }
        }
    }
    private Set<Class<? extends Annotation>> sortAnnotationClasses(Set<Class<? extends Annotation>> annotations) {

        TreeSet<Class<? extends Annotation>> set = new TreeSet<>(new Comparator<Class<? extends Annotation>>() {
            @Override
            public int compare(Class<? extends Annotation> o1, Class<? extends Annotation> o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                return o1.getCanonicalName().compareTo(o2.getCanonicalName());
            }
        });
        set.addAll(annotations);
        return set;
    }

    /**
     * Returns the next unique variable id.  These id's are monotonically increasing.
     * @return the next variable id to be used in VariableCreation
     */
    private int nextId() {
        return nextId++;
    }

    private void addToSlots(final Slot slot) {
        slots.put(slot.getId(), slot);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Slot getSlot(int id) {
        return slots.get(id);
    }

    /**
     * @inheritDoc
     */
    @Override
    public AnnotationMirror getAnnotation(final Slot slot) {
        AnnotationBuilder builder = new AnnotationBuilder(processingEnvironment, VarAnnot.class);
        builder.setValue("value", slot.getId());
        return builder.build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Slot getSlot(final AnnotatedTypeMirror atm) {
        AnnotationMirror annot = atm.getAnnotationInHierarchy(this.varAnnot);
        if (annot == null) {
            if (InferenceMain.isHackMode()) {
                return null;
            }
            throw new BugInCF("Missing VarAnnot annotation: " + atm);
        }
        return getSlot(annot);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Slot getSlot(final AnnotationMirror am) {

        final int id;
        if (InferenceQualifierHierarchy.isVarAnnot(am)) {
            if (am.getElementValues().isEmpty()) {
                throw new BugInCF("Attempting to obtain a slot for VarAnnot with no ID: " + am);
            }
            final AnnotationValue annoValue = am.getElementValues().values().iterator().next();
            id = Integer.valueOf(annoValue.toString());

            return getSlot(id);

        } else {
            if (constantCache.containsKey(am)) {
                return constantCache.get(am);
            } else {
                for (Class<? extends Annotation> realAnno : realQualifiers) {
                    if (AnnotationUtils.areSameByClass(am, realAnno)) {
                        return createConstantSlot(am);
                    }
                }
            }
        }

        // for any other annotation mirror, in hack mode return the constant slot for Top
        if (InferenceMain.isHackMode()) {
            return createConstantSlot(InferenceMain.getInstance().getRealTypeFactory()
                    .getQualifierHierarchy().getTopAnnotations().iterator().next());
        }
        throw new BugInCF( am + " is a type of AnnotationMirror not handled by getVariableSlot." );
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Slot> getSlots() {
        return new ArrayList<Slot>(this.slots.values());
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Slot> getVariableSlots() {
        List<Slot> varSlots = new ArrayList<>();
        for (Slot slot : slots.values()) {
            if (slot.isVariable()) {
                varSlots.add(slot);
            }
        }
        return varSlots;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<ConstantSlot> getConstantSlots() {
        List<ConstantSlot> constants = new ArrayList<>();
        for (Slot slot : slots.values()) {
            if (slot.isConstant()) {
                constants.add((ConstantSlot) slot);
            }
        }
        return constants;
    }

    @Override
    public int getNumberOfSlots() {
        return nextId - 1;
    }

    @Override
    public VariableSlot createVariableSlot(AnnotationLocation location) {
        VariableSlot variableSlot;
        if (location.getKind() == AnnotationLocation.Kind.MISSING) {
            // Don't cache slot for MISSING LOCATION. Just create a new one and return.
            variableSlot = new VariableSlot(nextId(), location);
            addToSlots(variableSlot);
        } else if (locationCache.containsKey(location)) {
            Slot slot = locationCache.get(location);
            if (!(slot instanceof VariableSlot)) {
                throw new BugInCF("Previous slot created for location " + location
                        + " is not a VariableSlot.");
            }
            variableSlot = (VariableSlot) slot;
        } else {
            variableSlot = new VariableSlot(nextId(), location);
            addToSlots(variableSlot);
            locationCache.put(location, variableSlot);
        }
        return variableSlot;
    }

    @Override
    public RefinementVariableSlot createRefinementVariableSlot(AnnotationLocation location,
            Slot refined) {
        RefinementVariableSlot refinementVariableSlot;
        if (location.getKind() == AnnotationLocation.Kind.MISSING) {
            // Don't cache slot for MISSING LOCATION. Just create a new one and return.
            refinementVariableSlot = new RefinementVariableSlot(nextId(), location, refined);
            addToSlots(refinementVariableSlot);
        } else if (locationCache.containsKey(location)) {
            Slot slot = locationCache.get(location);
            if (!(slot instanceof RefinementVariableSlot)) {
                throw new BugInCF("Previous slot created for location " + location
                        + " is not a RefinementVariableSlot.");
            }
            refinementVariableSlot = (RefinementVariableSlot) slot;
        } else {
            refinementVariableSlot = new RefinementVariableSlot(nextId(), location, refined);
            addToSlots(refinementVariableSlot);
            locationCache.put(location, refinementVariableSlot);
        }
        return refinementVariableSlot;
    }

    @Override
    public ConstantSlot createConstantSlot(AnnotationMirror am) {
        ConstantSlot constantSlot;
        if (constantCache.containsKey(am)) {
            constantSlot = constantCache.get(am);
        } else {
            constantSlot = new ConstantSlot(nextId(), am);
            addToSlots(constantSlot);
            constantCache.put(am, constantSlot);
        }
        return constantSlot;
    }

    @Override
    public VPAVariableSlot createVPAVariableSlot(Slot receiver, Slot declared) {
        VPAVariableSlot vpaVariableSlot;
        Pair<Slot, Slot> pair = Pair.of(receiver, declared);
        if (vpaSlotCache.containsKey(pair)) {
            vpaVariableSlot = vpaSlotCache.get(pair);
        } else {
            // vpaVariableSlot = new VPAVariableSlot(null, nextId(), receiver, declared);
            vpaVariableSlot = new VPAVariableSlot(nextId());
            addToSlots(vpaVariableSlot);
            vpaSlotCache.put(pair, vpaVariableSlot);
        }
        return vpaVariableSlot;
    }

    @Override
    public LUBVariableSlot createLubVariableSlot(Slot left, Slot right) {
        // Order of two ingredient slots doesn't matter, but for simplicity, we still use pair.
        LUBVariableSlot lubVariableSlot;
        Pair<Slot, Slot> pair = Pair.of(left, right);
        if (lubSlotCache.containsKey(pair)) {
            lubVariableSlot = lubSlotCache.get(pair);
        } else {
            // TODO: We need a non-null location in the future for better debugging outputs
            lubVariableSlot = new LUBVariableSlot(nextId(), null, left, right);
            addToSlots(lubVariableSlot);
            lubSlotCache.put(pair, lubVariableSlot);
        }
        return lubVariableSlot;
    }

    @Override
    public ExistentialVariableSlot createExistentialVariableSlot(Slot potentialSlot, Slot alternativeSlot) {
        ExistentialVariableSlot existentialVariableSlot;
        Pair<Slot, Slot> pair = Pair.of(potentialSlot, alternativeSlot);
        if (existentialSlotCache.containsKey(pair)) {
            existentialVariableSlot = existentialSlotCache.get(pair);
        } else {
            existentialVariableSlot = new ExistentialVariableSlot(nextId(), potentialSlot, alternativeSlot);
            addToSlots(existentialVariableSlot);
            existentialSlotCache.put(pair, existentialVariableSlot);
        }
        return existentialVariableSlot;
    }

    @Override
    public ArithmeticVariableSlot createArithmeticVariableSlot(AnnotationLocation location) {
        if (location == null || location.getKind() == AnnotationLocation.Kind.MISSING) {
            throw new BugInCF(
                    "Cannot create an ArithmeticVariableSlot with a missing annotation location.");
        }

        // create the arithmetic var slot if it doesn't exist for the given location
        if (!arithmeticSlotCache.containsKey(location)) {
            ArithmeticVariableSlot slot = new ArithmeticVariableSlot(nextId(), location);
            addToSlots(slot);
            arithmeticSlotCache.put(location, slot);
            return slot;
        }

        return getArithmeticVariableSlot(location);
    }

    @Override
    public ArithmeticVariableSlot getArithmeticVariableSlot(AnnotationLocation location) {
        if (location == null || location.getKind() == AnnotationLocation.Kind.MISSING) {
            throw new BugInCF(
                    "ArithmeticVariableSlots are never created with a missing annotation location.");
        }
        if (!arithmeticSlotCache.containsKey(location)) {
            return null;
        } else {
            return arithmeticSlotCache.get(location);
        }
    }

    @Override
    public AnnotationMirror createEquivalentVarAnno(AnnotationMirror realQualifier) {
        ConstantSlot varSlot = createConstantSlot(realQualifier);
        return getAnnotation(varSlot);
    }
}
