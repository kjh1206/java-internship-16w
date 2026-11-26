import java.util.ConcurrentModificationException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 练习 3：验证 subMap 是"视图"而不是"拷贝"。
 *
 * <p>四个结论：
 * <ol>
 *   <li>改原表 → 视图跟着变（同一份数据，只是窗口变小了）</li>
 *   <li>改视图 → 原表也变</li>
 *   <li>往视图里写超出范围的值 → IllegalArgumentException: key out of range</li>
 *   <li>遍历视图时改原表 → 抛 CME（视图和原表共用同一个 modCount）</li>
 * </ol>
 */
public class SubMapView {

    public static void main(String[] args) {
        // ---------- 准备数据：1..10 ----------
        TreeMap<Integer, String> tm = new TreeMap<>();
        for (int i = 1; i <= 10; i++) {
            tm.put(i, "v" + i);
        }
        System.out.println("原表: " + tm.keySet());

        // ---------- 1. subMap(3, 7) 是左闭右开 [3,7) ----------
        SortedMap<Integer, String> view = tm.subMap(3, 7);
        System.out.println("subMap(3,7): " + view.keySet() + "   (右开区间，7 不在里面)");

        // ---------- 2. 改原表，视图跟着变 ----------
        tm.put(4, "改过");
        System.out.println("原表 put(4,改过) 后，视图: " + view);

        // ---------- 3. 改视图，原表也变 ----------
        view.put(5, "从视图写");
        System.out.println("视图 put(5) 后，原表的 5 号: " + tm.get(5));

        // ---------- 4. 越界写：抛 IllegalArgumentException ----------
        try {
            view.put(20, "越界");
        } catch (IllegalArgumentException e) {
            System.out.println("view.put(20,..) -> IllegalArgumentException: " + e.getMessage());
        }

        // 边界值也不行：7 不在 [3,7) 里
        try {
            view.put(7, "边界");
        } catch (IllegalArgumentException e) {
            System.out.println("view.put(7,..)  -> IllegalArgumentException: " + e.getMessage());
        }

        System.out.println("视图现在是: " + view.keySet() + "，原表: " + tm.keySet());

        // ---------- 5. 遍历视图时改原表 -> CME ----------
        try {
            for (Integer k : view.keySet()) {
                if (k == 4) {
                    tm.put(100, "新key");   // 结构修改
                }
            }
            System.out.println("遍历视图时改原表：没抛");
        } catch (ConcurrentModificationException e) {
            System.out.println("遍历视图时改原表(新增 key)：抛了 CME");
        }

        // ---------- 6. 想要独立副本怎么办 ----------
        TreeMap<Integer, String> copy = new TreeMap<>(tm.subMap(3, 7));   // 拷进新对象
        copy.put(5, "只改副本");
        System.out.println("副本改成只改副本后，原表的 5 号仍是: " + tm.get(5) + "（互不影响）");

        // ---------- 7. 含边界的写法 ----------
        System.out.println("含边界的 subMap(3,true,7,true): "
                + tm.subMap(3, true, 7, true).keySet());
    }
}
