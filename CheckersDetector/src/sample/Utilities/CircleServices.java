package sample.Utilities;

import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Vector;

import static org.opencv.imgproc.Imgproc.*;

public class CircleServices {
    private void reduceNoise(Mat src, Mat srcGray) {
        cvtColor(src, srcGray, Imgproc.COLOR_RGBA2GRAY);
        GaussianBlur(srcGray, srcGray, new Size(9, 9), 2, 2);
    }


    public Mat findCircles(Mat src) {
        Mat srcGray = new Mat();
        reduceNoise(src, srcGray);
        Mat circles = new Mat();
        Vector<Mat> circlesList = new Vector<Mat>();

        HoughCircles(srcGray, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 60, 200, 20, 30, 0);
        System.out.println("#rows " + circles.rows() + " #cols " + circles.cols());
        double x = 0.0;
        double y = 0.0;
        int r = 0;

        for (int i = 0; i < circles.rows(); i++) {
            double[] data = circles.get(i, 0);
            for (int j = 0; j < data.length; j++) {
                x = data[0];
                y = data[1];
                r = (int) data[2];
            }
            Point center = new Point(x, y);
            circle(srcGray, center, 3, new Scalar(0, 255, 0), -1, 8, 0);
            circle(srcGray, center, r, new Scalar(0, 0, 255), 3, 8, 0);
        }
        System.out.println(circlesList);
        return srcGray;

    }

}
