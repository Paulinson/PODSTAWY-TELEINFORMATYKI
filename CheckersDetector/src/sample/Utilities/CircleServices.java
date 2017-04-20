package sample.Utilities;

import com.sun.javafx.geom.Vec3f;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
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

        HoughCircles(srcGray, circles, CV_HOUGH_GRADIENT, 1, 30, 200, 50, 0, 0 );
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
            circle(src, center, 3, new Scalar(0, 255, 0), -1, 8, 0);
            circle(src, center, (int) r, new Scalar(0, 0, 255), 20, 8, 0);
        }
        System.out.println(circlesList);
        return src;
    }

}
