import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 题一：用 ArrayDeque 手写括号匹配 isValid(String s)，只支持 () [] {}。
 *
 * 核心思路：栈。
 *   遇到左括号 → 压栈；
 *   遇到右括号 → 栈空说明没有对应的左括号，false；
 *                 否则弹出栈顶，和当前右括号配对，不配就 false。
 *   扫完 → 栈必须为空（否则有没闭合的左括号）。
 */
public class BracketCheck {

    public static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);                       // addFirst，O(1)
            } else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) {
                    return false;                    // 右括号多了
                }
                char top = stack.pop();              // removeFirst，O(1)
                if (!match(top, c)) {
                    return false;                    // 括号类型不对
                }
            } else {
                return false;                        // 题目限定只有这 6 个字符
            }
        }
        return stack.isEmpty();                      // 左括号多了也会剩东西
    }

    private static boolean match(char left, char right) {
        return (left == '(' && right == ')')
                || (left == '[' && right == ']')
                || (left == '{' && right == '}');
    }

    public static void main(String[] args) {
        String[] cases = {"()", "()[]{}", "(]", "([)]", "{[]}", "", "(", ")", "(((((", "){", "{[()]}"};
        boolean[] expected = {true, true, false, false, true, true, false, false, false, false, true};

        System.out.println("=== 题一：括号匹配 ===");
        int pass = 0;
        for (int i = 0; i < cases.length; i++) {
            boolean got = isValid(cases[i]);
            boolean ok = got == expected[i];
            if (ok) pass++;
            System.out.printf("  %-9s → %-5s 期望 %-5s %s%n",
                    "\"" + cases[i] + "\"", got, expected[i], ok ? "[OK]" : "[FAIL]");
        }
        System.out.printf("  通过 %d/%d%n", pass, cases.length);
    }
}
