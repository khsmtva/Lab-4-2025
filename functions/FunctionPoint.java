package functions;

public class FunctionPoint implements java.io.Serializable {
    private double x;
    private double y;
    
    // Конструктор с параметрами
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    // Конструктор копирования
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }
    
    // Конструктор по умолчанию
    public FunctionPoint() {
        this(0, 0);
    }
    
    // Геттеры
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    // Сеттеры
    public void setX(double x) {
        this.x = x;
    }
    
    public void setY(double y) {
        this.y = y;
    }
}
