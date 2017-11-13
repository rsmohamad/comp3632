import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Randitya Setyawan Mohamad
 * COMP 3632 Assignment 2, Fall 2017
 */
public class Encrypt {
    private ArrayList<byte[]> blocks = new ArrayList<>();

    public Encrypt(String filename) throws IOException {
        byte data[] = Files.readAllBytes(Paths.get(filename));

        if (data.length % 16 != 0)
            throw new IOException("Plaintext is not a multiple of 16");

        //Divide into blocks of 16
        for (int i = 0; i < data.length; i += 16)
            blocks.add(Arrays.copyOfRange(data, i, i + 16));
    }

    public void encrypt() throws IOException {
        ArrayList<byte[]> encrypted = new ArrayList<>();
        
        // Start with a random block
        // Last block = random block
        byte y[] = new byte[16];
        new Random().nextBytes(y);
        encrypted.add(y);

        // For every block in the plaintext:
        // C_i-1 = Dec(C_i) xor P_i
        // Work from the last block until the IV
        for (int i = blocks.size() - 1; i >= 0; i--) {
            y = Decrypt.decryptBlock(y);
            y = Decrypt.xorBlocks(y, blocks.get(i));
            encrypted.add(y);
        }
        
        // Print [IV|C_0|...|C_n-1] to stdout
        for (int i = encrypted.size() - 1; i >= 0; i--)
            System.out.write(encrypted.get(i));
    }

    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: encrypt <filename>");
            System.exit(0);
        }

        Encrypt encrypt = new Encrypt(args[0]);
        encrypt.encrypt();
    }
}
