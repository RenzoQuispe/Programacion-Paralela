import java.util.concurrent.*;

public class SumTask extends RecursiveTask<Integer> {
    private int[] arr;
    private int start, end;

    public SumTask(int[] arr, int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (end - start <= 1000) {  // Tamaño pequeño, resolver directamente
            int sum = 0;
            for (int i = start; i < end; i++) {
                sum += arr[i];
            }
            return sum;
        }

        int length = end - start;
        int step = length / 4;

        // Crear 4 tareas
        SumTask t1 = new SumTask(arr, start, start + step);
        SumTask t2 = new SumTask(arr, start + step, start + 2 * step);
        SumTask t3 = new SumTask(arr, start + 2 * step, start + 3 * step);
        SumTask t4 = new SumTask(arr, start + 3 * step, end);

        // Lanzar tareas en paralelo
        t1.fork();
        t2.fork();
        t3.fork();

        int r4 = t4.compute(); // Ejecuta en el hilo actual
        int r3 = t3.join();
        int r2 = t2.join();
        int r1 = t1.join();

        return r1 + r2 + r3 + r4;
    }

    public static void main(String[] args) {
        int[] data = new int[10_000_000];
        for (int i = 0; i < data.length; i++) {
            data[i] = 1;
        }

        // Paralela
        ForkJoinPool pool = new ForkJoinPool(4); // Usar 4 hilos
        SumTask task = new SumTask(data, 0, data.length);

        long startTime1 = System.currentTimeMillis();
        int result = pool.invoke(task);
        long endTime1 = System.currentTimeMillis();

        System.out.println("Resultado: " + result);
        System.out.println("Tiempo: " + (endTime1 - startTime1) + " ms");

        //Solo recursividad
        long startTime2 = System.currentTimeMillis();
        int sumRecursividad =0;
        for(int i=0; i<data.length; i++){
            sumRecursividad = sumRecursividad + data[i];
        }
        long endTime2 = System.currentTimeMillis();
        System.out.println("Resultado: " + sumRecursividad);
        System.out.println("Tiempo: " + (endTime2 - startTime2) + " ms");

    }
}
