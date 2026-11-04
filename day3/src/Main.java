//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        // 传统：忘了 break 会贯穿到下一个 case
        int day = 1;
        int month = 3;
        switch (day) {
            case 1: System.out.println("周一");
            break;
            case 2: System.out.println("周二");
            default: System.out.println("其他");
        }
// 箭头（Java 14+，你的 JDK 21 支持）：没有贯穿，不用 break
        switch (month) {
            case 3, 4, 5 -> System.out.println("春");
            case 6, 7, 8 -> System.out.println("夏");
            default -> System.out.println("其他");
        }
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.printf(i+"*"+j+"="+i*j+"\t");
            }
            System.out.println();
        }
        int n=5;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <=2*n; j++) {
                if(j<=n-i||j>=n+i)System.out.printf(" ");
                else System.out.printf("*");
            }
            System.out.println();
        }
        for (int i =n-1; i >=0; i--) {
            for (int j =1; j <=2*n; j++) {
                if(j<=n-i||j>=n+i)System.out.printf(" ");
                else System.out.printf("*");
            }
            System.out.println();
        }
        int m=100;
        for (int j = 2; j < m; j++) {
            boolean prime = true;                    // ← 先假设
            for (int i = 2; i <=Math.sqrt(j); i++) {       // ← 只负责找反例
                if (j % i == 0) {
                    prime = false;
                    break;
                }
            }
            if (prime) {                             // ← 循环彻底结束后才下结论
                System.out.print(j + "\t");
            }
        }
    }
}