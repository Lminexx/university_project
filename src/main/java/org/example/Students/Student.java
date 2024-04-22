package org.example.Students;
import java.awt.*;
import java.io.Serializable;

public abstract class Student implements IBehaviour, Serializable {
    public int x;
    public int y;
    public double timeToBorn;
    public double timeToDie;

    public Student(int x, int y, int timeToBorn, int timeToDie) {
        this.x = x;
        this.y = y;
        this.timeToBorn = timeToBorn;
        this.timeToDie = timeToDie;
    }

    public abstract void draw(Graphics g);

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setTimeToBorn(double timeToBorn) {
        this.timeToBorn = timeToBorn;
    }
    public abstract String getGender();

}


