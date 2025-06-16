import java.util.Arrays;
import java.util.concurrent.*;

public class PascalTriangleForkJoinMemo {
    private static final int NUM_ROWS = 25;
    private static final ForkJoinPool POOL = new ForkJoinPool(4);

    // Memoization cache: r,c -> value
    private static final ConcurrentMap<String, Integer> memo = new ConcurrentHashMap<>();

    // Task to compute a single Pascal Triangle value with memoization
    static class PascalValueTask extends RecursiveTask<Integer> {
        private final int r, c;

        PascalValueTask(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        protected Integer compute() {
            if (c == 0 || c == r) {
                return 1;
            }

            String key = r + "," + c;

            // Use memoization
            Integer cached = memo.get(key);
            if (cached != null) return cached;

            PascalValueTask left = new PascalValueTask(r - 1, c - 1);
            PascalValueTask right = new PascalValueTask(r - 1, c);

            left.fork(); // execute left in parallel
            int rightResult = right.compute(); // compute right directly
            int leftResult = left.join(); // wait for left

            int result = leftResult + rightResult;
            memo.putIfAbsent(key, result);
            return result;
        }
    }

    public static int[][] generateTriangle(int numRows) {
        int[][] triangle = new int[numRows][];

        for (int i = 0; i < numRows; i++) {
            int rowLen = i + 1;
            triangle[i] = new int[rowLen];
            RecursiveTask<Integer>[] tasks = new RecursiveTask[rowLen];

            // Launch tasks for all elements in the row
            for (int j = 0; j < rowLen; j++) {
                PascalValueTask task = new PascalValueTask(i, j);
                tasks[j] = task;
                POOL.execute(task); // submit to ForkJoinPool
            }

            // Join all tasks
            for (int j = 0; j < rowLen; j++) {
                triangle[i][j] = tasks[j].join();
            }
        }

        return triangle;
    }

    public static void main(String[] args) {
        long start = System.nanoTime();
        int[][] triangle = generateTriangle(NUM_ROWS);
        long end = System.nanoTime();

        System.out.println("Fork/Join parallel per-value + Memoization time: " + (end - start) / 1000 + " µs");

        for (int[] row : triangle) {
            System.out.println(Arrays.toString(row));
        }

        POOL.shutdown();
    }
}