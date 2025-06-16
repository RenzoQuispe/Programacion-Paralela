import java.util.*;

public class LZ78 {

    // Método para comprimir una cadena usando LZ78
    public static List<Pair> compress(String input) {
        Map<String, Integer> dictionary = new HashMap<>();
        List<Pair> result = new ArrayList<>();
        int dictIndex = 1;

        int i = 0;
        while (i < input.length()) {
            String current = "";
            int j = i;

            // Buscar la subcadena más larga que ya esté en el diccionario
            while (j < input.length() && dictionary.containsKey(current + input.charAt(j))) {
                current += input.charAt(j);
                j++;
            }

            // Obtener el índice del prefijo (si existe)
            int prefixIndex = dictionary.getOrDefault(current, 0);
            char nextChar = (j < input.length()) ? input.charAt(j) : 0;

            // Añadir al resultado
            result.add(new Pair(prefixIndex, nextChar));

            // Añadir la nueva entrada al diccionario
            if (j < input.length()) {
                dictionary.put(current + nextChar, dictIndex++);
            }

            i = j + 1;
        }

        return result;
    }

    // Método para descomprimir una lista de pares
    public static String decompress(List<Pair> compressed) {
        List<String> dictionary = new ArrayList<>();
        dictionary.add(""); // índice 0 vacío por convención

        StringBuilder output = new StringBuilder();

        for (Pair pair : compressed) {
            String entry = dictionary.get(pair.index) + (pair.character != 0 ? pair.character : "");
            output.append(entry);
            dictionary.add(entry);
        }

        return output.toString();
    }

    // Clase para representar pares (índice, carácter)
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

    // Main para probar el algoritmo
    public static void main(String[] args) {
        String input = "ABAABABAABAB";
        System.out.println("Input: " + input);

        List<Pair> compressed = compress(input);
        System.out.println("Compressed: " + compressed);

        String decompressed = decompress(compressed);
        System.out.println("Decompressed: " + decompressed);
    }
}