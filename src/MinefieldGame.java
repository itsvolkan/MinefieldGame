import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.util.Random;

public class MinefieldGame extends Application {

    private static final int GRID_SIZE = 10;
    private static final int NUM_MINES = 20;
    private static final int CELL_SIZE = 50;

    private boolean[][] mineGrid;
    private boolean[][] revealedGrid;
    private boolean[][] flaggedGrid;

    private MinefieldModel model;

    private GridPane gridPane;
    private Canvas canvas;
    private GraphicsContext gc;
    private Label statusLabel;
    private Button resetButton;
    private TextField nameTextField;
    private TextField surnameTextField;
    private Label playerNameLabel;
    private Label scoreLabel;



    @Override
    public void start(Stage primaryStage) {
        mineGrid = new boolean[GRID_SIZE][GRID_SIZE];
        revealedGrid = new boolean[GRID_SIZE][GRID_SIZE];
        flaggedGrid = new boolean[GRID_SIZE][GRID_SIZE];
        model = new MinefieldModel();
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(30));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        canvas = new Canvas(CELL_SIZE * GRID_SIZE, CELL_SIZE * GRID_SIZE);
        gc = canvas.getGraphicsContext2D();

        statusLabel = new Label("Welcome! Click a cell to New Game.");
        statusLabel.setFont(Font.font(18));

        resetButton = new Button("New Game");
        resetButton.setFont(Font.font(14));
        resetButton.setOnAction(e -> resetGame());
        resetButton.setStyle("-fx-background-color: #fff; -fx-border-color: #000;");

