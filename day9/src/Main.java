//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        Person[] s = new Student[3];
        s[0] = new Student(18, "kjh", "ak");
        s[1] = new Student(19, "kjg", "ak");
        s[2] = new Student(20, "kjf", "ak");
        for(int i=0;i<3;i++){
            s[i].introduce();
        }
    }
}

class Person {
    private String name;
    private int age;

    Person(int age, String name) {
        this.age = age;
        this.name = name;
    }

    void introduce() {
        System.out.printf(name + " " + age);
    }
}

class Student extends Person {
    private String school;

    Student(int age, String name, String school) {
        super(age, name);
        this.school = school;
    }
    void introduce() {
        super.introduce();
        System.out.println(" " + school);
    }
}