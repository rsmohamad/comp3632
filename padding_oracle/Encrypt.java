import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * COMP 3632 Assignment 2, Fall 2017
 *
 * @author Randitya Setyawan Mohamad
 */
public class Encrypt {
    private ArrayList<byte []> blocks = new ArrayList<>();

    public Encrypt(String filename)throws IOException{
        byte data[] = Files.readAllBytes(Paths.get(filename));

        if (data.length % 16 != 0)
            throw new IOException("Plaintext is not a multiple of 16");

        //Divide into blocks of 16
        for (int i = 0; i < data.length; i+=16)
            blocks.add(Arrays.copyOfRange(data, i, i+16));
    }

    /**
     * 1. Generate Yn (Random)
     * 2. Yi-1 = Dec(Yi) xor Xi
     *    For all i in X
     */
    public void encrypt() throws IOException{
        ArrayList<byte []> encrypted = new ArrayList<>();

        byte y[] = new byte[16];
        new Random().nextBytes(y);
        encrypted.add(y);

        for (int i = blocks.size() -1; i >= 0; i--){
            y = Decrypt.decryptBlock(y);
            y = Decrypt.xorBlocks(y, blocks.get(i));
            encrypted.add(y);
        }

        for (int i = encrypted.size() -1; i >= 0; i--)
            System.out.write(encrypted.get(i));
    }

    public static void main(String args[]) throws IOException{
        if (args.length != 1) {
            System.err.println("Usage: encrypt <filename>");
            System.exit(0);
        }

        Encrypt encrypt = new Encrypt(args[0]);
        encrypt.encrypt();
    }
}
