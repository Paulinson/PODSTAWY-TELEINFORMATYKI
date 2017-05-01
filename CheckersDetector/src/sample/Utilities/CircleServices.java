package sample.Utilities;

import com.sun.javafx.geom.Vec3f;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Vector;

import static org.opencv.imgproc.Imgproc.*;

public class CircleServices {
    private void reduceNoise(Mat src, Mat dst) {
        medianBlur(src, src, 5);
        cvtColor(src, dst, Imgproc.COLOR_BGR2HSV);
    }


    public Mat findCircles(Mat src) {
        Mat converted = new Mat();
        reduceNoise(src, converted);
        Mat lowerRedRange = new Mat();
        Mat upperRedRange = new Mat();
        Mat redImage = new Mat();
        Core.inRange(converted, new Scalar(0, 100, 100), new Scalar(10, 255, 255), lowerRedRange);
        Core.inRange(converted, new Scalar(160, 100, 100), new Scalar(179, 255, 255), upperRedRange);
        Core.addWeighted(lowerRedRange, 1.0, upperRedRange, 1.0, 1.0, redImage);
        GaussianBlur(redImage, redImage, new Size(9, 9), 2, 2);
        Mat circles = new Mat();
        Vector<Mat> circlesList = new Vector<Mat>();

        HoughCircles(redImage, circles, CV_HOUGH_GRADIENT, 1, redImage.rows() / 8, 100, 20, 0, 0);
        System.out.println("#rows " + circles.rows() + " #cols " + circles.cols());

        double x, y, r;
        x = y = r = 0.0;
        for (int i = 0; i < circles.cols(); i++) {
            double[] data = circles.get(0, i);
            for (int j = 0; j < data.length; j++) {
                x = data[0];
                y = data[1];
                r = (int) data[2];
            }
            Point center = new Point(x, y);
            circle(redImage, center, (int) r, new Scalar(0, 0, 255), 20, 8, 0);
        }
        System.out.println(circlesList);
        return redImage;
    }

}
