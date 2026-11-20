import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 练习 5：用 Collections.frequency 统计一句话里每个单词出现的次数（配 HashMap 也行）。
 *
 * Collections.frequency(list, x) 的语义是"x 在 list 里出现几次"，
 * 所以要用它统计词频，需要：对每个不同的词，都扫一遍整个 list → 双重循环。
 *
 *   时间复杂度：O(d·n)，d 为不同词个数
 *   HashMap 版：O(n)
 *
 * 这正是"工具方法很方便，但可能藏着复杂度"的典型例子。
 */
public class WordFrequency {

    private static final String SENTENCE =
            "the quick brown fox jumps over the lazy dog the fox runs and the dog sleeps";

    public static void main(String[] args) {
        String[] words = SENTENCE.split("\\s+");
        List<String> list = Arrays.asList(words);
        System.out.println("句子：" + SENTENCE);
        System.out.println("单词数：" + list.size());
        System.out.println();

        System.out.println("=== ① Collections.frequency：对每个不同的词扫一遍 list ===");
        Map<String, Integer> byFrequency = new LinkedHashMap<>();
        for (String w : list) {
            if (!byFrequency.containsKey(w)) {          // 只为避免重复计算
                byFrequency.put(w, Collections.frequency(list, w));
            }
        }
        System.out.println("  结果：" + byFrequency);
        System.out.println("  注意：frequency 内部又是一次 O(n) 遍历，外层每个词都要来一次");

        System.out.println();
        System.out.println("=== ② HashMap 一次遍历（推荐）===");
        Map<String, Integer> byMap = new LinkedHashMap<>();
        for (String w : list) {
            byMap.merge(w, 1, Integer::sum);            // 或 getOrDefault(w,0)+1
        }
        System.out.println("  结果：" + byMap);
        System.out.println("  结果一致：" + byFrequency.equals(byMap));

        System.out.println();
        System.out.println("=== ③ 排序后输出 Top3 ===");
        byMap.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(3)
                .forEach(e -> System.out.println("  " + e.getKey() + " × " + e.getValue()));

        System.out.println();
        System.out.println("=== ④ 性能对比（3 万词、1000 个不同词）===");
        List<String> big = new ArrayList<>();
        for (int i = 0; i < 30_000; i++) big.add("w" + (i % 1000));
        long d = countDistinct(big);
        System.out.println("  规模：" + big.size() + " 词，" + d + " 个不同词");
        System.out.println("  理论比较次数：frequency 版 = d*n = " + (d * big.size())
                + "；HashMap 版 = n = " + big.size());

        for (int warm = 0; warm < 1; warm++) { byFrequencyMethod(big); }

        long t1 = System.nanoTime();
        Map<String, Integer> r1 = byFrequencyMethod(big);
        long cost1 = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        Map<String, Integer> r2 = byHashMap(big);
        long cost2 = System.nanoTime() - t2;

        System.out.printf("  Collections.frequency 版：%8.2f ms%n", cost1 / 1e6);
        System.out.printf("  HashMap 一次遍历版     ：%8.2f ms%n", cost2 / 1e6);
        System.out.printf("  慢 %.0f 倍，结果一致：%s%n", (double) cost1 / cost2, r1.equals(r2));
        System.out.println();
        System.out.println("  结论：frequency 的签名是 (Collection, Object)，本质是线性查找；");
        System.out.println("        想统计「每个」词的次数，等于把线性查找套在 n 个词上 → O(d·n)。");
    }

    static Map<String, Integer> byFrequencyMethod(List<String> list) {
        Map<String, Integer> result = new HashMap<>();
        for (String w : list) {
            if (!result.containsKey(w)) result.put(w, Collections.frequency(list, w));
        }
        return result;
    }

    static Map<String, Integer> byHashMap(List<String> list) {
        Map<String, Integer> result = new HashMap<>();
        for (String w : list) result.merge(w, 1, Integer::sum);
        return result;
    }

    static long countDistinct(List<String> list) {
        return new java.util.HashSet<>(list).size();
    }
}
