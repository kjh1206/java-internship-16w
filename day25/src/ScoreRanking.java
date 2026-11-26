import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 练习 2：用 TreeMap 实现"按分数排名"。
 *
 * <p>三个考点：
 * <ol>
 *   <li>同分会互相覆盖 —— 所以 value 用 List&lt;String&gt; 存同分的多个人</li>
 *   <li>倒序：构造器传 Comparator.reverseOrder()，遍历就是从高分到低分</li>
 *   <li>范围查询：tailMap(85, true) 是"分数 ≥ 85"（第二个参数 = 是否包含边界）</li>
 * </ol>
 */
public class ScoreRanking {

    /** 学生数据：姓名 + 分数。 */
    static final String[][] DATA = {
            {"张三", "88"}, {"李四", "92"}, {"王五", "76"}, {"赵六", "85"},
            {"钱七", "92"}, {"孙八", "68"}, {"周九", "95"}, {"吴十", "81"},
            {"郑一", "85"}, {"王二", "79"}
    };

    public static void main(String[] args) {
        // ---------- 1. 分数为 key、姓名列表为 value，按分数倒序 ----------
        TreeMap<Integer, List<String>> byScore = new TreeMap<>(Comparator.reverseOrder());
        for (String[] d : DATA) {
            int score = Integer.parseInt(d[1]);
            // computeIfAbsent：key 不存在就先建一个空 List，再往里加名字
            byScore.computeIfAbsent(score, k -> new ArrayList<>()).add(d[0]);
        }
        System.out.println("按分数倒序: " + byScore);

        // ---------- 2. 输出分数最高的 3 个人 ----------
        System.out.print("分数最高的 3 个人: ");
        int need = 3;
        for (Map.Entry<Integer, List<String>> e : byScore.entrySet()) {
            for (String name : e.getValue()) {
                if (need-- > 0) {
                    System.out.print(name + "(" + e.getKey() + ") ");
                }
            }
            if (need <= 0) {
                break;
            }
        }
        System.out.println();

        // ---------- 3. 查询分数 >= 85 的所有人 ----------
        // 注意：tailMap 要有序，所以这里用自然序的 TreeMap（升序）
        TreeMap<Integer, List<String>> natural = new TreeMap<>();
        for (String[] d : DATA) {
            natural.computeIfAbsent(Integer.parseInt(d[1]), k -> new ArrayList<>()).add(d[0]);
        }

        // tailMap(85, true)：从 85 开始到最后，true 表示包含 85 本身
        System.out.println("分数 >= 85: " + natural.tailMap(85, true));
        System.out.println("分数 >= 85 的人数: "
                + natural.tailMap(85, true).values().stream().mapToInt(List::size).sum());

        // 注意：tailMap(fromKey) 默认【包含】fromKey（和 headMap 相反），
        // 想要"大于 85"必须显式写 tailMap(85, false)
        System.out.println("分数 >= 85（tailMap 默认含边界）: " + natural.tailMap(85));
        System.out.println("分数 > 85（显式 false 才不含）: " + natural.tailMap(85, false));

        // ---------- 4. 降序的另一种写法：descendingMap ----------
        System.out.println("最高分: " + natural.lastKey() + "，最低分: " + natural.firstKey());
        System.out.println("倒序遍历前 3 个分数: "
                + new ArrayList<>(natural.descendingMap().keySet()).subList(0, 3));
    }
}
