import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 练习 2：复现 Arrays.asList(new int[]{1,2,3}).size() == 1，并给出正确写法。
 *
 * 原因：Arrays.asList 的形参是 T... （可变参数），T 必须是**引用类型**。
 *       传入 int[] 时，整个数组被当成"一个 T"，T 推断为 int[]，
 *       于是得到 List<int[]>，里面只有一个元素 —— 那个数组本身。
 *
 *       传入 Integer[] 或直接传 1,2,3 才符合直觉。
 */
public class AsListIntBug {

    public static void main(String[] args) {
        System.out.println("=== 坑：传 int[] ===");

        int[] nums = {1, 2, 3};
        List<int[]> bad = Arrays.asList(nums);
        System.out.println("  bad.size()        = " + bad.size() + "     ← 期望 3，实际 1");
        System.out.println("  bad.get(0) 的类型  = " + bad.get(0).getClass().getSimpleName());
        System.out.println("  Arrays.toString   = " + Arrays.toString(bad.get(0)));
        System.out.println("  泛型实际是         = List<int[]>（不是 List<Integer>）");

        System.out.println();
        System.out.println("=== 对照：传 Integer[] 或裸字面量 ===");

        Integer[] boxed = {1, 2, 3};
        List<Integer> ok1 = Arrays.asList(boxed);
        System.out.println("  Arrays.asList(new Integer[]{1,2,3}).size() = " + ok1.size());

        List<Integer> ok2 = Arrays.asList(1, 2, 3);
        System.out.println("  Arrays.asList(1, 2, 3).size()              = " + ok2.size());

        System.out.println();
        System.out.println("=== 正确写法（int[] → List<Integer>）===");

        // ① 流：boxed 装箱
        List<Integer> w1 = Arrays.stream(nums).boxed().collect(Collectors.toList());
        System.out.println("  ① stream().boxed()      → " + w1 + "  size=" + w1.size());

        // ② 循环手动装
        List<Integer> w2 = new ArrayList<>(nums.length);
        for (int v : nums) {
            w2.add(v);
        }
        System.out.println("  ② 循环 add               → " + w2 + "  size=" + w2.size());

        // ③ 想要"定长视图"就直接改写成 Integer[]
        System.out.println("  ③ 直接把声明改成 Integer[]，再 Arrays.asList");

        System.out.println();
        System.out.println("=== 另一个坑：Arrays.asList 返回的是定长视图 ===");
        List<Integer> view = Arrays.asList(1, 2, 3);
        view.set(0, 99);
        System.out.println("  set 之后 = " + view + "（可以改值，因为写的是原数组）");
        try {
            view.add(4);
        } catch (UnsupportedOperationException e) {
            System.out.println("  add(4)   → UnsupportedOperationException（长度固定）");
        }
        System.out.println("  需要可变：new ArrayList<>(Arrays.asList(...)) 或 List.copyOf / new ArrayList<>(List.of(...))");
    }
}
