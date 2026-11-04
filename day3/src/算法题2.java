public class 算法题2 {
    public static void main(String[] args) {
        int[] a ={0,1,2,2,3,0,4,2};
        int val = 3;
        Solution1 solution=new Solution1();
        int len=solution.removeElement(a,val);
        System.out.println(len);
        for(int i=0;i<len;i++){
            System.out.print(a[i]+" ");
        }
    }
}
class Solution1 {
    public int removeElement(int[] nums, int val) {
        if(nums.length==0)return 0;
        int slow=0;
        for(int fast=0;fast<nums.length;fast++){
            if(nums[fast]!=val){
                nums[slow]=nums[fast];
                slow++;
            }
        }
        return slow;
    }
}