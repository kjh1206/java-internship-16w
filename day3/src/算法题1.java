public class 算法题1 {
    public static void main(String[] args) {
        int[] a={0,1,1,2,2,3};
        Solution solution=new Solution();
        int len=solution.removeDuplicates(a);
        System.out.println(len);
        for(int i=0;i<len;i++){
          System.out.print(a[i]+" ");
        }
    }
}
class Solution {
    public int removeDuplicates(int[] nums) {
        if(nums.length==0)return 0;
        int slow=0;
        for(int fast=0;fast<nums.length;fast++){
            if(nums[fast]!=nums[slow]){
                slow++;
                nums[slow]=nums[fast];
            }
        }
        return slow+1;
    }
}