package exercice1;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class MauvaiseFonctionHashage {

    // Alphabet autorisé : majuscules, minuscules et caractères spéciaux listés
    private static final char[] SPECIALS = new char[]{'!','#','(',')','*','+','/','?'};
    private static final List<Character> ALPHABET;

    static {
        List<Character> tmp = new ArrayList<>();
        for (char c='A'; c<='Z'; c++) tmp.add(c);
        for (char c='a'; c<='z'; c++) tmp.add(c);
        for (char c : SPECIALS) tmp.add(c);
        ALPHABET = Collections.unmodifiableList(tmp);
    }


    public static int H(char c) {
        if (!ALPHABET.contains(c)) {
            throw new IllegalArgumentException(
                "Caractère non autorisé : '" + c + "'. Utilise majuscules, minuscules, ou l’un de ces caractères spéciaux : ! # ( ) * + / ?");
        }

        byte[] utf8 = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
        if (utf8.length != 1)
            throw new IllegalArgumentException("Le caractère ne s’encode pas sur un seul octet UTF-8.");

        return utf8[0] & 0xFF;
    }

    /** Méthode pour afficher le code binaire sur 8 bits */
    public static String toBinary8(int value) {
        return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
    }

    /** Méthode pour afficher en hexadécimal */
    public static String toHex8(int value) {
        return String.format("0x%02X", value & 0xFF);
    }

    /** Méthode de tests */
    public static void main(String[] args) {
        System.out.println("========= Exercice 1 =========\n");
        char[] tests = {'A', 'a', '!', '?', 'Z', 'z'};
        for (char c : tests) {
            int h = H(c);
            System.out.println("H('" + c + "') = " + h +
                    " = " + toHex8(h) + " = " + toBinary8(h));
        }

        try {
            H('é');
        } catch (IllegalArgumentException e) {
            System.out.println("\nExemple d’erreur attendue : \n" + e.getMessage());
        }
    }
}
