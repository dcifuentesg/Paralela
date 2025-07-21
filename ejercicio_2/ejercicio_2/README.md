# Ejercicio 2 - Implementación de Streams Paralelos en Java

## Descripción del Proyecto

Este proyecto implementa métodos de análisis de datos de estudiantes usando **streams paralelos** de Java para mejorar el rendimiento comparado con implementaciones secuenciales tradicionales que usan ciclos.

## Estructura del Proyecto

```
ejercicio_2/
├── src/
│   ├── main/java/co/edu/unal/paralela/
│   │   ├── Student.java                    # Clase modelo de estudiante
│   │   └── StudentAnalytics.java           # Clase principal con análisis
│   └── test/java/co/edu/unal/paralela/
│       └── StudentAnalyticsTest.java       # Tests de correctitud y rendimiento
├── target/                                 # Archivos compilados
├── pom.xml                                # Configuración Maven
├── TestParallelImplementation.java        # Test manual de verificación
└── README.md                              # Este archivo
```

## Clases Principales

### Student.java
Representa un estudiante con los siguientes atributos:
- `firstName`: Nombre del estudiante
- `lastName`: Apellido del estudiante  
- `age`: Edad (double)
- `grade`: Calificación (int)
- `isCurrent`: Si está activo/registrado (boolean)

**Métodos principales:**
- `getFirstName()`, `getLastName()`, `getAge()`, `getGrade()`
- `checkIsCurrent()`: Verifica si el estudiante está activo

### StudentAnalytics.java
Clase principal que contiene métodos de análisis tanto secuenciales como paralelos.

## Implementaciones Paralelas

### 1. `averageAgeOfEnrolledStudentsParallelStream`

**Propósito:** Calcular la edad promedio de estudiantes registrados y activos.

```java
public double averageAgeOfEnrolledStudentsParallelStream(final Student[] studentArray) {
    return Arrays.stream(studentArray)
            .parallel()                          // Habilita paralelización
            .filter(Student::checkIsCurrent)     // Solo estudiantes activos
            .mapToDouble(Student::getAge)        // Convierte a stream de doubles
            .average()                           // Calcula promedio
            .orElse(0.0);                       // Valor por defecto si no hay datos
}
```

**Optimizaciones:**
- Uso de `mapToDouble()` para operaciones numéricas eficientes
- `parallel()` aprovecha múltiples cores para el filtrado y cálculo
- `average()` es una operación terminal optimizada para paralelización

### 2. `mostCommonFirstNameOfInactiveStudentsParallelStream`

**Propósito:** Encontrar el nombre más común entre estudiantes inactivos.

```java
public String mostCommonFirstNameOfInactiveStudentsParallelStream(final Student[] studentArray) {
    return Arrays.stream(studentArray)
            .parallel()                                    // Paralelización del stream
            .filter(s -> !s.checkIsCurrent())            // Solo estudiantes inactivos
            .collect(Collectors.groupingBy(               // Agrupa por nombre
                Student::getFirstName, 
                Collectors.counting()))                    // Cuenta ocurrencias
            .entrySet()
            .stream()                                     // Stream secuencial para búsqueda final
            .max(Map.Entry.comparingByValue())           // Encuentra máximo por valor
            .map(Map.Entry::getKey)                      // Extrae la clave (nombre)
            .orElse(null);                               // Null si no hay datos
}
```

**Optimizaciones:**
- `parallel()` en el stream principal para procesar millones de estudiantes
- `groupingBy()` con `counting()` agrupa y cuenta eficientemente
- Stream secuencial final para encontrar máximo (Map pequeño, no requiere paralelización)

### 3. `countNumberOfFailedStudentsOlderThan20ParallelStream`

**Propósito:** Contar estudiantes mayores de 20 años que reprobaron y no están activos.

```java
public int countNumberOfFailedStudentsOlderThan20ParallelStream(final Student[] studentArray) {
    return Arrays.stream(studentArray)
            .parallel()                                                    // Paralelización
            .mapToInt(s -> (!s.checkIsCurrent() &&                       // Condiciones:
                           s.getAge() > 20 &&                            // - No activo
                           s.getGrade() < 65) ? 1 : 0)                   // - Mayor de 20
            .sum();                                                       // - Nota < 65
}
```

