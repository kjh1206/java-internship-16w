import java.util.Locale;

/**
 * 学生实体。
 *
 * <p>这个类只负责一件事：<b>保证自己任何时候都是合法的</b>。
 * 手段是把校验写进 setter，构造器也去调 setter —— 校验逻辑只有一份，
 * 不管是 new 出来的还是后来改出来的，都不可能存在 age = -5 这种对象。
 *
 * <p>对比你原来的写法：构造器校验、setter 不校验，就会出现
 * "新建时合法、改完变非法"的对象。这叫做"不变式（invariant）只在构造时成立"，
 * 是对象设计里最常见的坑。
 */
public class Student {
    // 范围写成常量，校验和提示语共用一份，改的时候只改一处
    static final int MIN_AGE = 1, MAX_AGE = 150;
    static final double MIN_GRADE = 0, MAX_GRADE = 100;

    // id 是身份，创建后不允许改，所以 final 且没有 setter
    private final int id;
    private String name;
    private int age;
    private Level level;
    private double grade;

    public Student(int id, String name, int age, int levelCode, double grade) throws InvalidFieldException {
        if (id <= 0) throw new IllegalArgumentException("学号必须是正整数");
        this.id = id;          // final 字段只能在构造器里赋值
        setName(name);         // 复用 setter，校验不重复写
        setAge(age);
        setGrade(grade);
        setLevel(levelCode);
    }

    /** 姓名只接受非空、≤20 字、且不含逗号（逗号是 CSV 分隔符，含逗号会破坏文件格式）。 */
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("姓名不能为空");
        if (name.length() > 20) throw new IllegalArgumentException("姓名过长（不超过 20 字）");
        if (name.contains(",")) throw new IllegalArgumentException("姓名不能包含逗号（会破坏 CSV）");
        this.name = name.trim();   // 去掉首尾空格，" Tom " 和 "Tom" 视为同一个
    }

    public void setAge(int age) throws InvalidFieldException {
        if (age < MIN_AGE || age > MAX_AGE) throw new InvalidFieldException("age", age, "年龄应在 1~150 之间");
        this.age = age;
    }

    public void setGrade(double grade) throws InvalidFieldException {
        // 必须先单独拦 NaN / Infinity：NaN 和任何数比较都是 false，
        // 所以 NaN < 0 || NaN > 100 也是 false，光靠范围判断会漏过去。
        if (Double.isNaN(grade) || Double.isInfinite(grade)) throw new InvalidFieldException("grade", grade, "成绩必须是有限数字");
        if (grade < MIN_GRADE || grade > MAX_GRADE) throw new InvalidFieldException("grade", grade, "成绩应在 0~100 之间");
        this.grade = grade;
    }

    /** 传数字码，内部翻译成枚举；码不认识会抛 IllegalArgumentException。 */
    public void setLevel(int code) { this.level = Level.ofCode(code); }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public Level getLevel() { return level; }
    public double getGrade() { return grade; }

    /** 转成一行 CSV。字段用逗号连，顺序必须和 {@link #fromCsv(String)} 一致。 */
    public String toCsv() { return id + "," + name + "," + age + "," + level.getCode() + "," + grade; }

    /**
     * 从一行 CSV 还原成对象。
     *
     * <p>细节：
     * <ul>
     *   <li>{@code split(",", -1)} 的 -1 表示保留末尾空串，否则 "1,Tom,20,1," 会少一列；</li>
     *   <li>每个字段 trim()，兼容手改文件时多打的空格；</li>
     *   <li>把受检异常统一包成 IllegalArgumentException —— 文件里一行坏数据，
     *       调用方（load）只需要处理一种异常就能跳过它。</li>
     * </ul>
     */
    public static Student fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length != 5) throw new IllegalArgumentException("列数不是 5: " + line);
        try {
            return new Student(Integer.parseInt(p[0].trim()), p[1].trim(),
                    Integer.parseInt(p[2].trim()), Integer.parseInt(p[3].trim()),
                    Double.parseDouble(p[4].trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("数字格式错误: " + line);
        } catch (InvalidFieldException e) {
            throw new IllegalArgumentException("数据不合法: " + e.getMessage());
        }
    }

    /** 显示用。Locale.ROOT 防止某些地区把小数点打成逗号（1,5 会毁掉 CSV 观感）。 */
    @Override public String toString() {
        return String.format(Locale.ROOT, "Student{id=%d, name='%s', age=%d, level=%s, grade=%.1f}",
                id, name, age, level, grade);
    }
}
