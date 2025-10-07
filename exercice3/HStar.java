package exercice3;
import java.nio.charset.StandardCharsets;

public class HStar {

    /* Fonction de l'exercice 1*/
    public static int H(char c) {
        byte[] utf8 = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
        return utf8[0] & 0xFF;
    }

    /* Fonction H*(texte) */
    public static int H_Star(String texte) {
        int acc = 0;
        for (int i = 0; i < texte.length(); i++) {
            acc ^= H(texte.charAt(i));
        }
        return acc;
    }

    /* Utilitaires */
    public static String toHex8(int x) {
        return String.format("0x%02X", x & 0xFF);
    }

    public static String toBinary8(int x) {
        return String.format("%8s", Integer.toBinaryString(x & 0xFF)).replace(' ', '0');
    }

    /* MAIN */
    public static void main(String[] args) {
        System.out.println("=== Exercice 3 : Fonction H*(texte) ===\n");

        // Exemples de calculs
        String[] tests = {"A","AA", "AB","BA","abc","aBc","test","TEST","Hello","Hello!"};

        for (String s : tests) {
            int h = H_Star(s);
            System.out.printf("H*(\"%s\") = %3d = %s = %s%n", s, h, toHex8(h), toBinary8(h));
        }

        // Exemple de collision volontaire : "AB" et "BA"
        System.out.println("\n--- Vérification de collision ---");
        int h1 = H_Star("AB");
        int h2 = H_Star("BA");
        if (h1 == h2) {
            System.out.println("Collision trouvée : H*(\"AB\") = H*(\"BA\") = " + toHex8(h1));
        } else {
            System.out.println("Pas de collision entre AB et BA (rare).");
        }

        // Exemple : texte long
        String texte = "Cryptographie";
        System.out.println("\nH*(\"" + texte + "\") = " + toHex8(H_Star(texte)));
    }
}