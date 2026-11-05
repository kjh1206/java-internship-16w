public class Main {
    static void hanoi(int n, char from, char aux, char to) {
        if (n == 0) return;
        hanoi(n - 1, from, to, aux);              // 上面的 n-1 个挪到辅助柱
        System.out.println(n+" "+ from + " -> " + to);   // 最大的那个挪到目标柱
        hanoi(n - 1, aux, from, to);              // 再把 n-1 个挪到目标柱
    }
    static int binarySearch(int[] a, int target, int left, int right) {
        if (left > right) return -1;                       // 出口：区间空
        int mid = left + (right - left) / 2;               // 防溢出
        if (a[mid] == target) return mid;
        if (a[mid] < target) return binarySearch(a, target, mid + 1, right);
        return binarySearch(a, target, left, mid - 1);
    }
    static void swapInt(int a, int b)  { int t = a; a = b; b = t;}
    static void reassign(int[] arr) {
        arr = new int[]{9, 9};   // 造一个新对象，把它的地址写进 arr 这个变量
    }
    public static void main(String[] args) {
        hanoi(3, 'A', 'B', 'C');
        int[] a = {1, 2, 3, 4, 5, 6, 7};
        System.out.println(binarySearch(a, 5, 0, a.length - 1));
        int b = 5, c = 10;
        swapInt(b, c);
        System.out.println(b + " " + c);
        int[] arr = {1, 2};
        reassign(arr);
        System.out.println(arr[0] + " " + arr[1]);
    }

}
