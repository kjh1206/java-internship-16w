import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * MyArrayList 的验收测试：覆盖题目要求的四种情况，外加几个边界。
 *
 * <p>为什么用反射：容量（data.length）、末尾空槽这些是私有字段，
 * 不反射就没法验证"扩容确实是 1.5 倍""最后一格确实置了 null"。
 * 平时写业务代码不要这么干，但写测试验证内部实现时它很好用。
 */
public class MyArrayListTest {
    static int pass = 0, fail = 0;

    public static void main(String[] args) throws Exception {
        case1_grow();
        case2_insertMiddle();
        case3_removeMiddle();
        case4_cmeOnForeachRemove();
        case5_iteratorRemoveOk();
        case6_setDoesNotThrow();
        case7_bounds();

        System.out.println("\n通过 " + pass + " 项，失败 " + fail + " 项");
        if (fail > 0) System.exit(1);
    }

    /** ① 加满触发扩容：容量序列必须是 10 → 15 → 22 → 33 → 49（1.5 倍）。 */
    static void case1_grow() throws Exception {
        MyArrayList<Integer> l = new MyArrayList<>();
        List<Integer> seq = new ArrayList<>();
        int last = 0;
        for (int i = 1; i <= 40; i++) {
            l.add(i);
            int c = capacity(l);
            if (c != last) { seq.add(c); last = c; }
        }
        check("加满触发扩容（1.5 倍）",
                seq.equals(List.of(10, 15, 22, 33, 49)),
                "容量序列 = " + seq + "，size = " + l.size());
    }

    /** ② 中间插入：用 System.arraycopy 搬元素，index 之后整体后移。 */
    static void case2_insertMiddle() {
        MyArrayList<Integer> l = new MyArrayList<>();
        l.add(1); l.add(2); l.add(3); l.add(4);
        l.add(2, 99);
        check("中间插入", l.toString().equals("[1, 2, 99, 3, 4]"), l.toString());

        l.add(0, 0);              // 头插
        l.add(l.size(), 5);       // 尾插（index == size 合法）
        check("头插/尾插", l.toString().equals("[0, 1, 2, 99, 3, 4, 5]"), l.toString());
    }

    /** ③ 删中间：返回值是旧元素、后面整体前移、最后那个槽位必须置 null。 */
    static void case3_removeMiddle() throws Exception {
        MyArrayList<Integer> l = new MyArrayList<>();
        for (int i = 1; i <= 5; i++) l.add(i);      // [1, 2, 3, 4, 5]
        Integer removed = l.remove(2);              // 删 3
        check("删除返回旧值", removed != null && removed == 3, "返回 " + removed);
        check("删除后元素前移", l.toString().equals("[1, 2, 4, 5]"), l.toString());

        Object[] raw = rawData(l);
        check("末尾槽位置 null（防内存泄漏）", raw[4] == null,
                "data[4] = " + raw[4] + "，size = " + l.size());
    }

    /**
     * ④ for-each 遍历时 remove → 必须抛 ConcurrentModificationException。
     *
     * <p>注意这里有个坑（我第一次就踩了）：CME 是在 {@code next()} 里检查的，
     * 而 {@code hasNext()} 只是 {@code cursor != size}。如果你在**最后一个元素**
     * 上 remove，size 会减到和 cursor 相等，循环直接结束，压根不会调用下一次 next()，
     * 于是"边遍历边删"竟然不报错。要用 4 个元素、在遍历早期删，才能稳定触发。
     */
    static void case4_cmeOnForeachRemove() {
        MyArrayList<Integer> l = new MyArrayList<>();
        for (int i = 1; i <= 4; i++) l.add(i);
        String msg = "";
        boolean thrown = false;
        try {
            for (Integer x : l) {
                if (x == 1) l.remove(0);            // 边遍历边改结构
            }
        } catch (ConcurrentModificationException e) {
            thrown = true;
            msg = "抛出 " + e.getClass().getSimpleName();
        }
        check("foreach 中 remove 抛 CME", thrown, msg.isEmpty() ? "没有抛异常（错误）" : msg);
    }

    /** ⑤ 用 iterator.remove() 删除是安全的：它自己会同步 expectedModCount。 */
    static void case5_iteratorRemoveOk() {
        MyArrayList<Integer> l = new MyArrayList<>();
        for (int i = 1; i <= 5; i++) l.add(i);
        Iterator<Integer> it = l.iterator();
        while (it.hasNext()) {
            if (it.next() % 2 == 0) it.remove();    // 删 2、4
        }
        check("iterator.remove 安全", l.toString().equals("[1, 3, 5]"), l.toString());
    }

    /** ⑥ set 只改值不改结构，modCount 不变，所以 for-each 里 set 不会抛。 */
    static void case6_setDoesNotThrow() {
        MyArrayList<Integer> l = new MyArrayList<>();
        for (int i = 1; i <= 3; i++) l.add(i);
        boolean thrown = false;
        try {
            for (Integer x : l) l.set(0, 100);
        } catch (ConcurrentModificationException e) {
            thrown = true;
        }
        check("foreach 中 set 不抛 CME", !thrown, thrown ? "错误地抛了 CME" : "");
    }

    /** ⑦ 越界检查。 */
    static void case7_bounds() {
        MyArrayList<Integer> l = new MyArrayList<>();
        l.add(1);
        check("get(-1) 抛 IOOBE", throwsIOOBE(() -> l.get(-1)), "");
        check("get(size) 抛 IOOBE", throwsIOOBE(() -> l.get(1)), "");
        check("add(2) 抛 IOOBE", throwsIOOBE(() -> l.add(2, 9)), "");
    }

    // ------------------------------------------------------------ 工具方法

    interface Action { void run() throws Exception; }

    static boolean throwsIOOBE(Action a) {
        try {
            a.run();
            return false;
        } catch (IndexOutOfBoundsException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static int capacity(MyArrayList<?> list) throws Exception {
        return rawData(list).length;
    }

    static Object[] rawData(MyArrayList<?> list) throws Exception {
        Field f = MyArrayList.class.getDeclaredField("data");
        f.setAccessible(true);
        return (Object[]) f.get(list);
    }

    static void check(String name, boolean ok, String detail) {
        if (ok) {
            pass++;
            System.out.println("[OK]   " + name + (detail.isEmpty() ? "" : "   （" + detail + "）"));
        } else {
            fail++;
            System.out.println("[FAIL] " + name + "   " + detail);
        }
    }
}
