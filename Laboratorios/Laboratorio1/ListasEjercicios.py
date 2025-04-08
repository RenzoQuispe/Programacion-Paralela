import math

# FUNCIONES CREADAS
# Funcion para el problema 1 
def ListaRaizCuadrada (lista):
    for i in range(len(lista)): # for i en {1,2,3,4,..., longitudlista-2,longitudlista-1}
        lista[i] = math.sqrt(lista[i])
    return lista
# Funcion para el problema 2
def ConcatenacionListas (lista1, lista2):
    return lista1 + lista2
# Funcion para el problema 3
def MostrarElementosEnOrdenEspecfico (lista1, lista2): # Se muestra los elementos de la lista1 en el orden original y la lista 2 en el orden inverso.
    for i in range(max(len(lista1),len(lista2))):
        if (i<len(lista1)): # Mostrar en orden normal
            print("Elemento ",i," de la Primera Lista: ",lista1[i])
        if(len(lista2)-i-1>=0): # Mostrar primero el ultimo elemento hasta el primero
            print("Elemento ",i," de la Segunda Lista: ",lista2[len(lista2)-i-1])
# Funcion para el problema 4
def EliminarStringVacios (lista):
    listaActualida = []
    for i in lista: # Recorremos cada elemento de la lista
        elementoDeListaEstaVacio = True
        for j in i: # Recorremos cada caracter del elemento de la lista, si hay un caracter(no solo esta vacio, o solo hay espacios) entonces dicho elemento se agrega a la lista actualizada
            if(j!=" "): # Si hay un caracter salimos del for y pasamos a analizar el siguiente elemnto 
                elementoDeListaEstaVacio = False
                break 
        if (elementoDeListaEstaVacio==False):
            listaActualida.append(i)
    return listaActualida
# Funcion para el problema 7
def CambiarPrimerElemento20por200 (lista):
    for i in range(len(lista)):
        if(lista[i] == 20):
            lista[i]=200
            break # Tras encontrar el primer elemento 20 y cmabiarlo salimos del for con break 
    return lista
# Funcion para el problema 8
def Eliminar20s (lista):
    listaActualizada = []
    for i in range(len(lista)): # for i en {1,2,3,4,..., longitudlista-2,longitudlista-1}
        if(lista[i]!=20):
            listaActualizada.append(lista[i])
    return listaActualizada

#----------------------------------------------------------------------------------------------------------------

# PRUEBAS y DEMOSTRACIONES
# Problema 1
listaNumeros = [1000, 100, 25, 36,81, 50]
resultado = ListaRaizCuadrada(listaNumeros)
print("Resultado Problema 1 : ",resultado)
# Problema 2
lista1 = ["M", "na", "i", "Ke"]
lista2 = ["y","me","s","lly"]
listasConcatenadas = ConcatenacionListas(lista1, lista2)
print("Resultado Problema 2 : ",listasConcatenadas)
# Problema 3
lista3 = [2,50,0,10]
lista4 = [1,2,3,4,66,0,0,9]
print("Resultado Problema 3")
MostrarElementosEnOrdenEspecfico(lista3, lista4)
# Problema 4
lista5 = ["","Renzo","Juan","","   ","Daniel"]
print("Resultado Problema 4 :")
print(EliminarStringVacios(lista5))
# Problema 5
lista = [10, 20, [300, 400, [5000, 6000], 500], 30, 40]
sublista = lista[2][2]
indice6000 = sublista.index(6000)
sublista.insert(indice6000 + 1, 7000)
print("Resultado Problema 5: ",lista)
# Problema 6
lista6 = ['a', 'b', ['c', ['d', 'e', ['f', 'g'], 'k'], 'l'], 'm', 'n']
sublista = ['h', 'i', 'j']
lista6[2][1][2].append(sublista)
print("Resultado Problema 6: ",lista6)
# Problema 7 python
lista7 = [1,2,20,45,20,100]
print("Resultado Problema 7: ",CambiarPrimerElemento20por200(lista7))
# Problema 8
lista8 = [20, 20, 30, 20, 4, 20, 5, 20, 9, 20, 20, 20, 1, 20]
print("Resultado Problema 8: ", Eliminar20s(lista8))