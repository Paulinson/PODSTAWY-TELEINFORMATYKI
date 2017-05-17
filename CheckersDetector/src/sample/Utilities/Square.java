package sample.Utilities;

import org.opencv.core.Point;

import java.util.Arrays;
import java.util.List;

public class Square {
    public Point topLeft;
    public Point topRight;
    public Point bottomLeft;
    public Point bottomRight;

    public Square(Point topLeft, Point topRight, Point bottomRight, Point bottomLeft) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    public List<Point> getPoints() {
        return Arrays.asList(topLeft, topRight, bottomRight, bottomLeft);
    }
}
