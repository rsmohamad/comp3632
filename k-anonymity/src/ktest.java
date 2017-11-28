import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ktest {
    public static int testK(String filename) throws IOException {
        Map<ArrayList<Integer>, Integer> attributes = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));

        for (String line : lines) {
            String entry[] = line.split(",");
            ArrayList<Integer> tuple = new ArrayList<>();
            for (int i = 0; i < entry.length - 1; i++)
                tuple.add(Integer.valueOf(entry[i]));
            int count = attributes.getOrDefault(tuple, 0);
            attributes.put(tuple, ++count);
        }

        int min = Collections.min(attributes.values());
        System.out.println(String.format("Satisfies %d-anonymity", min));
        return min;
    }

    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java ktest <inputfile>");
            System.exit(-1);
        }

        System.exit(testK(args[0]));
    }
}
