import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class VectorSumaParalela extends RecursiveAction {
    private static final int THRESHOLD = 10000; // Umbral de tamaño para dividir el trabajo
    private long[] array;
    private int start;
    private int end;
    private long[] result; // Array para almacenar los resultados parciales

    // Constructor para inicializar los parámetros
    public VectorSumaParalela(long[] array, int start, int end, long[] result) {
        this.array = array;
        this.start = start;
        this.end = end;
        this.result = result;
    }

    // Método para realizar la suma directamente sobre el segmento
    private void computeDirectly() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += array[i];
        }
        result[0] = sum; // Guardamos el resultado en el índice 0 del array 'result'
    }

    @Override
    protected void compute() {
        if (end - start <= THRESHOLD) {
            computeDirectly();  // Si el segmento es suficientemente pequeño, lo procesamos de manera secuencial
        } else {
            // Si el segmento es grande, lo dividimos en dos tareas y las ejecutamos en paralelo
            int middle = (start + end) / 2;
            long[] leftResult = new long[1];  // Array para el resultado de la sub-tarea izquierda
            long[] rightResult = new long[1]; // Array para el resultado de la sub-tarea derecha

            VectorSumaParalela leftTask = new VectorSumaParalela(array, start, middle, leftResult);
            VectorSumaParalela rightTask = new VectorSumaParalela(array, middle, end, rightResult);

            // Invocamos las dos tareas de forma paralela
            invokeAll(leftTask, rightTask);

            // Combinamos los resultados de las dos tareas
            result[0] = leftResult[0] + rightResult[0];
        }
    }

    public static long parallelSum(long[] array) {
        long[] result = new long[1];  // Array para almacenar el resultado final
        ForkJoinPool pool = new ForkJoinPool();  // Creamos un pool de hilos
        VectorSumaParalela task = new VectorSumaParalela(array, 0, array.length, result);
        pool.invoke(task);  // Ejecutamos la tarea en el pool de hilos
        return result[0];  // Devolvemos la suma total
    }

    // Implementación recursiva normal
    public static long recursiveSum(long[] array, int start, int end) {
        // Condición base: Si el rango es un solo elemento, retornamos ese elemento
        if (start >= end) {
            return 0;  // No se debe sumar si los índices están fuera de rango
        }
        if (start == end - 1) {  // Si solo hay un elemento en el rango
            return array[start];
        }
        // Caso recursivo: sumamos los elementos dividiendo el rango
        int middle = (start + end) / 2;
        return recursiveSum(array, start, middle) + recursiveSum(array, middle, end);
    }

    public static void main(String[] args) {
        long[] array = new long[100000000];  // Creamos un array con 100,000 elementos
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1;  // Rellenamos el array con números del 1 al 100,000
            //array[i] = 1;
        }

        // Medimos el tiempo de ejecución de la suma paralela
        long startTime = System.nanoTime();
        long parallelSumResult = parallelSum(array);
        long endTime = System.nanoTime();
        long parallelExecutionTime = endTime - startTime;

        // Medimos el tiempo de ejecución de la suma recursiva normal
        startTime = System.nanoTime();
        long recursiveSumResult = recursiveSum(array, 0, array.length);
        endTime = System.nanoTime();
        long recursiveExecutionTime = endTime - startTime;

        // Mostramos los resultados y los tiempos de ejecución
        System.out.println("Resultado suma paralela: " + parallelSumResult);
        System.out.println("Tiempo de ejecución suma paralela: " + parallelExecutionTime + " nanosegundos");

        System.out.println("Resultado suma recursiva normal: " + recursiveSumResult);
        System.out.println("Tiempo de ejecución suma recursiva normal: " + recursiveExecutionTime + " nanosegundos");
    }
}
