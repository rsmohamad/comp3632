import java.io.*;
import java.nio.file.*;
import java.util.*;

public class kanon {
    private ArrayList<int[]> original_order = new ArrayList<>();
    private String filename;

    private kanon(String filename) throws IOException {
        this.filename = filename;
        List<String> lines = Files.readAllLines(Paths.get(filename));

        for (String line : lines) {
            String entry[] = line.split(",");
            int data[] = new int[entry.length];
            for (int i = 0; i < entry.length; i++)
                data[i] = Integer.parseInt(entry[i]);
            original_order.add(data);
        }
    }

    // Find the median between a range (inclusive)
    public static int findMedian(ArrayList<int[]> entries, int start, int end) {
        int median = entries.get((end + start) / 2)[0];
        if ((end - start) % 2 == 0) return median;
        median += entries.get((end + start) / 2 + 1)[0];
        median = (int) Math.round(median / 2.0);
        return median;
    }

    // Find the k-anonymization cost between a range (inclusive)
    private static int cost(ArrayList<int[]> entries, int start, int end) {
        int cost = 0;
        int median = findMedian(entries, start, end);

        for (int i = start; i <= end; i++)
            cost += Math.abs(entries.get(i)[0] - median);

        return cost;
    }

    private int anonymize(int k) throws IOException {
        // Sort entries - entry[0] = age, entry[1]  = phage
        ArrayList<int[]> entries = new ArrayList<>(this.original_order);
        entries.sort((u, v) -> u[0] - v[0]);

        // Get number of anonymity sets
        int anonymitySets = entries.size() / k;

        // Allocate DP storage table
        int opt[][] = new int[entries.size()][anonymitySets];
        Integer prev[] = new Integer[entries.size()];

        // Fill the costs for the first anonymity set
        for (int i = k - 1; i < entries.size(); i++)
            opt[i][0] = cost(entries, 0, i);

        // DP part
        for (int col = 1; col < anonymitySets; col++) {
            for (int index = (col + 1) * k - 1; index < entries.size(); index++) {
                int min = opt[index][col - 1], minDist = 0;
                if (prev[index] != null)
                    minDist = prev[index];
                for (int dist = Math.max(col * k - 1, index - 2 * k); dist <= index - k; dist++) {
                    int newCost = opt[dist][col - 1] + cost(entries, dist + 1, index);
                    if (newCost <= min) {
                        min = newCost;
                        minDist = dist;
                    }
                }
                prev[index] = minDist;
                opt[index][col] = min;
            }
        }

        // Get anonymized result
        int index, result[] = new int[entries.size()];
        for (index = entries.size() - 1; prev[index] != null; index = prev[index])
            Arrays.fill(result, prev[index] + 1, index + 1, findMedian(entries, prev[index] + 1, index));
        Arrays.fill(result, 0, index + 1, findMedian(entries, 0, index));

        // Keep track of costs for correctness checking
        int actualCost = 0;
        int DPCost = opt[opt.length - 1][anonymitySets - 1];

        // Create a HashMap <reference to entry, result>
        Map<int[], Integer> entry_result = new HashMap<>();
        for (int i = 0; i < entries.size(); i++) {
            entry_result.put(entries.get(i), result[i]);
            actualCost += Math.abs(entries.get(i)[0] - result[i]);
        }

        // Check if DP cost == actual cost
        System.out.println(String.format("Expected: %d, Changed: %d", DPCost, actualCost));

        // Write the result to CSV in original order
        try (PrintWriter out = new PrintWriter(filename)) {
            for (int i = 0; i < original_order.size(); i++) {
                int entry[] = original_order.get(i);
                if (i < original_order.size() - 1)
                    out.println(String.format("%d,%d", entry_result.get(entry), entry[1]));
                else
                    out.print(String.format("%d,%d", entry_result.get(entry), entry[1]));
            }
        }

        return DPCost;
    }

    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java kanon <inputfile>");
            System.exit(-1);
        }

        int k = 4;
        System.out.println("k: " + k);
        new kanon(args[0]).anonymize(k);
    }
}
