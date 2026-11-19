import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 题二：removeIfOdd 两种实现，10 万随机数比耗时。
 *
 *   ① 迭代器：Iterator + it.remove()
 *   ② 倒序 for：for (int i = size-1; i >= 0; i--) if (odd) list.remove(i)
 *
 * 注意：这不是"选一个更聪明的算法"，两者都是 O(n)，
 *       差别在常数：迭代器版每次删除要数组搬移；倒序版删的是靠后的位置，搬移更少。
 *       所以预期倒序版更快 —— 用数据验证这个直觉。
 */
public class RemoveOddBench {

    private static final int N = 100_000;
    private static final int ROUNDS = 5;

    public static void main(String[] args) {
        int[] seed = new int[N];
        Random rnd = new Random(2026);
        for (int i = 0; i < N; i++) {
            seed[i] = rnd.nextInt(1_000_000);
        }

        // 预热
        for (int i = 0; i < 2; i++) {
            removeOddByIterator(toList(seed));
            removeOddByReverseFor(toList(seed));
        }

        long bestIt = Long.MAX_VALUE;
        long bestRev = Long.MAX_VALUE;
        int sizeIt = 0;
        int sizeRev = 0;
        for (int i = 0; i < ROUNDS; i++) {
            List<Integer> a = toList(seed);
            long t = System.nanoTime();
            removeOddByIterator(a);
            bestIt = Math.min(bestIt, System.nanoTime() - t);
            sizeIt = a.size();

            List<Integer> b = toList(seed);
            t = System.nanoTime();
            removeOddByReverseFor(b);
            bestRev = Math.min(bestRev, System.nanoTime() - t);
            sizeRev = b.size();
        }

        System.out.println("=== 题二：删除所有奇数（10 万元素）===");
        System.out.printf("  ① 迭代器 Iterator.remove() : %7.2f ms   剩余 %d 个%n", bestIt / 1e6, sizeIt);
        System.out.printf("  ② 倒序 for + remove(i)     : %7.2f ms   剩余 %d 个%n", bestRev / 1e6, sizeRev);
        System.out.printf("  加速比：%.2f 倍（倒序更快则为正值）%n", (double) bestIt / bestRev);
        System.out.println();
        System.out.println("  为什么两者都是 O(n) 但常数不同：");
        System.out.println("    ArrayList.remove(i) 要把 i 之后的所有元素左移一位。");
        System.out.println("    迭代器删的是\"当前位置\"，平均在中间，每次搬 n/2；");
        System.out.println("    倒序删的是从尾部往前，搬移量依次减少，总搬移量约为迭代器版的一半。");
        System.out.println("  但两者正确性相同：都只看一次 size、都不会漏元素。");
    }

    /** 迭代器版 */
    static void removeOddByIterator(List<Integer> list) {
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            if (it.next() % 2 != 0) {
                it.remove();
            }
        }
    }

    /** 倒序 for 版 */
    static void removeOddByReverseFor(List<Integer> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i) % 2 != 0) {
                list.remove(i);
            }
        }
    }

    static List<Integer> toList(int[] arr) {
        List<Integer> list = new ArrayList<>(arr.length);
        for (int v : arr) {
            list.add(v);
        }
        return list;
    }
}
