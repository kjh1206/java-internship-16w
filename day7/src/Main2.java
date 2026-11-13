import java.util.Scanner;
class Record{
    int xuhao;
    String data;
    int type;
    double count;
    String nots;

    Record(String data, int type, double count, String nots) {
        this.data = data;
        this.type = type;
        this.count = count;
        this.nots = nots;
    }

    public String getNots() {return nots;}

    public void setNots(String nots) {this.nots = nots;}

    public double getCount() {return count;}

    public void setCount(double count) {this.count = count;}

    public int getType() {return type;}

    public void setType(int type) {this.type = type;}

    public String getData() {return data;}

    public void setData(String data) {this.data = data;}

    @Override
    public String toString() {
        if (type == 1) {
            return xuhao+" " +
                " " + data   +
                " " + "收入" +
                " " + count +
                " " + nots;
        } else {
            return xuhao+" " +
                    " " + data  +
                    " " + "支出" +
                    " " + count +
                    " " + nots;
        }

    }
}

public class Main2 {
    static double sum = 0;
    static Record[] records = new Record[100];
    static Scanner scanner = new Scanner(System.in);
    static int i=0;

    //加入一笔记录
    static void add() {
        String data=null;
        int type=0;
        double count=0;
        if (i == 100) {
            System.out.println("已达到最大记录数，无法添加更多记录！");
            return;
        }
        System.out.println("请输入日期（如 11-07）：");
        boolean flag = true;
        while (flag) {
            data = scanner.nextLine();
            if (data.length() != 5
                    || data.charAt(2) != '-'
                    || !Character.isDigit(data.charAt(0))
                    || !Character.isDigit(data.charAt(1))
                    || !Character.isDigit(data.charAt(3))
                    || !Character.isDigit(data.charAt(4)))
            {
                System.out.println("日期格式错误，请重新输入！");
                continue;
            }
            int month = Integer.parseInt(data.substring(0, 2));
            int day = Integer.parseInt(data.substring(3, 5));
            int[] days = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            if (month < 1 || month > 12 || day < 1 || day > days[month]) {
                System.out.println("日期格式错误，请重新输入！");
            } else {
                flag = false;
            }
        }
        //改枚举
        System.out.println("请输入类型（1 收入 / 2 支出）：");
        boolean flag1 = true;
        while (flag1) {
            type = scanner.nextInt();
            scanner.nextLine(); // 消费掉换行符
            if (type < 1 || type > 2) {
                System.out.println("类型输入错误，请重新输入！");
            } else {
                flag1 = false;
            }
        }

        System.out.println("请输入金额：");
        boolean flag2 = true;
        while (flag2) {
            count= scanner.nextDouble();
            scanner.nextLine(); // 消费掉换行符
            if (count <= 0) {
                System.out.println("金额必须大于0，请重新输入！");
            } else {
                flag2 = false;
            }
        }

        System.out.println("请输入备注：");
        String nots = scanner.nextLine();

        records[i]=new Record(data,type,count,nots);
        records[i].xuhao=i+1;
        i++;
        if (type == 1) {
            sum += count;
        }else{
            sum -= count;
        }
        System.out.println("添加成功！当前共" + i + "笔");
    }

    //显示所有记录
    static void showAll() {
        for (int j = 0; j < i; j++) {
            System.out.println(records[j].toString());
        }
    }



    public static void main(String[] args) {
        boolean j = true;
        while (j) {
            System.out.println("========== 记账本 v0.1 ==========\n" +
                    "1. 添加一笔\n" +
                    "2. 查看全部\n" +
                    "3. 退出\n" +
                    "请选择：");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 消费掉换行符
            switch (choice) {
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
                    System.out.println("退出");
                    j = false;
                    break;
                default:
                    System.out.println("无效的选择");
            }
        }
    }
}