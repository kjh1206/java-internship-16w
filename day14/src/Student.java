import java.util.Locale;

public class Student {
    static final int MIN_AGE = 1, MAX_AGE = 150;
    static final double MIN_GRADE = 0, MAX_GRADE = 100;

    private final int id;
    private String name;
    private int age;
    private Level level;
    private double grade;

    public Student(int id, String name, int age, int levelCode, double grade) throws InvalidFieldException {
        if (id <= 0) throw new IllegalArgumentException("学号必须是正整数");
        this.id = id;
        setName(name);
        setAge(age);
        setGrade(grade);
        setLevel(levelCode);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("姓名不能为空");
        if (name.length() > 20) throw new IllegalArgumentException("姓名过长（不超过 20 字）");
        if (name.contains(",")) throw new IllegalArgumentException("姓名不能包含逗号（会破坏 CSV）");
        this.name = name.trim();
    }

    public void setAge(int age) throws InvalidFieldException {
        if (age < MIN_AGE || age > MAX_AGE) throw new InvalidFieldException("age", age, "年龄应在 1~150 之间");
        this.age = age;
    }

    public void setGrade(double grade) throws InvalidFieldException {
        if (Double.isNaN(grade) || Double.isInfinite(grade)) throw new InvalidFieldException("grade", grade, "成绩必须是有限数字");
        if (grade < MIN_GRADE || grade > MAX_GRADE) throw new InvalidFieldException("grade", grade, "成绩应在 0~100 之间");
        this.grade = grade;
    }

    public void setLevel(int code) { this.level = Level.ofCode(code); }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public Level getLevel() { return level; }
    public double getGrade() { return grade; }

    public String toCsv() { return id + "," + name + "," + age + "," + level.getCode() + "," + grade; }

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

    @Override public String toString() {
        return String.format(Locale.ROOT, "Student{id=%d, name='%s', age=%d, level=%s, grade=%.1f}",
                id, name, age, level, grade);
    }
}
