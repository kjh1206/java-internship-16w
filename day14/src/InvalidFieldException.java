/**
 * 字段校验失败的异常 —— 这是"受检异常"（extends Exception），
 * 意思是：谁调用可能抛它的方法，编译器就强制谁处理（catch 或再往上声明 throws）。
 *
 * <p>为什么不直接用 IllegalArgumentException？
 * 因为它只带一句 message，上层想知道"到底哪个字段错了、错的值是什么"，
 * 只能去解析字符串。这里把 field 和 value 单独存成字段，调用方可以这样写：
 * <pre>{@code
 * catch (InvalidFieldException e) {
 *     System.out.println(e.getField() + " 字段的值 " + e.getValue() + " 不合法：" + e.getMessage());
 * }
 * }</pre>
 */
public class InvalidFieldException extends Exception {
    // Exception 实现了 Serializable；显式写死版本号，避免以后改类结构导致反序列化失败
    private static final long serialVersionUID = 1L;

    private final String field;  // 哪个字段出错，例如 "age" / "grade"
    private final String value;  // 出错的值。存 String 而不是 Object：方便打印，也不会持有调用方对象

    public InvalidFieldException(String field, Object value, String message) {
        super(message);                       // message 交给父类，getMessage() 才能取到
        this.field = field;
        this.value = String.valueOf(value);   // 传 null 也不会 NPE，会变成 "null"
    }

    public String getField() { return field; }

    public String getValue() { return value; }

    /** 调试/日志用：一眼看全所有信息，别让 toString 输出成 Object@1a2b3c。 */
    @Override public String toString() {
        return "InvalidFieldException{field=" + field + ", value=" + value + ", message=" + getMessage() + "}";
    }
}
