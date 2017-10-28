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
    private static byte decryptByte(int index, byte random[], byte target[]) throws IOException {
        byte tampered[] = new byte[32];
        System.arraycopy(random, 0, tampered, 0, 16);
        System.arraycopy(target, 0, tampered, 16, 16);

        while (!checkOracle(tampered))
            tampered[index]++;

        int i;
        for (i = 0; i < index && checkOracle(tampered); i++)
            tampered[i]++;

        return (byte) (tampered[index] ^ (16 - i));
    }

    /**
     * Decrypt block step
     *
     * @return Dec(block) with same key as the oracle
     */
    public static byte[] decryptBlock(byte block[]) throws IOException {
        byte decrypted[] = new byte[16];
        byte random[] = new byte[16];

        for (int k = 15; k >= 0; k--) {
            random[k] = 0;

            for (int j = k + 1; j < 16; j++)
                random[j] = (byte) (decrypted[j] ^ (16 - k));

            decrypted[k] = decryptByte(k, random, block);
        }
        return decrypted;
    }

    /**
     * Decrypt step
     * Decrypts every block and XOR with previous block
     */
    public void decryptAll() throws IOException {
        for (int i = 1; i < blocks.size(); i++)
            // XOR with previous block to get the right text
            // CBC mode
            System.out.write(xorBlocks(blocks.get(i - 1), decryptBlock(blocks.get(i))));
    }

    public static byte[] xorBlocks(byte b1[], byte b2[]) {
        byte retval[] = new byte[16];
        for (int i = 0; i < 16; i++)
            retval[i] = (byte) (b1[i] ^ b2[i]);
        return retval;
    }

    private static boolean checkOracle(byte data[]) throws IOException {
        Files.write(Paths.get("temp_file"), data);
        Process oracle = Runtime.getRuntime().exec("python ./oracle temp_file");

        // ASCII character '1' = 49 in integer
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
