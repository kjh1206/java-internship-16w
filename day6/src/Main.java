import java.util.Scanner;

public class Main {
    static int[] xuhao = new int[100];
    static String[] data = new String[100];
    static int [] type = new int[100];
    static double[] count = new double[100];
    static String [] nots = new String[100];
    static int i=0;
    static double sum=0;
    static Scanner scanner = new Scanner(System.in);
    static void add(){
        if(i==100){
            System.out.println("已达到最大记录数，无法添加更多记录！");
            return;
        }
        System.out.println("请输入日期（如 11-07）：");
        data[i]=scanner.nextLine();

        System.out.println("请输入类型（1 收入 / 2 支出）：");
        boolean flag=true;
        while(flag){
            type[i]=scanner.nextInt();
            if(type[i]<1 || type[i]>2){
                System.out.println("类型输入错误，请重新输入！");
            }else {
                flag=false;
            }
        }
        System.out.println("请输入金额：");
        count[i]=scanner.nextDouble();

        System.out.println("请输入备注：");
        scanner.nextLine();
        nots[i]=scanner.nextLine();
        xuhao[i]=i+1;
        i++;
        if(type[i-1]==2){
            sum+=count[i-1];
        }
        System.out.println("添加成功！当前共"+i+"笔");
    }
    static void showAll(){
        for(int j=0;j<i;j++){
            if(type[j]==1){
                System.out.println(xuhao[j]+" "+data[j]+" 收入 "+count[j]+" "+nots[j]);
            }else{
                System.out.println(data[j]+" 支出 "+count[j]+" "+nots[j]);
            }
        }
    }
    static void showByType(int t){
        for(int j=0;j<i;j++){
            if(type[j]==t){
                System.out.println(data[j]+" "+type[j]+" "+count[j]+" "+nots[j]);
            }
        }
    }
    public static void main(String[] args) {
        boolean j=true;
        while(j){
            System.out.println("========== 记账本 v0.1 ==========\n" +
                    "1. 添加一笔\n" +
                    "2. 查看全部\n" +
                    "3. 按类型筛选\n" +
                    "4. 统计总支出\n" +
                    "5. 退出\n" +
                    "请选择：");
            int choice = scanner.nextInt();
            switch(choice){
                case 1:
                    System.out.println("添加一笔");
                    add();
                    break;
                case 2:
                    System.out.println("查看全部");
                    System.out.println("序号 日期 类型 金额 备注");
                    showAll();
                    break;
                case 3:
                    System.out.println("按类型筛选");
                    int t=scanner.nextInt();
                    showByType(t);
                    break;
                case 4:
                    System.out.println("统计总支出");
                    System.out.println("总支出为："+sum);
                    break;
                case 5:
                    System.out.println("退出");
                    j=false;
                    break;
                default:
                    System.out.println("无效的选择");
            }
        }
    }
}