import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * 练习③：验证 set(i, v) 不修改 modCount。
 *
 * 三层证据：
 *   1. 行为层：遍历中 set 不抛；遍历中 add 抛 CME。
 *   2. 源码层：ArrayList.set 只做 elementData[index] = element，没有 modCount++。
 *   3. 反射层：直接读出 modCount，set 前后不变，add 前后 +1。
 */
public class SetVsAdd {

    public static void main(String[] args) throws Exception {
        behavior();
        reflect();
    }

    private static void behavior() {
        System.out.println("=== ③-1 行为：遍历中 set vs add ===");

        List<Integer> a = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        try {
            for (Integer x : a) {
                a.set(0, 99);          // 只改内容，不改结构
            }
            System.out.println("  遍历中 set(0, 99) → 没有抛，list = " + a);
        } catch (ConcurrentModificationException e) {
            System.out.println("  遍历中 set → 抛了 CME（预期之外）");
        }

        List<Integer> b = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        try {
            for (Integer x : b) {
                b.add(99);             // 改结构，size 变大
            }
            System.out.println("  遍历中 add(99) → 没有抛（预期之外），list = " + b);
        } catch (ConcurrentModificationException e) {
            System.out.println("  遍历中 add(99) → 抛出 ConcurrentModificationException");
        }
        System.out.println();
    }

    /** 用反射直接读 modCount（字段在 AbstractList 里，需要 --add-opens java.base/java.util=ALL-UNNAMED） */
    private static void reflect() throws Exception {
        Field f = java.util.AbstractList.class.getDeclaredField("modCount");
        f.setAccessible(true);

        ArrayList<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        int before = f.getInt(list);

        list.set(0, 99);
        list.set(1, 98);
        int afterSet = f.getInt(list);

        list.add(6);
        int afterAdd = f.getInt(list);

        list.remove(0);
        int afterRemove = f.getInt(list);

        System.out.println("=== ③-2 反射读 modCount ===");
        System.out.println("  创建时        modCount = " + before);
        System.out.println("  set 两次之后   modCount = " + afterSet + "   （变化 " + (afterSet - before) + "）");
        System.out.println("  add 一次之后   modCount = " + afterAdd + "   （变化 " + (afterAdd - afterSet) + "）");
        System.out.println("  remove 一次后  modCount = " + afterRemove + "   （变化 " + (afterRemove - afterAdd) + "）");
        System.out.println("  结论：set 是「原地改元素」，不算结构修改；add/remove 才 ++modCount。");
        System.out.println("        所以遍历中 set 安全（但不推荐），遍历中 add/remove 必炸。");
    }
}
