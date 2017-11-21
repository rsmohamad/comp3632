import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class Cluster {
    ArrayList<int[]> points = new ArrayList<>();
    int center[];
    int dimension;

    Cluster(int center[]) {
        this.dimension = center.length;
        this.center = Arrays.copyOf(center, center.length);
    }

    void computeCenter() {
        if (points.size() == 0)
            return;

        ArrayList<ArrayList<Integer>> dimensions = new ArrayList<>();
        for (int i = 0; i < dimension; i++)
            dimensions.add(new ArrayList<>());

        for (int point[] : points)
            for (int i = 0; i < dimension; i++)
                dimensions.get(i).add(point[i]);

        int means[] = new int[dimension];

        for (int i = 0; i < dimension; i++) {
            OptionalDouble average = dimensions.get(i).stream().mapToInt(Integer::intValue).average();
            means[i] = (int) Math.round(average.getAsDouble());
        }

        center = means;
    }

    int[] getCenter() {
        return center;
    }

    void clear() {
        points.clear();
    }

    void add(int[] entry) {
        points.add(entry);
    }
}

public class kanon2 {
    String filename;
    ArrayList<int[]> quasi = new ArrayList<>();
    ArrayList<Integer> sensitive = new ArrayList<>();
    ArrayList<Cluster> clusters = new ArrayList<>();
    int dimension;

    public kanon2(String filename) throws IOException {
        this.filename = filename;
        List<String> lines = Files.readAllLines(Paths.get(filename));
        HashSet<ArrayList<Integer>> distinctSet = new HashSet<>();

        for (String line : lines) {
            String entry[] = line.split(",");
            int data[] = new int[entry.length - 1];

            for (int i = 0; i < entry.length - 1; i++)
                data[i] = Integer.parseInt(entry[i]);

            quasi.add(data);
            ArrayList<Integer> d = new ArrayList<>();
            for (int integer : data)
                d.add(integer);
            distinctSet.add(d);
            sensitive.add(Integer.valueOf(entry[entry.length - 1]));
        }

        // Check if the data is valid
        dimension = quasi.get(0).length;
        for (int data[] : quasi)
            if (dimension != data.length)
                throw new IOException("The dimensions of every tuple are not consistent");

    }

    public double findDistance(int a[], int b[]) {
        if (a.length != b.length)
            System.out.println(a.length + " " + b.length);

        int distance = 0;
        for (int i = 0; i < a.length; i++)
            distance += Math.pow(a[i] - b[i], 2);
        return Math.sqrt(distance);
    }

    public int findNearestCluster(int point[], ArrayList<Cluster> clusters) {
        double minDistance = findDistance(point, clusters.get(0).getCenter());
        int minIndex = 0;
        for (int i = 1; i < clusters.size(); i++) {
            double distance = findDistance(point, clusters.get(i).getCenter());
            if (distance < minDistance) {
                minDistance = distance;
                minIndex = i;
            }
        }

        return minIndex;
    }

    void clusterize(ArrayList<int[]> data, Map<int[], Cluster> clusterTable) {
        boolean isConverged = false;
        ArrayList<Integer> prevClusterList = new ArrayList<>();

        while (!isConverged) {
            clusterTable.clear();
            for (Cluster c : clusters)
                c.clear();

            ArrayList<Integer> clusterList = new ArrayList<>();

            for (int[] aData : data) {
                int cluster = findNearestCluster(aData, clusters);
                clusters.get(cluster).add(aData);
                clusterList.add(cluster);
                clusterTable.put(aData, clusters.get(cluster));
            }

            for (Cluster c : clusters)
                c.computeCenter();

            isConverged = clusterList.equals(prevClusterList);
            prevClusterList = clusterList;
        }
    }

    // TODO: Implement this to fix Cluster initialization problem
    public ArrayList<int[]> findDistinctPoints(ArrayList<int[]> points, int n) {
        return null;
    }

    public double anonymize(int k) throws IOException {
        // Get anonymity sets
        int anonymitySets = quasi.size() / k;

        // Initialize cluster
        HashSet<List<Integer>> used = new HashSet<>();
        for (int i = 0, index = 0; i < anonymitySets; i++) {
            int center[] = quasi.get(i);
            clusters.add(new Cluster(center));
        }

        // HashMap <entry, cluster>
        Map<int[], Cluster> clusterTable = new HashMap<>();
        boolean ok = false;

        while (!ok) {
            // Divide into clusters
            clusterize(quasi, clusterTable);

            // Check if each cluster satisfies k-size
            // TODO: Handle this better
            ok = true;
            for (Cluster c : clusters)
                if (c.points.size() < k) {
                    System.out.println(c.points);
                    ok = false;
                    clusters.remove(c);
                    anonymitySets--;
                    break;
                }
        }

        // Write to CSV
        PrintWriter out = new PrintWriter(filename);
        double cost = 0;
        for (int i = 0; i < quasi.size(); i++) {
            Cluster cluster = clusterTable.get(quasi.get(i));
            int anonymized[] = cluster.getCenter();
            cost += findDistance(anonymized, quasi.get(i));
            StringBuilder output = new StringBuilder();

            for (int qid : anonymized) {
                output.append(qid);
                output.append(",");
            }

            output.append(String.valueOf(sensitive.get(i)));
            out.println(output.toString());
        }
        out.close();

        System.out.println(anonymitySets);

        return cost;
    }

    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java kanon2 <inputfile>");
            System.exit(-1);
        }

        int k = 1;
        System.out.println(String.format("k: %d\n", k));
        kanon2 algo = new kanon2(args[0]);

        System.out.println(String.format("cost: %.2f", algo.anonymize(k)));
    }
}
