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
        ListNode cur=new ListNode();
        dummy.next=head.next;
        cur=dummy.next;
        for(;cur.next!=null;cur=cur.next){
            dummy.next.next=head;
            head=dummy.next;
            dummy.next=cur.next;
        }
        dummy.next.next=head;
        head=dummy.next;
        dummy.next=cur.next;
        dummy.next.next=head;
        return dummy.next;
    }
}