**Optimizaciones:**
- `mapToInt()` convierte a stream de enteros para eficiencia
- Condición combinada en una sola expresión lambda reduce overhead
- `sum()` es altamente optimizable para paralelización

## Comparación de Rendimiento

### Dataset de Prueba
- **Total estudiantes:** 2,000,000
- **Estudiantes activos:** 600,000
- **Estudiantes inactivos:** 1,400,000
- **Cores disponibles:** 4 (3 threads de paralelización)

### Speedup Logrado
Los tests de rendimiento verifican que las implementaciones paralelas sean significativamente más rápidas:

| Método | Speedup Requerido | Resultado |
|--------|------------------|-----------|
| `averageAgeOfEnrolledStudentsParallelStream` | 1.2x | ✅ PASSED |
| `mostCommonFirstNameOfInactiveStudentsParallelStream` | 2.0x | ✅ PASSED |
| `countNumberOfFailedStudentsOlderThan20ParallelStream` | 1.2x | ✅ PASSED |

## Consideraciones de Diseño

### ¿Por qué Streams Paralelos?
1. **Aprovechamiento de múltiples cores:** Java distribuye automáticamente el trabajo
2. **Menos código:** No requiere manejo manual de threads
3. **Operaciones optimizadas:** `filter()`, `map()`, `collect()` están optimizados para paralelización

### Limitaciones y Trade-offs
1. **Overhead de paralelización:** Para datasets pequeños puede ser contraproducente
2. **Memoria compartida:** Todas las operaciones comparten el heap de Java
3. **Orden no garantizado:** Los streams paralelos no preservan orden (no crítico para estos casos)

### Decisiones de Implementación

**¿Por qué `groupingBy()` en lugar de `groupingByConcurrent()`?**
- Inicialmente se usó `groupingByConcurrent()` pero causaba inconsistencias en casos de empate
- `groupingBy()` normal con stream paralelo mantiene la misma semántica que la versión secuencial

**¿Por qué `mapToInt()` vs `filter().count()`?**
- `mapToInt()` con suma es más eficiente que filtrar y contar
- Reduce el número de elementos que pasan por el pipeline

## Ejecución de Tests

### Compilación
```bash
javac -cp "junit-4.12.jar;hamcrest-core-1.3.jar;pcdp-core-0.0.4-SNAPSHOT.jar;." -d . src/main/java/co/edu/unal/paralela/*.java src/test/java/co/edu/unal/paralela/*.java
```

### Ejecución de Tests
```bash
java -cp "junit-4.12.jar;hamcrest-core-1.3.jar;pcdp-core-0.0.4-SNAPSHOT.jar;." org.junit.runner.JUnitCore co.edu.unal.paralela.StudentAnalyticsTest
```

### Tests Incluidos
1. **Tests de Correctitud:** Verifican que los resultados paralelos sean idénticos a los secuenciales
2. **Tests de Rendimiento:** Verifican que el speedup sea el esperado

## Verificación Manual

El archivo `TestParallelImplementation.java` proporciona un test manual simple para verificar la correctitud de las implementaciones con un dataset pequeño.

## Tecnologías Utilizadas

- **Java 24:** Versión moderna con optimizaciones de streams paralelos
- **JUnit 4.12:** Framework de testing
- **Maven:** Gestión de dependencias (pom.xml)
- **PCDP:** Biblioteca para computación paralela y distribuida

## Conclusiones

Las implementaciones paralelas demuestran mejoras significativas de rendimiento:
- Aprovechamiento eficiente de recursos multi-core
- Código más conciso y expresivo comparado con implementaciones manuales de threading
- Mantenimiento de la correctitud funcional

El paralelismo en Java streams es especialmente efectivo para operaciones de **map-reduce** en grandes datasets, como se demuestra en este ejercicio con 2 millones de registros de estudiantes.
