import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 练习 2：验证扩容时的高低链拆分
 *
 * <p>自定义 key，hashCode() 分别返回 1 和 17。
 * 容量 16 时：{@code (16-1) & 1 = 1}、{@code (16-1) & 17 = 1} → 同一个桶。
 * 扩容到 32 后：{@code 1 & 16 == 0} → 留在桶 1；{@code 17 & 16 != 0} → 挪到 1+16 = 17。
 *
 * <p>运行需要：{@code java --add-opens java.base/java.util=ALL-UNNAMED -cp out HighLowChain}
 */
public class HighLowChain {

    static Field tableField;

    public static void main(String[] args) throws Exception {
        tableField = HashMap.class.getDeclaredField("table");
        tableField.setAccessible(true);

        Map<Key, String> map = new HashMap<>(16);
        Key k1 = new Key(1);
        Key k17 = new Key(17);
        map.put(k1, "one");
        map.put(k17, "seventeen");

        System.out.println("=== 扩容前（容量 16）===");
        System.out.printf("  容量 = %d%n", capacity(map));
        System.out.printf("  hash=1  的下标 = (16-1) & 1  = %d%n", index(map, k1));
        System.out.printf("  hash=17 的下标 = (16-1) & 17 = %d%n", index(map, k17));
        dump(map);

        // 继续塞到触发扩容（阈值 12）→ 容量变 32
        for (int i = 0; i < 15; i++) {
            map.put(new Key(1000 + i), "v" + i);
        }

        System.out.println();
        System.out.println("=== 扩容后（容量 32）===");
        System.out.printf("  容量 = %d%n", capacity(map));
        System.out.printf("  hash=1  的下标 = (32-1) & 1  = %d%n", index(map, k1));
        System.out.printf("  hash=17 的下标 = (32-1) & 17 = %d%n", index(map, k17));
        dump(map);

        // 关键判断
        System.out.println();
        System.out.println("=== 为什么 ===");
        System.out.printf("  1  & oldCap(16) = %d  → 0 → 留在原位 j=1%n", 1 & 16);
        System.out.printf("  17 & oldCap(16) = %d → 非 0 → 移到 j+16=17%n", 17 & 16);

        System.out.println();
        System.out.println("=== 校验 ===");
        check("扩容前都在桶 1", index(map, k1) == 1 && indexBefore(map, k1) == indexBefore(map, k17), 1);
        check("hash=1 扩容后在桶 1", index(map, k1) == 1, index(map, k1));
        check("hash=17 扩容后在桶 17", index(map, k17) == 17, index(map, k17));
    }

    /** 按当前容量算下标 */
    static int index(Map<?, ?> m, Object key) throws Exception {
        return (capacity(m) - 1) & key.hashCode();
    }

    /** 按 16 算下标（用于验证扩容前） */
    static int indexBefore(Map<?, ?> m, Object key) {
        return 15 & key.hashCode();
    }

    static int capacity(Map<?, ?> m) throws Exception {
        Object[] t = (Object[]) tableField.get(m);
        return t == null ? 0 : t.length;
    }

    /** 打印所有非空桶 */
    static void dump(Map<?, ?> m) throws Exception {
        Object[] t = (Object[]) tableField.get(m);
        for (int b = 0; b < t.length; b++) {
            StringBuilder sb = new StringBuilder();
            for (Object e = t[b]; e != null; e = next(e)) {
                sb.append(String.valueOf(keyOf(e))).append(' ');
            }
            if (sb.length() > 0) {
                System.out.printf("  table[%d] → %s%n", b, sb.toString().trim());
            }
        }
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

    static Object keyOf(Object node) throws Exception {
        Field f = node.getClass().getDeclaredField("key");
        f.setAccessible(true);
        return f.get(node);
    }

    static void check(String name, boolean ok, int actual) {
        System.out.printf("%s %s（实际 %d）%n", ok ? "[OK]  " : "[FAIL]", name, actual);
    }

    /** hashCode 可控、equals 按 hashCode 判断 */
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
            return o instanceof Key k && k.h == h;
        }

        @Override
        public String toString() {
            return String.valueOf(h);
        }
    }
}
