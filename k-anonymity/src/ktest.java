import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ktest {
    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java ktest <inputfile>");
            System.exit(-1);
        }

        Map<Integer, Integer> attributes = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(args[0]));

        for (String line : lines) {
            int age = Integer.parseInt(line.split(",")[0]);
            int count = attributes.getOrDefault(age, 0);
            attributes.put(age, ++count);
        }

        Collection<Integer> values = attributes.values();
        System.exit(values.stream().mapToInt(Integer::intValue).min().orElse(1));
    }
}
