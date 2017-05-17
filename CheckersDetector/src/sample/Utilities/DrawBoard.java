package sample.Utilities;

/**
 * Created by Paula on 2017-05-17.
 */
public class DrawBoard {
    public Integer SCREEN_SIZE;
    public Integer pawnRadius;
    public Integer fieldSize;
    public Color yellowPawn;
    public Color bluePawn;
    public Color lightField;
    public Color darkField;
    public Mat dispGameBoard;
    public Mat boardBackground;

    public DrawBoard(Board board) {
        this.SCREEN_SIZE = 800;
        this.pawnRadius = ((Double) (SCREEN_SIZE * 0.9 / 16)).intValue();
        this.fieldSize = SCREEN_SIZE / board.SIZE;
        this.yellowPawn = new Color(0,255,255);
        this.bluePawn = new Color(0,0,255);
        this.lightField = new Color(16);
        this.darkField = new Color(256);
        this.dispGameBoard =  new Mat(new Size(SCREEN_SIZE, SCREEN_SIZE), CvType.CV_8UC3);
        this.boardBackground = drawBackground(board);
    }

    public Mat drawGame(Board board){
        clearBoard();
        drawPawns(board);
        return dispGameBoard;
    }

    public void clearBoard(){
        dispGameBoard = boardBackground;
    }

    public Mat drawBackground(Board board) {
        List<Integer> lF = lightField.getMinValues();
        List<Integer> dF = darkField.getMinValues();

        Imgproc.rectangle(dispGameBoard, new Point(0, 0), new Point(SCREEN_SIZE, SCREEN_SIZE),
                new Scalar(dF.get(0), dF.get(1), dF.get(2)), -1);
        for (int x = 0; x < board.SIZE; x++) {
            for (int y = 0; y < board.SIZE; y++) {
                if (y % 2 == x % 2) {
                    Imgproc.rectangle(dispGameBoard, new Point(x * fieldSize, y * fieldSize),
                            new Point((x + 1) * fieldSize, (y + 1) * fieldSize),
                            new Scalar(lF.get(0), lF.get(1), lF.get(2)), -1);
                }
            }
        }
        return dispGameBoard;
    }

    public void drawPawns(Board board) {
        List<Integer> yP = yellowPawn.getMinValues();
        List<Integer> bP = bluePawn.getMinValues();
        for (int x = 0; x < board.SIZE; x++) {
            for (int y = 0; y < board.SIZE; y++) {
                List<Integer> currentPawnColor = new ArrayList<>();
                if (board.boardState[x][y] == State.YELLOW) {
                    currentPawnColor = yP;
                } else if (board.boardState[x][y] == State.BLUE) {
                    currentPawnColor = bP;
                } else {
                    continue;
                }
                Imgproc.circle(dispGameBoard, new Point(((y + .5)) * fieldSize, ((x + .5) * fieldSize)),
                        pawnRadius, new Scalar(currentPawnColor.get(0), currentPawnColor.get(1), currentPawnColor.get(2)
                        ), -1);
            }
        }
    }

    public void putPawn(double[] pawn, Board board, State state){
        board.boardState[(int) pawn[0]][(int) pawn[1]] = state;
    }



}
