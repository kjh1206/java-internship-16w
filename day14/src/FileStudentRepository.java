import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用 CSV 文件实现的学生仓库。
 *
 * <p>存储策略：内存里放一份 {@link LinkedHashMap}，文件只是"快照"。
 * 每次增删改之后整份重写文件（数据量小的时候最简单、最不容易出错）。
 *
 * <p>为什么是 LinkedHashMap 而不是 HashMap：它保证遍历顺序 = 插入顺序，
 * 列表显示时学生不会随机跳来跳去。
 */
public class FileStudentRepository implements StudentRepository {
    private final Path file;
    // key = 学号，value = 学生对象；学号天然唯一，所以用 Map 而不是 List
    private final Map<Integer, Student> data = new LinkedHashMap<>();

    public FileStudentRepository(Path file) {
        this.file = file;
        load();   // 构造时就加载，外部拿到对象时数据已经就绪
    }

    /** 启动时读文件。单行解析失败只跳过该行并打印警告，不让整个程序起不来。 */
    private void load() {
        if (!Files.exists(file)) return;   // 第一次运行没有文件，正常，直接空着
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;   // 容忍空行
                try {
                    Student s = Student.fromCsv(line);
                    data.put(s.getId(), s);
                } catch (IllegalArgumentException e) {
                    System.out.println("[警告] 跳过坏行: " + line + " → " + e.getMessage());
                }
            }
        } catch (IOException e) {
            // 接口方法没有声明 IOException，这里包成非受检异常往外抛
            throw new UncheckedIOException(e);
        }
    }

    /** 把内存里的全部数据覆盖写回文件。 */
    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Student s : data.values()) lines.add(s.toCsv());
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);   // 覆盖写，不是追加
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** 返回新 List：调用方改它（增删元素）不会动到仓库内部。 */
    @Override public List<Student> findAll() { return new ArrayList<>(data.values()); }

    /** Map.get 查不到返回 null，正好符合接口约定。 */
    @Override public Student findById(int id) { return data.get(id); }

    @Override public void add(Student s) {
        // 先检查再放，防止同一个学号被覆盖 —— 覆盖是静默的数据丢失，比报错严重
        if (data.containsKey(s.getId())) {
            throw new IllegalStateException("学号已存在: " + s.getId());
        }
        data.put(s.getId(), s);
        persist();   // 内存改了立刻落盘，保证"看到的就是存下来的"
    }

    @Override public void update(Student s) {
        // 修改必须建立在已存在的前提上，否则就是变相新增，语义会乱
        if (!data.containsKey(s.getId())) {
            throw new IllegalStateException("学号不存在: " + s.getId());
        }
        data.put(s.getId(), s);
        persist();
    }

    @Override public boolean delete(int id) {
        // Map.remove 返回被删的值；返回 null 说明本来就没有
        if (data.remove(id) == null) return false;
        persist();
        return true;
    }
}
