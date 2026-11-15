public class InvalidFieldException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String field;
    private final String value;

    public InvalidFieldException(String field, Object value, String message) {
        super(message);
        this.field = field;
        this.value = String.valueOf(value);
    }

    public String getField() { return field; }

    public String getValue() { return value; }

    @Override public String toString() {
        return "InvalidFieldException{field=" + field + ", value=" + value + ", message=" + getMessage() + "}";
    }
}
