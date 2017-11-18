import java.io.*;

public class Generator {
    public static void main(String args[]) throws IOException {
        int count = 500;
        if (args.length == 1)
            count = Integer.parseInt(args[0]);

        try (PrintWriter normal = new PrintWriter("inputfile");
             PrintWriter bonus = new PrintWriter("inputfile_bonus")) {
            for (int i = 0; i < count; i++) {
                int age = (int) (100 * Math.random());
                int height = (int) (200 * Math.random());
                int weight = (int) (200 * Math.random());
                int width = (int) (200 * Math.random());
                int shoes = (int) (15 * Math.random());
                int children = (int) (10 * Math.random());
                int phage = (int) Math.round(Math.random());

                normal.println(String.format("%d,%d", age, phage));
                bonus.println(String.format("%d,%d,%d,%d,%d,%d,%d", age, height, weight, width, shoes, children, phage));
            }
        }
    }
}
