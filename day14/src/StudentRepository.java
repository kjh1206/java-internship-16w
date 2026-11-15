import java.util.List;

public interface StudentRepository {
    List<Student> findAll();

    Student findById(int id);

    void add(Student s);

    void update(Student s);

    boolean delete(int id);
}
