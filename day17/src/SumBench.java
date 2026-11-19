import java.util.LinkedList;
import java.util.Random;

/**
 * 题四：LinkedList<Integer> 求和的两种写法与 nanoTime 对比（1 万元素）。
 *
 *   ① get(i) 索引遍历：每次定位 O(n) → 整体 O(n^2)
 *   ② for-each：迭代器逐个走 → O(n)
 *
 * 测法：预热 3 轮（让 JIT 编译生效），再测 7 轮取中位数，避免单次抖动。
 */
public class SumBench {

    private static final int N = 10_000;
    private static final int WARMUP = 3;
    private static final int ROUNDS = 7;

    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList<>();
        Random rnd = new Random(7);
        for (int i = 0; i < N; i++) {
            list.add(rnd.nextInt(100));
        }

        // 预热
        for (int i = 0; i < WARMUP; i++) {
            sumByIndex(list);
            sumByForEach(list);
        }

        long[] byIndex = new long[ROUNDS];
        long[] byForEach = new long[ROUNDS];
        long s1 = 0, s2 = 0;
        for (int i = 0; i < ROUNDS; i++) {
            long t = System.nanoTime();
            s1 = sumByIndex(list);
            byIndex[i] = System.nanoTime() - t;

            t = System.nanoTime();
            s2 = sumByForEach(list);
            byForEach[i] = System.nanoTime() - t;
        }

        long m1 = median(byIndex);
        long m2 = median(byForEach);

        System.out.println("=== 题四：1 万元素求和 ===");
        System.out.printf("  get(i) 索引遍历 : %9.3f ms   平均 %7.1f ns/元素（sum=%d）%n",
                m1 / 1e6, (double) m1 / N, s1);
        System.out.printf("  for-each 迭代   : %9.3f ms   平均 %7.1f ns/元素（sum=%d）%n",
                m2 / 1e6, (double) m2 / N, s2);
        System.out.printf("  加速比：%.0f 倍%n", (double) m1 / m2);
        System.out.println();
        System.out.println("  为什么差这么多：");
        System.out.println("    get(i) 每次从头或从尾走到 i，平均 n/4 步；执行 n 次 → O(n^2)");
        System.out.println("    for-each 编译成 iterator()，一条 next 链走到底 → O(n)");
        System.out.println("  一句话：LinkedList 千万别用下标循环，get(i) 是陷阱。");
    }

    private static long sumByIndex(LinkedList<Integer> list) {
        long sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return sum;
    }

    private static long sumByForEach(LinkedList<Integer> list) {
        long sum = 0;
        for (Integer v : list) {
            sum += v;
        }
        return sum;
    }

    private static long median(long[] a) {
        long[] b = a.clone();
        java.util.Arrays.sort(b);
        return b[b.length / 2];
    }
}
