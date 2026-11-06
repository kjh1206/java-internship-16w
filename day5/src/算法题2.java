public class 算法题2 {
    class Solution {
        public int findMaxConsecutiveOnes(int[] nums) {
            int maxCount = 0;
            int count = 0;
            for(int i=0;i<nums.length;i++){
                if(nums[i] == 1){
                    count++;
                    maxCount = Math.max(maxCount, count);
                }
                else{
                    if(nums.length-i <= maxCount) break;
                    count = 0;
                }
            }
            return maxCount;
        }
    }
    public static void main(String args[]) {
        int[] nums = {1,1,0,1,1,1};
        Solution solution = new 算法题2().new Solution();
        int result = solution.findMaxConsecutiveOnes(nums);
        System.out.println("最大连续 1 的个数为: " + result);
    }
}
