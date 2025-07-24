# Documentación Línea por Línea - StudentAnalytics.java

## Encabezado y Declaración del Package

```java
package co.edu.unal.paralela;
```
**Línea 1:** Declara que esta clase pertenece al paquete `co.edu.unal.paralela`.

## Importaciones

```java
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
```
- **Línea 3:** Importa la interfaz `List` para trabajar con listas.
- **Línea 4:** Importa `ArrayList` para crear listas dinámicas.
- **Línea 5:** Importa la interfaz `Map` para trabajar con mapas clave-valor.
- **Línea 6:** Importa `HashMap` para crear mapas hash.
- **Línea 7:** Importa `Stream` para trabajar con streams de datos.
- **Línea 8:** Importa `Arrays` para utilidades de arrays como `Arrays.stream()`.
- **Línea 9:** Importa `Function` para interfaces funcionales.
- **Línea 10:** Importa `Collectors` para operaciones de recolección en streams.

## Declaración de la Clase

```java
/**
 * Una clase 'envoltorio' (wrapper) para varios métodos analíticos.
 */
public final class StudentAnalytics {
```
- **Líneas 12-14:** Comentario Javadoc que describe el propósito de la clase.
- **Línea 15:** Declara la clase `StudentAnalytics` como `public final` (no puede ser heredada).

---

# Método 1: averageAgeOfEnrolledStudentsImperative

## Documentación y Firma del Método

```java
/**
 * Calcula secuencialmente la edad promedio de todos los estudientes registrados y activos 
 * utilizando ciclos.
 *
 * @param studentArray Datos del estudiante para la clase.
 * @return Edad promedio de los estudiantes registrados
 */
public double averageAgeOfEnrolledStudentsImperative(
        final Student[] studentArray) {
```
- **Líneas 16-22:** Comentario Javadoc explicando el método.
- **Líneas 23-24:** Declara método público que retorna `double`, recibe array de `Student`.

## Implementación Secuencial

```java
List<Student> activeStudents = new ArrayList<Student>();

for (Student s : studentArray) {
    if (s.checkIsCurrent()) {
        activeStudents.add(s);
    }
}
```
- **Línea 25:** Crea una lista vacía para almacenar estudiantes activos.
- **Línea 27:** Inicia ciclo for-each para recorrer todos los estudiantes.
- **Línea 28:** Verifica si el estudiante está actualmente registrado.
- **Línea 29:** Si está activo, lo añade a la lista de estudiantes activos.

```java
double ageSum = 0.0;
for (Student s : activeStudents) {
    ageSum += s.getAge();
}

return ageSum / (double) activeStudents.size();
```
- **Línea 33:** Inicializa variable para sumar todas las edades.
- **Línea 34:** Ciclo for-each para recorrer solo estudiantes activos.
- **Línea 35:** Suma la edad de cada estudiante activo.
- **Línea 38:** Calcula el promedio dividiendo la suma entre el número de estudiantes activos.

---

# Método 2: averageAgeOfEnrolledStudentsParallelStream

## Documentación y Firma del Método

```java
/**
 * PARA HACER calcular la edad promedio de todos los estudiantes registrados y activos usando
 * streams paralelos. Debe reflejar la funcionalidad de 
 * averageAgeOfEnrolledStudentsImperative. Este método NO debe utilizar ciclos.
 *
 * @param studentArray Datos del estudiante para esta clase.
 * @return Edad promedio de los estudiantes registrados
 */
public double averageAgeOfEnrolledStudentsParallelStream(
        final Student[] studentArray) {
```
- **Líneas 41-48:** Comentario Javadoc explicando que es una implementación paralela.
- **Líneas 49-50:** Declara método paralelo equivalente al anterior.

## Implementación Paralela

```java
return Arrays.stream(studentArray)
        .parallel()
        .filter(Student::checkIsCurrent)
        .mapToDouble(Student::getAge)
        .average()
        .getAsDouble();
```
- **Línea 51:** Convierte el array en un stream de datos.
- **Línea 52:** Habilita el procesamiento paralelo del stream.
- **Línea 53:** Filtra solo estudiantes activos usando referencia de método.
- **Línea 54:** Convierte el stream a `DoubleStream` extrayendo las edades.
- **Línea 55:** Calcula el promedio de las edades (operación terminal).
- **Línea 56:** Extrae el valor double del `OptionalDouble` resultante.

