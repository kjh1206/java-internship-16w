import java.util.Arrays;

/**
 * 数组 + 双指针 · 第 2 题：两数之和 II —— 输入有序数组（LeetCode 167）
 *
 * 题目：升序数组 numbers，找两个数使它们相加等于 target，返回它们的下标（从 1 开始）。
 *      假设每个输入只对应一个答案，且同一个元素不能重复使用。
 *
 * 例：numbers = [2,7,11,15], target = 9  →  [1,2]（因为 2 + 7 = 9）
 *
 * 思路一（暴力）：两层循环试所有组合，O(n²)。n 大就超时，先知道它错在哪：
 *                "数组是有序的"这个条件完全没用上。
 *
 * 思路二（左右指针，本题标准解法）：
 *   left 指最左（最小值），right 指最右（最大值），看 sum = numbers[left] + numbers[right]：
 *     sum == target  → 找到，返回
 *     sum <  target  → 说明 left 太小，left++ 换个大点的数
 *     sum >  target  → 说明 right 太大，right-- 换个小点的数
 *
 *   为什么不会漏解（这是这道题的精髓，面试常问）：
 *     当 sum < target 时，numbers[left] 和任何 <= numbers[right] 的数相加都 <= sum < target，
 *     所以 numbers[left] 跟右边任何一个搭档都不可能成立 —— 可以直接把 left 排除掉。
 *     同理 sum > target 时可以把 right 排除掉。每次排除一个元素，共 n 步。
 *
 *   时间 O(n)：left 和 right 各走一遍，最多 n 步
 *   空间 O(1)
 *
 * 思路三（哈希表）：时间 O(n) 但空间 O(n)，而且**没有利用有序性**，面试官会追问"能不能 O(1) 空间"。
 *                  无序数组的两数之和（LeetCode 1）才用哈希表。
 */
public class TwoSumSorted {

    /**
     * @return 长度为 2 的数组，值是 1-based 下标；找不到返回 {-1, -1}
     */
    public static int[] twoSum(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                return new int[]{left + 1, right + 1};  // 题目要求从 1 开始
            } else if (sum < target) {
                left++;      // 左边太小，往右挪
            } else {
                right--;     // 右边太大，往左挪
            }
        }
        return new int[]{-1, -1};
    }

    private static void check(int[] numbers, int target, int[] expect) {
        int[] got = twoSum(numbers, target);
        System.out.print("  " + Arrays.toString(numbers) + " target=" + target
                + " -> " + Arrays.toString(got));
        System.out.println(Arrays.equals(got, expect) ? "  [OK]" : "  [FAIL] 期望 " + Arrays.toString(expect));
    }

    public static void main(String[] args) {
        System.out.println("用例 1（题目示例）");
        check(new int[]{2, 7, 11, 15}, 9, new int[]{1, 2});

        System.out.println("用例 2（答案在两端）");
        check(new int[]{1, 2, 3, 4, 5, 6}, 11, new int[]{5, 6});

        System.out.println("用例 3（负数）");
        check(new int[]{-5, -1, 0, 2, 3}, -1, new int[]{2, 3});

        System.out.println("用例 4（只有两个元素）");
        check(new int[]{1, 2}, 3, new int[]{1, 2});

        System.out.println("用例 5（和为两端之和）");
        check(new int[]{-1, 0, 1, 3}, 2, new int[]{1, 4});

        System.out.println("用例 6（无解，返回 -1,-1）");
        check(new int[]{1, 2}, 10, new int[]{-1, -1});

        // 性能对比：暴力 vs 双指针（n = 20000 时暴力会明显慢）
        System.out.println();
        int n = 20000;
        int[] big = new int[n];
        for (int i = 0; i < n; i++) {
            big[i] = i * 2;          // 0,2,4,... 递增
        }
        int target = big[n - 2] + big[n - 1];

        long t1 = System.nanoTime();
        int[] r1 = bruteForce(big, target);
        long t2 = System.nanoTime();
        int[] r2 = twoSum(big, target);
        long t3 = System.nanoTime();

        System.out.println("n = " + n + "，target = " + target);
        System.out.println("  暴力     : " + Arrays.toString(r1) + "  用时 " + (t2 - t1) / 1_000_000.0 + " ms");
        System.out.println("  双指针   : " + Arrays.toString(r2) + "  用时 " + (t3 - t2) / 1_000_000.0 + " ms");
    }

    /** 仅供对比用的暴力解法 */
    static int[] bruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i + 1, j + 1};
                }
            }
        }
        return new int[]{-1, -1};
    }
}
