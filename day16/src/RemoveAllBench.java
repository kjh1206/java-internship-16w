import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 练习④：removeAll vs removeIf + HashSet，1 万元素时的加速比。
 *
 * 背景：
 *   ArrayList.removeAll(c) 对每个元素调 c.contains(e)。
 *   如果 c 是 List，contains 是 O(m) → 整体 O(n*m)；
 *   如果 c 是 HashSet，contains 是 O(1) → 整体 O(n)。
 *   removeIf 是单遍扫描 + 谓词判断，配合 HashSet 也是 O(n)，但少一层迭代器开销。
 */
public class RemoveAllBench {

    private static final int N = 10_000;   // 原表大小
    private static final int M = 5_000;    // 待删集合大小
    private static final int MEASURE = 3;

    public static void main(String[] args) {
        Random rnd = new Random(42);
        List<Integer> source = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            source.add(i);
        }
        // 从源里随机挑 M 个当"待删除集合"
        List<Integer> toRemove = new ArrayList<>(M);
        Set<Integer> toRemoveSet = new HashSet<>(M * 2);
        while (toRemove.size() < M) {
            int v = rnd.nextInt(N);
            if (toRemoveSet.add(v)) {
                toRemove.add(v);
            }
        }

        // 预热
        for (int i = 0; i < 2; i++) {
            runRemoveAll(source, toRemove);
            runRemoveIf(source, toRemoveSet);
            runRemoveAllHashSet(source, toRemoveSet);
        }

        long t1 = Long.MAX_VALUE, t2 = Long.MAX_VALUE, t3 = Long.MAX_VALUE;
        for (int i = 0; i < MEASURE; i++) {
            t1 = Math.min(t1, runRemoveAll(source, toRemove));
            t2 = Math.min(t2, runRemoveIf(source, toRemoveSet));
            t3 = Math.min(t3, runRemoveAllHashSet(source, toRemoveSet));
        }

        System.out.println("=== ④ 1 万元素，删掉 5 千个 ===");
        System.out.printf("  removeAll(List)          : %8.2f ms%n", t1 / 1e6);
        System.out.printf("  removeAll(HashSet)       : %8.2f ms%n", t3 / 1e6);
        System.out.printf("  removeIf + HashSet       : %8.2f ms%n", t2 / 1e6);
        System.out.println();
        System.out.printf("  removeAll(List) → removeIf+HashSet 加速比：%.1f 倍%n", (double) t1 / t2);
        System.out.printf("  removeAll(List) → removeAll(HashSet) 加速比：%.1f 倍%n", (double) t1 / t3);
        System.out.println("  原因：List.contains 是 O(m)，整个 removeAll 退化成 O(n*m)；");
        System.out.println("        HashSet.contains 是 O(1)，整体回到 O(n)。");
    }

    /** 原版：removeAll 传 List */
    private static long runRemoveAll(List<Integer> source, List<Integer> toRemove) {
        List<Integer> copy = new ArrayList<>(source);
        long start = System.nanoTime();
        copy.removeAll(toRemove);
        long cost = System.nanoTime() - start;
        check(copy);
        return cost;
    }

    /** 只把参数换成 HashSet，仍然用 removeAll */
    private static long runRemoveAllHashSet(List<Integer> source, Set<Integer> toRemove) {
        List<Integer> copy = new ArrayList<>(source);
        long start = System.nanoTime();
        copy.removeAll(toRemove);
        long cost = System.nanoTime() - start;
        check(copy);
        return cost;
    }

    /** 推荐写法：removeIf + HashSet 谓词 */
    private static long runRemoveIf(List<Integer> source, Set<Integer> toRemove) {
        List<Integer> copy = new ArrayList<>(source);
        long start = System.nanoTime();
        copy.removeIf(toRemove::contains);
        long cost = System.nanoTime() - start;
        check(copy);
        return cost;
    }

    /** 顺手校验三个版本结果一致，避免"快但删错" */
    private static int expected = -1;
    private static void check(List<Integer> result) {
        if (expected == -1) {
            expected = result.size();
        } else if (expected != result.size()) {
            throw new AssertionError("结果不一致：" + expected + " vs " + result.size());
        }
    }
}
