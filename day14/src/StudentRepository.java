import java.util.List;

/**
 * 学生数据的"仓库"接口 —— 只声明能做什么，不管数据到底存在哪。
 *
 * <p>为什么要多这一层接口？
 * <ul>
 *   <li>现在实现是 {@link FileStudentRepository}（存 CSV 文件）；</li>
 *   <li>以后换成 MySQL，只要再写一个 {@code DbStudentRepository} 实现这 5 个方法，
 *       {@code Main} 一行都不用改 —— 这就是"面向接口编程"：上层依赖抽象，不依赖具体实现。</li>
 * </ul>
 *
 * <p>面试常问"为什么抽 Repository 层"，标准答案两点：
 * 可替换（换存储方式不影响业务代码）、可测试（测试时用内存里的假实现，不碰真文件）。
 */
public interface StudentRepository {

    /** 查全部。返回的是新 List，调用方改它不会影响仓库内部数据（保护内部状态）。 */
    List<Student> findAll();

    /** 按学号查。查不到返回 null，不抛异常 —— "没找到"是正常业务结果，不是错误。 */
    Student findById(int id);

    /** 新增。学号已存在时抛 IllegalStateException（重复是违反不变式，属于错误）。 */
    void add(Student s);

    /** 修改。学号不存在时抛 IllegalStateException。 */
    void update(Student s);

    /** 删除。返回 true = 真的删掉了，false = 本来就没有这个学号。 */
    boolean delete(int id);
}
