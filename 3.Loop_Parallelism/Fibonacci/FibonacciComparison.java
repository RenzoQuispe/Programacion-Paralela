//https://byjus.com/gate/difference-between-hashmap-and-concurrenthashmap/#:~:text=HashMap%20isn't%20thread%2Dsafe,are%20capable%20of%20performing%20simultaneously.

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FibonacciComparison {

    // 1. Sequential Recursive (Time Complexity: O(2^n))
    public static int fibSequential(int n) {
        if (n <= 1) return n;
        return fibSequential(n - 1) + fibSequential(n - 2);
    }

    // 2. Sequential with Memoization (Time Complexity: O(n))
    private static Map<Integer, Long> memo = new HashMap<>();
    public static long fibMemo(int n) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);
        long value = fibMemo(n - 1) + fibMemo(n - 2);
        memo.put(n, value);
        return value;
    }

    // 3. Parallel RecursiveAction (Time Complexity: O(2^n), Parallel)
    public static class FibParallel extends RecursiveAction {
        private int n;
        private long result;

        public FibParallel(int n) {
            this.n = n;
        }

        public long getResult() {
            return result;
        }

        protected void compute() {
            if (n <= 1) {
                result = n;
            } else {
                FibParallel f1 = new FibParallel(n - 1);
                FibParallel f2 = new FibParallel(n - 2);
                f1.fork();
                f2.compute();
                f1.join();
                result = f1.getResult() + f2.getResult();
            }
        }
    }

    // 4. Parallel RecursiveAction with Memoization (Time Complexity: O(n), Parallel)
    public static class FibParallelMemo extends RecursiveAction {
        private static ConcurrentHashMap<Integer, Integer> cache = new ConcurrentHashMap<>();
        private int n;
        private AtomicInteger result;

        public FibParallelMemo(int n, AtomicInteger result) {
            this.n = n;
            this.result = result;
        }

        protected void compute() {
            if (n <= 1) {
                result.set(n);
            } else if (cache.containsKey(n)) {
                result.set(cache.get(n));
            } else {
                AtomicInteger r1 = new AtomicInteger();
                AtomicInteger r2 = new AtomicInteger();
                FibParallelMemo f1 = new FibParallelMemo(n - 1, r1);
                FibParallelMemo f2 = new FibParallelMemo(n - 2, r2);
                f1.fork();
                f2.compute();
                f1.join();
                int value = r1.get() + r2.get();
                cache.putIfAbsent(n, value);
                result.set(value);
            }
        }
    }

    // 5. Parallel RecursiveTask (Time Complexity: O(2^n), but better CPU utilization)
    public static class FibTask extends RecursiveTask<Long> {
        private int n;

        public FibTask(int n) {
            this.n = n;
        }

        @Override
        protected Long compute() {
            if (n <= 1) {
                return (long) n;
            }
            FibTask f1 = new FibTask(n - 1);
            FibTask f2 = new FibTask(n - 2);
            f1.fork();
            long r2 = f2.compute();
            long r1 = f1.join();
            return r1 + r2;
        }
    }
    
     // 6. Parallel RecursiveTask with memo
        static class FibonacciTask extends RecursiveTask<Long> {
        private final int n;

        public FibonacciTask(int n) {
            this.n = n;
        }

        @Override
        protected Long compute() {
            // Base cases
            if (n <= 1) {
                return (long) n;
            }

            // Check memoized value
            if (memo.containsKey(n)) {
                return memo.get(n);
            }

            // Fork two subtasks
            FibonacciTask f1 = new FibonacciTask(n - 1);
            FibonacciTask f2 = new FibonacciTask(n - 2);

            f1.fork(); // fork f1 to run asynchronously
            long result2 = f2.compute(); // compute f2 directly (to reduce overhead)
            long result1 = f1.join();    // wait for f1 to complete

            long result = result1 + result2;

            // Store result in memoization cache
            memo.putIfAbsent(n, result);

            return result;
        }
    }

    public static long computeFibonacci(int n) {
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(new FibonacciTask(n));
    }
    

    // Timing Utility
    public static long timeExecution(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return (System.nanoTime() - start) / 1_000_000; // milliseconds
    }

    public static void main(String[] args) {
        int n = 45;
        ForkJoinPool pool = new ForkJoinPool();

        // Sequential Recursive
        long t1 = timeExecution(() -> System.out.println("Seq Rec: " + fibSequential(n)));

        // Memoized
        long t2 = timeExecution(() -> System.out.println("Memo: " + fibMemo(n)));

        // Parallel RecursiveAction
        long t3 = timeExecution(() -> {
            FibParallel task = new FibParallel(n);
            pool.invoke(task);
            System.out.println("Parallel: " + task.getResult());
        });

        // Parallel with Memo
        long t4 = timeExecution(() -> {
            AtomicInteger result = new AtomicInteger();
            pool.invoke(new FibParallelMemo(n, result));
            System.out.println("Parallel Memo: " + result.get());
        });

        // Parallel RecursiveTask
        long t5 = timeExecution(() -> {
            FibTask task = new FibTask(n);
            long result = pool.invoke(task);
            System.out.println("RecursiveTask: " + result);
        });
        
             // Parallel RecursiveTask withMemo
        long t6 = timeExecution(() -> {
            long result = computeFibonacci(n);
            System.out.println("ParalleRecursiveTaskMemo: " + result);
        });

        System.out.println("\nExecution Times (ms):");
        System.out.println("1. Sequential Recursive     : " + t1);
        System.out.println("2. Memoized Recursive       : " + t2);
        System.out.println("3. Parallel RecursiveAction : " + t3);
        System.out.println("4. Parallel Memoized        : " + t4);
        System.out.println("5. RecursiveTask Parallel   : " + t5);
        System.out.println("6. RecursiveTask ParallelMemo   : " + t6);
    }
}
