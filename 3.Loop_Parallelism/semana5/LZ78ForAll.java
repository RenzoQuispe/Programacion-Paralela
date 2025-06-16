import java.util.*;

public class LZ78ForAll {

    public static class Pair {
        int index;
        char character;

        public Pair(int index, char character) {
            this.index = index;
            this.character = character;
        }

        @Override
        public String toString() {
            return "(" + index + ", " + character + ")";
        }
    }

    // 🔁 COMPRESIÓN USANDO SOLO FOR
    public static List<Pair> compress(String input) {
        Map<String, Integer> dictionary = new HashMap<>();
        List<Pair> result = new ArrayList<>();
        int dictIndex = 1;

        for (int i = 0; i < input.length(); ) {
            String current = "";
            int prefixIndex = 0;

            int j;
            for (j = i; j < input.length(); j++) {
                String candidate = current + input.charAt(j);
                if (dictionary.containsKey(candidate)) {
                    current = candidate;
                    prefixIndex = dictionary.get(candidate);
                } else {
                    break;
                }
            }

            char nextChar = (j < input.length()) ? input.charAt(j) : 0;
            result.add(new Pair(prefixIndex, nextChar));

            if (j < input.length()) {
                dictionary.put(current + nextChar, dictIndex++);
            }

            i = j + 1;
        }

        return result;
    }

    // 🔁 DESCOMPRESIÓN
    public static String decompress(List<Pair> compressed) {
        List<String> dictionary = new ArrayList<>();
        dictionary.add(""); // índice 0 vacío
        StringBuilder output = new StringBuilder();

        for (Pair pair : compressed) {
            String entry = dictionary.get(pair.index) + (pair.character != 0 ? pair.character : "");
            output.append(entry);
            dictionary.add(entry);
        }

        return output.toString();
    }

    // 🧪 MAIN
    public static void main(String[] args) {
        String input = "ABAABABAABAB";
        System.out.println("Input: " + input);

        List<Pair> compressed = compress(input);
        System.out.println("Compressed: " + compressed);

        String decompressed = decompress(compressed);
        System.out.println("Decompressed: " + decompressed);
    }
}