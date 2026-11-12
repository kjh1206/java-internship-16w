//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        Level type;
        type= Level.ofCode(1);
        System.out.println(type);
        System.out.println(Level.valueOf("MID"));
        System.out.println(Level.MID.name());
        System.out.println(Level.MID==Level.valueOf("MID"));
        int i=127;
        System.out.println(Integer.valueOf(i));
        System.out.println(i==Integer.valueOf(i));
        System.out.println(Integer.valueOf(i).equals(i));
        i=128;
        System.out.println(Integer.valueOf(i));
        System.out.println(Integer.valueOf(i)==Integer.valueOf(i));
        System.out.println(Integer.valueOf(i).equals(i));
        i=-129;
        System.out.println(Integer.valueOf(i));
        System.out.println(Integer.valueOf(i)==Integer.valueOf(i));
        System.out.println(Integer.valueOf(i).equals(i));
    }
}
enum Level {
    LOW(1, "低"), MID(2, "中"), HIGH(3, "高");

    private final int code;
    private final String label;

    Level(int code, String label) {   // 构造器默认 private，写不写都一样
        this.code = code;
        this.label = label;
    }

    public int getCode() { return code; }
    public String getLabel() { return label; }

    public static Level ofCode(int code) {          // 反查：数据库里的 2 → MID
        for (Level l : values()) {
            if (l.code == code) return l;
        }
        throw new IllegalArgumentException("未知的 code: " + code);
    }

    @Override
    public String toString() { return label; }      // 打印时显示中文
}