import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * 题四：5 个元素，遍历到第 5 个时删掉它 —— 到底抛不抛？
 *
 * 先给结论（跑出来为准）：**会抛**。
 *   "删最后一个不抛 CME" 是个流传很广的错误说法，
 *   真正不抛的位置是**原表的倒数第二个**（删完后 cursor == size）。
 *
 * 本类把两个位置都跑一遍，并打印每一步的 cursor/size 推演。
 */
public class DeleteLastCme {

    public static void main(String[] args) {
        System.out.println("=== 题四：删第 5 个（最后一个）===");
        run(5, "删最后一个");

        System.out.println("\n=== 对照：删第 4 个（倒数第二个）===");
        run(4, "删倒数第二个");

        System.out.println("\n=== 对照：删第 1 个 ===");
        run(1, "删第一个");

        System.out.println();
        System.out.println("推演（原表 5 个，n=5）：");
        System.out.println("  第 k 个元素被 next() 返回后，cursor = k，size = n = 5");
        System.out.println("  删掉它之后 size = 4");
        System.out.println("  k=5（最后一个） → cursor=5, size=4 → 5 != 4 → hasNext 为 true → 再调 next() → CME");
        System.out.println("  k=4（倒数第二） → cursor=4, size=4 → 4 == 4 → hasNext 为 false → 循环结束，不抛");
        System.out.println("  k=1             → cursor=1, size=4 → 1 != 4 → 继续 → CME");
    }

    private static void run(int target, String title) {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("  初始 " + list + "，目标 " + target);
        try {
            for (Integer x : list) {
                System.out.println("    next() 返回 " + x);
                if (x == target) {
                    list.remove(Integer.valueOf(target));
                    System.out.println("    删除 " + target + "，此时 size=" + list.size());
                }
            }
            System.out.println("  " + title + " → 没有抛 CME，结果 " + list);
        } catch (ConcurrentModificationException e) {
            System.out.println("  " + title + " → 抛出 ConcurrentModificationException");
        }
    }
}
