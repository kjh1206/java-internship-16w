import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<int[]> one = Arrays.asList(new int[]{1, 2, 3});
        System.out.println(one.size());
        List<Integer> src = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        System.out.println(src.size());
    }
}