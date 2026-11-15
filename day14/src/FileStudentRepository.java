import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileStudentRepository implements StudentRepository {
    private final Path file;
    private final Map<Integer, Student> data = new LinkedHashMap<>();

    public FileStudentRepository(Path file) {
        this.file = file;
        load();
    }

    private void load() {
        if (!Files.exists(file)) return;
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                try {
                    Student s = Student.fromCsv(line);
                    data.put(s.getId(), s);
                } catch (IllegalArgumentException e) {
                    System.out.println("[警告] 跳过坏行: " + line + " → " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Student s : data.values()) lines.add(s.toCsv());
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override public List<Student> findAll() { return new ArrayList<>(data.values()); }

    @Override public Student findById(int id) { return data.get(id); }

    @Override public void add(Student s) {
        if (data.containsKey(s.getId())) {
            throw new IllegalStateException("学号已存在: " + s.getId());
        }
        data.put(s.getId(), s);
        persist();
    }

    @Override public void update(Student s) {
        if (!data.containsKey(s.getId())) {
            throw new IllegalStateException("学号不存在: " + s.getId());
        }
        data.put(s.getId(), s);
        persist();
    }

    @Override public boolean delete(int id) {
        if (data.remove(id) == null) return false;
        persist();
        return true;
    }
}
