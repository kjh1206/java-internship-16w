import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * 练习①：遍历中删除，什么时候抛 CME、什么时候不抛。
 *
 * 真实规律（本文件跑出来的，不是背的）：
 *   抛出条件 = 删完之后 hasNext() 仍为 true，于是又调了一次 next()。
 *   hasNext() 的判断是 cursor != size，所以「不抛」只发生在删完后 cursor == size。
 *   对 for-each 来说，cursor 在 next() 返回第 k 个元素（下标 k-1）后等于 k。
 *   删除该元素后 size 变成 n-1，要满足 k == n-1，即删的是原表倒数第二个元素。
 *
 *   ⇒ 删最后一个：抛（cursor=n，size=n-1，还想再走一轮）
 *   ⇒ 删倒数第二个：不抛（cursor=n-1 == size=n-1，循环直接结束）
 *   ⇒ 删第一个：抛
 */
public class CmeLastFirst {

    public static void main(String[] args) {
        test("① 删最后一个（5）", 5);
        test("② 删倒数第二个（4）", 4);
        test("③ 删第一个（1）", 1);
    }

    private static void test(String title, int target) {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("=== " + title + " ===");
        int visited = 0;
        try {
            for (Integer x : list) {
                visited++;
                if (x == target) {
                    list.remove(Integer.valueOf(target));
                    System.out.println("  访问 " + x + " 时删除，删除后 size=" + list.size());
                }
            }
            System.out.println("  结果：没有抛 CME，共访问 " + visited + " 次，list = " + list);
            System.out.println("  原因：删完后 size=" + list.size() + " 恰好等于游标，hasNext() 为 false，next() 没被再调用");
        } catch (ConcurrentModificationException e) {
            System.out.println("  结果：抛出 ConcurrentModificationException（共访问 " + visited + " 次）");
            System.out.println("  原因：删完后 size=" + list.size() + "，游标还没到末尾，hasNext() 为 true → 又调 next() → 对账失败");
        }
        System.out.println();
    }
}
