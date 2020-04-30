import nninf.qual.*;

// inconsistent.constructor.type: the qualifier on returning type is expected not to be top
@SuppressWarnings({"inconsistent.constructor.type"})
class SimpleNninfTest3 {
    private static final String id = "12345";

    public Character getId() {
        return id.charAt(0);
    }
}
