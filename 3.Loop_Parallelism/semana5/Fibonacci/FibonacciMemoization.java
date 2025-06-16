
import java.util.HashMap;


    

public class FibonacciMemoization {
    // HashMap to store already computed Fibonacci values
    private static HashMap<Integer, Long> memo = new HashMap<>();
    
  
    static long fibonacciSeq(int n) {
        if (n < 2) {
            return n;
        } else {
            return fibonacciSeq(n - 1) + fibonacciSeq(n - 2);
        }
    }    
    
// Timing Utility
    public static long timeExecution(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return (System.nanoTime() - start) / 1_000_000; // milliseconds
    }
    
    public static long fibonacci(int n) {
        // Base cases
        if (n <= 1) {
            return n;
        }

        // Check if result is already in memo
        if (memo.containsKey(n)) {
            return memo.get(n);
        }

        // Compute and store in memo
        long result = fibonacci(n - 1) + fibonacci(n - 2);
        memo.put(n, result);

        return result;
    }

    public static void main(String[] args) {
        int number = 30;
        
         // Sequential Recursive
        long t1 = timeExecution(() -> System.out.println("Seq Rec: " + fibonacciSeq(number)));

        // Memoized
        long t2 = timeExecution(() -> System.out.println("Memo: " +  fibonacci(number)));
        
        System.out.println("\nExecution Times (ms):");
        System.out.println("1. Sequential Recursive     : " + t1);
        System.out.println("2. Memoized Recursive       : " + t2);
        
    }
}
