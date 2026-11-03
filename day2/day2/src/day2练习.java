public class day2练习 {
    public static void main(String agrs[]){
        int a=2147483647;
        a=a+1;
        byte b=(byte)200;
        System.out.println(a);
        System.out.println(b);
        System.out.println(7/2);
        System.out.println(7%2);
        System.out.println(-7%2);
        int i=10;int j=i++ + i++;
        System.out.println(i+","+j);
        double d=0.1+0.2;
        System.out.println(d==0.3);
        char c = 'A'; c = (char)(c + 2);
        System.out.println(c);
        char e=99;
        System.out.println(e);
        boolean r = false && (1/0 == 1);
        System.out.println(r);
        int x=5;
        x+=x++ +3;
        System.out.println(x);
        int f=789;
        System.out.println((f/100)+","+(f%100/10)+","+(f%10));
    }
}
