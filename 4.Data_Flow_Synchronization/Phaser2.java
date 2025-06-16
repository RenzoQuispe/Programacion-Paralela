/* SINCRONIZACION PUNTO A PUNTO CON FASERS

Cada tarea se sincroniza solamente con otras tareas específicas, evitando la sobre-sincronización típica de una barrera global.
Este modelo reduce el span (camino crítico) de ejecución total.
Tienes 3 tareas (Tarea 0, Tarea 1, Tarea 2) que:
- Ejecutan operaciones con cierto costo (tiempo de ejecución).
- Se sincronizan entre sí parcialmente, mediante objetos Phaser individuales: ph0, ph1, ph2.
- Cada Phaser se usa para coordinar una relación punto a punto (por ejemplo, Tarea 0 sincroniza con Tarea 1 a través de ph0).
- Puedes reducir el tiempo total (span) usando Phasers individuales por dependencia, en lugar de sincronizar todo el sistema en cada fase.
- Es especialmente útil en grafos de tareas dependientes, simulaciones, o procesamiento distribuido.
- Puede variar por el orden de ejecución, pero la sincronización garantiza que ciertas operaciones no ocurren hasta que otras hayan terminado, sin usar una barrera global.
 */
import java.util.concurrent.Phaser;

public class Phaser2 {

    public static void main(String[] args) {
        // Crear phasers para sincronizaciones específicas
        Phaser ph0 = new Phaser(1);
        Phaser ph1 = new Phaser(1);
        Phaser ph2 = new Phaser(1);

        // Lanzar las tareas
        Thread tarea0 = new Thread(() -> {
            accion("X=A()", 1); // Ejecuta A

            ph0.arrive(); // Señala que terminó A (tenemos X)
            ph1.awaitAdvance(0); // Espera a que Tarea 1 termine B (necesitamos Y)

            accion("D(X,Y)", 3); // Ejecuta D
        });

        Thread tarea1 = new Thread(() -> {
            accion("Y=B()", 2); // Ejecuta B

            ph1.arrive(); // Señala que terminó B (tenemos Y)
            ph0.awaitAdvance(0); // Espera a que Tarea 0 termine A (necesitamos X)
            ph2.awaitAdvance(0); // Espera a que Tarea 2 termine C (necesitamos Z)

            accion("E(X,Y,Z)", 2); // Ejecuta E
        });

        Thread tarea2 = new Thread(() -> {
            accion("Z=C()", 3); // Ejecuta C

            ph2.arrive(); // Señala que termino C (tenemos Z)
            ph1.awaitAdvance(0); // Esperea que tarea 1 termine B (necesitamos Y)

            accion("F(Y,Z)", 1); // Ejecuta F
        });

        // Ejecutar las tareas
        tarea0.start();
        tarea1.start();
        tarea2.start();
    }

    // Simula una operación con un "cost" de unidades de tiempo
    static void accion(String nombre, int costo) {
        System.out.println(nombre + " (costo=" + costo + ")");
        try {
            Thread.sleep(costo * 100); // 100 ms por unidad de costo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
/*
Esta sincronización reduce el tiempo total de ejecución comparado con una barrera global. 
En lugar de esperar a que todas las tareas terminen cada fase, cada tarea espera solo a quienes realmente necesita, lo cual mejora el paralelismo y el rendimiento.
*/
