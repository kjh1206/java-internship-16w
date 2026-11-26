import java.util.TreeMap;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        TreeMap<Integer, String> tm = new TreeMap<>();
        for (int x : new int[]{50, 20, 70, 10, 30, 60, 80}) tm.put(x, "v" + x);

        System.out.println(tm.keySet());              // [10, 20, 30, 50, 60, 70, 80]  按 key 升序
        System.out.println(tm.firstKey());            // 10
        System.out.println(tm.lastKey());             // 80
        System.out.println(tm.floorKey(65));          // 60   ≤65 的最大 key
        System.out.println(tm.ceilingKey(65) );       // 70   ≥65 的最小 key
        System.out.println(tm.lowerKey(60) );         // 50   严格小于
        System.out.println(tm.higherKey(60) );        // 70   严格大于
        System.out.println(tm.headMap(50));           // {10,20,30}   不含 50
        System.out.println(tm.tailMap(50) );          // {50,60,70,80} 含 50
        System.out.println(tm.subMap(20, true, 60, false));  // {20,30,50}  左闭右开
        System.out.println(tm.descendingKeySet());    // [80,70,60,50,30,20,10]
        System.out.println(tm.descendingMap());    // 反转视图
    }
}