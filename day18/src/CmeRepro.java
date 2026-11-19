import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * 题一：用自己写的 MyArrayList 复现 CME，再改成 iterator.remove() 的安全版本。
 *
 * MyArrayList 来自 day15/src，它的迭代器和 JDK 一样带 expectedModCount 检查：
 *   hasNext() = cursor != size（不查 modCount）
 *   next()    = 先 checkForComodification()
 *   remove()  = 查 lastRet + 查 modCount，删完把 expectedModCount 对齐
 */
public class CmeRepro {

    public static void main(String[] args) {
        System.out.println("=== 题一：MyArrayList 复现 CME ===");

        // ① 错误写法：foreach 中用 list.remove
        MyArrayList<Integer> bad = new MyArrayList<>();
        for (int i = 1; i <= 5; i++) bad.add(i);
        System.out.println("  初始：" + bad);
        try {
            for (Integer x : bad) {
                System.out.println("    访问 " + x + "，准备删 0 号元素");
                bad.remove(0);
            }
            System.out.println("  没有抛（预期之外）：" + bad);
        } catch (ConcurrentModificationException e) {
            System.out.println("  → ConcurrentModificationException（第一次 next 之后、第二次 next 时触发）");
            System.out.println("  → 崩的时候 list = " + bad + "，说明只删掉了第一个，后面的没处理完");
        }

        // ② 安全写法：iterator.remove()
        MyArrayList<Integer> good = new MyArrayList<>();
        for (int i = 1; i <= 5; i++) good.add(i);
        System.out.println("\n  安全版：初始 " + good);
        Iterator<Integer> it = good.iterator();
        while (it.hasNext()) {
            Integer x = it.next();
            if (x % 2 == 1) {
                it.remove();          // 删完内部会把 expectedModCount 重新对齐
                System.out.println("    删掉奇数 " + x + " → " + good);
            }
        }
        System.out.println("  结果：" + good + "（没有异常）");
    }
}
