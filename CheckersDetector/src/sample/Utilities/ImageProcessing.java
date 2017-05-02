package sample.Utilities;

import javafx.scene.image.Image;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;
import static org.opencv.imgproc.Imgproc.circle;

public class ImageProcessing {
    //<editor-fold desc="Static">
    private static final int BOARDS_NUMBER = 20;
    private static final int HORIZONTAL_CORNERS = 5;
    private static final int VERTICAL_CORNERS = 5;
    //</editor-fold>
    //<editor-fold desc="Variables">
    private Timer timer;
    private boolean cameraActive;
    private Mat savedImage;
    private Image undistoredImage, camStream;
    private List<Mat> imagePoints;
    private List<Mat> objectPoints;
    private MatOfPoint3f obj;
    private MatOfPoint2f imageCorners;
    private int successes;
    private Mat intrinsic;
    private Mat distCoeffs;
    private VideoCapture capture;
    //</editor-fold>

    public ImageProcessing() {
        this.capture = new VideoCapture();
        this.cameraActive = false;
        this.obj = new MatOfPoint3f();
        this.imageCorners = new MatOfPoint2f();
        this.savedImage = new Mat();
        this.undistoredImage = null;
        this.imagePoints = new ArrayList<>();
        this.objectPoints = new ArrayList<>();
        this.intrinsic = new Mat(3, 3, CvType.CV_32FC1);
        this.distCoeffs = new Mat();
        this.successes = 0;
        this.cameraActive = false;
        this.timer = new Timer();
    }

    private void mat2Hsv(Mat src, Mat dst) {
        cvtColor(src, dst, Imgproc.COLOR_BGR2HSV);
    }

    public Mat grabFrame() {
        Mat frame = new Mat();

        if (this.capture.isOpened()) {
            try {
                this.capture.read(frame);
                if (!frame.empty()) {
//                    this.findAndDrawPoints(frame);
                }

            } catch (Exception e) {
                System.err.print("ERROR");
                e.printStackTrace();
            }
        }
//        this.findAndDrawPoints(frame);

        return frame;
    }

    private void findAndDrawPoints(Mat frame) {
        Mat grayImage = new Mat();

        if (this.successes < this.BOARDS_NUMBER) {
            Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
            Size boardSize = new Size(this.HORIZONTAL_CORNERS, this.VERTICAL_CORNERS);
            boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners,
                    Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
            if (found) {
                TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
                Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11), new Size(-1, -1), term);
                grayImage.copyTo(this.savedImage);
                Calib3d.drawChessboardCorners(frame, boardSize, imageCorners, found);
//                double x, y;
//                x = y = 0.0;
//                System.out.println(imageCorners.cols() + " " + imageCorners.rows());
//                for (int i = 0; i < imageCorners.rows(); i++) {
//                    double[] data = imageCorners.get(i, 0);
//                    for (int j = 0; j < data.length; j++) {
//                        x = data[0];
//                        y = data[1];
//                    }
//                    System.out.println(x + " " + y);
//                }
            }
        }
    }

    public Mat findPerspectiveMatrix(Mat Frame) {
        return null;
    }


    public Mat findCircles(Mat src, List<Integer> minValues, List<Integer> maxValues) {
        Mat converted = new Mat();
        mat2Hsv(src, converted);
        Mat colorRange = new Mat();
        Core.inRange(converted, new Scalar(minValues.get(0), minValues.get(1), minValues.get(2)), new Scalar(maxValues.get(0), maxValues.get(1), maxValues.get(2)), colorRange);
        Mat circles = new Mat();
        Vector<Mat> circlesList = new Vector<Mat>();
//        medianBlur(colorRange, colorRange, 5);
//        HoughCircles(colorRange, circles, CV_HOUGH_GRADIENT, 1, 100, 4, 6, 16, 64);
//        System.out.println("#rows " + circles.rows() + " #cols " + circles.cols());
//
//        double x, y, r;
//        x = y = r = 0.0;
//        for (int i = 0; i < circles.cols(); i++) {
//            double[] data = circles.get(0, i);
//            for (int j = 0; j < data.length; j++) {
//                x = data[0];
//                y = data[1];
//                r = (int) data[2];
//            }
//            Point center = new Point(x, y);
//            circle(colorRange, center, (int) r, new Scalar(0, 0, 255), 20, 8, 0);
//        }
//        System.out.println(circlesList);
        return colorRange;
    }

    //<editor-fold desc="Getters and Setters">
    public static int getBoardsNumber() {
        return BOARDS_NUMBER;
    }

    public static int getHorizontalCorners() {
        return HORIZONTAL_CORNERS;
    }

    public static int getVerticalCorners() {
        return VERTICAL_CORNERS;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public boolean isCameraActive() {
        return cameraActive;
    }

    public void setCameraActive(boolean cameraActive) {
        this.cameraActive = cameraActive;
    }

    public Mat getSavedImage() {
        return savedImage;
    }

    public void setSavedImage(Mat savedImage) {
        this.savedImage = savedImage;
    }

    public Image getUndistoredImage() {
        return undistoredImage;
    }

    public void setUndistoredImage(Image undistoredImage) {
        this.undistoredImage = undistoredImage;
    }

    public Image getCamStream() {
        return camStream;
    }

    public void setCamStream(Image camStream) {
        this.camStream = camStream;
    }

    public List<Mat> getImagePoints() {
        return imagePoints;
    }

    public void setImagePoints(List<Mat> imagePoints) {
        this.imagePoints = imagePoints;
    }

    public List<Mat> getObjectPoints() {
        return objectPoints;
    }

    public void setObjectPoints(List<Mat> objectPoints) {
        this.objectPoints = objectPoints;
    }

    public MatOfPoint3f getObj() {
        return obj;
    }

    public void setObj(MatOfPoint3f obj) {
        this.obj = obj;
    }

    public MatOfPoint2f getImageCorners() {
        return imageCorners;
    }

    public void setImageCorners(MatOfPoint2f imageCorners) {
        this.imageCorners = imageCorners;
    }

    public int getSuccesses() {
        return successes;
    }

    public void setSuccesses(int successes) {
        this.successes = successes;
    }

    public Mat getIntrinsic() {
        return intrinsic;
    }

    public void setIntrinsic(Mat intrinsic) {
        this.intrinsic = intrinsic;
    }

    public Mat getDistCoeffs() {
        return distCoeffs;
    }

    public void setDistCoeffs(Mat distCoeffs) {
        this.distCoeffs = distCoeffs;
    }

    public VideoCapture getCapture() {
        return capture;
    }

    public void setCapture(VideoCapture capture) {
        this.capture = capture;
    }
    //</editor-fold>
}
