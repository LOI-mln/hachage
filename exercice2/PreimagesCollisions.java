package exercice2;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Ex2_TroisAlgos.java
 *
 * Trois algorithmes différents pour l'exercice 2 (tests collisions / préimages)
 *
 * - Algo A : detectCollisionWithMap()    -> détection de collisions via HashMap (déterministe, O(n))
 * - Algo B : findPreimageLinear(target)  -> recherche de préimage par parcours linéaire de l'alphabet (O(n))
 * - Algo C : findPreimageRandom(target)  -> recherche probabiliste/aléatoire (Monte-Carlo) démonstrative
 *
 * Compile : javac Ex2_TroisAlgos.java
 * Run     : java Ex2_TroisAlgos
 */
public class PreimagesCollisions {

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
        byte[] b = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
        return b[0] & 0xFF;
    }


    /* Algo 1 : détection de collision */

    public static Optional<String> detectCollisionWithMap() {
        Map<Integer, Character> seen = new HashMap<>();
        for (char c : ALPHABET) {
            int h = H(c);
            if (seen.containsKey(h) && seen.get(h) != c) {
                return Optional.of(String.format("Collision: '%c' and '%c' -> %s", seen.get(h), c, toHex(h)));
            }
            seen.put(h, c);
        }
        return Optional.empty();
    }

    /*Algo 2 : recherche de préimage (linéaire) */

    public static Optional<Character> findPreimageLinear(int target) {
        for (char c : ALPHABET) {
            if (H(c) == (target & 0xFF)) return Optional.of(c);
        }
        return Optional.empty();
    }

    /* Algo 3 : recherche de préimage aléatoire */

    public static Map.Entry<String,Integer> findPreimageRandom(int target, int maxAttempts, Random rnd) {
        for (int tries = 1; tries <= maxAttempts; tries++) {
            char c = ALPHABET.get(rnd.nextInt(ALPHABET.size()));
            if (H(c) == (target & 0xFF)) {
                return Map.entry(String.valueOf(c), tries);
            }
        }
        return null;
    }

    /* Utilitaires */
    private static String toHex(int x) {
        return String.format("0x%02X", x & 0xFF);
    }

    /* MAIN */
    public static void main(String[] args) {
        System.out.println("=== Exercice 2 - Trois algorithmes ===\n");

        // Algo 1 : Collision detection
        System.out.println("[Algo A] Détection de collisions (HashMap)");
        Optional<String> collision = detectCollisionWithMap();
        if (collision.isPresent()) {
            System.out.println("  -> " + collision.get());
        } else {
            System.out.println("  -> Aucune collision trouvée dans l'alphabet (H est injective sur cet alphabet).");
        }
        System.out.println();

        // Algo 2 : Préimage
        int target1 = 0x41; // 'A'
        System.out.println("[Algo B] Recherche linéaire de préimage pour " + toHex(target1));
        Optional<Character> pre1 = findPreimageLinear(target1);
        System.out.println("  -> " + (pre1.isPresent() ? "Préimage trouvée : '" + pre1.get() + "'" : "Aucune préimage dans l'alphabet"));

        int target2 = 200; // probablement hors alphabet
        System.out.println("[Algo B] Recherche linéaire de préimage pour " + toHex(target2));
        Optional<Character> pre2 = findPreimageLinear(target2);
        System.out.println("  -> " + (pre2.isPresent() ? "Préimage trouvée : '" + pre2.get() + "'" : "Aucune préimage dans l'alphabet"));
        System.out.println();

        // Algo 3 : Préimage aléatoire (probabiliste)
        Random rnd = new Random();
        int target3 = ALPHABET.isEmpty() ? 0x00 : H(ALPHABET.get(rnd.nextInt(ALPHABET.size()))); // cible qui a forcement une préimage
        System.out.println("[Algo C] Recherche aléatoire de préimage pour " + toHex(target3) + " (max 1000 essais)");
        Map.Entry<String,Integer> res = findPreimageRandom(target3, 1000, rnd);
        if (res != null) {
            System.out.println("  -> Trouvé '" + res.getKey() + "' en " + res.getValue() + " essais (attendu ~" + ALPHABET.size()/2 + ")");
        } else {
            System.out.println("  -> Échec après 1000 essais (rare si target a une préimage dans l'alphabet)");
        }

    }
}
