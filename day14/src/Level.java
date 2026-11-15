/**
 * 学历层次枚举。
 *
 * <p>为什么用枚举，而不是 {@code int code = 1} 或字符串 "本科生"？
 * <ul>
 *   <li>取值被编译器限定在 UNDERGRAD / GRAD 两个里，写错立刻编译报错；</li>
 *   <li>每个常量能携带数据：code 用于落盘（数字稳定），label 用于显示（中文友好）；</li>
 *   <li>switch 里用它编译器能检查分支，IDE 也会提示补全。</li>
 * </ul>
 */
public enum Level {
    // 枚举常量必须写在类的最前面。括号里的就是下面构造器的参数，
    // 等价于在类加载时 new 了两个 Level 对象，且永远只有这两个。
    UNDERGRAD(1, "本科生"),
    GRAD(2, "研究生");

    private final int code;      // 存文件/传网络用数字，改动它等于改数据格式，要慎重
    private final String label;  // 给人看的中文名

    // 枚举的构造器天然是 private，只能被上面的常量调用，外部 new 不出来
    Level(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() { return code; }

    public String getLabel() { return label; }

    /**
     * 把从文件里读回来的数字还原成枚举。
     * 查不到就抛异常 —— 宁可启动时报错，也不要静默产生一个"非法层次"的学生对象。
     */
    public static Level ofCode(int code) {
        for (Level l : values()) if (l.code == code) return l;
        throw new IllegalArgumentException("未知的层次代码: " + code);
    }

    /** 打印学生信息时直接输出中文，而不是 UNDERGRAD。 */
    @Override public String toString() { return label; }
}
