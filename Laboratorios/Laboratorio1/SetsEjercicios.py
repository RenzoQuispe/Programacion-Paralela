# FUNCIONES CREADAS
# Funcion para el problema 1
def AgregarElementosListaAConjunto (lista, conjunto):
    for i in range(len(lista)): # for i en {1,2,3,4,..., longitudlista-2,longitudlista-1}
        conjunto.add(lista[i])
    return conjunto
# Funcion para el problema 2
def InterseccionListas (lista1, lista2):
    ConjuntoInterseccion = set() #conjunto vacio en el cual se agregar los elemnetos comunes
    # Recorrer la primera lista
    for i in lista1:
        # Si el elemento está en la segunda lista, agregarlo al conjunto
        if i in lista2:
            ConjuntoInterseccion.add(i)
    return ConjuntoInterseccion
# Funcion para el problema 3
def UnionListas (lista1, lista2):
    union = set()
    # Primera agregamos todos los elementos de la lista1
    union = AgregarElementosListaAConjunto(lista1, union) #usamos la funcion creada antes en el problema 1
    for i in lista2:
        if(i not in union):
            union.add(i)
    return union
# Funcion para el problema 4
def ActualizarConjuntoPorOtroConjunto(conjunto1,conjunto2):
    conjuntoActualizado = set()
    for i in conjunto1: # Recorremos los elemtnos de conjunto 1 y si este no esta en conjunto 2 lo agregamos a conjuntoActualizado
        if (i not in conjunto2): 
            conjuntoActualizado.add(i)
    return conjuntoActualizado
# Funcion para el problema 5
# Funcion para el problema 6
# Funcion para el problema 7
# Funcion para el problema 8
# Funcion para el problema 9



#----------------------------------------------------------------------------------------------------------------
# PRUEBAS y DEMOSTRACIONES
# Problema 1
conjunto1 = {"yellow", "black", "orange"}
lista1 = ["blue", "green", "red"]
print("Resultado Problema 1: ",AgregarElementosListaAConjunto(lista1,conjunto1))
# Problema 2
lista2 = [10, 20, 30, 40, 50]
lista3 = [30, 40, 50, 60, 70]
print("Resultado Problema 2: ",InterseccionListas(lista2,lista3))
# Problema 3
print("Resultado Problema 3: ", UnionListas(lista2,lista3))
# Problema 4
conjunto2 = {10, 20, 30}
conjunto3 = {20, 40, 50}
print("Resultado Problema 4: ", ActualizarConjuntoPorOtroConjunto(conjunto2,conjunto3))
# Problema 5
# Problema 6
# Problema 7
# Problema 8
# Problema 9
