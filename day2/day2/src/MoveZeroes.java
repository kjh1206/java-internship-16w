/**
 * 数组 + 双指针 · 第 1 题：移动零（LeetCode 283）
 *
 * 题目：给定数组 nums，把所有 0 移到末尾，同时保持非零元素的相对顺序。
 *      要求原地操作，不能拷贝新数组。
 *
 * 例：[0,1,0,3,12] → [1,3,12,0,0]
 *
 * 思路一（快慢指针，本题标准解法）：
 *   slow 指向"下一个非零元素该放的位置"，fast 负责扫描。
 *   fast 遇到非零就写到 slow 位置，然后 slow++。
 *   扫完之后，从 slow 到末尾全部填 0。
 *
 *   为什么正确：非零元素按 fast 扫描顺序依次写到前面，相对顺序天然保持；
 *              没被覆盖的位置只能是 0（因为所有非零都被搬走了）。
 *
 *   时间 O(n)：每个元素最多被看两次（fast 一次、补零一次）
 *   空间 O(1)：只用两个下标
 *
 * 思路二（更短的写法，交换法）：
 *   fast 遇到非零就和 slow 交换，slow++。这样 0 会被换到后面去。
 *   少一次补零循环，但每次交换多两次写操作。两种都对。
 */
public class MoveZeroes {

    /** 快慢指针 + 末尾补零 */
    public static void moveZeroes(int[] nums) {
        int slow = 0;
        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != 0) {
                nums[slow] = nums[fast];
                slow++;
            }
        }
        // 剩下没填的位置必然是 0
        while (slow < nums.length) {
            nums[slow] = 0;
            slow++;
        }
    }

    /** 交换法（推荐背这个，代码短） */
    public static void moveZeroesSwap(int[] nums) {
        int slow = 0;
        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != 0) {
                int tmp = nums[slow];
                nums[slow] = nums[fast];
                nums[fast] = tmp;
                slow++;
            }
        }
    }

    private static void check(int[] input, int[] expect) {
        moveZeroes(input);
        System.out.print("  快慢指针 -> " + java.util.Arrays.toString(input));
        System.out.println(java.util.Arrays.equals(input, expect) ? "  [OK]" : "  [FAIL] 期望 " + java.util.Arrays.toString(expect));

        int[] input2 = {0, 1, 0, 3, 12};
        moveZeroesSwap(input2);
        System.out.println("  交换法   -> " + java.util.Arrays.toString(input2)
                + (java.util.Arrays.equals(input2, expect) ? "  [OK]" : "  [FAIL]"));
    }

    public static void main(String[] args) {
        System.out.println("用例 1：[0,1,0,3,12] → [1,3,12,0,0]");
        check(new int[]{0, 1, 0, 3, 12}, new int[]{1, 3, 12, 0, 0});

        System.out.println("用例 2：[0] → [0]");
        int[] a = {0};
        moveZeroes(a);
        System.out.println("  " + java.util.Arrays.toString(a));

        System.out.println("用例 3：[1,2,3] → [1,2,3]（没有 0）");
        int[] b = {1, 2, 3};
        moveZeroes(b);
        System.out.println("  " + java.util.Arrays.toString(b));

        System.out.println("用例 4：[0,0,1] → [1,0,0]");
        int[] c = {0, 0, 1};
        moveZeroes(c);
        System.out.println("  " + java.util.Arrays.toString(c));

        System.out.println("用例 5：[1,0] → [1,0]");
        int[] d = {1, 0};
        moveZeroes(d);
        System.out.println("  " + java.util.Arrays.toString(d));
    }
}
