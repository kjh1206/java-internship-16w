import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 练习 1：验证扩容序列
 *
 * <p>用反射读 {@code table.length}，连续 put 100 个元素，打印每次扩容发生时的 (size, 容量, 阈值)。
 * 预期：第 13 个元素时容量 16→32（阈值 12），第 25 个元素时 32→64（阈值 24）。
 *
 * <p>运行需要：{@code java --add-opens java.base/java.util=ALL-UNNAMED -cp out ResizeSeries}
 */
public class ResizeSeries {

    static Field tableField;

    public static void main(String[] args) throws Exception {
        tableField = HashMap.class.getDeclaredField("table");
        tableField.setAccessible(true);

        Map<Integer, Integer> map = new HashMap<>();
        System.out.printf("初始（未 put）：%s%n", state(map));

        int lastCap = 0;
        for (int i = 1; i <= 100; i++) {
            map.put(i, i);
            int cap = capacity(map);
            if (cap != lastCap) {
                int thr = thresholdOf(cap);
                System.out.printf("  第 %3d 个元素 → 容量 %3d（阈值 %d）%s%n",
                        i, cap, thr, lastCap == 0 ? "  ← 首次分配" : "  ← 扩容");
                lastCap = cap;
            }
        }

        System.out.println();
        System.out.printf("最终：size=%d，容量=%d，阈值=%d%n",
                map.size(), capacity(map), thresholdOf(capacity(map)));

        // 校验预期
        System.out.println();
        System.out.println("=== 校验 ===");
        check("第 13 个元素扩容到 32", expansionAt(13) == 32, expansionAt(13));
        check("第 25 个元素扩容到 64", expansionAt(25) == 64, expansionAt(25));
        check("阈值 == 容量 × 0.75", thresholdOf(32) == 24, thresholdOf(32));
    }

    /** 重新建一个 map，返回第 n 次 put 之后的容量 */
    static int expansionAt(int n) throws Exception {
        Map<Integer, Integer> m = new HashMap<>();
        for (int i = 1; i <= n; i++) m.put(i, i);
        return capacity(m);
    }

    /** 阈值：容量较小时是 容量*0.75 取整 */
    static int thresholdOf(int cap) {
        return (int) (cap * 0.75f);
    }

    static int capacity(Map<?, ?> m) throws Exception {
        Object[] t = (Object[]) tableField.get(m);
        return t == null ? 0 : t.length;
    }

    static String state(Map<?, ?> m) throws Exception {
        Object[] t = (Object[]) tableField.get(m);
        return t == null ? "table=null，size=" + m.size() : "table.length=" + t.length;
    }

    static void check(String name, boolean ok, int actual) {
        System.out.printf("%s %s（实际 %d）%n", ok ? "[OK]  " : "[FAIL]", name, actual);
    }
}
