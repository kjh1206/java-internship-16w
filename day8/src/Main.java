//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        Student s1 = new Student("张三", 20, "001");
        Student s2 = new Student("李四", 25, "002");
        Student s3 = new Student("王五", 30, "003");
        s3.setAge(18);
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);

        System.out.println("学生总数：" + Student.getCount());
    }
}
class Student{
    private String name;
    private int age;
    private String id;
    static  int count=0;
    Student(){}
    Student(String name){
        this.name = name;
        count++;
    }
    Student(String name, int age, String id){
        this.name = name;
        this.age = age;
        this.id = id;
        count++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if(name==null || name.trim().isEmpty()){
            System.out.println("姓名不能为空");
        }
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        if(age < 0|| age > 150){
            System.out.println("年龄不能为负数或超过150");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Override
    public String toString(){
        return "Student{" +
                "id='" + id + "'" +
                ", name='" + name + "'" +
                ", age=" + age +
                '}';
    }
    static int getCount(){
        return count;
    }
}