        HBox statusBox = new HBox(50, statusLabel, resetButton);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Button button = createCellButton(row, col);
                gridPane.add(button, col, row);
            }
        }


        GridPane root = new GridPane();
        root.add(statusBox, 0, 0);
        root.add(gridPane, 0, 1);
        root.add(canvas, 1, 1);



        VBox rightPane = new VBox(30);
        rightPane.setPadding(new Insets(30));
        rightPane.getChildren().addAll(statusBox, createPlayerInfoBox(), createScoreTable());

        HBox mainPane = new HBox(30, gridPane, rightPane);
        mainPane.setStyle("-fx-background-color: #ddf;");

        Scene scene = new Scene(mainPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Minefield Game");

        generateMinefield(-1, -1);
        primaryStage.show();
    }

    private Button createCellButton(int row, int col) {
        Button button = new Button();
        button.setMinSize(50, 50);
        button.setFont(Font.font(14));
        button.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000;");
        button.setOnMouseClicked(e -> handleButtonClick(row, col, button, e.isSecondaryButtonDown()));
        return button;
    }

    private VBox createPlayerInfoBox() {
        VBox playerInfoBox = new VBox(5);
        playerInfoBox.setPadding(new Insets(10));
        playerInfoBox.setStyle("-fx-border-color: #000; -fx-border-width: 2;");

        playerNameLabel = new Label("Player: ");
        playerNameLabel.setFont(Font.font(18));
        playerNameLabel.setStyle("-fx-font-weight: bold;");

        nameTextField = new TextField();
        nameTextField.setPromptText("Enter your name");

        surnameTextField = new TextField();
        surnameTextField.setPromptText("Enter your surname");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> handlePlayerSubmit());

        playerInfoBox.getChildren().addAll(playerNameLabel, nameTextField, surnameTextField, submitButton);
        return playerInfoBox;
    }

    private VBox createScoreTable() {
        VBox scoreBox = new VBox(5);
        scoreBox.setPadding(new Insets(30));
        scoreBox.setStyle("-fx-border-color: #000; -fx-border-width: 2;");

        Label scoreTitleLabel = new Label("Score");
        scoreTitleLabel.setFont(Font.font(18));
        scoreTitleLabel.setStyle("-fx-font-weight: bold;");

        scoreLabel = new Label("0");
        scoreLabel.setFont(Font.font(30));
        scoreLabel.setTextFill(Color.GREEN);

        scoreBox.getChildren().addAll(scoreTitleLabel, scoreLabel);
        return scoreBox;
    }

    private void handleButtonClick(int row, int col, Button button, boolean isRightClick) {
        if (isRightClick) {
            toggleFlag(row, col, button);
        } else if (mineGrid[row][col]) {
            button.setText("💣");
            button.setTextFill(Color.BLACK);
            revealMines();
            gameOver();
        } else {
            model.increaseScore(10);
            updateScoreLabel();
            revealedGrid[row][col] = true;
            int adjacentMines = countAdjacentMines(row, col);
            if (adjacentMines > 0) {
                button.setText(Integer.toString(adjacentMines));
                button.setTextFill(getTextColor(adjacentMines));
            } else {
                revealEmptyCells(row, col);
            }
            button.setDisable(true);
            checkGameWin();
        }
    }
    private void toggleFlag(int row, int col, Button button) {
        if (!revealedGrid[row][col]) {
            flaggedGrid[row][col] = !flaggedGrid[row][col];
            button.setText(flaggedGrid[row][col] ? "🚩" : "");
        }
    }

    private void generateMinefield(int startRow, int startCol) {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < NUM_MINES) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);

            if (!mineGrid[row][col] && !(row == startRow && col == startCol)) {
                mineGrid[row][col] = true;
                minesPlaced++;
            }
        }
    }
    private void revealEmptyCells(int row, int col) {
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE && !revealedGrid[row][col]) {
            revealedGrid[row][col] = true;
            int adjacentMines = countAdjacentMines(row, col);
            Button button = getCellButton(row, col);

            if (adjacentMines > 0) {
                button.setText(Integer.toString(adjacentMines));
                button.setTextFill(getTextColor(adjacentMines));
            } else {
                revealEmptyCells(row - 1, col);
                revealEmptyCells(row + 1, col);
                revealEmptyCells(row, col - 1);
                revealEmptyCells(row, col + 1);
            }
            button.setDisable(true);
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < GRID_SIZE && j >= 0 && j < GRID_SIZE && mineGrid[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    private Button getCellButton(int row, int col) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return (Button) node;
            }
        }
        return null;
    }

    private void revealMines() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (mineGrid[row][col]) {
                    revealedGrid[row][col] = true;
                }
            }
        }
    }

    private Color getTextColor(int adjacentMines) {
        switch (adjacentMines) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.RED;
            case 4:
                return Color.DARKBLUE;
            case 5:
                return Color.DARKRED;
            case 6:
                return Color.TEAL;
            case 7:
                return Color.BLACK;
            case 8:
                return Color.GRAY;
            default:
                return Color.BLACK;
        }
    }

    private void gameOver() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Button button = getCellButton(row, col);
                if (mineGrid[row][col]) {
                    button.setText("💣");
                    button.setTextFill(Color.BLACK);
                } else {
                    int adjacentMines = countAdjacentMines(row, col);
                    if (adjacentMines > 0) {
                        button.setText(Integer.toString(adjacentMines));
                        button.setTextFill(getTextColor(adjacentMines));
                    }
                }
                button.setDisable(true);
            }
        }
        statusLabel.setText("Game Over! Start New Game");
    }

    private void checkGameWin() {
        int revealedCells = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (revealedGrid[row][col]) {
                    revealedCells++;
                }
            }
        }

        if (revealedCells == GRID_SIZE * GRID_SIZE - NUM_MINES) {
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    Button button = getCellButton(row, col);
                    button.setDisable(true);
                }
            }
            statusLabel.setText("Congratulations! ");
        }
    }

    private void resetGame() {
        mineGrid = new boolean[GRID_SIZE][GRID_SIZE];
        revealedGrid = new boolean[GRID_SIZE][GRID_SIZE];
        flaggedGrid = new boolean[GRID_SIZE][GRID_SIZE];

        for (Node node : gridPane.getChildren()) {
            ((Button) node).setText("");
            ((Button) node).setTextFill(Color.BLACK);
            ((Button) node).setDisable(false);
        }

        nameTextField.setDisable(false);
        surnameTextField.setDisable(false);
        playerNameLabel.setText("Player: ");
        model = new MinefieldModel();
        updateScoreLabel();
        generateMinefield(-1, -1);
    }

    private void handlePlayerSubmit() {
        String name = nameTextField.getText().trim();
        String surname = surnameTextField.getText().trim();
        String playerName = name + " " + surname;
        playerNameLabel.setText("Player: " + playerName);
    }

    private void updateScoreLabel() {
        int score = model.getScore();
        scoreLabel.setText(Integer.toString(score));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
