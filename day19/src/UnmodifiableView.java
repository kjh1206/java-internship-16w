import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 练习 3：Collections.unmodifiableList 包一个 list，然后改原 list，
 *         打印只读视图，验证它"跟着变"。
 *
 * 关键概念：unmodifiableList 返回的是**视图（view）**，不是拷贝。
 *           它只是把"写操作"全部拦下来，读操作直接转发给底层 list。
 */
public class UnmodifiableView {

    public static void main(String[] args) {
        System.out.println("=== 视图 vs 拷贝 ===");

        List<String> base = new ArrayList<>(List.of("a", "b"));
        List<String> view = Collections.unmodifiableList(base);

        System.out.println("  初始 base = " + base);
        System.out.println("  初始 view = " + view);

        base.add("c");
        System.out.println("\n  base.add(\"c\") 之后：");
        System.out.println("    base = " + base);
        System.out.println("    view = " + view + "   ← 视图跟着变了（没有拷贝）");

        base.remove("a");
        System.out.println("\n  base.remove(\"a\") 之后：");
        System.out.println("    base = " + base);
        System.out.println("    view = " + view);

        System.out.println("\n  通过 view 改：");
        try {
            view.add("d");
        } catch (UnsupportedOperationException e) {
            System.out.println("    view.add(\"d\")  → UnsupportedOperationException");
        }
        try {
            view.set(0, "z");
        } catch (UnsupportedOperationException e) {
            System.out.println("    view.set(0,\"z\") → UnsupportedOperationException");
        }
        try {
            view.remove(0);
        } catch (UnsupportedOperationException e) {
            System.out.println("    view.remove(0)  → UnsupportedOperationException");
        }
        System.out.println("    view 的迭代器 remove 也一样：");
        try {
            var it = view.iterator();
            it.next();
            it.remove();
        } catch (UnsupportedOperationException e) {
            System.out.println("                    → UnsupportedOperationException");
        }

        System.out.println("\n=== 真正不可变：拷贝 ===");
        List<String> copy = List.copyOf(base);
        base.add("e");
        System.out.println("  base = " + base);
        System.out.println("  copy = " + copy + "   ← 拷贝不跟着变");

        System.out.println("\n=== 结论 ===");
        System.out.println("  unmodifiableList：只读视图，底层变了它也变（适合「对外只读、对内可写」）");
        System.out.println("  List.copyOf     ：真拷贝，彻底隔离（适合把可变数据「封住」再交给别人）");
        System.out.println("  如果想彻底防改，可以把 base 自己藏起来，只暴露视图；");
        System.out.println("  但注意视图本身仍然会随 base 变化 —— 它不是快照。");
    }
}
