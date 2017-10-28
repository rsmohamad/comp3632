import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

/**
 * COMP 3632 Assignment 2, Fall 2017
 * @author Randitya Setyawan Mohamad
 */
public class Decrypt {

    private byte plainData[];
    private byte cipherData[];
    private byte IV[];
    private int numOfBlocks;
    private Random random;
    public static int oracleCalls = 0;

    public Decrypt(String filename) throws IOException {
        loadCiphertext(filename);
        random = new Random();
    }

    /**
     * Decrypt byte step
     */
    private byte decryptByte(int blockNum, int byteNum, byte randomBlock[]) throws IOException {
        byte targetBlock[] = Arrays.copyOfRange(cipherData, blockNum * 16, (blockNum + 1) * 16);
        byte forgedBlock[] = concatByteArrays(randomBlock, targetBlock);

        while (!queryPaddingOracle(forgedBlock))
            forgedBlock[byteNum]++;

        int i;
        for (i = 0; i < byteNum; i++) {
            forgedBlock[i] = (byte) random.nextInt(256);
            if (!queryPaddingOracle(forgedBlock))
                break;
        }

        byte decryptedByte = (byte) (forgedBlock[byteNum] ^ (16 - i));

        if (blockNum > 0)
            return (byte) (decryptedByte ^ cipherData[(blockNum - 1) * 16 + byteNum]);
        else
            return (byte) (decryptedByte ^ IV[byteNum]);
    }

    /**
     * Decrypt block step
     */
    public byte[] decryptBlock(int blockNum) throws IOException {
        byte decryptedBlock[] = new byte[16];
        for (int byteNum = 15; byteNum >= 0; byteNum--) {
            byte randomBlock[] = new byte[16];
            random.nextBytes(randomBlock);
            randomBlock[byteNum] = 0;
            for (int j = byteNum + 1; j < 16; j++)
                if (blockNum > 0)
                    randomBlock[j] = (byte) (decryptedBlock[j] ^ cipherData[(blockNum - 1) * 16 + j] ^ (16 - byteNum));
                else
                    randomBlock[j] = (byte) (decryptedBlock[j] ^ IV[j] ^ (16 - byteNum));

            decryptedBlock[byteNum] = decryptByte(blockNum, byteNum, randomBlock);
            //System.out.println(byteArrayToString(decryptedBlock));
        }
        return decryptedBlock;
    }

    /**
     * Decrypt step
     */
    public void decryptAll() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < numOfBlocks; i++)
            out.write(decryptBlock(i));
        plainData = out.toByteArray();
    }

    public String getPlainText(){
        return byteArrayToString(plainData);
    }

    private void loadCiphertext(String filename) throws IOException {
        cipherData = Files.readAllBytes(Paths.get(filename));
        if (cipherData.length % 16 != 0)
            throw new IOException("Ciphertext not a multiple of 16");
        IV = Arrays.copyOfRange(cipherData, 0, 16);
        cipherData = Arrays.copyOfRange(cipherData, 16, cipherData.length);
        numOfBlocks = cipherData.length / 16;
    }

    private static void saveData(byte data[], String filename) throws IOException {
        Files.write(Paths.get(filename), data);
    }

    private static boolean queryPaddingOracle(byte data[]) throws IOException {
        oracleCalls++;
        saveData(data, "temp_file");
        Process oracle = Runtime.getRuntime().exec("python ./oracle temp_file");
        Scanner in = new Scanner(oracle.getInputStream());
        int result = in.nextInt();
        in.close();
        return result == 1;
    }

    private static byte[] concatByteArrays(byte array1[], byte array2[]) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(array1);
        out.write(array2);
        return out.toByteArray();
    }

    private static String byteArrayToString(byte arr[]) {
        StringBuilder text = new StringBuilder();
        for (byte character : arr)
            if (character <= 126 && character >= 32)
                text.append((char) character);
        return text.toString();
    }

    public static void cleanup(){
        try {
            Files.deleteIfExists(Paths.get("temp_file"));
        }catch (Exception e){}
    }

    public static void main(String args[]) throws IOException {
        Decrypt decrypt = new Decrypt(args[0]);
        decrypt.decryptAll();
        System.out.println(decrypt.getPlainText());
        System.out.println("Oracle calls: " + Decrypt.oracleCalls);
        decrypt.cleanup();
    }
}
