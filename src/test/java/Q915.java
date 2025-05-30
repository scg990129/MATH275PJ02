import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Q915 {
    public static void main(String[] args) {
        // 4♠, 4♦, 4♣, J♠, 8♥
        char[] elements =  {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};


        List<String> results = new ArrayList<>();
        permute(elements, 0, results);

        // Print all permutations
        AtomicInteger count = new AtomicInteger(0);
        results.stream().peek(s ->
                        count.addAndGet(s.indexOf("A") < 5 && s.indexOf("B") < 5 ||
                                (s.indexOf("A") >= 5 && s.indexOf("B") >= 5) ? 1 : 0))
                .forEachOrdered(System.out::println);

//        for (String s : results) {
//            System.out.println(s);
//            c += (s.indexOf("A") < 5 && s.indexOf("B") < 5) ||
//                    (s.indexOf("A") >= 5 && s.indexOf("B") >= 5)
//                    ? 1 : 0;
//        }

        System.out.println("Total: " + count.get());
        System.out.println("Total permutations: " + results.size());
    }

    // Heap's Algorithm for generating permutations
    public static void permute(char[] arr, int k, List<String> results) {
        if (k == arr.length - 1) {
            results.add(new String(arr));
        } else {
            for (int i = k; i < arr.length; i++) {
                swap(arr, i, k);
                permute(arr, k + 1, results);
                swap(arr, i, k); // backtrack
            }
        }
    }

    private static void swap(char[] arr, int i, int j) {
        char tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}