class MyResource implements AutoCloseable {
    private final String name;

    public MyResource(String name) {
        this.name = name;
        System.out.println("打开 " + name);      // 构造器打印，方便看顺序
    }

    @Override
    public void close() {
        System.out.println("关闭 " + name);      // 题目要求：close 里打印名字
    }
}
class BusinessException extends RuntimeException{
    private int code;
    private double balance;
    private double amount;
    public BusinessException(int code, String msg){
        super(msg);
        this.code=code;
    }

    public int getCode() {
        return code;
    }
    @Override public String toString() {
        return "BusinessException{code=" + code + ", msg=" + getMessage() + "}";
    }
}
class Bank {

    double withdraw(double balance, double amount) {
        if (amount <= 0) {
            throw new BusinessException(400, "取款金额必须大于 0");
        }
        if (amount > balance) {
            throw new BusinessException(402, "余额不足：余额 " + balance + "，需要 " + amount);
        }
        return balance - amount;
    }
}
public class Main {
    static int f() {
    int x = 10;
    try { return x; }
    finally { x = 20; System.out.println("finally x=" + x); }
}
static int readAge(String s){
        try {
                return Integer.parseInt(s);
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("年龄格式错误: " + s, e);
    }

}
    public static void main(String[] args) {
        Bank bank=new Bank();
        bank.withdraw(1,-1);
//        f();
//        try (MyResource r1 = new MyResource("A");
//             MyResource r2 = new MyResource("B")) {
//            System.out.println("  body 执行");
//        }
//        for (String s : new String[]{"20", "abc", "", "18.5"}) {
//            try {
//                System.out.println("输入 " + s + " → 返回 " + readAge(s));
//            } catch (IllegalArgumentException e) {
//                System.out.println("输入 " + s + " → 捕获 IllegalArgumentException");
//                System.out.println("   getMessage()      : " + e.getMessage());
//                System.out.println("   getCause()        : " + e.getCause());
//                System.out.println("   cause.getMessage(): " + e.getCause().getMessage());
//            }
//        }


//        try {
//            Integer.parseInt("abc");
//        } catch (NumberFormatException e) {
//            throw new IllegalStateException("解析失败", e);   // 第二个参数是 cause
//        }
//        try {
//            throw new ArithmeticException("x");
//        } catch (RuntimeException e) {      // 父类在前
//        } catch (ArithmeticException e) {   // 子类在后 → 永远到不了
//        }

    }
}