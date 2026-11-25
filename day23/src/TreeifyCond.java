import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 练习 3：验证树化的前置条件（容量必须 ≥ 64）
 *
 * <p>往同一个桶塞 9 个 key 全部冲突的元素，观察：
 * 容量 16 → 触发扩容到 32 → 仍是链表 → 继续扩容到 64 → 这时才可能树化。
 * 判断桶内类型：{@code e.getClass().getSimpleName()} 为 "Node" 或 "TreeNode"。
 *
 * <p>运行需要：{@code java --add-opens java.base/java.util=ALL-UNNAMED -cp out TreeifyCond}
 */
public class TreeifyCond {

    static Field tableField;

    public static void main(String[] args) throws Exception {
        tableField = HashMap.class.getDeclaredField("table");
        tableField.setAccessible(true);

        Map<Key, Integer> map = new HashMap<>(16);
        int bucket = -1;
        int treeifyCap = -1;      // 第一次出现 TreeNode 时的容量

        for (int i = 1; i <= 12; i++) {
            map.put(new Key(1), i);       // 全部 hashCode = 1，必然同桶
            if (bucket < 0) bucket = bucketOf(map, new Key(1));
            if (treeifyCap < 0 && "TreeNode".equals(typeName(map, bucket))) {
                treeifyCap = capacity(map);
            }
            System.out.printf("第 %2d 个：容量=%2d，桶[%d] 内节点数=%d，类型=%s%n",
                    i, capacity(map), bucket, chainLength(map, bucket), typeName(map, bucket));
        }

        System.out.println();
        System.out.println("=== 说明 ===");
        System.out.println("  put 到第 8 个时链表长度达到 8，JDK 调 treeifyBin；");
        System.out.println("  但 treeifyBin 发现容量 < 64（MIN_TREEIFY_CAPACITY）→ 只调用 resize() 扩容，不树化；");
        System.out.println("  容量到 64 之后再次达到 8 个才真正转 TreeNode。");
        System.out.println();

        // 把容量撑到 64，再塞满同一个桶
        Map<Key, Integer> big = new HashMap<>(64);
        for (int i = 0; i < 100; i++) big.put(new Key(100_000 + i), i);   // 先撑容量
        int b = bucketOf(big, new Key(1));
        for (int i = 1; i <= 12; i++) {
            big.put(new Key(1), i);
        }
        System.out.printf("容量=%d 时，桶[%d] 内节点数=%d，类型=%s%n",
                capacity(big), b, chainLength(big, b), typeName(big, b));

        System.out.println();
        System.out.println("=== 校验 ===");
        check("树化发生在容量 ≥ 64 时（容量 16/32 时都还是 Node）", treeifyCap >= 64, treeifyCap);
        check("最终容量 ≥ 64", capacity(big) >= 64, capacity(big));
    }

    static int bucketOf(Map<?, ?> m, Object key) throws Exception {
        return (capacity(m) - 1) & key.hashCode();
    }

    static int capacity(Map<?, ?> m) throws Exception {
        Object[] t = (Object[]) tableField.get(m);
        return t == null ? 0 : t.length;
    }

    static Object head(Map<?, ?> m, int bucket) throws Exception {
        Object[] t = (Object[]) tableField.get(m);
        if (t == null || bucket >= t.length) return null;
        return t[bucket];
    }

    static int chainLength(Map<?, ?> m, int bucket) throws Exception {
        int n = 0;
        for (Object e = head(m, bucket); e != null; e = next(e)) n++;
        return n;
    }

    static String typeName(Map<?, ?> m, int bucket) throws Exception {
        Object e = head(m, bucket);
        return e == null ? "空" : e.getClass().getSimpleName();
    }

    static Object next(Object node) throws Exception {
        Class<?> c = node.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField("next");
                f.setAccessible(true);
                return f.get(node);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    static void check(String name, boolean ok, int actual) {
        System.out.printf("%s %s（实际 %d）%n", ok ? "[OK]  " : "[FAIL]", name, actual);
    }

    static class Key {
        final int h;

        Key(int h) {
            this.h = h;
        }

        @Override
        public int hashCode() {
            return h;
        }

        @Override
        public boolean equals(Object o) {
            // 注意：equals 必须区分不同实例，否则同 hashCode 会被当成同一个 key 覆盖掉
            return o == this;
        }

        @Override
        public String toString() {
            return "K" + h + "@" + Integer.toHexString(System.identityHashCode(this));
        }
    }
}
