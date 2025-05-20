import java.util.*;

public class AllPermutations {
    public static void main(String[] args) {
        char[] elements = {'A', 'B', 'C', 'D', 'E', 'F'};
        List<String> results = new ArrayList<>();
        permute(elements, 0, results);

        // Print all permutations
        for (String s : results) {
            System.out.println(s);
        }
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