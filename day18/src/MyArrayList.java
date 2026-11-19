import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 手写的 ArrayList（简化版），用来吃透 JDK 里那 3000 行到底在干嘛。
 *
 * <p>核心只有三件事：
 * <ol>
 *   <li>一个 {@code Object[] data} 存元素 —— 数组，所以随机访问 O(1)；</li>
 *   <li>一个 {@code int size} 记"装了几个" —— 注意它和 {@code data.length} 不是一回事；</li>
 *   <li>一个 {@code int modCount} 记"结构改过几次" —— 迭代器靠它发现"边遍历边改"。</li>
 * </ol>
 *
 * <p>为什么用 {@code Object[]} 而不是 {@code E[]}：泛型在运行期会被擦除，
 * 你没法 {@code new E[10]}（编译不过）。JDK 里也是用 Object[]，取出时再强转。
 */
public class MyArrayList<E> implements Iterable<E> {

    /** 空表时的共享空数组：避免每次 new MyArrayList() 都创建一个新数组对象。 */
    private static final Object[] EMPTY = {};

    /** 第一次 add 时分配的容量（和 JDK 的 DEFAULT_CAPACITY 一致）。 */
    private static final int DEFAULT_CAP = 10;

    private Object[] data;   // 底层数组，长度 = 容量（可能比 size 大）
    private int size;        // 实际元素个数
    private int modCount;    // 结构修改次数（add/remove 才 ++，set 不算）

    public MyArrayList() {
        this.data = EMPTY;   // 延迟分配：先不占内存，第一次 add 才扩到 10
    }

    public MyArrayList(int cap) {
        if (cap < 0) throw new IllegalArgumentException("容量不能为负: " + cap);
        this.data = cap == 0 ? EMPTY : new Object[cap];
    }

    // ---------------------------------------------------------------- 增

    /** 尾部追加。均摊 O(1)：多数情况直接放，满了才扩容。 */
    public boolean add(E e) {
        ensureCapacity(size + 1);
        data[size++] = e;
        modCount++;
        return true;
    }

    /** 中间插入。O(n)：index 之后的元素都要整体后移一格。 */
    public void add(int index, E e) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        ensureCapacity(size + 1);
        // 把 [index, size) 这段整体往后搬一格，空出 index 这个位置
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = e;
        size++;
        modCount++;
    }

    // ---------------------------------------------------------------- 查 / 改

    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) data[index];   // 擦除后是 Object，取出来强转（JDK 里也是这么干的）
    }

    public E set(int index, E e) {
        checkIndex(index);
        @SuppressWarnings("unchecked")
        E old = (E) data[index];
        data[index] = e;
        return old;               // 注意：set 不改变结构，所以 modCount 不加
    }

    // ---------------------------------------------------------------- 删

    /** 删指定位置。O(n)：后面的元素整体前移一格。 */
    public E remove(int index) {
        checkIndex(index);
        @SuppressWarnings("unchecked")
        E old = (E) data[index];
        int moved = size - index - 1;          // 要搬几个元素
        if (moved > 0) {
            System.arraycopy(data, index + 1, data, index, moved);
        }
        data[--size] = null;                   // 关键：最后一格置 null，否则数组还引用着它，GC 回收不掉
        modCount++;
        return old;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    // ---------------------------------------------------------------- 扩容

    /**
     * 保证容量够用。1.5 倍增长：新容量 = 旧容量 + 旧容量/2。
     *
     * <p>为什么是 1.5 而不是 2：1.5 倍在"扩容次数"和"浪费的空间"之间比较平衡。
     * 用 2 倍，容量序列 10,20,40,80… 增长速度太快，容易浪费一半内存；
     * 1.5 倍还带来一个好处：新容量是旧容量的一半增长，多次扩容后的总和
     * 能更好地被复用（JDK 里 ArrayList 正是这么选的）。
     */
    private void grow(int minCapacity) {
        int oldCap = data.length;
        int newCap = oldCap + (oldCap >> 1);   // >>1 就是除以 2（位运算比除法快）
        if (newCap < minCapacity) newCap = minCapacity;
        if (oldCap == 0) newCap = Math.max(DEFAULT_CAP, minCapacity);
        data = java.util.Arrays.copyOf(data, newCap);   // copyOf 会新开数组并拷过去
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) grow(minCapacity);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
    }

    // ---------------------------------------------------------------- 迭代器

    /**
     * 简易迭代器，带 fail-fast 检查。
     *
     * <p>为什么需要它：for-each 语法糖编译后就是 {@code Iterator} 循环。
     * 迭代器创建时记录当时的 modCount（叫 expectedModCount）；
     * 每次 next() 都检查"现在的 modCount 还是不是当初那个"，
     * 不是就说明别人在遍历过程中改过结构 → 立刻抛 {@link ConcurrentModificationException}。
     *
     * <p>为什么 {@code iterator.remove()} 不抛：它删完元素后会自己同步
     * {@code expectedModCount = modCount}，所以下一轮检查仍然通过。
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int cursor = 0;               // 下一个要返回的下标
            private int lastRet = -1;             // 上一次 next() 返回的下标，-1 表示没有
            private int expectedModCount = modCount;

            @Override
            public boolean hasNext() { return cursor != size; }

            @Override
            public E next() {
                checkForComodification();
                if (cursor >= size) throw new NoSuchElementException();
                lastRet = cursor;
                @SuppressWarnings("unchecked")
                E e = (E) data[cursor++];
                return e;
            }

            @Override
            public void remove() {
                if (lastRet < 0) throw new IllegalStateException();
                checkForComodification();
                MyArrayList.this.remove(lastRet);   // 外部类的 remove
                cursor = lastRet;                  // 后面的元素前移了，游标要退一格
                lastRet = -1;
                expectedModCount = modCount;       // 同步：这是 iterator.remove 不抛 CME 的原因
            }

            private void checkForComodification() {
                if (modCount != expectedModCount) throw new ConcurrentModificationException();
            }
        };
    }

    // 方便打印：[1, 2, 3]
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(data[i]);
        }
        return sb.append("]").toString();
    }
}
