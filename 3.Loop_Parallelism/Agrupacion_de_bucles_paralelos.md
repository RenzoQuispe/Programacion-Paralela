### Agrupación de iteraciones: Agrupación de bucles paralelos
Se tiene la suma de vectores:
```python
forall (i : [0:n-1]) a[i] = b[i] + c[i]
```
Observamos que este enfoque crea n tareas , una por cada iteración forall , lo que supone un
derroche cuando (como es habitual en la práctica) n es mucho mayor que el número de núcleos de
procesador disponibles.
Para abordar este problema, aprendimos una táctica común utilizada en la práctica que se denomina
agrupación de iteraciones ochunking de bucles , y se centra en reducir el número de tareas creadas
para que se aproxime al número de núcleos del procesador, con el fin de reducir la sobrecarga de la
ejecución paralela:
Con la agrupación/chunking de iteraciones, el ejemplo anterior de suma paralela de vectores puede
reescribirse como sigue:
```python
forall (g:[0:ng-1])
    for (i : mygroup(g, ng, [0:n-1])) a[i] = b[i] + c[i]
```
- Agrupación en bloque (block distribution)

    Definición: Cada grupo ejecuta un bloque contiguo de iteraciones.

    Ejemplo con n = 10, ng = 2:
        Grupo 0: índices 0 a 4
        Grupo 1: índices 5 a 9

    Ventajas:
        Buena localidad de datos (útil para cachés).
        Simple de implementar.

    Desventajas:
        Puede haber desequilibrio si n no es divisible por ng.

- Agrupación cíclica (cyclic distribution)

    Definición: Cada grupo toma las iteraciones en forma intercalada, de modo que el grupo g toma las iteraciones i tales que i % ng == g.
    
    Ejemplo con n = 10, ng = 2:
        Grupo 0: índices 0, 2, 4, 6, 8
        Grupo 1: índices 1, 3, 5, 7, 9
    
    Ventajas:
        Mejor balance de carga, especialmente si el tiempo por iteración varía.
        Útil cuando hay desequilibrio en el trabajo por iteración.
    
    Desventajas:
        Peor localidad de datos (se salta entre posiciones del arreglo).
        Accesos más dispersos en memoria.
- Elegir entre bloque y cíclico depende del problema:

    Si todas las iteraciones son homogéneas (trabajo similar por iteración), es preferible la agrupación en bloque por eficiencia de memoria.

    Si las iteraciones varían mucho en tiempo de ejecución, la agrupación cíclica puede ser mejor para evitar que algunos grupos terminen mucho antes que otros.

Para mayor comodidad, la biblioteca PCDP proporciona métodos de ayuda, forallChunked() y
forall2dChunked(), que crean automáticamente bucles paralelos unidimensionales o
bidimensionales, y también realizan un agrupamiento/chunking de iteraciones estilo bloque en esos
bucles paralelos. Un ejemplo de utilización de la API forall2dChunked () para un bucle paralelo
bidimensional (como en el ejemplo de multiplicación de matrices) puede verse en el siguiente
boceto de código Java:
```java
forall2dChunked(0, N - 1, 0, N - 1, (i, j) -> {
. . . // Statements for parallel iteration (i,j)
});    
```
