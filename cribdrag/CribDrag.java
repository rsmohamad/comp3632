import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Scanner;

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
                c1XORc2 = textXOR(cipherText1, cipherText2);
                cribText = new byte[cipherText1.length];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlainTexts(String file1, String file2) {
        try (FileOutputStream writer1 = new FileOutputStream(file1);
             FileOutputStream writer2 = new FileOutputStream(file2)) {
            writer1.write(cribText);
            writer2.write(textXOR(cribText, c1XORc2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cribDrag(byte text[]) {
        System.out.println(byteArrayToString(text));
        byte crib[] = new byte[text.length];
        for (int i = 0; i <= c1XORc2.length - text.length; i++) {
            for (int j = 0; j < text.length; j++)
                crib[j] = (byte) (c1XORc2[i + j] ^ text[j]);
            System.out.println(i + ")  \t" + byteArrayToString(crib));
        }
        System.out.println();
    }

    private static byte[] textXOR(byte[] text1, byte[] text2) {
        byte[] retval = new byte[text1.length];
        for (int i = 0; i < text1.length; i++)
            retval[i] = (byte) (text1[i] ^ text2[i]);
        return retval;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Enter text: ");
            String text = sc.nextLine();
            cribDrag(text.getBytes());
            System.out.print("Set index to add (e to exit): ");
            String option = sc.nextLine().toLowerCase();

            if (option.equals("e"))
                break;
            else
                try {
                    setGuess(text, Integer.parseInt(option));
                } catch (NumberFormatException e) {
                }

            System.out.println("Plain: " + byteArrayToString(textXOR(c1XORc2, cribText)));
            System.out.println("Crib : " + byteArrayToString(cribText));
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

    private void setGuess(String text, int offset) {
        byte textArr[] = text.getBytes();
        if (textArr.length + offset <= cribText.length)
            for (int i = 0; i < textArr.length; i++)
                cribText[i + offset] = textArr[i];
    }

    public static void main(String args[]) {
        CribDrag cribDrag = new CribDrag();
        cribDrag.loadCipherTexts("citext0", "citext1");
        cribDrag.run();
        cribDrag.savePlainTexts("ptext0", "ptext1");
    }
}
