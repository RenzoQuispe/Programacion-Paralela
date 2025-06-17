import java.util.*;
import java.util.concurrent.*;

public class PascalTriangleComparison {
    private static final int NUM_ROWS = 50;
    private static final int NUM_CORES = 6;
    private static final ForkJoinPool POOL = new ForkJoinPool(NUM_CORES);

    // ---------- Version 1: Sequential ----------
    public static int[][] sequential() {
        int[][] triangle = new int[NUM_ROWS][];
        for (int i = 0; i < NUM_ROWS; i++) {
            triangle[i] = new int[i + 1];
            triangle[i][0] = triangle[i][i] = 1;
            for (int j = 1; j < i; j++) {
                triangle[i][j] = triangle[i - 1][j - 1] + triangle[i - 1][j];
            }
        }
        return triangle;
    }

    // ---------- Version 2: Sequential with Memoization ----------
    static Map<String, Integer> memoSeq = new HashMap<>();

    public static int memoSeqCalc(int r, int c) {
        if (c == 0 || c == r) return 1;
        String key = r + "," + c;
        if (memoSeq.containsKey(key)) return memoSeq.get(key);
        int val = memoSeqCalc(r - 1, c - 1) + memoSeqCalc(r - 1, c);
        memoSeq.put(key, val);
        return val;
    }

    public static int[][] sequentialMemo() {
        int[][] triangle = new int[NUM_ROWS][];
        for (int i = 0; i < NUM_ROWS; i++) {
            triangle[i] = new int[i + 1];
            for (int j = 0; j <= i; j++) {
                triangle[i][j] = memoSeqCalc(i, j);
            }
        }
        return triangle;
    }

    // ---------- Version 3: Parallel with RecursiveAction ----------
    static class PascalAction extends RecursiveAction {
        int[][] triangle;
        int row;

        PascalAction(int[][] triangle, int row) {
            this.triangle = triangle;
            this.row = row;
        }

        @Override
        protected void compute() {
            if (triangle[row - 1] == null) {
                PascalAction dependency = new PascalAction(triangle, row - 1);
                dependency.fork();
                dependency.join();
            }
            triangle[row] = new int[row + 1];
            triangle[row][0] = triangle[row][row] = 1;
            for (int j = 1; j < row; j++) {
                triangle[row][j] = triangle[row - 1][j - 1] + triangle[row - 1][j];
            }
        }
    }

    public static int[][] parallelAction() {
        int[][] triangle = new int[NUM_ROWS][];
        triangle[0] = new int[]{1};
        List<PascalAction> tasks = new ArrayList<>();
        for (int i = 1; i < NUM_ROWS; i++) {
            tasks.add(new PascalAction(triangle, i));
        }
        for (PascalAction task : tasks) {
            POOL.invoke(task);
        }
        return triangle;
    }

    // ---------- Version 4: Parallel with RecursiveAction + Memoization ----------
    static ConcurrentMap<String, Integer> memoAction = new ConcurrentHashMap<>();

    public static int memoActionCalc(int r, int c) {
        if (c == 0 || c == r) return 1;
        String key = r + "," + c;
        return memoAction.computeIfAbsent(key,
                k -> memoActionCalc(r - 1, c - 1) + memoActionCalc(r - 1, c));
    }

    static class PascalActionMemo extends RecursiveAction {
        int[][] triangle;
        int row;

        PascalActionMemo(int[][] triangle, int row) {
            this.triangle = triangle;
            this.row = row;
        }

        @Override
        protected void compute() {
            triangle[row] = new int[row + 1];
            for (int j = 0; j <= row; j++) {
                triangle[row][j] = memoActionCalc(row, j);
            }
        }
    }

