import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;
import co.edu.unal.paralela.*;

public class DebugTest {
    public static void main(String[] args) {
        // Crear dataset de prueba con nombres conocidos
        Student[] testStudents = {
            new Student("Alice", "Smith", 25, 70, false),
            new Student("Bob", "Jones", 22, 85, false),
            new Student("Alice", "Brown", 30, 60, false),
            new Student("Charlie", "Wilson", 35, 55, false),
            new Student("Alice", "Davis", 28, 90, false)
        };
        
        StudentAnalytics analytics = new StudentAnalytics();
        
        // Test imperativo
        String imperative = analytics.mostCommonFirstNameOfInactiveStudentsImperative(testStudents);
        System.out.println("Imperativo: " + imperative);
        
        // Test paralelo
        String parallel = analytics.mostCommonFirstNameOfInactiveStudentsParallelStream(testStudents);
        System.out.println("Paralelo: " + parallel);
        
        // Test manual para verificar lógica
        Map<String, Integer> counts = new HashMap<>();
        for (Student s : testStudents) {
            if (!s.checkIsCurrent()) {
                counts.put(s.getFirstName(), counts.getOrDefault(s.getFirstName(), 0) + 1);
            }
        }
        System.out.println("Conteos: " + counts);
        
        System.out.println("¿Coinciden? " + Objects.equals(imperative, parallel));
    }
}
