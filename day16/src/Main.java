import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> n = new ArrayList<>(List.of("a", "b", "c"));
        for (String s : n) if (s.equals("c")) n.remove(s);   // 不抛
    }
}