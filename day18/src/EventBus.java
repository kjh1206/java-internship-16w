import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 题五：用 CopyOnWriteArrayList 做事件监听器雏形。
 *
 * 要求：遍历监听器发通知时，允许新监听器注册（不能抛 CME）。
 *
 * COW 为什么能做到：
 *   add() 时复制一个新数组、写进去、再换引用；
 *   正在进行的那个 for-each 拿的是旧数组的迭代器，全程不受影响。
 *   代价：本轮通知看不到新注册的监听器（弱一致性），下一轮才生效。
 */
public class EventBus {

    /** 监听器：收到一条事件 */
    interface Listener {
        void onEvent(String event);
    }

    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

    public void register(Listener l) {
        listeners.add(l);          // 写时复制，遍历中的通知不受影响
    }

    public void unregister(Listener l) {
        listeners.remove(l);
    }

    public int size() {
        return listeners.size();
    }

    public void fire(String event) {
        for (Listener l : listeners) {
            l.onEvent(event);
        }
    }

    public static void main(String[] args) {
        EventBus bus = new EventBus();

        Listener a = e -> System.out.println("    [A] 收到 " + e);
        Listener late = e -> System.out.println("    [late] 收到 " + e);

        bus.register(a);

        // 关键场景：dispatch 过程中注册新监听器
        bus.register(e -> {
            System.out.println("    [B] 收到 " + e + "，并在回调里注册 C");
            bus.register(late);
        });

        System.out.println("=== 题五：COW 事件监听器 ===");
        System.out.println("  第一次 fire（B 会在回调里注册 late）：");
        bus.fire("order-created");       // 不抛 CME；late 本轮收不到

        System.out.println("  注册表 size = " + bus.size() + "（A、B、late 共 3 个）");

        System.out.println("  第二次 fire（late 这次能收到）：");
        bus.fire("order-paid");

        // 对照：如果换成 ArrayList，这里会抛 CME
        System.out.println();
        System.out.println("  对照实验（ArrayList 版本）：");
        java.util.List<Listener> plain = new java.util.ArrayList<>();
        plain.add(a);
        plain.add(e -> plain.add(late));
        try {
            for (Listener l : plain) {
                l.onEvent("x");
            }
            System.out.println("    ArrayList 版竟然没抛？");
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("    ArrayList 版 → ConcurrentModificationException（同时说明为什么这里必须用 COW）");
        }
    }
}
