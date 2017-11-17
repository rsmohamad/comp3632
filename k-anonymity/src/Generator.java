import java.io.IOException;
import java.io.PrintWriter;

public class Generator {
    public static void main(String args[]) throws IOException{
        int count = 500;
        if (args.length == 1)
            count = Integer.parseInt(args[0]);

        try (PrintWriter out = new PrintWriter("inputfile")){

            out.println("Age,Phage");
            for (int i = 0; i < count; i++){
                int age =   (int) (80 * Math.random());
                int phage = (int) Math.round(Math.random());
                out.println(age + "," + phage);
            }
        }
    }
}
