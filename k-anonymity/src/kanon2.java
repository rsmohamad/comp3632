import java.io.*;
import java.nio.file.*;
import java.util.*;

class Cluster {
    ArrayList<int[]> points = new ArrayList<>();
    private int center[];
    private int dimension;

    Cluster(int dimension) {
        this.dimension = dimension;
        center = new int[dimension];
    }

    /**
     * Update the center of the cluster
     * The center is simply the median of each dimension
     */
    private void calculateCenter() {
        if (points.size() == 0)
            return;

        ArrayList<ArrayList<Integer>> dimensions = new ArrayList<>();
        for (int i = 0; i < dimension; i++)
            dimensions.add(new ArrayList<>());

        for (int point[] : points)
            for (int i = 0; i < dimension; i++)
                dimensions.get(i).add(point[i]);

        int medians[] = new int[dimension];

        for (int i = 0; i < dimension; i++) {
            int dataNum = dimensions.get(i).size();
            dimensions.get(i).sort(Integer::compareTo);
            medians[i] = dimensions.get(i).get(dataNum / 2);
        }
        center = medians;
    }

    /**
     * Calculate the total cost in this cluster
     * Sum of L1 distances between every point and the center
     */
    int calculateTotalCost() {
        calculateCenter();
        int totalCost = 0;
        for (int[] p : points)
            totalCost += (int) kanon2.findDistanceL1(p, center);
        return totalCost;
    }

    int[] getCenter() {
        calculateCenter();
        return center;
    }

    void add(int[] entry) {
        points.add(entry);
    }

    void addAll(ArrayList<int[]> points) {
        this.points.addAll(points);
    }

    int size() {
        return points.size();
    }

    void remove(int[] entry) {
        points.remove(entry);
    }
}

public class kanon2 {
    // Identifiers
    private ArrayList<int[]> quasi = new ArrayList<>();
    private ArrayList<Integer> sensitive = new ArrayList<>();

    // Saved clusters
    private ArrayList<Cluster> clusters = new ArrayList<>();
    private Map<int[], Cluster> clusterTable = new HashMap<>();

    // Number of quasi identifiers
    private int dimension;

    // File to modify
    private String filename;

    private int existingK;

    /**
     * Load data from CSV file
     */
    public kanon2(String filename) throws IOException {
        this.filename = filename;
        List<String> lines = Files.readAllLines(Paths.get(filename));

        // Parse line by line
        for (String line : lines) {
            String entry[] = line.split(",");
            int data[] = new int[entry.length - 1];

            for (int i = 0; i < entry.length - 1; i++)
                data[i] = Integer.parseInt(entry[i]);

            quasi.add(data);
            sensitive.add(Integer.valueOf(entry[entry.length - 1]));
        }

        // Check if the data is valid
        dimension = quasi.get(0).length;
        for (int data[] : quasi)
            if (dimension != data.length)
                throw new IOException("The dimensions of every tuple are not consistent");

        existingK = ktest.testK(filename);
    }

    static double findDistanceL1(int a[], int b[]) {
        if (a.length != b.length)
            System.err.println(a.length + " " + b.length);

        int distance = 0;
        for (int i = 0; i < a.length; i++)
            distance += Math.abs(a[i] - b[i]);
        return (double) distance;
    }

    /**
     * Find the best cluster to insert the given point
     */
    private static Cluster findBestCluster(int point[], ArrayList<Cluster> clusters) {
        int minCost = Integer.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            int initialCost = cluster.calculateTotalCost();
            cluster.add(point);
            int diff = cluster.calculateTotalCost() - initialCost;
            if (diff < minCost) {
                minCost = diff;
                minIndex = i;
            }
            cluster.remove(point);
        }
        return clusters.get(minIndex);
    }

    private static Cluster findBestMerge(Cluster cluster, ArrayList<Cluster> clusters) {
        int minCost = Integer.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < clusters.size(); i++) {
            Cluster merge = clusters.get(i);
            int initialCost = merge.calculateTotalCost();
            merge.addAll(cluster.points);
            int diff = merge.calculateTotalCost() - initialCost;
            if (diff < minCost) {
                minCost = diff;
                minIndex = i;
            }
            merge.points.removeAll(cluster.points);
        }

        return clusters.get(minIndex);
    }

    private static int[] findFurthestPoint(int point[], ArrayList<int[]> points) {
        double maxDistance = Double.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < points.size(); i++) {
            double distance = findDistanceL1(point, points.get(i));
            if (distance > maxDistance && point != points.get(i)) {
                maxDistance = distance;
                maxIndex = i;
            }
        }
        return points.get(maxIndex);
    }

    /**
     * Find the best point to take into the cluster
     */
    private static int[] findBestPoint(Cluster cluster, ArrayList<int[]> points) {
        int minCost = Integer.MAX_VALUE;
        int minIndex = 0;
        int initialCost = cluster.calculateTotalCost();
        for (int i = 0; i < points.size(); i++) {
            int[] p = points.get(i);
            cluster.add(p);
            int diff = cluster.calculateTotalCost() - initialCost;
            if (diff < minCost) {
                minCost = diff;
                minIndex = i;
            }
            cluster.remove(p);
        }
        return points.get(minIndex);
    }

    /**
     * Anonymization procedure
     * Greedy k-members clustering
     * Return the total change given by the algorithm
     */
    public int anonymize(int k) throws IOException {
        clusters.clear();
        clusterTable.clear();
        if (k <= existingK)
            return 0;

        ArrayList<int[]> quasiClone = new ArrayList<>(quasi);
        int[] point = quasiClone.get(0);

        while (quasiClone.size() >= k) {
            Cluster cluster = new Cluster(dimension);
            point = findFurthestPoint(point, quasiClone);
            cluster.add(point);
            quasiClone.remove(point);
            clusterTable.put(point, cluster);

            while (cluster.size() < k) {
                point = findBestPoint(cluster, quasiClone);
                quasiClone.remove(point);
                cluster.add(point);
                clusterTable.put(point, cluster);
            }

            clusters.add(cluster);
        }

        while (quasiClone.size() > 0) {
            point = quasiClone.get(0);
            Cluster cluster = findBestCluster(point, clusters);
            cluster.add(point);
            quasiClone.remove(point);
            clusterTable.put(point, cluster);
        }

        int totalCost = 0;
        for (Cluster c : clusters)
            totalCost += c.calculateTotalCost();

        return totalCost;
    }

    /**
     * Writes back anonymized data to original file
     * Return the total change written back
     */
    public int writeToFile() throws IOException {
        if (clusters.isEmpty() || clusterTable.isEmpty())
            return 0;

        PrintWriter out = new PrintWriter(filename);
        int totalCost = 0;

        for (int i = 0; i < quasi.size(); i++) {
            Cluster cluster = clusterTable.get(quasi.get(i));
            int anonymity[] = cluster.getCenter();
            StringBuilder output = new StringBuilder();

            for (int qid : anonymity) {
                output.append(qid);
                output.append(",");
            }

            totalCost += findDistanceL1(anonymity, quasi.get(i));
            output.append(String.valueOf(sensitive.get(i)));

            if (i < quasi.size() - 1)
                out.println(output.toString());
            else
                out.print(output.toString());
        }

        out.close();
        return totalCost;
    }

    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java kanon2 <inputfile>");
            System.exit(-1);
        }

        kanon2 algo = new kanon2(args[0]);
        int k = 4;
        int expected = algo.anonymize(k);
        int changed = algo.writeToFile();

        System.out.println("k: " + k);
        System.out.println(String.format("Expected: %d, Changed: %d", expected, changed));
    }
}
