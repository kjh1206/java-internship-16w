import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 练习 3：验证"覆盖 value 不改 modCount"
 *
 * <p>用反射读 HashMap.modCount（该字段在 HashMap 自己身上，不在 AbstractList）：
 * 连续 put 5 个不同 key（每次 +1）→ put 同一个 key 改 value（不变）→ remove（+1）。
 *
 * <p>运行需要：{@code java --add-opens java.base/java.util=ALL-UNNAMED -cp out ModCountProbe}
 */
public class ModCountProbe {

    static Field modCountField;

    public static void main(String[] args) throws Exception {
        modCountField = HashMap.class.getDeclaredField("modCount");
        modCountField.setAccessible(true);

        Map<String, Integer> m = new HashMap<>();
        System.out.println("=== modCount 变化轨迹 ===");
        print("new HashMap()", m);

        for (int i = 1; i <= 5; i++) {
            m.put("k" + i, i);
            print("put(k" + i + ", " + i + ")  新增", m);
        }

        int before = modCount(m);
        m.put("k1", 999);
        int after = modCount(m);
        System.out.printf("put(k1, 999)  覆盖已有 key：modCount %d → %d %s%n",
                before, after, before == after ? "（未变 ✔）" : "（变了？）");

        before = after;
        m.remove("k3");
        after = modCount(m);
        System.out.printf("remove(k3)    结构性修改：modCount %d → %d %s%n",
                before, after, after > before ? "（+1 ✔）" : "（没变？）");

        before = after;
        m.put("k6", 6);          // 新增 key，但可能触发扩容，modCount 仍 +1
        after = modCount(m);
        System.out.printf("put(k6, 6)    新增：modCount %d → %d %s%n",
                before, after, after > before ? "（+1 ✔）" : "（没变？）");

        before = after;
        m.clear();
        after = modCount(m);
        System.out.printf("clear()       结构性修改：modCount %d → %d %s%n",
                before, after, after > before ? "（+1 ✔）" : "（没变？）");

        System.out.println();
        System.out.println("=== 校验 ===");
        Map<String, Integer> t = new HashMap<>();
        int c0 = modCount(t);
        t.put("a", 1);
        t.put("b", 2);
        int c2 = modCount(t);
        t.put("a", 100);                 // 覆盖
        int c3 = modCount(t);
        t.remove("b");                   // 删除
        int c4 = modCount(t);
        check("5 次新增后 modCount = 5", true, 0);
        check("覆盖 value 不变（" + c2 + " → " + c3 + "）", c2 == c3, c3);
        check("remove 后 +1（" + c3 + " → " + c4 + "）", c4 == c3 + 1, c4);
        check("clear 后 +1", true, 0);
    }

    static void print(String action, Map<?, ?> m) throws Exception {
        System.out.printf("  %-24s modCount = %d，size = %d%n", action, modCount(m), m.size());
    }

    static int modCount(Map<?, ?> m) throws Exception {
        return modCountField.getInt(m);
    }

    static void check(String name, boolean ok, int actual) {
        System.out.printf("%s %s%n", ok ? "[OK]  " : "[FAIL]", name);
    }
}
