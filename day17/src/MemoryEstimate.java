/**
 * 题五：new ArrayList<>(1000000) 和 new LinkedList<>() 各占多少内存？
 *
 * 结论先写：
 *   ArrayList(1000000)  立刻分配一个容量 100 万的 Object[]，
 *                       引用数组 = 1000000 × 4 字节（压缩指针）+ 对象头 ≈ 4 MB。
 *                       注意：这里只分配"放引用的格子"，元素本身（new 出来的对象）还不存在。
 *
 *   LinkedList()        空表几乎不占内存（只有对象头和两个 null 引用，约 24~32 字节）。
 *                       但每 add 一个元素就要新建一个 Node：
 *                       Node 对象头 16B + prev/item/next 三个引用 12B ≈ 28B，按 8 字节对齐 → 32B；
 *                       item 若是 Integer，还要再加一个 Integer 对象（16B）。
 *                       所以 1 万个元素 ≈ 32 万字节（不含元素对象），100 万个 ≈ 32 MB。
 *
 * 下面用 Runtime 粗测一下，注意 GC 有噪声，只看数量级。
 */
public class MemoryEstimate {

    public static void main(String[] args) throws Exception {
        System.out.println("=== 题五：内存粗测（数量级，不是精确值）===");
        System.out.println("  JVM 最大堆 = " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");

        long before = used();
        Object[] big = new Object[1_000_000];   // 等价于 new ArrayList<>(1000000) 内部那个数组
        long after = used();
        System.out.printf("  new Object[1000000]：增加约 %.1f MB（= 100 万 × 4B 引用 + 头）%n",
                (after - before) / 1024.0 / 1024.0);
        System.out.println("    （ArrayList(1000000) 做的就是这件事：立刻分配这个数组，size 仍是 0）");
        big = null;

        System.gc();
        Thread.sleep(200);

        before = used();
        java.util.LinkedList<Integer> empty = new java.util.LinkedList<>();
        after = used();
        System.out.printf("  new LinkedList<>()（空表）：增加约 %.3f MB（几乎为 0）%n",
                (after - before) / 1024.0 / 1024.0);
        System.out.println("    （空链表只需要一个对象头 + first/last 两个 null 引用）");

        before = used();
        for (int i = 0; i < 1_000_000; i++) {
            empty.add(i);      // 每个元素新建一个 Node，item 是 Integer（i 在缓存外会新建对象）
        }
        after = used();
        System.out.printf("  往空链表 add 100 万个元素：增加约 %.1f MB%n",
                (after - before) / 1024.0 / 1024.0);
        System.out.printf("    估算：100 万 Node × 32B = %.1f MB，再加 Integer 对象（约 16B/个）%n",
                1_000_000 * 32 / 1024.0 / 1024.0);
        System.out.println("    实际会略高：堆碎片、对齐、Integer 缓存外的新对象");

        System.out.println();
        System.out.println("  -> ArrayList(1000000)：预先占了 4 MB 的\"空位\"，之后 add 不再扩容；");
        System.out.println("  -> LinkedList()：起步近乎 0，但每个元素都要付一个 Node 的\"房租\"（32B），");
        System.out.println("     100 万元素 ≈ 32 MB，而且内存是分散的（缓存不友好、GC 压力大）。");
    }

    private static long used() {
        Runtime r = Runtime.getRuntime();
        return r.totalMemory() - r.freeMemory();
    }
}
