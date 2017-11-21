import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ktest {
    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java ktest <inputfile>");
            System.exit(-1);
        }

        Map<ArrayList<Integer>, Integer> attributes = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(args[0]));

        for (String line : lines) {
            String entry[] = line.split(",");
            ArrayList<Integer> tuple = new ArrayList<>();
            for (int i = 0; i < entry.length - 1; i++)
                tuple.add(Integer.valueOf(entry[i]));
            int count = attributes.getOrDefault(tuple, 0);
            attributes.put(tuple, ++count);
        }

        int min = Collections.min(attributes.values(), null);
        System.out.println(min);
        System.exit(min);
    }
}
