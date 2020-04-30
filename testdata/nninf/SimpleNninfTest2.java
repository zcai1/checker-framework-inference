import nninf.qual.*;

// inconsistent.constructor.type: the qualifier on returning type is expected not to be top
@SuppressWarnings({"inconsistent.constructor.type"})
class SimpleNninfTest2 {
    private static final String id = "12345";

    public @NonNull String getId() {
        return id;
    }
}
