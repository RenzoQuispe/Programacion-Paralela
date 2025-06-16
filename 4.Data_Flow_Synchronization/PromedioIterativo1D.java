/* PROMEDIO ITERATIVO UNIDIMENSIONAL CON FASERS

El programa simula un proceso de promedio iterativo unidimensional, 
donde cada elemento de un arreglo es actualizado repetidamente con el promedio de sus vecinos inmediatos.

- Se utiliza una clase Data para almacenar dos arreglos:
    oldX: contiene los valores actuales.
    newX: contiene los valores calculados en cada paso.
- El arreglo se inicializa con valores aleatorios (simulando una señal o estado físico inicial).
- Se crean n hilos, uno por cada elemento del arreglo, que:
    Calculan el promedio de los elementos vecinos (left y right).
    Almacenan ese promedio en newX[index].
    Usan objetos Phaser para sincronizar la ejecución entre vecinos, de modo que ningún hilo avance antes de que los valores necesarios estén actualizados.
    Solo un hilo (el de índice 0) realiza el intercambio (swap) de los arreglos al final de cada iteración.
- Al finalizar todas las iteraciones (nsteps), se imprime el contenido final del arreglo suavizado.

El uso del objeto Phaser permite sincronizar la ejecución por regiones locales, lo cual es más eficiente y escalable que una barrera global. 
Este enfoque permite que cada hilo espere únicamente a sus vecinos inmediatos, lo que mejora la concurrencia.

Resultado:
- Se muestra un arreglo inicial con valores aleatorios.
- Después de varias iteraciones, los valores se suavizan, eliminando cambios bruscos y convergiendo hacia una forma más estable y homogénea.
- No se imprime un promedio global, sino el estado final del sistema.
*/
import java.util.concurrent.Phaser;

public class PromedioIterativo1D {

    static class Data {

        double[] oldX;
        double[] newX;

        Data(int n) {
            oldX = new double[n];
            newX = new double[n];
            for (int i = 0; i < n; i++) {
                oldX[i] = Math.random() * 100; // Inicialización aleatoria
            }
        }

        // Método para hacer swap entre oldX y newX
        void swap() {
            double[] temp = oldX;
            oldX = newX;
            newX = temp;
        }
    }

    public static void main(String[] args) {
        final int n = 10;
        final int nsteps = 20;
        final Data data = new Data(n);
        
        // valores iniciales
        System.out.println("Inicial:");
        for (int i = 0; i < n; i++) {
            System.out.printf("%.2f ", data.oldX[i]);
        }

        // Crear faseres
        Phaser[] ph = new Phaser[n + 2];
        for (int i = 0; i < ph.length; i++) {
            ph[i] = new Phaser(1);
        }

        Thread[] threads = new Thread[n];

        for (int i = 0; i < n; i++) {
            final int index = i;

            threads[i] = new Thread(() -> {
                for (int iter = 0; iter < nsteps; iter++) {
                    double left = (index == 0) ? data.oldX[index] : data.oldX[index - 1];
                    double right = (index == n - 1) ? data.oldX[index] : data.oldX[index + 1];

                    data.newX[index] = (left + right) / 2.0;

                    ph[index + 1].arrive();

                    if (index > 0) {
                        ph[index].awaitAdvance(iter);
                    }
                    if (index < n - 1) {
                        ph[index + 2].awaitAdvance(iter);
                    }

                    // Solo un hilo hace el swap después de cada iteración
                    if (index == 0) {
                        // Asegurarse de que todos hayan llegado
                        for (int j = 0; j < n; j++) {
                            ph[j + 1].awaitAdvance(iter);
                        }
                        data.swap();
                    }
                }
            });

            threads[i].start();
        }

        // Esperamos que todos los hilos terminen
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // imprimir resultados finales
        System.out.println("\nResultado final:");
        for (int i = 0; i < n; i++) {
            System.out.printf("%.2f ", data.oldX[i]);
        }
    }
}
/*
La agrupación/chunking de iteraciones paralelas en un forall puede ser una consideración importante para el rendimiento (debido a la reducción de la sobrecarga).
La idea de la agrupación de iteraciones paralelas puede extenderse a los bucles forall con faser de la siguiente manera:

// Allocate array of phasers proportional to number of chunked tasks
Phaser[] ph = new Phaser[tasks+2]; //array of phasers
for (int i = 0; i < ph.length; i++) ph[i] = new Phaser(1);
// Main computation
forall ( i: [0:tasks-1]) {
    for (iter: [0:nsteps-1]) {
        // Compute leftmost boundary element for group
        int left = i * (n / tasks) + 1;
        myNew[left] = (myVal[left - 1] + myVal[left + 1]) / 2.0;
        // Compute rightmost boundary element for group
        int right = (i + 1) * (n / tasks);
        myNew[right] = (myVal[right - 1] + myVal[right + 1]) / 2.0;
        // Signal arrival on phaser ph AND LEFT AND RIGHT ELEMENTS ARE AV
        int index = i + 1;
        ph[index].arrive();
        // Compute interior elements in parallel with barrier
        for (int j = left + 1; j <= right - 1; j++)
            myNew[j] = (myVal[j - 1] + myVal[j + 1]) / 2.0;
        // Wait for previous phase to complete before advancing
        if (index > 1) ph[index - 1].awaitAdvance(iter);
        if (index < tasks) ph[index + 1].awaitAdvance(iter);
        swap pointers newX and oldX;
    }
}
*/

