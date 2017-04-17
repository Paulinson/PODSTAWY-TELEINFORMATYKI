package sample.Utilities;

import com.sun.javafx.geom.Vec3f;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import sample.Config.Colors;

import java.util.Vector;

import static org.opencv.imgproc.Imgproc.*;

public class CircleServices {



    private void reduceNoise(Mat src, Mat srcGray){
        cvtColor(src, srcGray, COLOR_BGR2HSV);

    }

    public void findCircles(Mat src){
        Scalar minColor = new Scalar(50, 32, 16);
        Scalar maxColor = new Scalar(80,245,245);
        Mat srcGray = new Mat();
        Core.inRange(src, minColor, maxColor, srcGray);
        reduceNoise(src,srcGray);
        Mat circles = new Mat();
        HoughCircles(srcGray, circles, CV_HOUGH_GRADIENT, 1, 100, 4, 6, 16, 65);
        System.out.println(circles);
    }
}
