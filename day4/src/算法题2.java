public class 算法题2 {
    public static void main(String[] args) {
        int[] nums = {-4,-1,0,3,10};
        Solution1 solution = new Solution1();
        int[] result = solution.sortedSquares(nums);
        for (int i = 0; i < result.length; i++) {
            System.out.print(result[i] + " ");
        }
    }
}
class Solution1 {
    public int[] sortedSquares(int[] nums) {
        int m=nums.length;
        int n=0;
        int i=m-1;
        int a[]=new int[m];
        while(n<m){
            if(nums[m-1]*nums[m-1]>nums[n]*nums[n]){
                a[i]=nums[m-1]*nums[m-1];
                i--;
                m--;
            }
            if(nums[m-1]*nums[m-1]<=nums[n]*nums[n]){
                a[i]=nums[n]*nums[n];
                i--;
                n++;
            }
        }
        return a;
    }
}