---

# Método 3: mostCommonFirstNameOfInactiveStudentsImperative

## Documentación y Firma del Método

```java
/**
 * Calcula secuencialmente -usando ciclos- el nombre más común de todos los estudiantes 
 * que no están activos en la clase.
 *
 * @param studentArray Datos del estudiante para esta clase.
 * @return Nombre más común de los estudiantes inactivos.
 */
public String mostCommonFirstNameOfInactiveStudentsImperative(
        final Student[] studentArray) {
```
- **Líneas 59-65:** Comentario Javadoc del método secuencial.
- **Líneas 66-67:** Declara método que retorna `String` (nombre más común).

## Filtrado de Estudiantes Inactivos

```java
List<Student> inactiveStudents = new ArrayList<Student>();

for (Student s : studentArray) {
    if (!s.checkIsCurrent()) {
        inactiveStudents.add(s);
    }
}
```
- **Línea 68:** Crea lista para estudiantes inactivos.
- **Línea 70:** Ciclo for-each para recorrer todos los estudiantes.
- **Línea 71:** Verifica si el estudiante NO está activo (negación con `!`).
- **Línea 72:** Añade estudiantes inactivos a la lista filtrada.

## Conteo de Nombres

```java
Map<String, Integer> nameCounts = new HashMap<String, Integer>();

for (Student s : inactiveStudents) {
    if (nameCounts.containsKey(s.getFirstName())) {
        nameCounts.put(s.getFirstName(),
                new Integer(nameCounts.get(s.getFirstName()) + 1));
    } else {
        nameCounts.put(s.getFirstName(), 1);
    }
}
```
- **Línea 76:** Crea mapa para contar occurrencias de cada nombre.
- **Línea 78:** Ciclo for-each para procesar estudiantes inactivos.
- **Línea 79:** Verifica si el nombre ya existe en el mapa.
- **Líneas 80-81:** Si existe, incrementa el contador en 1.
- **Líneas 82-83:** Si no existe, inicializa el contador en 1.

## Búsqueda del Máximo

```java
String mostCommon = null;
int mostCommonCount = -1;
for (Map.Entry<String, Integer> entry : nameCounts.entrySet()) {
    if (mostCommon == null || entry.getValue() > mostCommonCount) {
        mostCommon = entry.getKey();
        mostCommonCount = entry.getValue();
    }
}

return mostCommon;
```
- **Línea 86:** Inicializa variable para el nombre más común.
- **Línea 87:** Inicializa contador del máximo en -1.
- **Línea 88:** Ciclo for-each para recorrer entradas del mapa.
- **Línea 89:** Verifica si es el primer elemento o si el conteo es mayor.
- **Líneas 90-91:** Actualiza el nombre y conteo más común.
- **Línea 94:** Retorna el nombre más frecuente encontrado.

---

# Método 4: mostCommonFirstNameOfInactiveStudentsParallelStream

## Documentación y Firma del Método

```java
/**
 * PARA HACER calcula el nombre más común de todos los estudiantes que no están activos
 * en la clase utilizando streams paralelos. Debe reflejar la funcionalidad 
 * de mostCommonFirstNameOfInactiveStudentsImperative. Este método NO debe usar ciclos
 *
 * @param studentArray Datos de estudiantes para la clase.
 * @return Nombre más comun de los estudiantes inactivos.
 */
public String mostCommonFirstNameOfInactiveStudentsParallelStream(
        final Student[] studentArray) {
```
- **Líneas 97-104:** Comentario Javadoc para implementación paralela sin ciclos.
- **Líneas 105-106:** Declara método paralelo equivalente.

## Implementación Paralela

