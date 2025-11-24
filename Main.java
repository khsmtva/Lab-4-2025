import functions.*;
import functions.basic.*;
import functions.meta.*;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Лабораторная работа #4 ===");
            
            // Тестирование аналитических функций
            testBasicFunctions();
            
            // Тестирование комбинированных функций
            testMetaFunctions();
            
            // Тестирование табулирования и ввода/вывода
            testTabulationAndIO();
            
            // Тестирование сериализации
            testSerialization();
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testBasicFunctions() {
        System.out.println("\n--- Тестирование базовых функций ---");
        
        // Экспонента
        Function exp = new Exp();
        System.out.println("Exp: f(0) = " + exp.getFunctionValue(0));
        System.out.println("Exp: f(1) = " + exp.getFunctionValue(1));
        
        // Логарифм
        Function log = new Log(Math.E);
        System.out.println("Ln: f(1) = " + log.getFunctionValue(1));
        System.out.println("Ln: f(Math.E) = " + log.getFunctionValue(Math.E));
        
        // Тригонометрические функции
        Function sin = new Sin();
        Function cos = new Cos();
        System.out.println("Sin(0) = " + sin.getFunctionValue(0));
        System.out.println("Cos(0) = " + cos.getFunctionValue(0));
        System.out.println("Sin(pi/2) = " + sin.getFunctionValue(Math.PI/2));
    }
    
    private static void testMetaFunctions() {
        System.out.println("\n--- Тестирование комбинированных функций ---");
        
        Function sin = new Sin();
        Function cos = new Cos();
        
        // Сумма sin² + cos² (должна быть ≈ 1)
        Function sin2 = new Power(sin, 2);
        Function cos2 = new Power(cos, 2);
        Function sum = new Sum(sin2, cos2);
        
        System.out.println("sin²(x) + cos²(x) для различных x:");
        for (double x = 0; x <= Math.PI; x += Math.PI/4) {
            System.out.printf("  x=%.2f: %.6f%n", x, sum.getFunctionValue(x));
        }
        
        // Композиция
        Function exp = new Exp();
        Function ln = new Log(Math.E);
        Function composition = new Composition(exp, ln);
        System.out.println("exp(ln(5)) = " + composition.getFunctionValue(5));
    }
    
    private static void testTabulationAndIO() throws IOException {
        System.out.println("\n--- Тестирование табулирования и ввода/вывода ---");
        
        // Создаем функции
        Function sin = new Sin();
        Function cos = new Cos();
        
        // Пункт 1: Вывод Sin и Cos на отрезке от 0 до pi с шагом 0.1
        System.out.println("Sin(x) на отрезке [0, pi] с шагом 0.1:");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("  sin(%.1f) = %.6f%n", x, sin.getFunctionValue(x));
        }
        
        System.out.println("Cos(x) на отрезке [0, pi] с шагом 0.1:");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("  cos(%.1f) = %.6f%n", x, cos.getFunctionValue(x));
        }
        
        // Пункт 2: Табулированные аналоги с 10 точками
        TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        
        System.out.println("Табулированный синус (0 до pi, 10 точек):");
        printTabulatedFunction(tabulatedSin);
        
        System.out.println("Табулированный косинус (0 до pi, 10 точек):");
        printTabulatedFunction(tabulatedCos);
        
        // Сравнение исходных и табулированных функций
        System.out.println("Сравнение исходного и табулированного синуса:");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            double original = sin.getFunctionValue(x);
            double tabulated = tabulatedSin.getFunctionValue(x);
            System.out.printf("  x=%.1f: исходный=%.6f, табулированный=%.6f, разница=%.6f%n", 
                x, original, tabulated, Math.abs(original - tabulated));
        }
        
        // Пункт 3: Сумма квадратов табулированных функций
        Function sin2 = Functions.power(tabulatedSin, 2);
        Function cos2 = Functions.power(tabulatedCos, 2);
        Function sumSquares = Functions.sum(sin2, cos2);
        
        System.out.println("sin²(x) + cos²(x) через табулированные функции:");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("  x=%.2f: %.6f%n", x, sumSquares.getFunctionValue(x));
        }
        
        // Исследование влияния количества точек
        System.out.println("Исследование влияния количества точек на точность:");
        for (int points : new int[]{5, 10, 20, 50}) {
            TabulatedFunction testSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, points);
            double error = Math.abs(testSin.getFunctionValue(Math.PI/2) - 1.0);
            System.out.printf("  %d точек: погрешность в pi/2 = %.8f%n", points, error);
        }
        
        // Тестирование записи в файл и чтения
        testFileIO();
    }
    
    private static void testFileIO() throws IOException {
        System.out.println("\n--- Тестирование ввода/вывода в файлы ---");
        
        // Пункт 4: Экспонента - символьные потоки
        Function exp = new Exp();
        TabulatedFunction tabulatedExp = TabulatedFunctions.tabulate(exp, 0, 10, 11);
        
        // Запись в символьный файл
        try (FileWriter fw = new FileWriter("exp_text.txt")) {
            TabulatedFunctions.writeTabulatedFunction(tabulatedExp, fw);
        }
        
        // Чтение из символьного файла
        TabulatedFunction readExp;
        try (FileReader fr = new FileReader("exp_text.txt")) {
            readExp = TabulatedFunctions.readTabulatedFunction(fr);
        }
        
        System.out.println("Сравнение исходной и прочитанной экспоненты (символьные потоки):");
        compareFunctions(tabulatedExp, readExp, 0, 10, 1);
        
        // Пункт 5: Логарифм - байтовые потоки
        Function ln = new Log(Math.E);
        TabulatedFunction tabulatedLn = TabulatedFunctions.tabulate(ln, 1, 10, 11);
        
        // Запись в байтовый файл
        try (FileOutputStream fos = new FileOutputStream("ln_binary.dat")) {
            TabulatedFunctions.outputTabulatedFunction(tabulatedLn, fos);
        }
        
        // Чтение из байтового файла
        TabulatedFunction readLn;
        try (FileInputStream fis = new FileInputStream("ln_binary.dat")) {
            readLn = TabulatedFunctions.inputTabulatedFunction(fis);
        }
        
        System.out.println("Сравнение исходного и прочитанного логарифма (байтовые потоки):");
        compareFunctions(tabulatedLn, readLn, 1, 10, 1);
        
        // Анализ файлов
        analyzeFiles();
    }
    
    private static void testSerialization() throws IOException, ClassNotFoundException {
        System.out.println("\n--- Тестирование сериализации ---");
        
        // Создаем функцию: ln(exp(x)) = x
        Function exp = new Exp();
        Function ln = new Log(Math.E);
        Function composition = Functions.composition(exp, ln);
        
        // Табулируем
        TabulatedFunction tabulatedFunc = TabulatedFunctions.tabulate(composition, 0, 10, 11);
        
        System.out.println("Исходная функция (ln(exp(x))):");
        for (double x = 0; x <= 10; x += 1) {
            System.out.printf("  f(%.1f) = %.4f%n", x, tabulatedFunc.getFunctionValue(x));
        }
        
        // Сериализация в файл
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("function_serializable.dat"))) {
            oos.writeObject(tabulatedFunc);
            System.out.println("Функция сериализована в function_serializable.dat");
        }
        
        // Десериализация из файла
        TabulatedFunction deserializedFunc;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("function_serializable.dat"))) {
            deserializedFunc = (TabulatedFunction) ois.readObject();
            System.out.println("Функция десериализована из function_serializable.dat");
        }
        
        // Сравнение
        System.out.println("Сравнение исходной и десериализованной функции:");
        for (double x = 0; x <= 10; x += 1) {
            double y1 = tabulatedFunc.getFunctionValue(x);
            double y2 = deserializedFunc.getFunctionValue(x);
            System.out.printf("  x=%.1f: исходная=%.4f, десериализованная=%.4f%n", x, y1, y2);
        }
    }
    
    private static void analyzeFiles() {
        System.out.println("\n--- Анализ файлов ---");
        File expText = new File("exp_text.txt");
        File lnBinary = new File("ln_binary.dat");
        File serializable = new File("function_serializable.dat");
        
        System.out.println("Размеры файлов:");
        System.out.printf("  exp_text.txt (символьный): %d байт%n", expText.length());
        System.out.printf("  ln_binary.dat (байтовый): %d байт%n", lnBinary.length());
        System.out.printf("  function_serializable.dat (сериализация): %d байт%n", serializable.length());
        
        System.out.println("\nПреимущества/недостатки форматов:");
        System.out.println("  Символьный: читаем для человека, но больший размер");
        System.out.println("  Байтовый: компактный, но нечитаем для человека");
        System.out.println("  Сериализация: сохраняет всю структуру объекта, но зависит от версии Java");
    }
    
    private static void printTabulatedFunction(TabulatedFunction function) {
        for (int i = 0; i < function.getPointsCount(); i++) {
            try {
                FunctionPoint point = function.getPoint(i);
                System.out.printf("  (%.2f, %.4f)%n", point.getX(), point.getY());
            } catch (Exception e) {
                System.out.println("  Ошибка при получении точки " + i);
            }
        }
    }
    
    private static void compareFunctions(TabulatedFunction f1, TabulatedFunction f2, 
                                       double from, double to, double step) {
        for (double x = from; x <= to; x += step) {
            double y1 = f1.getFunctionValue(x);
            double y2 = f2.getFunctionValue(x);
            System.out.printf("  x=%.1f: исходная=%.4f, прочитанная=%.4f%n", x, y1, y2);
        }
    }
}