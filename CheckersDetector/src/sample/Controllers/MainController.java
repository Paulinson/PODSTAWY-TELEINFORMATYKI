package sample.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import sample.Utilities.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.TimerTask;

public class MainController {
    @FXML
    public Button startCameraButton;
    @FXML
    public Button nextMoveButton;
    @FXML
    public Button calibrateButton;

    @FXML
    public ImageView cameraView;
    @FXML
    public ImageView captureView;
    @FXML
    public ImageView calibrateView;
    @FXML
    public ImageView checkersBoardView;
    @FXML
    public TextArea infoTextArea;

    public ImageProcessing imageProcessing;

    Color firstPlayer = new Color(100, 150, 0, 140, 255, 255); //BLUE;
    Color secondPlayer = new Color(20, 100, 100, 30, 255, 255); //YELLOW
    Color borderEdges = new Color(50, 32, 16, 80, 245, 245); //GREEEN;
    Color currentColor;
    int gameState = 0;

    public MainController() {

    }

    @FXML
    public void startCamera() {
        Board board = new Board();
        DrawBoard drawBoard = new DrawBoard(board);
        checkersBoardView.setImage(mat2Image(drawBoard.drawBackground(board)));
        if (!imageProcessing.cameraActive) {
            imageProcessing = new ImageProcessing(MainController.this);
            imageProcessing.capture.open(0);
            if (imageProcessing.capture.isOpened()) {
                imageProcessing.cameraActive = true;

                TimerTask frameGrabber = new TimerTask() {
                    @Override
                    public void run() {
                        Mat frame = imageProcessing.grabFrame();
                        Mat topView = frame;
                        Image image = mat2Image(frame);
                        Image topViewImage = null;
                        if (imageProcessing.generatedPerspectiveMatrix == false) {
                            imageProcessing.findPerspectiveMatrix(frame, borderEdges);
                        } else {
                            topView = imageProcessing.topView(frame, imageProcessing.perspectiveMatrix);
                            topViewImage = mat2Image(topView);
                            calibrateView.setImage(topViewImage);
                            calibrateView.setFitWidth(380);
                            calibrateView.setFitHeight(400);
                            calibrateView.setPreserveRatio(true);
                        }
                        cameraView.setImage(image);
                        cameraView.setFitWidth(380);
                        cameraView.setFitHeight(400);
                        cameraView.setPreserveRatio(true);

                        if (gameState != 0) {
                            Mat circleFrame = imageProcessing.findCircles(topView, currentColor.getMinValues(), currentColor.getMaxValues(), true);
                            Image circleImage = mat2Image(circleFrame);
                            captureView.setImage(circleImage);
                            captureView.setFitWidth(380);
                            captureView.setFitHeight(400);
                            captureView.setPreserveRatio(true);

                            Mat firstPlayerPawns = imageProcessing.findCircles(topView, firstPlayer.getMinValues(), firstPlayer.getMaxValues(), false);
                            Mat secondPlayerPawns = imageProcessing.findCircles(topView, secondPlayer.getMinValues(), secondPlayer.getMaxValues(), false);
                            drawBoard.clearBoard();
                            for (double[] pawn : getPawnPositions(firstPlayerPawns)) {
                                drawBoard.putPawn(pawn, board, State.BLUE);
                            }
                            for (double[] pawn :  getPawnPositions(secondPlayerPawns)) {
                                drawBoard.putPawn(pawn, board, State.YELLOW);
                            }
                            
                            Mat backGround = drawBoard.drawGame(board);
                            checkersBoardView.setImage(mat2Image(backGround));
                            checkersBoardView.setFitWidth(400);
                            checkersBoardView.setFitHeight(400);
                            checkersBoardView.setPreserveRatio(true);
                        }
                    }
                };
                imageProcessing.timer.schedule(frameGrabber, 0, 100);
                this.startCameraButton.setText("Stop Camera");
            } else {
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            imageProcessing.cameraActive = false;
            startCameraButton.setText("Start Camera");
            if (imageProcessing.timer != null) {
                imageProcessing.timer.cancel();
                imageProcessing.timer = null;
            }
            imageProcessing.capture.release();
            cameraView.setImage(null);
            captureView.setImage(null);
        }
    }

    public Image mat2Image(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    public List<double[]> getPawnPositions(Mat mat) {
        List<double[]> result = imageProcessing.matToDouble(mat);
        for (double[] i : result) {
            i[0] = i[0] / 100;
            i[1] = i[1] / 100;
        }
        return result;
    }

    @FXML
    public void takeScreenshot() {
        File out = new File("screenShot0.png");
        int i = 0;
        while (out.exists()) {
            out = new File("screenShot" + i + ".png");
        }
        //    try {
        //        ImageIO.write(Image,"png",out);
        //   } catch (IOException e1) {
        //       e1.printStackTrace();
        //   }
    }

    @FXML
    public void calibrate() {
        imageProcessing.generatedPerspectiveMatrix = false;
    }

    @FXML
    public void nextMove() {
        switch (gameState) {
            case 1: {
                currentColor = firstPlayer;
                gameState = 2;
                break;
            }
            case 2: {
                currentColor = secondPlayer;
                gameState = 1;
                break;
            }
            case 0: {
                currentColor = firstPlayer;
                gameState = 1;
                break;
            }
        }
    }
}
