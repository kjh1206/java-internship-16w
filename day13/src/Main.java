public class Main {
    public static void main(String[] args) {
        String a = "1";
        String b = "1";
        String c = new String("1");
        System.out.println(a == b);        // ?
        System.out.println(a == c);        // ?
        System.out.println(a.equals(c));   // ?
        System.out.println(a == "1");      // ?
        StringBuilder stringBuilder=new StringBuilder("hello world");
        int i=stringBuilder.length();
        StringBuilder d=new StringBuilder();
        while(i>0){
            char s=stringBuilder.charAt(i-1);
            d=d.append(s);
            i--;
        }
        System.out.println(d);

    }
}