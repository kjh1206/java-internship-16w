public enum Level {
    UNDERGRAD(1, "本科生"), GRAD(2, "研究生");

    private final int code;
    private final String label;
    Level(int code, String label) { this.code = code; this.label = label; }
    public int getCode() { return code; }
    public String getLabel() { return label; }

    public static Level ofCode(int code) {
        for (Level l : values()) if (l.code == code) return l;
        throw new IllegalArgumentException("未知的层次代码: " + code);
    }

    @Override public String toString() { return label; }
}
