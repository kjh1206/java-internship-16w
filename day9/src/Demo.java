class Animal {
    String name = "动物";
    void say() { System.out.println("动物叫"); }
    static void info() { System.out.println("Animal.info"); }
}

class Dog extends Animal {
    String name = "狗";
    @Override void say() { System.out.println("汪汪"); }
    static void info() { System.out.println("Dog.info"); }
}

public class Demo {
    public static void main(String[] args) {
        Animal a = new Dog();
        a.say();          // ①
        System.out.println(a.name);   // ②
        a.info();         // ③
    }
}