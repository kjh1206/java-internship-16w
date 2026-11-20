import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 练习 4：ArrayList vs CopyOnWriteArrayList 的"N 次 add"耗时。
 *
 * 为什么差距这么大：
 *   ArrayList.add    → 均摊 O(1)：满了扩 1.5 倍，每次只需偶尔搬一次
 *   COW.add          → 每次 new 一个 n+1 的数组 + arraycopy n 个元素 = O(n)
 *   于是 1..N 求和 ≈ N²/2 次元素拷贝 → 差距随 N 线性放大。
 *
 * 这里不直接跑 10 万次 COW（O(n²) 太慢），而是测 5k/10k/20k 三档，
 * 观察"每翻倍一次，COW 耗时约涨 4 倍"的二次曲线，再按此推算 10 万。
 */
public class AddBenchCow {

    public static void main(String[] args) {
        System.out.println("=== ArrayList vs CopyOnWriteArrayList：N 次 add ===");
        System.out.printf("%8s %14s %16s %10s%n", "N", "ArrayList(ms)", "COW(ms)", "倍数");

        for (int n : new int[]{5_000, 10_000, 20_000}) {
            // 预热
            for (int i = 0; i < 2; i++) {
                addArrayList(n);
                addCow(n);
            }
            long a = bestOf5(n, false);
            long c = bestOf5(n, true);
            System.out.printf("%8d %14.2f %16.2f %9.1fx%n",
                    n, a / 1e6, c / 1e6, (double) c / Math.max(a, 1));
        }

        System.out.println();
        System.out.println("  外推：COW 的耗时 ≈ k·N²，N 从 2 万到 10 万是 5 倍 → 约 25 倍");
        long c20k = bestOf5(20_000, true);
        System.out.printf("  实测 2 万 → %.1f ms，按 25 倍推算 10 万 → 约 %.0f ms（≈ %.1f s）%n",
                c20k / 1e6, c20k * 25 / 1e6, c20k * 25 / 1e9);

        // 顺便对比：预分配容量的 ArrayList
        long pre = bestOfPreAlloc(200_000);
        System.out.printf("  参考：ArrayList 预分配容量（new ArrayList<>(n)）20 万次 add = %.2f ms%n", pre / 1e6);
    }

    static long bestOf5(int n, boolean cow) {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            long t = System.nanoTime();
            if (cow) addCow(n); else addArrayList(n);
            best = Math.min(best, System.nanoTime() - t);
        }
        return best;
    }

    static long bestOfPreAlloc(int n) {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            long t = System.nanoTime();
            List<Integer> list = new ArrayList<>(n);
            for (int j = 0; j < n; j++) list.add(j);
            best = Math.min(best, System.nanoTime() - t);
            if (list.size() != n) throw new AssertionError();
        }
        return best;
    }

    static void addArrayList(int n) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(i);
    }

    static void addCow(int n) {
        List<Integer> list = new CopyOnWriteArrayList<>();
        for (int i = 0; i < n; i++) list.add(i);
    }
}
