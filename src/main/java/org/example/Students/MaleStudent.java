package org.example.Students;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class MaleStudent extends Student {

    private Random random;
    private int direction;

    public MaleStudent(int x, int y, int timeToBorn, int timeToDie) {
        super(x, y, timeToBorn, timeToDie);
        Timer timer = new Timer(2000, e -> {
            random = new Random();
            this.direction = random.nextInt(1, 4);
        });
        timer.start();
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.blue);
        g.fillOval(x, y, 50, 50);
    }

    @Override
    public void move(int speed) {
        switch (direction) {
            case 0 -> x += speed;
            case 1 -> x -= speed;
            case 2 -> y += speed;
            case 3 -> y -= speed;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaleStudent that = (MaleStudent) o;
        return direction == that.direction && Objects.equals(random, that.random);
    }

    @Override
    public int hashCode() {
        return Objects.hash(random, direction);
    }

    @Override
    public String toString() {
        return "Students.MaleStudent{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public String getGender() {
        return "M";
    }
}