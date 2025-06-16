
import java.util.concurrent.Phaser;

/* BARRERAS DE FASE DIVIDIDA CON FASERS EN JAVA
Phaser es más flexible que una barrera común. Te da control explícito sobre:
- Cuándo un hilo llega (arrive()).
- Cuándo espera (awaitAdvance()).
- Cuándo se retira (arriveAndDeregister()).

int phase = ph.arrive();
ph.awaitAdvance(phase);
es equivalente a una barrera (barrier), pero solo si no haces nada relevante entre ambas llamadas. 
Si colocas código entre esas dos líneas, el comportamiento puede cambiar dependiendo de cuándo lo ejecutes y qué hace ese código.

int phase = ph.arrive();
hacerAlgo();
ph.awaitAdvance(phase);
Si haces mal haceAlgo(), puede haber:
- Condiciones de carrera (race conditions).
- Ejecución fuera de fase.
- Resultados incorrectos o difícil de depurar.

forall (i : [0:n-1]) {
    print HELLO, i;
    myId = lookup(i); // convert int to a string
    print BYE, myId;
}
Hacerlo así permite que el procesamiento de la barrera se produzca en paralelo con la llamada a
lookup(i), que era nuestro resultado deseado.

*/
public class Phaser1 {

    public static void main(String[] args) {
        int n = 5; // Número de participantes (hilos)
        Phaser ph = new Phaser(n); // Inicializar phaser con n participantes

        for (int i = 0; i < n; i++) {
            int id = i;
            new Thread(() -> {
                // Primera parte
                System.out.println("HELLO, " + id);

                // Llegamos a la barrera de la fase actual
                int phase = ph.arrive(); // Indica que ha terminado esta fase

                // Código entre arrive() y awaitAdvance()
                String id2 = String.valueOf(id); // Simula lookup(i)

                // Espera a que todos los hilos lleguen
                ph.awaitAdvance(phase);

                // Segunda parte
                System.out.println("BYE, " + id2 + ", " + id2.getClass().getSimpleName());
            }).start();
        }
    }
}
