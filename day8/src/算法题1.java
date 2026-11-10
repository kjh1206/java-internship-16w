
public class 算法题1 {
    public static void main(String[] args){
        int[] a=new int[]{1,2,6,3,4,5,6};
        ListNode dummy=new ListNode();
        dummy=dummy.build(a);
        Solution solution=new Solution();
       System.out.println( dummy.toString(solution.removeElements(dummy,6)));
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode() { }
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    ListNode build(int[] arr){
        ListNode dummy=new ListNode(0);
        ListNode cur=dummy;
        for(int v:arr){
            cur.next=new ListNode(v);
            cur=cur.next;
        }
        return dummy.next;
    }
    String toString(ListNode dummy){
        StringBuilder s=new StringBuilder("[");
        for(ListNode cur=dummy;cur!=null;cur=cur.next){
            s.append(cur.val);
            if(cur.next!=null)s.append(",");
        }
        s.append("]");
        return s.length()==0?" ":s.toString();
    }
}
class Solution {
    public ListNode removeElements(ListNode head, int val) {
        ListNode dummy=new ListNode(0);
        dummy.next=head;
        ListNode prev = dummy;     // 站在待检查节点的前一个
        ListNode cur = head;       // 当前要检查的节点
        while(cur!=null){
            if(cur.val==val){
                prev.next=cur.next;
            }else{
                prev=cur;
            }
            cur=cur.next;
        }
        return dummy.next;
    }
}