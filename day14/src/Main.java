import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * 控制台入口：菜单 + 输入 + 把异常翻译成人话。
 *
 * <p>职责边界很重要：
 * 校验规则在 {@link Student} 里，存储细节在 {@link FileStudentRepository} 里，
 * 这里只负责"读输入、调方法、打印结果"。所以改校验规则不用动这个文件。
 */
public class Main {
    // 全局唯一的 Scanner：如果每个方法各 new 一个，第一个会把输入流里的数据读走，
    // 后面再读就会抛 NoSuchElementException 或者读到空串。
    private static final Scanner SC = new Scanner(System.in);
    private static final StudentRepository REPO = new FileStudentRepository(Path.of("students.csv"));

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n1 添加  2 列表  3 按学号查  4 修改  5 删除  6 退出");
            // switch 箭头写法不会"贯穿"到下一个分支，也不需要写 break
            switch (readInt("请选择: ")) {
                case 1 -> add();
                case 2 -> list();
                case 3 -> find();
                case 4 -> update();
                case 5 -> delete();
                case 6 -> { System.out.println("再见"); return; }   // 退出整个程序
                default -> System.out.println("没有这个选项，请输入 1~6");
            }
        }
    }

    private static void add() {
        int id = readInt("学号: ");
        String name = readLine("姓名: ");
        int age = readInt("年龄: ");
        int level = readInt("层次(1 本科 / 2 研究生): ");
        double grade = readDouble("成绩: ");
        try {
            Student s = new Student(id, name, age, level, grade);
            REPO.add(s);
            System.out.println("[成功] 已添加: " + s);
        } catch (InvalidFieldException e) {
            // 受检异常：字段级错误，带字段名一起提示
            System.out.println("[失败] " + e.getField() + " " + e.getMessage());
        } catch (RuntimeException e) {
            // 学号重复、层次代码非法等
            System.out.println("[失败] " + e.getMessage());
        }
    }

    private static void list() {
        List<Student> all = REPO.findAll();
        if (all.isEmpty()) { System.out.println("（还没有学生）"); return; }
        // %-6s 等表示左对齐占 6 格，让列大致对齐（中文宽度不完美，先够用）
        System.out.printf("%-6s %-12s %-5s %-8s %-6s%n", "学号", "姓名", "年龄", "层次", "成绩");
        for (Student s : all) {
            System.out.printf("%-6d %-12s %-5d %-8s %-6.1f%n",
                    s.getId(), s.getName(), s.getAge(), s.getLevel(), s.getGrade());
        }
        System.out.println("共 " + all.size() + " 条");
    }

    private static void find() {
        int id = readInt("要查的学号: ");
        Student s = REPO.findById(id);
        // findById 查不到返回 null，这里直接判空，不用 try-catch
        System.out.println(s == null ? "没有学号 " + id + " 的学生" : s.toString());
    }

    private static void update() {
        int id = readInt("要修改的学号: ");
        Student s = REPO.findById(id);
        if (s == null) { System.out.println("没有学号 " + id + " 的学生"); return; }
        try {
            // 逐个字段问，直接回车表示"这项不改"
            String name = readLine("新姓名（回车跳过）: ");
            if (!name.isBlank()) s.setName(name);
            String age = readLine("新年龄（回车跳过）: ");
            if (!age.isBlank()) s.setAge(Integer.parseInt(age.trim()));
            String level = readLine("新层次 1/2（回车跳过）: ");
            if (!level.isBlank()) s.setLevel(Integer.parseInt(level.trim()));
            String grade = readLine("新成绩（回车跳过）: ");
            if (!grade.isBlank()) s.setGrade(Double.parseDouble(grade.trim()));
            REPO.update(s);
            System.out.println("[成功] 已更新: " + s);
        } catch (NumberFormatException e) {
            System.out.println("[失败] 输入不是合法数字");
        } catch (InvalidFieldException e) {
            System.out.println("[失败] " + e.getField() + " " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("[失败] " + e.getMessage());
        }
    }

    private static void delete() {
        int id = readInt("要删除的学号: ");
        // delete 返回 boolean，直接拿去选提示语
        System.out.println(REPO.delete(id) ? "[成功] 已删除 " + id : "没有学号 " + id + " 的学生");
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return SC.nextLine();
    }

    /**
     * 读一个整数，读不对就重新问，绝不把非数字塞给 parseInt。
     * 这比 nextInt() 好：nextInt 输入字母会直接抛 InputMismatchException 崩掉程序。
     */
    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt).trim());
            } catch (NumberFormatException e) {
                System.out.println("请输入整数");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(prompt).trim());
            } catch (NumberFormatException e) {
                System.out.println("请输入数字");
            }
        }
    }
}
