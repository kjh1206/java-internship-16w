public class 算法题2 {
    public static void main(String[] args){
        int[] a=new int[]{1,2,3,4,5,6};
        ListNode dummy=new ListNode();
        dummy=dummy.build(a);
        Solution1 solution1=new Solution1();
        System.out.println( dummy.toString(solution1.reverseList(dummy)));
    }
}
class Solution1 {
    public ListNode reverseList(ListNode head) {
        ListNode dummy=new ListNode();
        while(head!=null){
            ListNode cur=head.next;
            head.next=dummy.next;
            dummy.next=head;
            head=cur;
        }
        return dummy.next;

    }
}