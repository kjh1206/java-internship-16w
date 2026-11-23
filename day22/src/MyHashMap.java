import java.util.Objects;

/**
 * 简化版 HashMap（Day 22 练习）
 *
 * <p>目标：亲手把「算下标 → 放桶 → 链表比较 key → 覆盖或尾插 → 扩容重算下标」这条链路写一遍。
 * 刻意<b>不做</b>的东西（留给后面几天）：
 * <ul>
 *   <li>高低链优化：这里扩容时对每个节点重新计算下标，慢但直观</li>
 *   <li>树化：桶内永远用链表，不转红黑树</li>
 *   <li>fail-fast：迭代器相关的东西一律没有</li>
 * </ul>
 *
 * <p>结构：{@code Node[] table} 数组 + 每个桶一条单向链表 + {@code size} 元素个数 + {@code threshold} 扩容阈值。
 */
public class MyHashMap<K, V> {

    /** 链表节点：hash 缓存一份，比较时先比 hash 再比 equals，省掉重复计算。 */
    static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    /** 桶数组，长度永远是 2 的幂 */
    private Node<K, V>[] table;
    /** 键值对个数（不是数组长度） */
    private int size;
    /** 扩容阈值 = 容量 × 负载因子 */
    private int threshold;

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
        threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);   // 16 × 0.75 = 12
    }

    /** 扰动函数：让高位也参与下标计算，减少规律性 key 的冲突 */
    static int hash(Object key) {
        int h = (key == null) ? 0 : key.hashCode();
        return h ^ (h >>> 16);
    }

    /** 下标：容量是 2 的幂时，(n-1) & hash 等价于 hash % n，但快得多 */
    private int index(int hash) {
        return (table.length - 1) & hash;
    }

    // ---------------------------------------------------------------- put

    /**
     * 放入键值对。
     * @return 被覆盖的旧值；新插入返回 null
     */
    public V put(K key, V value) {
        // ① 先判断是否要扩容（注意：这里用 size+1 预判，等价于 JDK 的 ++size > threshold）
        if (size + 1 > threshold) {
            resize();
        }

        int h = hash(key);
        int i = index(h);

        // ② 桶是空的：直接放，结束
        if (table[i] == null) {
            table[i] = new Node<>(h, key, value, null);
            size++;
            return null;
        }

        // ③ 桶不空：沿链表找
        for (Node<K, V> e = table[i]; ; e = e.next) {
            // key 相同（先比 hash 再比 equals）→ 覆盖值，size 不变
            if (e.hash == h && Objects.equals(e.key, key)) {
                V old = e.value;
                e.value = value;
                return old;
            }
            // 走到链尾还是没找到 → 尾插新节点
            if (e.next == null) {
                e.next = new Node<>(h, key, value, null);
                size++;
                return null;
            }
        }
    }

    // ---------------------------------------------------------------- get

    /** 取 value；key 不存在返回 null。 */
    public V get(K key) {
        int h = hash(key);
        for (Node<K, V> e = table[index(h)]; e != null; e = e.next) {
            if (e.hash == h && Objects.equals(e.key, key)) {
                return e.value;
            }
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** 调试用：当前桶数组长度 */
    public int capacity() {
        return table.length;
    }

    // ---------------------------------------------------------------- resize

    /**
     * 扩容：容量翻倍 + 所有元素重新计算下标。
     *
     * <p>这里故意用最笨的写法（每个节点重新算 index），
     * 明天在课件里会看到 JDK 8 的优化：只需判断 {@code (e.hash & oldCap) == 0}，
     * 把一条链拆成"留原位"和"挪 oldCap"两条链，不用重算。
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int oldCap = table.length;
        int newCap = oldCap << 1;                       // 容量翻倍
        Node<K, V>[] oldTab = table;
        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
        table = newTab;                                  // 先换表，下面 index() 用新长度

        for (Node<K, V> head : oldTab) {
            Node<K, V> e = head;
            while (e != null) {
                Node<K, V> next = e.next;                // 先存 next，防止被改乱
                int i = index(e.hash);                   // 重新算下标
                e.next = newTab[i];                      // 头插进新桶（顺序不重要）
                newTab[i] = e;
                e = next;
            }
        }
        threshold = (int) (newCap * LOAD_FACTOR);
    }

    // ---------------------------------------------------------------- 测试

    private static int pass = 0;
    private static int fail = 0;

    private static void check(String name, boolean ok, Object actual) {
        if (ok) {
            pass++;
            System.out.printf("[OK]   %s%n", name);
        } else {
            fail++;
            System.out.printf("[FAIL] %s → %s%n", name, actual);
        }
    }

    public static void main(String[] args) {
        case1_新增();
        case2_覆盖();
        case3_冲突();
        case4_扩容后仍能get();
        case5_一千个随机数();

        System.out.println();
        System.out.printf("通过 %d 项，失败 %d 项%n", pass, fail);
        if (fail > 0) {
            System.exit(1);
        }
    }

    /** 用例 1：新增 —— size 增长，值能取回 */
    static void case1_新增() {
        System.out.println("=== 用例 1：新增 ===");
        MyHashMap<String, Integer> m = new MyHashMap<>();
        check("add(a,1) 返回 null", m.put("a", 1) == null, null);
        check("add(b,2) 返回 null", m.put("b", 2) == null, null);
        check("size == 2", m.size() == 2, m.size());
        check("get(a) == 1", m.get("a") == 1, m.get("a"));
        check("get(b) == 2", m.get("b") == 2, m.get("b"));
        check("get(不存在) == null", m.get("zzz") == null, m.get("zzz"));
        System.out.println();
    }

    /** 用例 2：覆盖 —— 同一个 key 再 put，size 不变、返回旧值 */
    static void case2_覆盖() {
        System.out.println("=== 用例 2：覆盖 ===");
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("a", 1);
        Integer old = m.put("a", 100);
        check("put(a,100) 返回旧值 1", Integer.valueOf(1).equals(old), old);
        check("get(a) == 100", m.get("a") == 100, m.get("a"));
        check("size 仍是 1", m.size() == 1, m.size());
        System.out.println();
    }

    /** 用例 3：冲突 —— 两个 hashCode 相同、equals 不同的 key 落在同一个桶 */
    static void case3_冲突() {
        System.out.println("=== 用例 3：冲突（两个 key 同桶）===");
        MyHashMap<Key, String> m = new MyHashMap<>();
        Key k1 = new Key(1, "one");
        Key k2 = new Key(1, "uno");    // hashCode 与 k1 相同，equals 不同
        m.put(k1, "第一");
        m.put(k2, "第二");

        int i1 = (m.capacity() - 1) & hash(k1);
        int i2 = (m.capacity() - 1) & hash(k2);
        check("两个 key 下标相同（同桶）", i1 == i2, i1 + " vs " + i2);
        check("size == 2（没有互相覆盖）", m.size() == 2, m.size());
        check("k1 取到第一", "第一".equals(m.get(k1)), m.get(k1));
        check("k2 取到第二", "第二".equals(m.get(k2)), m.get(k2));
        System.out.println();
    }

    /** 用例 4：扩容后仍能 get —— 20 个元素会触发两次扩容（12 → 24） */
    static void case4_扩容后仍能get() {
        System.out.println("=== 用例 4：扩容后仍能 get ===");
        MyHashMap<Integer, Integer> m = new MyHashMap<>();
        check("初始容量 16", m.capacity() == 16, m.capacity());

        for (int i = 0; i < 20; i++) {
            m.put(i, i * 10);
        }
        check("放入 20 个后触发扩容（容量 > 16）", m.capacity() > 16, m.capacity());
        check("size == 20", m.size() == 20, m.size());

        boolean allOk = true;
        for (int i = 0; i < 20; i++) {
            if (!Integer.valueOf(i * 10).equals(m.get(i))) {
                allOk = false;
                check("扩容后 get(" + i + ")", false, m.get(i));
                break;
            }
        }
        check("扩容后 20 个 key 全部取回", allOk, "见上");
        System.out.println();
    }

    /** 用例 5：1000 个随机数 —— 随机 key 大量冲突 + 多次扩容 */
    static void case5_一千个随机数() {
        System.out.println("=== 用例 5：1000 个随机数 ===");
        MyHashMap<Integer, Integer> m = new MyHashMap<>();
        java.util.Random r = new java.util.Random(42);
        int n = 1000;
        int[] keys = new int[n];
        for (int i = 0; i < n; i++) {
            keys[i] = r.nextInt(10_000);
            m.put(keys[i], keys[i] * 2);
        }

        boolean allOk = true;
        for (int k : keys) {
            if (!Integer.valueOf(k * 2).equals(m.get(k))) {
                allOk = false;
                check("随机 key " + k, false, m.get(k));
                break;
            }
        }
        check("1000 个随机 key 全部正确", allOk, "见上");
        check("size 在 (0, 1000] 之间（随机数可能重复）", m.size() > 0 && m.size() <= n, m.size());
        System.out.printf("       最终容量 = %d，阈值 = %d，实际元素 = %d%n", m.capacity(), m.threshold, m.size());
        System.out.println();
    }

    /** 测试用的 Key：hashCode 可控，equals 看 name */
    static class Key {
        final int h;
        final String name;

        Key(int h, String name) {
            this.h = h;
            this.name = name;
        }

        @Override
        public int hashCode() {
            return h;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Key k && k.h == h && Objects.equals(k.name, name);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
