package exercice4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class attack {

    /* Définition de l'alphabet et des caractères autorisés */

    private static final char[] CARACTERES_SPECIAUX = new char[]{'!','#','(',')','*','+','/','?'};
    private static final List<Character> ALPHABET;
    static {
        List<Character> listeTemporaire = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) listeTemporaire.add(c);
        for (char c = 'a'; c <= 'z'; c++) listeTemporaire.add(c);
        for (char c : CARACTERES_SPECIAUX) listeTemporaire.add(c);
        ALPHABET = Collections.unmodifiableList(listeTemporaire);
    }

    /* Redéfinition des fonctions */

    public static int H(char caractere) {
        byte[] octets = String.valueOf(caractere).getBytes(StandardCharsets.UTF_8);
        return octets[0] & 0xFF;
    }

    public static int HStar(String mot) {
        int resultat = 0;
        for (int i = 0; i < mot.length(); i++) {
            resultat ^= H(mot.charAt(i));
        }
        return resultat & 0xFF;
    }

    /* Lecture du fichier avec les mots du dictionnaire */

    public static List<String> lireFichierDictionnaire(Path cheminFichier) throws IOException {
        List<String> lignes = Files.readAllLines(cheminFichier, StandardCharsets.UTF_8);
        List<String> mots = new ArrayList<>();

        for (String ligne : lignes) {
            String mot = ligne.trim();
            if (!mot.isEmpty()) mots.add(mot);
        }
        return mots;
    }

    /* Construction de la map avec les mots */

    public static Map<Integer, List<String>> construireTableDeHachage(List<String> mots) {
        Map<Integer, List<String>> tableHachage = new HashMap<>();

        for (String mot : mots) {
            try {
                int valeurHash = HStar(mot);
                tableHachage.computeIfAbsent(valeurHash, k -> new ArrayList<>()).add(mot);
            } catch (Exception e) {
                // Si un mot contient un caractère pas compris dans les termes on l'ignore
            }
        }
        return tableHachage;
    }

    /* Recherche de la première collision */

    public static Optional<String[]> premiereCollision(Map<Integer, List<String>> tableHachage) {
        int essais = 0;
        for (Map.Entry<Integer, List<String>> entree : tableHachage.entrySet()) {
            essais++;
            List<String> liste = entree.getValue();
            if (liste.size() >= 2) {
                return Optional.of(new String[]{
                        liste.get(0),
                        liste.get(1),
                        String.valueOf(entree.getKey()),
                        String.valueOf(essais)
                });
            }
        }
        return Optional.empty();
    }

    /* Attaque : préimage à deux lettres */

    public static Optional<String> preimageDeuxLettres(int valeurCible) {
        for (char a : ALPHABET) {
            int valeurRecherchee = valeurCible ^ H(a);
            for (char b : ALPHABET) {
                if (H(b) == valeurRecherchee)
                    return Optional.of("" + a + b);
            }
        }
        return Optional.empty();
    }

    /* Utilitaire */

    private static String toHex(int valeur) {
        return String.format("0x%02X", valeur & 0xFF);
    }

    /* MAIN */
    public static void main(String[] args) {
        Path cheminDictionnaire = Paths.get("./ods5.txt");
        Integer valeurCible = null;



        try {
            List<String> mots = lireFichierDictionnaire(cheminDictionnaire);
            System.out.println("Dictionnaire chargé : " + mots.size()+ " mots.");

            Map<Integer, List<String>> tableHachage = construireTableDeHachage(mots);

            // Recherche de la première collision
            Optional<String[]> collision = premiereCollision(tableHachage);
            if (collision.isPresent()) {
                String[] resultats = collision.get();
                System.out.println("Collision trouvée : \"" + resultats[0] + "\" et \"" + resultats[1] +
                        "\" → H* = " + toHex(Integer.parseInt(resultats[2])) +
                        " (trouvée après " + resultats[3] + " essais)");
            } else {
                System.out.println("Aucune collision trouvée dans ce dictionnaire.");
            }

            // Affichage du top 10 des hash les plus remplis
            List<Map.Entry<Integer, List<String>>> ensembles = new ArrayList<>(tableHachage.entrySet());
            ensembles.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));
            System.out.println("\nTop 10 des valeurs H* avec le plus de mots :");
            for (int i = 0; i < Math.min(10, ensembles.size()); i++) {
                Map.Entry<Integer, List<String>> e = ensembles.get(i);
                System.out.printf("  %s : %d mots → %s%n",
                        toHex(e.getKey()), e.getValue().size(),
                        e.getValue().subList(0, Math.min(5, e.getValue().size())));
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
            System.exit(3);
        }
    }
}
