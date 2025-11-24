package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction, java.io.Serializable {
    
    // Внутренний класс для узла списка
    private class FunctionNode {
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;
        
        FunctionNode(FunctionPoint point) {
            this.point = point;
        }
    }
    
    private FunctionNode head; // Голова списка (не хранит данные)
    private int pointsCount;
    private FunctionNode lastAccessedNode; // Для оптимизации доступа
    private int lastAccessedIndex;
    
    // Конструкторы
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        
        initializeList();
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, 0);
        }
    }
    
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        
        initializeList();
        double step = (rightX - leftX) / (values.length - 1);
        
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, values[i]);
        }
    }
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
    if (points.length < 2) {
        throw new IllegalArgumentException("Количество точек должно быть не менее 2");
    }
    
    // Проверка упорядоченности точек по X
    for (int i = 1; i < points.length; i++) {
        if (points[i].getX() <= points[i-1].getX() + 1e-10) {
            throw new IllegalArgumentException("Точки должны быть строго упорядочены по возрастанию X");
        }
    }
    
    initializeList();
    
    // Добавляем точки в список
    for (FunctionPoint point : points) {
        addNodeToTail().point = new FunctionPoint(point);
    }
}
    // Инициализация пустого списка с головой
    private void initializeList() {
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        pointsCount = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }
    
    // Методы для работы со списком
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }
        
        // Оптимизация: начинаем с последнего доступного узла
        FunctionNode current;
        int currentIndex;
        
        if (lastAccessedIndex != -1 && Math.abs(index - lastAccessedIndex) < Math.abs(index)) {
            current = lastAccessedNode;
            currentIndex = lastAccessedIndex;
        } else {
            current = head.next;
            currentIndex = 0;
        }
        
        // Двигаемся к нужному узлу
        while (currentIndex < index) {
            current = current.next;
            currentIndex++;
        }
        while (currentIndex > index) {
            current = current.prev;
            currentIndex--;
        }
        
        lastAccessedNode = current;
        lastAccessedIndex = currentIndex;
        return current;
    }
    
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        newNode.next = head;
        newNode.prev = head.prev;
        head.prev.next = newNode;
        head.prev = newNode;
        pointsCount++;
        return newNode;
    }
    
    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }
        
        FunctionNode newNode = new FunctionNode(null);
        FunctionNode targetNode = (index == pointsCount) ? head : getNodeByIndex(index);
        
        newNode.next = targetNode;
        newNode.prev = targetNode.prev;
        targetNode.prev.next = newNode;
        targetNode.prev = newNode;
        pointsCount++;
        
        if (index <= lastAccessedIndex) {
            lastAccessedIndex++;
        }
        
        return newNode;
    }
    
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы");
        }
        
        FunctionNode nodeToDelete = getNodeByIndex(index);
        nodeToDelete.prev.next = nodeToDelete.next;
        nodeToDelete.next.prev = nodeToDelete.prev;
        pointsCount--;
        
        if (lastAccessedIndex == index) {
            lastAccessedNode = head;
            lastAccessedIndex = -1;
        } else if (lastAccessedIndex > index) {
            lastAccessedIndex--;
        }
        
        return nodeToDelete;
    }
    
    // Реализация методов интерфейса TabulatedFunction
    public double getLeftDomainBorder() {
        if (pointsCount == 0) return Double.NaN;
        return head.next.point.getX();
    }
    
    public double getRightDomainBorder() {
        if (pointsCount == 0) return Double.NaN;
        return head.prev.point.getX();
    }
    
    public double getFunctionValue(double x) {
    if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
        return Double.NaN;
    }
    
    // Оптимизация: проверка точного совпадения с существующей точкой
    FunctionNode current = head.next;
    while (current != head) {
        if (Math.abs(current.point.getX() - x) < 1e-10) {
            return current.point.getY();
        }
        current = current.next;
    }
    
    // Линейная интерполяция если точного совпадения нет
    current = head.next;
    while (current != head && current.next != head) {
        double x1 = current.point.getX();
        double x2 = current.next.point.getX();
        
        if (x >= x1 && x <= x2) {
            double y1 = current.point.getY();
            double y2 = current.next.point.getY();
            return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
        }
        current = current.next;
    }
    
    return Double.NaN;
}
    
    public int getPointsCount() {
        return pointsCount;
    }
    
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.point);
    }
    
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException, FunctionPointIndexOutOfBoundsException {
        FunctionNode node = getNodeByIndex(index);
        
        // Проверка упорядоченности
        if (index > 0 && point.getX() <= getNodeByIndex(index - 1).point.getX() + 1e-10) {
            throw new InappropriateFunctionPointException("Координата X должна быть больше предыдущей точки");
        }
        if (index < pointsCount - 1 && point.getX() >= getNodeByIndex(index + 1).point.getX() - 1e-10) {
            throw new InappropriateFunctionPointException("Координата X должна быть меньше следующей точки");
        }
        
        node.point = new FunctionPoint(point);
    }
    
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        return getNodeByIndex(index).point.getX();
    }
    
    public void setPointX(int index, double x) throws InappropriateFunctionPointException, FunctionPointIndexOutOfBoundsException {
        FunctionPoint temp = new FunctionPoint(x, getPointY(index));
        setPoint(index, temp);
    }
    
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        return getNodeByIndex(index).point.getY();
    }
    
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        getNodeByIndex(index).point.setY(y);
    }
    
    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException {
        if (pointsCount <= 2) {
            throw new IllegalStateException("Нельзя удалить точку: должно остаться минимум 2 точки");
        }
        deleteNodeByIndex(index);
    }
    
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на дублирование X
        FunctionNode current = head.next;
        while (current != head) {
            if (Math.abs(current.point.getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с такой координатой X уже существует");
            }
            current = current.next;
        }
        
        // Поиск позиции для вставки
        int insertIndex = 0;
        current = head.next;
        while (current != head && current.point.getX() < point.getX()) {
            insertIndex++;
            current = current.next;
        }
        
        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = new FunctionPoint(point);
    }
}
