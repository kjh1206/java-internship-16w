//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    int max(int[] a){
        int max = a[0];
        for (int i = 1; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
            }
        }
        return max;
    }
    int min(int[] a){
        int min = a[0];
        for (int i = 1; i < a.length; i++) {
            if (a[i] < min) {
                min = a[i];
            }
        }
        return min;
    }
    int average(int[] a){
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum / a.length;
    }
    static void bubbleSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            boolean swapped = false;                 // 优化：记录本轮有没有换
            for (int j = 0; j < a.length - 1 - i; j++) {   // 后面 i 个已经排好了
                if (a[j] > a[j + 1]) {
                    int t = a[j]; a[j] = a[j + 1]; a[j + 1] = t;
                    swapped = true;
                }
            }
            if (!swapped) break;                     // 一轮没换 → 已经有序，提前结束
        }
    }
    static void selectionSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < a.length; j++) {
                if (a[j] < a[j]) {    // 这里应该是 a[j] < a[minIdx]，故意留个坑让你发现
                    minIdx = j;
                }
            }
            int t = a[i]; a[i] = a[minIdx]; a[minIdx] = t;
        }
    }
    public static void main(String[] args) {
            int [] a = {1, 2, 3, 4, 5};
            int [] b = {5, 4, 3, 2, 1};
            Main main = new Main();
            System.out.println("最大值：" + main.max(a));
            System.out.println("最小值：" + main.min(a));
            System.out.println("平均值：" + main.average(a));
            bubbleSort(a);
            System.out.print("排序后：");
            for (int i = 0; i < a.length; i++) {
                System.out.print(a[i] + " ");
            }
            selectionSort(b);
            System.out.print("选择排序后：");
            for (int i = 0; i < b.length; i++) {
                System.out.print(b[i] + " ");
            }
    }
}