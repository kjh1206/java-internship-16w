
public class 算法题1 {
    public static void main(String agrs[]){
        ListNode dummy=new ListNode();
    }
}
class ListNode {
    int val;
    ListNode next;

    ListNode() { }
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
class Solution {
    public ListNode removeElements(ListNode head, int val) {
        ListNode dummy=new ListNode(0,head);
        if(dummy.next.val==val){
            dummy.next=dummy.next.next;
        }else{
            dummy=dummy.next;
        }
        return dummy.next;
    }
}