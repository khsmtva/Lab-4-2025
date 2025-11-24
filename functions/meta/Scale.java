package functions.meta;

import functions.Function;

public class Scale implements Function {
    private Function f;
    private double scaleX, scaleY;
    
    public Scale(Function f, double scaleX, double scaleY) {
        this.f = f;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    public double getLeftDomainBorder() {
        if (scaleX >= 0) {
            return f.getLeftDomainBorder() * scaleX;
        } else {
            return f.getRightDomainBorder() * scaleX;
        }
    }
    
    public double getRightDomainBorder() {
        if (scaleX >= 0) {
            return f.getRightDomainBorder() * scaleX;
        } else {
            return f.getLeftDomainBorder() * scaleX;
        }
    }
    
    public double getFunctionValue(double x) {
        double originalX = x / scaleX;
        double originalY = f.getFunctionValue(originalX);
        if (Double.isNaN(originalY)) {
            return Double.NaN;
        }
        return originalY * scaleY;
    }
}
