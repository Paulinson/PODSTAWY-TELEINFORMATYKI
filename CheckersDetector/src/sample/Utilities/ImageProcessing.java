package sample.Utilities;

import javafx.scene.image.Image;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;
import sample.Controllers.MainController;
import sample.Main;

import java.util.*;

import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;
import static org.opencv.imgproc.Imgproc.circle;

public class ImageProcessing {
    //<editor-fold desc="Static">
    private static final int BOARDS_NUMBER = 20;
    private static final int HORIZONTAL_CORNERS = 5;
    private static final int VERTICAL_CORNERS = 5;
    private static final Double SCREEN_SIZE = 800d;
    private static final Double EDGE_SIZE = 10d;
    //</editor-fold>
    //<editor-fold desc="Variables">
    public Timer timer;
    public boolean cameraActive;
    public Mat savedImage;
    public Image undistoredImage, camStream;
    public List<Mat> imagePoints;
    public List<Mat> objectPoints;
    public MatOfPoint3f obj;
    public MatOfPoint2f imageCorners;
    public Mat perspectiveMatrix;
    public int successes;
    public Mat intrinsic;
    public Mat distCoeffs;
    public VideoCapture capture;
    public boolean generatedPerspectiveMatrix;
    public MainController mainController;
    //</editor-fold>


    public ImageProcessing(MainController mainController) {
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
        this.perspectiveMatrix = null;
        this.generatedPerspectiveMatrix = false;
        this.mainController = mainController;
    }

    private void mat2Hsv(Mat src, Mat dst) {
        cvtColor(src, dst, Imgproc.COLOR_BGR2HSV);
    }

    private void mat2Gray(Mat src, Mat dst) {
        cvtColor(src, dst, Imgproc.COLOR_RGBA2GRAY);
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

    public void findPerspectiveMatrix(Mat frame, Color color) {
        Mat boardMarkers = findCircles(frame, color.getMinValues(), color.getMaxValues(), false);
        Mat perspectiveMatrix;
        Square board;
        if (boardMarkers != null && boardMarkers.cols() == 4) {
            Mat boardMakersNew = boardMarkers;
            Square square = new Square(new Point(0, 0), new Point(SCREEN_SIZE - 1, 0), new Point(SCREEN_SIZE - 1, SCREEN_SIZE - 1),
                    new Point(0, SCREEN_SIZE - 1));
            board = findCorners(boardMakersNew);
            perspectiveMatrix = Imgproc.getPerspectiveTransform(Converters.vector_Point2f_to_Mat(board.getPoints()),
                    Converters.vector_Point2f_to_Mat(square.getPoints()));
            this.perspectiveMatrix = perspectiveMatrix;
            this.generatedPerspectiveMatrix = true;
        }
    }

    public Square findCorners(Mat corners) {
        Point topLeft = new Point(1000d, 1000d);
        Point topRight = new Point(0d, 1000d);
        Point bottomRight = new Point(0d, 0d);
        Point bottomLeft = new Point(1000d, 0d);
        for (int i = 0; i < corners.cols(); i++) {
            double[] data = corners.get(0, i);
            for (int j = 0; j < data.length; j++) {
                if (data[0] + data[1] <= topLeft.x + topLeft.y) {
                    topLeft.x = data[0] + EDGE_SIZE;
                    topLeft.y = data[1] + EDGE_SIZE;
                } else if (data[0] + data[1] >= bottomRight.x + bottomRight.y) {
                    bottomRight.x = data[0] - EDGE_SIZE;
                    bottomRight.y = data[1] - EDGE_SIZE;
                } else if (data[0] >= topRight.x && data[1] <= topRight.y) {
                    topRight.x = data[0] - EDGE_SIZE;
                    topRight.y = data[1] + EDGE_SIZE;
                } else if (data[0] <= bottomLeft.x && data[1] >= bottomLeft.y) {
                    bottomLeft.x = data[0] + EDGE_SIZE;
                    bottomLeft.y = data[1] + EDGE_SIZE;
                }
            }
        }
        return new Square(topLeft, topRight, bottomRight, bottomLeft);
    }

    public Mat topView(Mat frame, Mat perspectiveMatrix) {
        Mat wrappedPerspective = new Mat();
        Imgproc.warpPerspective(frame, wrappedPerspective, perspectiveMatrix, new Size(SCREEN_SIZE, SCREEN_SIZE));
        return wrappedPerspective;
    }


    public Mat findCircles(Mat src, List<Integer> minValues, List<Integer> maxValues, boolean view) {
        Mat hsv = new Mat();
        mat2Hsv(src, hsv);
        Mat colorRange = new Mat();
        Core.inRange(hsv, new Scalar(minValues.get(0), minValues.get(1), minValues.get(2)), new Scalar(maxValues.get(0), maxValues.get(1), maxValues.get(2)), colorRange);
        Mat circles = new Mat();
        Vector<Mat> circlesList = new Vector<Mat>();
        medianBlur(colorRange, colorRange, 5);
        HoughCircles(colorRange, circles, CV_HOUGH_GRADIENT, 1, 100, 4, 6, 16, 64);
        System.out.println("#rows " + circles.rows() + " #cols " + circles.cols());
        mainController.infoTextArea.setText("Pawns: " + circles.cols());
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
            circle(src, center, (int) r, new Scalar(0, 0, 255), 2, 8, 0);
        }
        if (view) {
            return colorRange;
        } else {
            return colorRange;
        }
    }

}
