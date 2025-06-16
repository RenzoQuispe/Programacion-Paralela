import java.util.HashMap;

public class FibonacciMemoizacion {
    private HashMap<Integer, Long> memo = new HashMap<>();

    public long fib(int n) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) {
            return memo.get(n); // Ya lo calculamos antes
        }
        long result = fib(n - 1) + fib(n - 2);
        memo.put(n, result); // Guardamos el resultado

        return result;
    }
    public long fibRecursion(int n) {
        if (n <= 1) return n;
        return fibRecursion(n - 1) + fibRecursion(n - 2);
    }

    public static void main(String[] args) {
        FibonacciMemoizacion f = new FibonacciMemoizacion();
        int n = 50;
        //Usando Memoizacion
        long start = System.currentTimeMillis();
        long result = f.fib(n);
        long end = System.currentTimeMillis();
        System.out.println("fib(" + n + ") = " + result);
        System.out.println("Tiempo: " + (end - start) + " ms");
        //Solo recursvidad
        long start2 = System.currentTimeMillis();
        long result2 =f.fibRecursion(n);
        long end2 = System.currentTimeMillis();
        System.out.println("fib(" + n + ") = " + result2);
        System.out.println("Tiempo: " + (end2 - start2) + " ms");

    }
}
