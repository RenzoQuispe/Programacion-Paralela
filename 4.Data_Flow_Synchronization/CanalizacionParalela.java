/* CANALIZACION PARALELA
Una canalización de procesamiento de datos, como una línea de ensamblaje donde:
- Cada dato pasa por varias etapas (por ejemplo: filtrado, normalización, segmentación, etc.).
- Cada etapa se ejecuta en paralelo, pero solo cuando el dato anterior ya ha sido procesado en la etapa anterior.
- Los datos avanzan por etapas sin interferirse entre sí.
- La ejecución se realiza en forma de canal, manteniendo la dependencia entre etapas pero permitiendo concurrencia general.

Realizamos un análisis simplificado del TRABAJO y el SPAN para el paralelismo del pipeline de la
siguiente manera. Sea n el número de elementos de entrada y p el número de etapas de la
canalización, TRABAJO = n × p es el trabajo total que debe realizarse para todos los elementos de
datos, y CPL = n + p -1es la longitud de la ruta crítica spanorpara la canalización. Así, el
paralelismo ideal es PAR= TRABAJO /CPL = np / (n + p - 1 ). Esta fórmula puede validarse
considerando algunos casos límite. Cuando p = 1, el paralelismo ideal degenera a PAR = 1, lo que
confirma que el cálculo es secuencial cuando sólo se dispone de una etapa. Del mismo modo,
cuando n = 1, el paralelismo ideal degenera de nuevo en PAR = 1, lo que confirma que el cómputo
es secuencial cuando sólo se dispone de un elemento de datos. Cuando n es mucho mayor que p (n
* p), entonces el paralelismo ideal se aproxima a PAR = p en el límite, que es el mejor caso posible.

La sincronización necesaria para el paralelismo de canalización puede implementarse utilizando
fáseres mediante la asignación de una matriz de fáseres, de tal forma que el fáser ph[i] se "señala"
en la iteración i mediante una llamada a ph[i].arrive() como sigue:
// Code for pipeline stage i
while ( there is an input to be processed ) {
    // wait for previous stage, if any
    if (i > 0) ph[i - 1].awaitAdvance();
    process input;
    // signal next stage
    ph[i].arrive();
}
*/
import java.util.concurrent.Phaser;

public class CanalizacionParalela {

    static final int NUM_ETAPAS = 4;
    static final int NUM_DATOS = 10;

    public static void main(String[] args) {

        Phaser[] ph = new Phaser[NUM_ETAPAS + 1]; // Una por etapa + final
        for (int i = 0; i < ph.length; i++) {
            ph[i] = new Phaser(1); // Cada una tiene al menos 1 party
        }

        for (int etapa = 0; etapa < NUM_ETAPAS; etapa++) {
            final int etapaActual = etapa;

            new Thread(() -> {
                for (int dato = 0; dato < NUM_DATOS; dato++) {

                    // Esperar a que la etapa anterior procese este dato
                    if (etapaActual > 0) {
                        ph[etapaActual].awaitAdvance(dato);
                    }

                    // Procesar el dato
                    System.out.printf("Dato %d procesado en etapa %d por %s%n",
                            dato, etapaActual, Thread.currentThread().getName());

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Señalar que esta etapa ha terminado con este dato
                    ph[etapaActual + 1].arrive();
                }
            }, "Etapa-" + etapa).start();
        }
    }
}

