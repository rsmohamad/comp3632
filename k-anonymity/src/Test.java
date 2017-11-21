import java.io.IOException;
import java.io.PrintWriter;

public class Test {

    public static void main(String args[]) throws IOException{
        PrintWriter out = new PrintWriter("outputfile");
        for (int i = 0; i < 500; i++){
            Generator.generate(20);
            double cost = new kanon2("inputfile_bonus").anonymize(4);
            out.print(String.format("%.2f,", cost));
        }

        Generator.generate(20);
        double cost = new kanon2("inputfile_bonus").anonymize(4);
        out.print(String.format("%.2f", cost));

        out.close();
    }
}
