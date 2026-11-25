import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 练习 1：复现"丢失更新"
 *
 * <p>4 个线程，各 put 2000 个互不相同的 key 到同一个 map。期望 8000 条。
 * 先跑 HashMap（会丢），再跑 ConcurrentHashMap（不丢），各 5 轮看波动。
 *
 * <p>关键：每个线程写的 key 区间不重叠，所以"丢"只可能来自并发覆盖。
 */
public class LostUpdate {

    static final int THREADS = 4;
    static final int PER_THREAD = 2000;

    public static void main(String[] args) throws Exception {
        int expect = THREADS * PER_THREAD;
        System.out.printf("每个线程 put %d 个不同 key，共 %d 条%n%n", PER_THREAD, expect);

        System.out.println("=== HashMap（期望丢）===");
        for (int round = 1; round <= 5; round++) {
            Map<Integer, Integer> map = new HashMap<>();
            long t = System.currentTimeMillis();
            runAll(map);
            long d = System.currentTimeMillis() - t;
            int got = map.size();
            System.out.printf("  第 %d 轮：size=%4d / %d，丢 %4d 条（%.1f%%），耗时 %d ms%n",
                    round, got, expect, expect - got, (expect - got) * 100.0 / expect, d);
        }

        System.out.println();
        System.out.println("=== ConcurrentHashMap（期望不丢）===");
        for (int round = 1; round <= 5; round++) {
            Map<Integer, Integer> map = new ConcurrentHashMap<>();
            long t = System.currentTimeMillis();
            runAll(map);
            long d = System.currentTimeMillis() - t;
            int got = map.size();
            System.out.printf("  第 %d 轮：size=%4d / %d，丢 %4d 条，耗时 %d ms%n",
                    round, got, expect, expect - got, d);
        }

        System.out.println();
        System.out.println("结论：HashMap 的 size 少于期望值，说明 put 被并发覆盖；ConcurrentHashMap 不会。");
    }

    static void runAll(Map<Integer, Integer> map) throws InterruptedException {
        Thread[] ts = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            final int base = t * PER_THREAD;
            ts[t] = new Thread(() -> {
                for (int i = 0; i < PER_THREAD; i++) {
                    map.put(base + i, i);
                }
            });
            ts[t].start();
        }
        for (Thread t : ts) t.join();
    }
}
