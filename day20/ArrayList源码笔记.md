# ArrayList 源码笔记

> 字数：约 950 字（不含代码）；数据均为本机 JDK 21 实测，采集程序 `tmp2/note/NoteData.java`。

## 一、底层结构

ArrayList 就是一个会自己长大的数组：

```java
transient Object[] elementData;   // 真正存数据的地方
private int size;                 // 已用了几个格子
protected transient int modCount; // 结构修改次数（定义在 AbstractList）
```

`elementData` 的长度是**容量**，`size` 是**元素个数**，二者不同步。
所以 `get(i)` 是 `elementData[i]` 的数组下标访问，O(1)；代价是数组必须连续，中间插删要搬移。

## 二、扩容机制

公式（JDK 8+）：

```java
int newCapacity = oldCapacity + (oldCapacity >> 1);   // 1.5 倍，右移比除法快
```

规则：新容量 = `old + old/2`；若仍不够（比如 `addAll` 一次加很多），直接用需要的最小容量；上限是 `Integer.MAX_VALUE - 8`。

**真实序列**（实测，反射读 `elementData.length`）：

```
10(加第1个)  15(第11个)  22(第16个)  33(第23个)  49(第34个)  73(第50个)
```

不是"每次 ×1.5 就取整"这么简单——`10 → 15 → 22 → 33 → 49 → 73`，每次都是 `旧 + 旧/2` 向下取整。
`new ArrayList<>()` 的初始容量是 **0**（共享空数组），第一次 `add` 才扩到 10；`new ArrayList<>(16)` 直接分配 16。

为什么是 1.5 倍而不是 2 倍：1.5 倍在"扩容次数"和"空间浪费"之间折中——2 倍平均浪费约 50% 空间，1.5 倍约 25%，而扩容次数只多约 1.7 倍；几何级数增长都保证均摊 O(1)。

## 三、size 与 capacity 的区别

| | 含义 | 谁在用 |
|---|---|---|
| `size` | 已有元素个数 | 业务代码（`list.size()`） |
| `capacity` | 数组长度，能装多少 | 内部（`elementData.length`） |

三个直接影响：

1. `size()` 返回 `size`，不是数组长度——`new ArrayList<>(100)` 的 `size()` 是 0；
2. 遍历必须用 `size`，用 `elementData.length` 会读到 null；
3. 已知规模就 `new ArrayList<>(n)`，实测 10 万次 add：默认构造 **1.2~4.2 ms**，预分配 **0.7~1.1 ms**，省掉约 3~4 倍（主要是省掉 11 次扩容的数组复制）。

## 四、modCount 与 fail-fast

`modCount` 记录**结构修改**（增删、clear），`set` 不算——实测轨迹：

```
创建 0 → add 1 → add 3 → set(0,99) 3（不变）→ get(1) 3（不变）→ remove(0) 4 → clear() 5
```

迭代器创建时记下 `expectedModCount = modCount`，之后**每次 `next()` 检查两者是否相等**，不等就抛 `ConcurrentModificationException`。注意：`hasNext()` 不检查，它只看 `cursor != size`。

这就是"删倒数第二个不抛"的原因——实测 `[1,2,3,4,5]` 遍历中删第 k 个：

| 删除位置 | 结果 |
|---|---|
| 第 1、2、3 个 | 抛 CME |
| **第 4 个（倒数第二）** | 不抛，结果 `[1,2,3,5]` |
| 第 5 个（最后一个） | 抛 CME |

删第 4 个后 `cursor(4) == size(4)`，`hasNext()` 直接 false，`next()` 再没被调用，所以没有检查、也没有抛。
**"删最后一个不抛 CME"是错的**——这是本周最容易记错的一条。

正确删除：`iterator.remove()`（删完对齐 `expectedModCount`，且 `cursor = lastRet`）、`removeIf`、或倒序 for。

## 五、其他实测数据

| 场景 | 数据 | 结论 |
|---|---|---|
| 尾部 `add` 1 万次 | 0.15 ms | 均摊 O(1) |
| 头部 `add(0, x)` 1 万次 | 1.98 ms（**13 倍**） | 每次搬 1 万个元素，O(n²) |
| `remove(0)` 10 万次（size=1000） | 4.2 ms（41.9 ns/次） | 成本全在 `arraycopy` |
| 1 万元素 `removeAll(List)` | 20.93 ms | contains 是 O(m) |
| 1 万元素 `removeAll(HashSet)` | 0.15 ms（**139 倍**） | 赢在 contains 变 O(1) |

## 六、使用建议

- 能用 ArrayList 就别用 LinkedList（除头尾增删 + 顺序遍历）；
- 已知规模必须预分配；
- 别在 `for-each` 里 `list.remove`，用 `iterator.remove()` 或 `removeIf`；
- 大量删除优先转 Set，用 `removeAll(Collection)` 而不是逐条删；
- 需要线程安全且读多写极少 → `CopyOnWriteArrayList`，否则加锁或换容器。
