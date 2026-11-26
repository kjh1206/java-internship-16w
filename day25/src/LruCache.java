import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 练习 1：容量 3 的 LRU 缓存。
 *
 * <p>LRU = Least Recently Used，最久未使用的先淘汰。
 * 用 LinkedHashMap 实现只需要两件事：
 * <ol>
 *   <li>构造器第三个参数 accessOrder = true —— 让链表按"访问顺序"排，每次 get 把节点搬到尾部</li>
 *   <li>重写 removeEldestEntry —— 超过容量就返回 true，LinkedHashMap 会自动删掉 head（最久未访问的）</li>
 * </ol>
 *
 * <p>要点：链表从 head（最久未访问）到 tail（最近访问）；
 * 淘汰删的是 head，保留的是 tail。
 */
public class LruCache {

    /** 容量固定的 LRU 缓存。 */
    static class LRU<K, V> extends LinkedHashMap<K, V> {
        private final int cap;

        LRU(int cap) {
            // 16 = 初始容量，0.75f = 负载因子，true = 按访问顺序排序（关键）
            super(16, 0.75f, true);
            this.cap = cap;
        }

        /**
         * 每次 put 之后，LinkedHashMap 都会回调这个方法。
         * 返回 true 表示"删掉最老的 entry"（即 head），返回 false 表示不删。
         */
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > cap;
        }
    }

    public static void main(String[] args) {
        LRU<String, Integer> lru = new LRU<>(3);

        lru.put("A", 1);
        lru.put("B", 2);
        lru.put("C", 3);
        System.out.println("put A,B,C   -> " + lru.keySet());   // [A, B, C]

        lru.get("A");                                           // A 变成"最近访问"，搬到尾部
        System.out.println("get(A)      -> " + lru.keySet());   // [B, C, A]

        lru.put("D", 4);                                        // 超容量，淘汰 head = B
        System.out.println("put(D)      -> " + lru.keySet());   // [C, A, D]

        lru.get("C");                                           // C 搬到尾部
        System.out.println("get(C)      -> " + lru.keySet());   // [A, D, C]

        lru.put("E", 5);                                        // 淘汰 head = A
        System.out.println("put(E)      -> " + lru.keySet());   // [D, C, E]
    }
}
