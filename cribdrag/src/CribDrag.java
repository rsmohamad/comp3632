import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * @author Randitya Setyawan Mohamad
 * Small class that implements crib dragging
 * For COMP3632 Assignment 2, Spring 2017
 */
public class CribDrag {
    private byte[] cipherText1;
    private byte[] cipherText2;
    private byte[] c1XORc2;
    private byte[] cribText;

    public void loadCipherTexts(String file1, String file2) {
        try {
            cipherText1 = Files.readAllBytes(Paths.get(file1));
            cipherText2 = Files.readAllBytes(Paths.get(file2));

            if (cipherText1.length != cipherText2.length) {
                System.out.println("Cipher texts lengths are not equal. " + cipherText1.length + " " + cipherText2.length);
                cipherText1 = cipherText2 = null;
            } else {
                System.out.println("Loaded cipher texts with length = " + cipherText1.length);
                c1XORc2 = new byte[cipherText1.length];
                cribText = new byte[cipherText1.length];

                for (int i = 0; i < cipherText1.length; i++)
                    c1XORc2[i] = (byte) (cipherText1[i] ^ cipherText2[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlainTexts(String file1, String file2) {
        try (PrintWriter writer1 = new PrintWriter(new FileWriter(file1));
             PrintWriter writer2 = new PrintWriter(new FileWriter(file2))) {
            writer1.print(getCribText());
            writer2.print(getPlainTextFromCrib());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cribDrag(byte text[]) {
        System.out.println(byteArrayToString(text));
        for (int i = 0; i <= c1XORc2.length - text.length; i++) {
            for (int j = 0; j < text.length; j++)
                c1XORc2[i + j] = (byte) (c1XORc2[i + j] ^ text[j]);

            System.out.println(i + ")\t" + byteArrayToString(c1XORc2));

            for (int j = 0; j < text.length; j++)
                c1XORc2[i + j] = (byte) (c1XORc2[i + j] ^ text[j]);
        }
        System.out.println();
    }

    private static byte[] textXOR(byte[] text1, byte[] text2) {
        byte[] retval = new byte[text1.length];
        for (int i = 0; i < text1.length; i++)
            retval[i] = (byte) (text1[i] ^ text2[i]);
        return retval;
    }

    private void cribDrag(String text) {
        cribDrag(stringToByteArray(text));
    }

    public void run() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Enter text: ");
            String text = sc.nextLine();
            cribDrag(text);
            System.out.print("Set index to add (n to skip, e to exit): ");
            String option = sc.nextLine().toLowerCase();

            if (option.equals("n"))
                continue;
            else if (option.equals("e"))
                break;
            else
                try {
                    setGuess(text, Integer.parseInt(option));
                } catch (NumberFormatException e) {
                }

            System.out.println("Current plaintext: " + getPlainTextFromCrib());
            System.out.println("Current cribtext : " + byteArrayToString(cribText));
        }

        sc.close();
    }

    private static String byteArrayToString(byte arr[]) {
        StringBuilder text = new StringBuilder();
        for (byte character : arr)
            if (character <= 126 && character >= 32)
                text.append((char) character);
            else
                text.append('*');

        return text.toString();
    }

    private static byte[] stringToByteArray(String text) {
        byte arr[] = new byte[text.length()];
        for (int i = 0; i < arr.length; i++)
            arr[i] = (byte) text.charAt(i);
        return arr;
    }

    private void setGuess(String text, int offset) {
        byte textArr[] = stringToByteArray(text);
        if (textArr.length + offset <= cribText.length)
            for (int i = 0; i < textArr.length; i++)
                cribText[i + offset] = textArr[i];
    }

    public String getPlainTextFromCrib() {
        return byteArrayToString(textXOR(c1XORc2, cribText));
    }

    public String getCribText() {
        return byteArrayToString(cribText);
    }

    public String getCipher1() {
        return byteArrayToString(cipherText1);
    }

    public String getCipher2() {
        return byteArrayToString(cipherText2);
    }

    public String getXORCipher() {
        return byteArrayToString(c1XORc2);
    }

    public static void main(String args[]) {
        CribDrag cribDrag = new CribDrag();
        cribDrag.loadCipherTexts("citext0", "citext1");
        System.out.println(cribDrag.getCipher1());
        System.out.println(cribDrag.getCipher2());
        System.out.println(cribDrag.getXORCipher());
        cribDrag.run();
        cribDrag.savePlainTexts("ptext0", "ptext1");
    }
}
