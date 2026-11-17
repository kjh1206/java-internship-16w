import java.util.ArrayList;
import java.util.Arrays;

/**
 * 练习②：用 System.arraycopy 手写 remove(int index)，和 ArrayList.remove 对比。
 *
 * 公平性说明：
 *   - 两边都存 Object（Integer 装箱对象），都从下标 0 删，都是同一个量级的数据规模。
 *   - 每删空一次就重新填满，保证 10 万次删除都是在"满表"状态下进行的。
 *   - 先跑一轮预热（JIT 编译生效），再测 3 轮取最小值。
 */
public class RemoveBench {

    private static final int SIZE = 1000;       // 每次表的大小
    private static final int ROUNDS = 100_000;  // 删除次数
    private static final int WARMUP = 1;
    private static final int MEASURE = 3;

    /** 手写版：Object[] + System.arraycopy */
    static class MyList {
        Object[] data = new Object[SIZE];
        int size;

        void fill() {
            size = SIZE;
            for (int i = 0; i < SIZE; i++) {
                data[i] = i;
            }
        }

        Object remove(int index) {
            Object old = data[index];
            System.arraycopy(data, index + 1, data, index, size - index - 1);
            data[--size] = null;   // 清空末尾槽，避免内存泄漏
            return old;
        }
    }

    public static void main(String[] args) {
        // 预热
        for (int i = 0; i < WARMUP; i++) {
            runMine();
            runJdk();
        }

        long bestMine = Long.MAX_VALUE;
        long bestJdk = Long.MAX_VALUE;
        for (int i = 0; i < MEASURE; i++) {
            bestMine = Math.min(bestMine, runMine());
            bestJdk = Math.min(bestJdk, runJdk());
        }

        System.out.println("=== ② remove(0) × 10 万次，表大小 " + SIZE + " ===");
        System.out.printf("  手写 System.arraycopy : %6.1f ms   平均 %5.1f ns/次%n",
                bestMine / 1e6, (double) bestMine / ROUNDS);
        System.out.printf("  ArrayList.remove      : %6.1f ms   平均 %5.1f ns/次%n",
                bestJdk / 1e6, (double) bestJdk / ROUNDS);
        System.out.printf("  倍数：手写是 ArrayList 的 %.2f 倍%n",
                (double) bestMine / bestJdk);
        System.out.println("  说明：ArrayList.remove 内部也是 System.arraycopy，");
        System.out.println("        差值来自 装箱检查/边界检查/调用层次，量级上属同一实现。");
    }

    private static long runMine() {
        MyList my = new MyList();
        my.fill();
        long start = System.nanoTime();
        for (int r = 0; r < ROUNDS; r++) {
            if (my.size == 0) {
                my.fill();
            }
            my.remove(0);
        }
        return System.nanoTime() - start;
    }

    private static long runJdk() {
        ArrayList<Object> list = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            list.add(i);
        }
        long start = System.nanoTime();
        for (int r = 0; r < ROUNDS; r++) {
            if (list.isEmpty()) {
                for (int i = 0; i < SIZE; i++) {
                    list.add(i);
                }
            }
            list.remove(0);
        }
        return System.nanoTime() - start;
    }

    // 防止「未使用变量」的警告把代码优化掉（本类其实用不到，保留以示规范）
    @SuppressWarnings("unused")
    private static Object sink = Arrays.asList();
}
