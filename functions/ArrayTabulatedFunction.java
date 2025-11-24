package functions;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ArrayTabulatedFunction implements TabulatedFunction, java.io.Serializable, java.io.Externalizable {
    private FunctionPoint[] points;
    private int pointsCount;
        public ArrayTabulatedFunction() {
        // Создаем минимальную валидную функцию
        this.points = new FunctionPoint[2];
        this.points[0] = new FunctionPoint(0.0, 0.0);
        this.points[1] = new FunctionPoint(1.0, 1.0);
        this.pointsCount = 2;
    }
    
    // Конструктор с количеством точек
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        
        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 5];
        
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }
    
    // Конструктор с массивом значений
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        
        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 5];
        
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }
    public ArrayTabulatedFunction(FunctionPoint[] points) {
    if (points.length < 2) {
        throw new IllegalArgumentException("Количество точек должно быть не менее 2");
    }
    
    // Проверка упорядоченности точек по X
    for (int i = 1; i < points.length; i++) {
        if (points[i].getX() <= points[i-1].getX() + 1e-10) {
            throw new IllegalArgumentException("Точки должны быть строго упорядочены по возрастанию X");
        }
    }
    
    this.pointsCount = points.length;
    this.points = new FunctionPoint[pointsCount + 10]; // Запас места
    
    // Копируем точки с обеспечением инкапсуляции
    for (int i = 0; i < pointsCount; i++) {
        this.points[i] = new FunctionPoint(points[i]);
    }
}
    // Методы для работы с функцией
    public double getLeftDomainBorder() {
        return points[0].getX();
    }
    
    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }
    
    public double getFunctionValue(double x) {
    if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
        return Double.NaN;
    }
    
    // Оптимизация
    for (int i = 0; i < pointsCount; i++) {
        if (Math.abs(points[i].getX() - x) < 1e-10) {
            return points[i].getY();
        }
    }
    
    // Линейная интерполяция если точного совпадения нет
    for (int i = 0; i < pointsCount - 1; i++) {
        double x1 = points[i].getX();
        double x2 = points[i + 1].getX();
        
        if (x >= x1 && x <= x2) {
            double y1 = points[i].getY();
            double y2 = points[i + 1].getY();
            return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
        }
    }
    
    return Double.NaN;
}

    // Методы для работы с точками
    public int getPointsCount() {
        return pointsCount;
    }
    
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс точки выходит за границы");
        }
        return new FunctionPoint(points[index]);
    }
    
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException, FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс точки выходит за границы");
        }
        
        // Проверка что x находится между соседями (машинный эпсилон)
        if (index > 0 && point.getX() <= points[index - 1].getX() + 1e-10) {
            throw new InappropriateFunctionPointException("Координата X должна быть больше предыдущей точки");
        }
        if (index < pointsCount - 1 && point.getX() >= points[index + 1].getX() - 1e-10) {
            throw new InappropriateFunctionPointException("Координата X должна быть меньше следующей точки");
        }
        
        points[index] = new FunctionPoint(point);
    }
    
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс точки выходит за границы");
        }
        return points[index].getX();
    }
    
    public void setPointX(int index, double x) throws InappropriateFunctionPointException, FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс точки выходит за границы");
        }
        FunctionPoint temp = new FunctionPoint(x, points[index].getY());
        setPoint(index, temp);
    }
    
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс точки выходит за границы");
        }
        return points[index].getY();
    }
    
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс точки выходит за границы");
        }
        points[index].setY(y);
    }

    // Методы изменения количества точек
    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс точки выходит за границы");
        }
        if (pointsCount <= 2) {
            throw new IllegalStateException("Нельзя удалить точку: должно остаться минимум 2 точки");
        }
        
        for (int i = index; i < pointsCount - 1; i++) {
            points[i] = points[i + 1];
        }
        pointsCount--;
    }
    
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на дублирование X (машинный эпсилон)
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с такой координатой X уже существует");
            }
        }
        
        // Поиск позиции для вставки
        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].getX() < point.getX()) {
            insertIndex++;
        }
        
        // Расширяем массив если нужно
        if (pointsCount >= points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length + 10];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }
        
        // Сдвигаем точки вправо
        for (int i = pointsCount; i > insertIndex; i--) {
            points[i] = points[i - 1];
        }
        
        // Вставляем новую точку
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }
        // Реализация Externalizable
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }

    public void readExternal(ObjectInput in) throws IOException {
        pointsCount = in.readInt();
        points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }
}
