import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 练习 1：用 CopyOnWriteArrayList 实现监听器列表。
 * 要求：注册 / 注销 / 触发全部，且"遍历中注销"不抛异常。
 *
 * COW 的关键：fireAll 里的 for-each 拿的是**快照数组**，
 * 回调里 add/remove 只是造一个新数组换引用，正在跑的循环完全不受影响。
 */
public class CowListenerList {

    interface Listener {
        void onEvent(String event);
    }

    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

    public void register(Listener l) {
        listeners.add(l);            // 写时复制
    }

    public void unregister(Listener l) {
        listeners.remove(l);         // 写时复制
    }

    public int size() {
        return listeners.size();
    }

    /** 触发全部：遍历的是创建迭代器那一刻的快照 */
    public void fireAll(String event) {
        for (Listener l : listeners) {
            l.onEvent(event);        // 回调里可以随便 add/remove
        }
    }

    public static void main(String[] args) {
        CowListenerList bus = new CowListenerList();

        // 一个"用完就注销自己"的一次性监听器
        Listener[] once = new Listener[1];
        once[0] = e -> {
            System.out.println("    [once] 收到 " + e + "，注销自己");
            bus.unregister(once[0]);
        };

        Listener a = e -> System.out.println("    [A] 收到 " + e);
        Listener c = e -> {
            System.out.println("    [C] 收到 " + e + "，顺手注册 D");
            bus.register(x -> System.out.println("    [D] 收到 " + x));
        };

        bus.register(a);
        bus.register(once[0]);
        bus.register(c);

        System.out.println("=== 第 1 次 fire（once 注销自己、C 注册 D）===");
        System.out.println("  触发前 size = " + bus.size());
        bus.fireAll("order-created");        // 不抛异常
        System.out.println("  触发后 size = " + bus.size() + "（once 走了，D 进来了）");

        System.out.println();
        System.out.println("=== 第 2 次 fire（once 已不在，D 能收到）===");
        bus.fireAll("order-paid");

        System.out.println();
        System.out.println("=== 对照：同样逻辑用 ArrayList ===");
        java.util.List<Listener> plain = new java.util.ArrayList<>();
        plain.add(a);
        plain.add(c);
        plain.add(e -> {
            System.out.println("    [plain-C] 收到 " + e + "，注销 A");
        });
        try {
            for (Listener l : plain) {
                l.onEvent("x");
                if (l == a) plain.remove(c);   // 遍历中途改结构（删的不是最后一个）
            }
            System.out.println("    ArrayList 版没抛？");
        } catch (java.util.ConcurrentModificationException ex) {
            System.out.println("    ArrayList 版 → ConcurrentModificationException");
        }
    }
}
