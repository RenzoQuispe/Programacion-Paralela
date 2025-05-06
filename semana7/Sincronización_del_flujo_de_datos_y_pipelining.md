## Sincronización del flujo de datos y pipelining

### 1. Barreras de fase dividida con fasers Java
Resumen de la clase: En esta clase, examinamos una variante del ejemplo de barrera que
estudiamos anteriormente:
reescribirse como sigue:
```python
forall (i : [0:n-1]) {
    print HELLO, i;
    myId = lookup(i); // convert int to a string
    print BYE, myId;
}
```
Aprendimos sobre la clase Phaser de Java, y que la operación ph.arriveAndAwaitAdvance() ,
puede utilizarse para implementar una barrera a través del objeto phaser ph. También observamos
que hay dos posiciones posibles para insertar una barrera entre las dos sentencias print anteriores:
antes o después de la llamada a lookup(i). Sin embargo, si lo examinamos más detenidamente,
veremos que la llamada a lookup(i) es local a la iteración i y que no hay ninguna necesidad
específica de completarla antes de la barrera o de completarla después de la barrera. De hecho, la
llamada a lookup(i) puede realizarse en paralelo con la barrera. Para facilitar esta barrera de fase
dividida (también conocida como barrera difusa) utilizamos dos API independientes de la clase Java
Phaser: ph.arrive() y ph.awaitAdvance(). Juntas, estas dos API forman una barrera, pero ahora
tenemos la libertad de insertar un cálculo como lookup(i) entre las dos llamadas, como se indica a
continuación:
```python
// initialize phaser ph for use by n tasks ("parties")
Phaser ph = new Phaser(n);
// Create forall loop with n iterations that operate on ph
forall (i : [0:n-1]) {
print HELLO, i;
int phase = ph.arrive();
myId = lookup(i); // convert int to a string
ph.awaitAdvance(phase);
print BYE, myId;
}

```
Hacerlo así permite que el procesamiento de la barrera se produzca en paralelo con la llamada a
lookup(i), que era nuestro resultado deseado.

#### Explicación línea a línea:

// initialize phaser ph for use by n tasks ("parties")
Phaser ph = new Phaser(n);

✅ Significado:
Se crea un objeto Phaser llamado ph con n "parties" (participantes). Cada "party" representa una tarea o hilo que debe llegar a un punto de sincronización antes de que todos puedan continuar.

// Create forall loop with n iterations that operate on ph
forall (i : [0:n-1]) {

✅ Significado:
Este es pseudocódigo para indicar que se van a lanzar n tareas en paralelo, cada una con un identificador i de 0 hasta n - 1.

En Java real, esto se haría con hilos o un ExecutorService.

print HELLO, i;

✅ Significado:
Cada tarea imprime un mensaje indicando que ha comenzado. Ejemplo de salida: HELLO, 0, HELLO, 1, etc.

int phase = ph.arrive();

✅ Significado:
La tarea notifica al Phaser que ha llegado a la barrera (punto de sincronización).
El método arrive() devuelve el número de fase actual.

myId = lookup(i); // convert int to a string

✅ Significado:
Convierte el identificador numérico i a una representación en texto. Por ejemplo, lookup(2) podría devolver "Tarea-2".

ph.awaitAdvance(phase);

✅ Significado:
La tarea espera a que todas las demás tareas también hayan llamado a arrive() en esta misma fase.
Cuando todas llegan, el Phaser avanza a la siguiente fase, y todas continúan.

print BYE, myId;

✅ Significado:
Una vez que todos han llegado a la barrera, la tarea imprime un mensaje de despedida.
Ejemplo de salida: BYE, Tarea-2.
Resultado general:

Todas las tareas imprimen HELLO, i, luego esperan a que todas lleguen, y después imprimen BYE, Tarea-i.
Salida esperada (orden de BYE sincronizado):

HELLO, 0
HELLO, 1
HELLO, 2
HELLO, 3
HELLO, 4
BYE, Tarea-0
BYE, Tarea-1
BYE, Tarea-2
BYE, Tarea-3
BYE, Tarea-4

Los HELLO pueden salir en cualquier orden (por ejecución paralela), pero ningún BYE se imprimirá hasta que todos hayan llegado.

### 2. Sincronización punto a punto con fasers