/**
 * 题二：手写双向链表 MyLinkedList，Node 结构。
 * 支持：addFirst、addLast、removeFirst、get(i)、size。
 *
 * 关键设计：
 *   - 两个哨兵指针 first / last（也可以理解成"头尾引用"），不带头节点。
 *   - addFirst / addLast / removeFirst 全是 O(1)。
 *   - get(i) 走"从近的那头开始"，平均 O(n/4)，最坏 O(n/2)。
 */
public class MyLinkedList<E> {

    private static class Node<E> {
        E item;
        Node<E> prev;
        Node<E> next;

        Node(Node<E> prev, E item, Node<E> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }

    private Node<E> first;
    private Node<E> last;
    private int size;

    /** 头插 O(1) */
    public void addFirst(E e) {
        Node<E> f = first;
        Node<E> newNode = new Node<>(null, e, f);
        first = newNode;
        if (f == null) {
            last = newNode;          // 空表：头尾都是它
        } else {
            f.prev = newNode;
        }
        size++;
    }

    /** 尾插 O(1) */
    public void addLast(E e) {
        Node<E> l = last;
        Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        size++;
    }

    /** 删头 O(1) */
    public E removeFirst() {
        Node<E> f = first;
        if (f == null) {
            throw new java.util.NoSuchElementException("链表为空");
        }
        E element = f.item;
        Node<E> next = f.next;
        first = next;
        if (next == null) {
            last = null;             // 删完变空表
        } else {
            next.prev = null;        // 断开旧头的引用，帮助 GC
        }
        f.item = null;
        f.next = null;
        size--;
        return element;
    }

    /** 取第 index 个（0 起） */
    public E get(int index) {
        checkIndex(index);
        return node(index).item;
    }

    /** 从靠近的一端开始找，平均走 n/4 步 */
    private Node<E> node(int index) {
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++) {
                x = x.next;
            }
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--) {
                x = x.prev;
            }
            return x;
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** 从头打印，用于验证结构是否正确 */
    public String toForwardString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<E> x = first; x != null; x = x.next) {
            sb.append(x.item);
            if (x.next != null) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    /** 从尾打印：反过来沿 prev 走，验证双向链接真的连上了 */
    public String toBackwardString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<E> x = last; x != null; x = x.prev) {
            sb.append(x.item);
            if (x.prev != null) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    public static void main(String[] args) {
        MyLinkedList<String> list = new MyLinkedList<>();
        System.out.println("=== 题二：手写双向链表 ===");

        list.addLast("b");
        list.addFirst("a");
        list.addLast("c");
        System.out.println("  addFirst(a) → addLast(b) → addLast(c)");
        System.out.println("  正向：" + list.toForwardString() + "  size=" + list.size());
        System.out.println("  反向：" + list.toBackwardString());

        System.out.println("  get(0)=" + list.get(0) + "，get(1)=" + list.get(1) + "，get(2)=" + list.get(2));
        System.out.println("  removeFirst() = " + list.removeFirst());
        System.out.println("  删头后正向：" + list.toForwardString() + "  size=" + list.size());

        list.addFirst("z");
        System.out.println("  addFirst(z) 后：" + list.toForwardString());

        // 边界测试
        MyLinkedList<Integer> empty = new MyLinkedList<>();
        try {
            empty.removeFirst();
        } catch (Exception e) {
            System.out.println("  空表 removeFirst → " + e.getClass().getSimpleName());
        }
        try {
            list.get(99);
        } catch (Exception e) {
            System.out.println("  get(99) → " + e.getClass().getSimpleName());
        }

        // 交替头尾插，验证 first/last 两个指针都对
        MyLinkedList<Integer> l2 = new MyLinkedList<>();
        for (int i = 1; i <= 6; i++) {
            if (i % 2 == 1) l2.addFirst(i);
            else l2.addLast(i);
        }
        System.out.println("  交替插入后正向：" + l2.toForwardString());
        System.out.println("  交替插入后反向：" + l2.toBackwardString() + "（若与正向相反且无缺失，说明 prev 链正确）");
    }
}
