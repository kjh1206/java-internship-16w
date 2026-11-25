import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 练习 2：复现 CME（ConcurrentModificationException）
 *
 * <p>场景 A：for-each 遍历 keySet()，循环里 remove → 抛 CME，打印完整栈。
 * <p>场景 B：改成 entrySet().removeIf(...) → 不抛。
 * <p>场景 C：用 Iterator.remove() → 不抛。
 * <p>场景 D：删"最后一个"元素 → 可能不抛（fail-fast 不保证一定抛）。
 */
public class CmeRepro {

    public static void main(String[] args) {
        caseA_ForEachRemove();
        caseB_RemoveIf();
        caseC_IteratorRemove();
        caseD_RemoveLast();
    }

    static Map<String, Integer> newMap() {
        Map<String, Integer> m = new HashMap<>();
        m.put("tmp1", 1);
        m.put("keep", 2);
        m.put("tmp2", 3);
        m.put("keep2", 4);
        return m;
    }

    /** 场景 A：for-each 里 remove → CME */
    static void caseA_ForEachRemove() {
        System.out.println("=== A：for-each + remove（预期抛 CME）===");
        Map<String, Integer> m = newMap();
        try {
            for (String k : m.keySet()) {
                if (k.startsWith("tmp")) {
                    m.remove(k);
                }
            }
            System.out.println("  没有抛异常，size=" + m.size());
        } catch (ConcurrentModificationException e) {
            System.out.println("  捕获到：" + e.getClass().getName() + "（getMessage()=" + e.getMessage() + "）");
            StackTraceElement[] st = e.getStackTrace();
            for (int i = 0; i < Math.min(5, st.length); i++) {
                System.out.println("    at " + st[i]);
            }
        }
        System.out.println();
    }

    /** 场景 B：entrySet().removeIf → 不抛 */
    static void caseB_RemoveIf() {
        System.out.println("=== B：entrySet().removeIf（预期不抛）===");
        Map<String, Integer> m = newMap();
        try {
            m.entrySet().removeIf(e -> e.getKey().startsWith("tmp"));
            System.out.println("  成功，剩余 = " + new TreeMap<>(m).keySet());
        } catch (ConcurrentModificationException e) {
            System.out.println("  意外抛出：" + e);
        }
        System.out.println();
    }

    /** 场景 C：iterator.remove() → 不抛 */
    static void caseC_IteratorRemove() {
        System.out.println("=== C：iterator.remove()（预期不抛）===");
        Map<String, Integer> m = newMap();
        try {
            Iterator<String> it = m.keySet().iterator();
            while (it.hasNext()) {
                if (it.next().startsWith("tmp")) {
                    it.remove();
                }
            }
            System.out.println("  成功，剩余 = " + new TreeMap<>(m).keySet());
        } catch (ConcurrentModificationException e) {
            System.out.println("  意外抛出：" + e);
        }
        System.out.println();
    }

    /** 场景 D：删最后一个元素 → 可能不抛 */
    static void caseD_RemoveLast() {
        System.out.println("=== D：删最后一个 / 倒数第二个（验证 fail-fast 不保证）===");

        System.out.print("  5 个元素，删最后一个：");
        System.out.println(runRemove(5, 5));

        System.out.print("  5 个元素，删倒数第二个：");
        System.out.println(runRemove(5, 4));

        System.out.print("  2 个元素，删第一个（它就是倒数第二个）：");
        System.out.println(runRemove(2, 1));
        System.out.println();
        System.out.println("  规律：不抛 ⟺ 删完 cursor == size ⟺ 删的是原表的倒数第二个");
    }

    /** 构造 n 个元素，遍历时删第 k 个（1-based），返回结果描述 */
    static String runRemove(int n, int k) {
        // 用 LinkedHashMap 保证遍历顺序与插入顺序一致（HashMap 也行，但顺序不确定）
        Map<Integer, Integer> m = new java.util.LinkedHashMap<>();
        for (int i = 1; i <= n; i++) {
            m.put(i, i);
        }
        try {
            int idx = 0;
            for (Integer key : m.keySet()) {
                idx++;
                if (idx == k) {
                    m.remove(key);
                }
            }
            return "不抛，剩余 " + m.keySet();
        } catch (ConcurrentModificationException e) {
            return "抛 CME";
        }
    }
}
