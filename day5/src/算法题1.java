public class 算法题1 {
    class Solution {
        public int minSubArrayLen(int target, int[] nums) {
            int left = 0, right = 0;
            int sum = 0;
            int minLength = Integer.MAX_VALUE;

            while (right < nums.length) {
                sum += nums[right];
                right++;

                while (sum >= target) {
                    minLength = Math.min(minLength, right - left);
                    sum -= nums[left];
                    left++;
                }
            }

            return minLength == Integer.MAX_VALUE ? 0 : minLength;
        }
    }
    public static void main(String args[]) {
        int[] nums = {2,3,1,2,4,3};
        int target = 7;
        Solution solution = new 算法题1().new Solution();
        int result = solution.minSubArrayLen(target, nums);
        System.out.println("最小长度为: " + result);
    }
}
