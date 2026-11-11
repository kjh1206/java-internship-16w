import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Circle(2));
        shapes.add(new Rectangle(3, 4));
        shapes.add(new Circle(1));

        shapes.sort(Comparator.comparingDouble(Shape::area));//升序

//        shapes.sort(Comparator.comparingDouble(Shape::area).reversed());//降序

//        shapes.sort(new Comparator<Shape>() {
//            @Override
//            public int compare(Shape o1, Shape o2) {
//                return (int) (o2.area()-o1.area());
//            }
//        });
        for (Shape s : shapes) {
            System.out.printf("%s 面积=%.2f 周长=%.2f %s %n",
                    s.getClass().getSimpleName(), s.area(), s.perimeter(),s.describe());
        }
    }
}
abstract class Animal{
    public String name;

    public Animal(String name) {
        this.name = name;
    }
}
interface Shape {
    double area();
    double perimeter();
    default String describe(){
        return "面积是"+area();
    }//调用重写后的
}

class Circle implements Shape {
    private final double r;
    Circle(double r) { this.r = r; }
    @Override public double area()      { return Math.PI * r * r; }
    @Override public double perimeter() { return 2 * Math.PI * r; }

    @Override
    public String describe() {
        return area()+"为面积";
    }
}

class Rectangle implements Shape {
    private final double w, h;
    Rectangle(double w, double h) { this.w = w; this.h = h; }
    @Override public double area()      { return w * h; }
    @Override public double perimeter() { return 2 * (w + h); }
}