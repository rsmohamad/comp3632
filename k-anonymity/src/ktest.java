import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ktest {
    private Map<Integer, Integer> attributes = new HashMap<>();

    private ktest(String filename) throws IOException{
        loadCSV(filename);
    }

    private void loadCSV(String filename) throws IOException{
        try (BufferedReader in = new BufferedReader(new FileReader(filename))){
            in.readLine();
            String line;
            while((line = in.readLine()) != null){
                int age = Integer.parseInt(line.split(",")[0]);
                int count = attributes.getOrDefault(age, 0);
                count++;
                attributes.put(age, count);
            }
        }
    }

    private int getAnonimityDegree(){
        Collection<Integer> values = attributes.values();
        return values.stream().mapToInt(Integer::intValue).min().getAsInt();
    }

    public static void main(String args[])throws IOException{
        if (args.length != 1){
            System.out.println("Usage: java ktest <inputfile>");
            System.exit(-1);
        }

        ktest k = new ktest(args[0]);
        int degree = k.getAnonimityDegree();
        System.out.println(degree);
        System.exit(degree);
    }
}
