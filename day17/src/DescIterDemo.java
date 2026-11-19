import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * 题三：从尾到头打印 LinkedList。
 *
 * 三种写法：
 *   ① "自己写 prev 版本"：用 ListIterator 从末尾往前（等价于沿 node.prev 走）
 *      —— 如果你用的是题二那种自己写的链表，就是 `for (Node x = last; x != null; x = x.prev)`
 *   ② JDK 现成的 list.descendingIterator()
 *   ③ 反例：for (int i = size-1; i>=0; i--) list.get(i)
 *      —— 能跑，但每次 get 都要定位，整体 O(n^2)
 */
public class DescIterDemo {

    public static void main(String[] args) {
        LinkedList<String> list = new LinkedList<>();
        for (String s : new String[]{"a", "b", "c", "d", "e"}) {
            list.add(s);
        }
        System.out.println("=== 题三：从尾到头打印 ===");
        System.out.println("  list = " + list);

        // ① 沿 prev 走（用 ListIterator 的 previous()）
        System.out.print("  ① prev 版（ListIterator 倒着走）：");
        StringBuilder sb1 = new StringBuilder();
        for (ListIterator<String> it = list.listIterator(list.size()); it.hasPrevious(); ) {
            sb1.append(it.previous()).append(' ');
        }
        System.out.println(sb1);

        // ② descendingIterator
        System.out.print("  ② descendingIterator：           ");
        StringBuilder sb2 = new StringBuilder();
        for (Iterator<String> it = list.descendingIterator(); it.hasNext(); ) {
            sb2.append(it.next()).append(' ');
        }
        System.out.println(sb2);

        // ③ 反例：下标 + get(i)
        System.out.print("  ③ get(i) 倒序（慢）：            ");
        StringBuilder sb3 = new StringBuilder();
        for (int i = list.size() - 1; i >= 0; i--) {
            sb3.append(list.get(i)).append(' ');
        }
        System.out.println(sb3);

        // ---- 规模一大就见真章：2 万元素求和 ----
        LinkedList<Integer> big = new LinkedList<>();
        for (int i = 0; i < 20_000; i++) {
            big.add(i);
        }

        long t1 = System.nanoTime();
        long sum1 = 0;
        for (int i = big.size() - 1; i >= 0; i--) {
            sum1 += big.get(i);
        }
        long d1 = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        long sum2 = 0;
        for (Iterator<Integer> it = big.descendingIterator(); it.hasNext(); ) {
            sum2 += it.next();
        }
        long d2 = System.nanoTime() - t2;

        System.out.printf("%n  2 万元素求和：%n");
        System.out.printf("    get(i) 倒序        : %8.1f ms（结果 %d）%n", d1 / 1e6, sum1);
        System.out.printf("    descendingIterator : %8.2f ms（结果 %d）%n", d2 / 1e6, sum2);
        System.out.printf("    倍数：%.0f 倍%n", (double) d1 / d2);
        System.out.println("  原因：get(i) 每次从近端走到 i 是 O(n)，循环 n 次 → O(n^2)；迭代器是一条链走到底 O(n)。");
    }
}
