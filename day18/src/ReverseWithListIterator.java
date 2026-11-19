import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * 题三：用 ListIterator 原地反转列表（不许用 Collections.reverse）。
 *
 * 思路：双指针 + ListIterator。
 *   左指针从 0 往后，右指针从 size 往前；
 *   每次交换 left.next() 与 right.previous() 的值，走到中间就停。
 *
 * 为什么能"原地"：ListIterator.set() 会覆盖当前元素，不改变结构（不改 modCount）。
 */
public class ReverseWithListIterator {

    public static <T> void reverseInPlace(List<T> list) {
        ListIterator<T> left = list.listIterator();
        ListIterator<T> right = list.listIterator(list.size());
        // 每次循环消耗左右各一个元素，比较两个游标即可判断是否相遇
        while (left.nextIndex() < right.previousIndex()) {
            T l = left.next();
            T r = right.previous();
            left.set(r);
            right.set(l);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 题三：ListIterator 原地反转 ===");

        for (int n : new int[]{1, 2, 3, 4, 5, 6}) {
            List<Integer> list = new ArrayList<>();
            for (int i = 1; i <= n; i++) list.add(i);
            String before = list.toString();
            reverseInPlace(list);
            System.out.printf("  %s → %s%n", before, list);
        }

        // 非 Integer 也能用：泛型方法
        List<String> words = new ArrayList<>(List.of("a", "b", "c"));
        reverseInPlace(words);
        System.out.println("  [a, b, c] → " + words);

        // 对照：JDK 的 Collections.reverse 结果应一致
        List<Integer> check = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        List<Integer> copy = new ArrayList<>(check);
        reverseInPlace(check);
        java.util.Collections.reverse(copy);
        System.out.println("  与 Collections.reverse 结果一致：" + check.equals(copy) + "（" + check + "）");
    }
}