    public static int[][] parallelActionMemo() {
        int[][] triangle = new int[NUM_ROWS][];
        List<PascalActionMemo> tasks = new ArrayList<>();
        for (int i = 0; i < NUM_ROWS; i++) {
            tasks.add(new PascalActionMemo(triangle, i));
        }
        for (PascalActionMemo task : tasks) {
            POOL.invoke(task);
        }
        return triangle;
    }

    // ---------- Version 5: Parallel with RecursiveTask ----------
    static class PascalTask extends RecursiveTask<int[]> {
        int[][] triangle;
        int row;

        PascalTask(int[][] triangle, int row) {
            this.triangle = triangle;
            this.row = row;
        }

        @Override
        protected int[] compute() {
            if (triangle[row - 1] == null) {
                PascalTask dep = new PascalTask(triangle, row - 1);
                triangle[row - 1] = dep.fork().join();
            }
            int[] r = new int[row + 1];
            r[0] = r[row] = 1;
            for (int j = 1; j < row; j++) {
                r[j] = triangle[row - 1][j - 1] + triangle[row - 1][j];
            }
            triangle[row] = r;
            return r;
        }
    }

    public static int[][] parallelTask() {
        int[][] triangle = new int[NUM_ROWS][];
        triangle[0] = new int[]{1};
        List<PascalTask> tasks = new ArrayList<>();
        for (int i = 1; i < NUM_ROWS; i++) {
            tasks.add(new PascalTask(triangle, i));
        }

        List<ForkJoinTask<int[]>> futures = new ArrayList<>();
        for (PascalTask task : tasks) {
            futures.add(POOL.submit(task));
        }

        for (int i = 1; i < NUM_ROWS; i++) {
            try {
                triangle[i] = futures.get(i - 1).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return triangle;
    }

    // ---------- Version 6: Parallel with RecursiveTask + Memoization ----------
    static ConcurrentMap<String, Integer> memoTask = new ConcurrentHashMap<>();

    public static int memoTaskCalc(int r, int c) {
        if (c == 0 || c == r) return 1;
        String key = r + "," + c;
        return memoTask.computeIfAbsent(key,
                k -> memoTaskCalc(r - 1, c - 1) + memoTaskCalc(r - 1, c));
    }

    static class PascalTaskMemo extends RecursiveTask<int[]> {
        int row;

        PascalTaskMemo(int row) {
            this.row = row;
        }

        @Override
        protected int[] compute() {
            int[] r = new int[row + 1];
            for (int j = 0; j <= row; j++) {
                r[j] = memoTaskCalc(row, j);
            }
            return r;
        }
    }

    public static int[][] parallelTaskMemo() {
        int[][] triangle = new int[NUM_ROWS][];
        List<PascalTaskMemo> tasks = new ArrayList<>();
        for (int i = 0; i < NUM_ROWS; i++) {
            tasks.add(new PascalTaskMemo(i));
        }

        List<ForkJoinTask<int[]>> futures = new ArrayList<>();
        for (PascalTaskMemo task : tasks) {
            futures.add(POOL.submit(task));
        }

        for (int i = 0; i < NUM_ROWS; i++) {
            try {
                triangle[i] = futures.get(i).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return triangle;
    }

    // ---------- Timing ----------
    public static void runAndTime(String label, Callable<int[][]> method) {
        try {
            long start = System.nanoTime();
            method.call();
            long end = System.nanoTime();
            System.out.printf("%s: %d µs%n", label, (end - start) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        runAndTime("1. Sequential", PascalTriangleComparison::sequential);
        runAndTime("2. Sequential + Memo", PascalTriangleComparison::sequentialMemo);
        runAndTime("3. Parallel (RecursiveAction)", PascalTriangleComparison::parallelAction);
        runAndTime("4. Parallel + Memo (Action)", PascalTriangleComparison::parallelActionMemo);
        runAndTime("5. Parallel (RecursiveTask)", PascalTriangleComparison::parallelTask);
        runAndTime("6. Parallel + Memo (Task)", PascalTriangleComparison::parallelTaskMemo);
    }
}