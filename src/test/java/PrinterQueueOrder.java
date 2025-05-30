import java.util.*;

public class PrinterQueueOrder {
    static int count = 0;
    static boolean printPermutations = false; // Set to true to print each permutation

    public static void main(String[] args) {
        // List of 10 jobs: jobA, jobB, job1, job2, ..., job8
        List<String> jobs = new ArrayList<>(Arrays.asList(
                "jobA", "jobB",
                "job1", "job2", "job3", "job4",
                "job5", "job6", "job7", "job8"
        ));

        permute(jobs, 0);

        System.out.println("Total number of valid orders (jobA before jobB): " + count);
    }

    static void permute(List<String> arr, int k) {
        if (k == arr.size()) {
            int posA = arr.indexOf("jobA");
            int posB = arr.indexOf("jobB");
            if (posA < posB) {
                count++;
                if (printPermutations) {
                    System.out.println(arr);
                }
            }
        } else {
            for (int i = k; i < arr.size(); i++) {
                Collections.swap(arr, i, k);
                permute(arr, k + 1);
                Collections.swap(arr, i, k);
            }
        }
    }
}
