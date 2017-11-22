import java.io.*;
import java.net.*;
import java.util.*;

public class Test {

    public static void main(String args[]) throws IOException {
        URL datafile1_url = new URL("https://course.cse.ust.hk/comp3632/datafile1");
        URL datafile2_url = new URL("https://course.cse.ust.hk/comp3632/datafile2");
        URL datafile1_out_url = new URL("https://course.cse.ust.hk/comp3632/datafile1_out");
        URL datafile2_out_url = new URL("https://course.cse.ust.hk/comp3632/datafile2_out");

        ArrayList<Integer> datafile1 = new ArrayList<>();
        ArrayList<Integer> datafile2 = new ArrayList<>();
        ArrayList<Integer> datafile1_out = new ArrayList<>();
        ArrayList<Integer> datafile2_out = new ArrayList<>();

        String line;
        BufferedReader buffer;

        buffer = new BufferedReader(new InputStreamReader(datafile1_url.openStream()));
        while ((line = buffer.readLine()) != null)
            datafile1.add(Integer.valueOf(line.split(",")[0]));

        buffer = new BufferedReader(new InputStreamReader(datafile2_url.openStream()));
        while ((line = buffer.readLine()) != null)
            datafile2.add(Integer.valueOf(line.split(",")[0]));

        buffer = new BufferedReader(new InputStreamReader(datafile1_out_url.openStream()));
        while ((line = buffer.readLine()) != null)
            datafile1_out.add(Integer.valueOf(line.split(",")[0]));

        buffer = new BufferedReader(new InputStreamReader(datafile2_out_url.openStream()));
        while ((line = buffer.readLine()) != null)
            datafile2_out.add(Integer.valueOf(line.split(",")[0]));

        System.out.println(datafile1);
        System.out.println(datafile2);
        System.out.println(datafile1_out);
        System.out.println(datafile2_out);

        int changes = 0;
        for (int i = 0; i < datafile1.size() && datafile1.size() == datafile1_out.size(); i++)
            changes += Math.abs(datafile1.get(i) - datafile1_out.get(i));
        System.out.println("datafile1: " + changes);

        changes = 0;
        for (int i = 0; i < datafile2.size() && datafile2.size() == datafile2_out.size(); i++)
            changes += Math.abs(datafile2.get(i) - datafile2_out.get(i));
        System.out.println("datafile2: " + changes);
    }
}