```java
return Arrays.stream(studentArray)
        .parallel()
        .filter(s -> !s.checkIsCurrent())
        .collect(Collectors.groupingBy(
            Student::getFirstName, 
            Collectors.counting()))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);
```
- **Línea 107:** Convierte array en stream paralelo.
- **Línea 108:** Habilita procesamiento paralelo.
- **Línea 109:** Filtra estudiantes inactivos usando lambda.
- **Líneas 110-112:** Agrupa por nombre y cuenta occurrencias usando collectors.
- **Línea 113:** Obtiene las entradas del mapa resultado.
- **Línea 114:** Convierte a stream secuencial (el mapa es pequeño).
- **Línea 115:** Encuentra la entrada con mayor valor (más occurrencias).
- **Línea 116:** Extrae solo la clave (nombre) de la entrada.
- **Línea 117:** Retorna `null` si no hay elementos.

---

# Método 5: countNumberOfFailedStudentsOlderThan20Imperative

## Documentación y Firma del Método

```java
/**
 * calcula secuencialmente el número de estudiantes que han perdido el curso 
 * que son mayores de 20 años. Una calificación de perdido es cualquiera por debajo de 65 
 * 65. Un estudiante ha perdido el curso si tiene una calificación de perdido 
 * y no está activo en la actuialidad
 *
 * @param studentArray Datos del estudiante para la clase.
 * @return Cantidad de calificacione sperdidas de estudiantes mayores de 20 años de edad.
 */
public int countNumberOfFailedStudentsOlderThan20Imperative(
        final Student[] studentArray) {
```
- **Líneas 120-128:** Comentario Javadoc explicando criterios de filtrado.
- **Líneas 129-130:** Declara método que retorna `int` (contador).

## Implementación Secuencial

```java
int count = 0;
for (Student s : studentArray) {
    if (!s.checkIsCurrent() && s.getAge() > 20 && s.getGrade() < 65) {
        count++;
    }
}
return count;
```
- **Línea 131:** Inicializa contador en 0.
- **Línea 132:** Ciclo for-each para procesar todos los estudiantes.
- **Línea 133:** Verifica 3 condiciones: inactivo AND mayor de 20 AND nota < 65.
- **Línea 134:** Incrementa contador si cumple todas las condiciones.
- **Línea 137:** Retorna el total de estudiantes que cumplen los criterios.

---

# Método 6: countNumberOfFailedStudentsOlderThan20ParallelStream

## Documentación y Firma del Método

```java
/**
 * PARA HACER calcular el número de estudiantes que han perdido el curso 
 * que son mayores de 20 años de edad . una calificación de perdido está por debajo de 65. 
 * Un estudiante ha perdido el curso si tiene una calificación de perdido 
 * y no está activo en la actuialidad. Debe reflejar la funcionalidad de 
 * countNumberOfFailedStudentsOlderThan20Imperative. El método no debe usar ciclos.
 *
 * @param studentArray Datos del estudiante para la clase.
 * @return Cantidad de calificacione sperdidas de estudiantes mayores de 20 años de edad.
 */
public int countNumberOfFailedStudentsOlderThan20ParallelStream(
        final Student[] studentArray) {
```
- **Líneas 140-149:** Comentario Javadoc para versión paralela.
- **Líneas 150-151:** Declara método paralelo equivalente.

## Implementación Paralela

```java
return Arrays.stream(studentArray)
        .parallel()
        .mapToInt(s -> (!s.checkIsCurrent() && s.getAge() > 20 && s.getGrade() < 65) ? 1 : 0)
        .sum();
```
- **Línea 152:** Convierte array en stream paralelo.
- **Línea 153:** Habilita procesamiento paralelo.
- **Línea 154:** Mapea cada estudiante a 1 (si cumple criterios) o 0 (si no cumple).
  - `!s.checkIsCurrent()`: Estudiante inactivo
  - `s.getAge() > 20`: Mayor de 20 años
  - `s.getGrade() < 65`: Calificación reprobada
- **Línea 155:** Suma todos los valores (cuenta los 1s, ignora los 0s).

## Resumen de Optimizaciones Paralelas

1. **Stream Paralelo:** Usa `.parallel()` para distribuir trabajo entre múltiples threads.
2. **Operaciones Eficientes:** `mapToDouble()`, `mapToInt()` para operaciones numéricas.
3. **Collectors Especializados:** `groupingBy()` y `counting()` para agregaciones.
4. **Evita Ciclos:** Todas las operaciones usan APIs funcionales de streams.
5. **Operaciones Terminales:** `average()`, `sum()`, `max()` están optimizadas para paralelización.
