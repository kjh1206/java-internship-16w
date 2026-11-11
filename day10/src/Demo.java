public class Demo {
    public static void main(String[] agrs){
    Duck duck=new Duck("唐老鸭");
    Swimmable a=duck;
    Flyable b=duck;
    System.out.println(a==b);
    a.swim();
    b.fly();
    System.out.println(duck.move());
    C c=new C();
    c.hi();
    }
}
interface A{
    default String hi(){
        System.out.printf("A");
        return "";
    }
}
interface B{
    default String hi(){
        System.out.printf("B");
        return "";
    }
}
class C implements A,B{
    @Override public String hi(){
        return A.super.hi()+B.super.hi();
    }
}
interface Swimmable{
    public void swim();
    default String move() { return "在水里游"; }
}
interface Flyable{
    public void fly();
    default String move() { return "在天上飞"; }
}
class Duck implements Swimmable,Flyable{
    private final String name;
    public Duck(String name) { this.name = name; }

    @Override public void swim() { System.out.println(name + " 正在游泳"); }
    @Override public void fly()  { System.out.println(name + " 正在飞行"); }
    @Override public String move() {
        return Swimmable.super.move() + " + " + Flyable.super.move();
    }
}