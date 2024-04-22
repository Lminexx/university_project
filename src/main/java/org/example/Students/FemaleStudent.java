package org.example.Students;

import java.awt.*;
import java.lang.Math;
import java.util.Objects;

public class FemaleStudent extends Student {
    private double radius;
    private double angle;

    public FemaleStudent(int x, int y, int timeToBorn, int timeToDie, double radius) {
        super(x, y, timeToBorn, timeToDie);
        this.radius = radius;
        this.angle = 0;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.pink);
        g.fillOval((int) (x + radius * Math.cos(angle)), (int) (y + radius * Math.sin(angle)), 50, 50);
    }

    @Override
    public void move(int speed) {
        angle += speed / radius;
        angle %= (2 * Math.PI);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FemaleStudent that = (FemaleStudent) o;
        return Double.compare(radius, that.radius) == 0 && Double.compare(angle, that.angle) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius, angle);
    }

    @Override
    public String toString() {
        return "Students.FemaleStudent{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
    @Override
    public String getGender(){
        return "F";
    }
}
