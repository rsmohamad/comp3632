import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class DBEntry {
    int age;
    int phage;
    DBEntry(String line[]){
        this.age = Integer.parseInt(line[0]);
        this.phage = Integer.parseInt(line[1]);
    }
}

public class kanon {
    private ArrayList<Integer> ages = new ArrayList<>();
    private ArrayList<DBEntry> original_order = new ArrayList<>();
    private String headers[];

    private kanon(String filename) throws IOException {
        loadCSV(filename);
    }

    private void loadCSV(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            headers = in.readLine().split(",");
            String line;

            while ((line = in.readLine()) != null){
                DBEntry e = new DBEntry(line.split(","));

                // Maintain arrays of reference to remember original order
                original_order.add(e);
                ages.add(e.age);
            }

        }
    }

    // Find the median between specified range (inclusive)
    private int findMedian(ArrayList<Integer> ages, int start, int end){
        int median;
        if ((end - start) % 2 == 0)
            // Even number of data
            // Median =  ([(start+end)/2] + [(start+end)/2 - 1])/2
            median = (ages.get((end + start) / 2) + ages.get((end + start) / 2 - 1)) / 2;
        else
            // Odd number of data
            // Median = [(start + end)/2]
            median = ages.get((end + start) / 2);
        return median;
    }

    /**
     * Find the anonymization cost between the specified range
     * @param ages Arraylist to operate on
     * @param start Starting index
     * @param end End index (inclusive)
     * @return The anonymization cost
     */
    private int cost(ArrayList<Integer> ages, int start, int end) {
        int cost = 0, median = findMedian(ages, start, end);

        for (int i = start; i <= end; i++)
            cost += Math.abs(ages.get(i) - median);

        return cost;
    }

    /**
     *
     */
    private int anonymize(int k) {
        ArrayList<Integer> ages = new ArrayList<>(this.ages);
        ArrayList<DBEntry> entries = new ArrayList<>(this.original_order);
        ages.sort((u, v) -> u - v);
        entries.sort((u, v) -> u.age - v.age);

        // Get number of anonymity sets
        int N = ages.size() / k;

        // DP storage table
        int opt[][] = new int[ages.size()][N];
        Integer prev[] = new Integer[ages.size()];

        // Fill cost for one anonymity set
        for (int i = k - 1; i < ages.size(); i++){
            opt[i][0] = cost(ages, 0, i);
            prev[i] = null;
        }


        // DP part
        for (int col = 1; col < N; col++) {
            for (int index = (col + 1) * k - 1; index < ages.size(); index++){
                int min = opt[index][col - 1], minDist;

                if (prev[index] == null)
                    minDist = 0;
                else
                    minDist = prev[index];

                for (int dist = col * k - 1; dist <= index - k; dist++){
                    int newCost = opt[dist][col - 1] + cost (ages, dist + 1, index);
                    if (newCost < min){
                        min = newCost;
                        minDist = dist;
                    }
                }

                prev[index] = minDist;
                opt[index][col] = min;
            }
        }

        int indexes = ages.size() - 1;
        ArrayList<Integer> result = new ArrayList<>();
        while (prev[indexes] != null){
            for (int j = prev[indexes] + 1; j <= indexes; j++)
                result.add(ages.get(indexes));
            indexes = prev[indexes];
        }

        for (int n = 0; n <= indexes; n++){
            result.add(ages.get(indexes));
        }

        Collections.reverse(result);

        Map<DBEntry, Integer> ref_newAge = new HashMap<>();
        for (int i = 0; i < ages.size(); i++)
            ref_newAge.put(entries.get(i), result.get(i));

        try (PrintWriter out = new PrintWriter("inputfile")){
            out.println("Age,Phage");
            for (DBEntry e : original_order)
                out.println(String.format("%d,%d", ref_newAge.get(e),e.phage));
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

        return opt[opt.length - 1][N - 1];
    }


    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java kanon <inputfile>");
            System.exit(-1);
        }

        kanon k = new kanon(args[0]);
        System.out.println();
        System.out.println(k.anonymize(4));
    }
}
