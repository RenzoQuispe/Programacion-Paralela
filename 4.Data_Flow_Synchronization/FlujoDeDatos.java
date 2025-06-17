/*PARALELISMO DEL FLUJO DE DATOS

Consiste en un enfoque que especifica tareas computacionales como un grafo dirigido acíclico (DAG), 
donde los nodos son tareas y las aristas representan dependencias de datos entre esas tarea. 
El grafo de flujo de datos simple estudiado en la ponencia constaba de cinco nodos y
cuatro aristas: A → C, A → D, B → D, B → E. Aunque los futuros pueden utilizarse para generar
dicho grafo de computación, por ejemplo, incluyendo llamadas a A.get() y B.get() en la tarea D, las
aristas del grafo de computación grafo de computación están implícitas en las llamadas a get()
cuando se utilizan futuros. En su lugar, introducimos la notación asyncAwait para especificar una
tarea junto con un conjunto explícito de precondiciones (eventos que la tarea debe esperar antes de
poder iniciar la ejecución). Con este enfoque, el programa puede generarse directamente a partir del
grafo de computación de la siguiente manera:

async( () -> {/ Task A /; A.put(); } ); // Complete task and trigger event A
async( () -> {/ Task B /; B.put(); } ); // Complete task and trigger event B
asyncAwait(A, () -> {/TaskC/} );//Only execute task after event A is triggered
asyncAwait(A,B,()->{/TaskD/});//Only execute task after events A, B are triggered
asyncAwait(B, () -> {/TaskE /} );// Only execute task after event B is triggered

Puede apreciar que, el orden de las declaraciones no es significativo. Al igual que un grafo puede
definirse enumerando sus aristas en cualquier orden, el programa de flujo de datos anterior puede
reescribirse como siguiente, sin cambiar su significado:

asyncAwait(A, () -> {/ Task C /} );//Only execute task after event A is triggered
asyncAwait(A,B,()->{/Task D/});//Only execute task after events A,B are triggered
asyncAwait(B,() -> {/TaskE/} );// Only execute task after event B is triggered
async( () -> {/ Task A /; A.put(); } ); // Complete task and trigger event A
async( () -> {/ Task B /; B.put(); } ); // Complete task and trigger event B

Por último, observamos que la potencia y la elegancia de la programación paralela de flujo de datos
va acompañada de la posibilidad de una falta de progreso que puede verse como una
*/
import java.util.concurrent.CompletableFuture;

public class FlujoDeDatos {

    // Evento representado como un CompletableFuture vacío
    static class Event {
        CompletableFuture<Void> event = new CompletableFuture<>();

        void trigger() {
            event.complete(null);
        }

        CompletableFuture<Void> await() {
            return event;
        }
    }

    public static void main(String[] args) {

        // Eventos correspondientes a cada tarea
        Event A = new Event();
        Event B = new Event();
        Event C = new Event();
        Event D = new Event();
        Event E = new Event();

        // TASK A: se ejecuta inmediatamente y dispara evento A
        CompletableFuture.runAsync(() -> {
            System.out.println("Task A ejecutada");
            A.trigger(); // activa evento A
        });

        // TASK B: se ejecuta inmediatamente y dispara evento B
        CompletableFuture.runAsync(() -> {
            System.out.println("Task B ejecutada");
            B.trigger(); // activa evento B
        });

        // TASK C: depende solo de A
        A.await().thenRunAsync(() -> {
            System.out.println("Task C ejecutada (después de A)");
            C.trigger();
        });

        // TASK D: depende de A y B
        CompletableFuture.allOf(A.await(), B.await()).thenRunAsync(() -> {
            System.out.println("Task D ejecutada (después de A y B)");
            D.trigger();
        });

        // TASK E: depende solo de B
        B.await().thenRunAsync(() -> {
            System.out.println("Task E ejecutada (después de B)");
            E.trigger();
        });

        // Para evitar que termine el programa antes de que acaben las tareas
        try {
            Thread.sleep(2000); // espera artificial
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
