import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Randitya Setyawan Mohamad
 * COMP 3632 Assignment 2, Fall 2017
 */
public class Decrypt {
    private ArrayList<byte[]> blocks = new ArrayList<>();

    public Decrypt(String filename) throws IOException {
        byte data[] = Files.readAllBytes(Paths.get(filename));

        if (data.length % 16 != 0)
            throw new IOException("Ciphertext is not a multiple of 16");

        //Divide into blocks of 16
        for (int i = 0; i < data.length; i += 16)
            blocks.add(Arrays.copyOfRange(data, i, i + 16));
    }

    /**
     * Decrypt byte step
     */
    private static byte decryptByte(int index, byte tampered[]) throws IOException {
        while (!checkOracle(tampered))
            tampered[index]++;

        int i;
        for (i = 0; i < index && checkOracle(tampered); i++)
            tampered[i]++;

        return (byte) (tampered[index] ^ (16 - i));
    }

    /**
     * Decrypt block step
     */
    public static byte[] decryptBlock(byte block[]) throws IOException {
        // tampered = [ fabricatedIV | targetBlock ]
        byte tampered[] = new byte[32];
        byte decrypted[] = new byte[16];
        System.arraycopy(block, 0, tampered, 16, 16);

        for (int k = 15; k >= 0; k--) {
            tampered[k] = 0;
            for (int j = k + 1; j < 16; j++)
                tampered[j] = (byte) (decrypted[j] ^ (16 - k));
            decrypted[k] = decryptByte(k, tampered);
        }
        return decrypted;
    }

    /**
     * Decrypt step
     */
    public void decryptAll() throws IOException {
        for (int i = 1; i < blocks.size(); i++)
            System.out.write(xorBlocks(blocks.get(i - 1), decryptBlock(blocks.get(i))));
    }

    public static byte[] xorBlocks(byte b1[], byte b2[]) {
        byte retval[] = new byte[16];
        for (int i = 0; i < 16; i++)
            retval[i] = (byte) (b1[i] ^ b2[i]);
        return retval;
    }

    private static boolean checkOracle(byte data[]) throws IOException {
        Files.write(Paths.get("oracle_file"), data);
        Process oracle = new ProcessBuilder("python", "./oracle", "oracle_file").start();
        return oracle.getInputStream().read() == 49;
    }

    public static void main(String args[]) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: decrypt <filename>");
            System.exit(0);
        }

        Decrypt decrypt = new Decrypt(args[0]);
        decrypt.decryptAll();
    }
}
