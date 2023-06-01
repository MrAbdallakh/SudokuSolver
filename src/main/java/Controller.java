import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class Controller implements Initializable {

    @FXML
    private AnchorPane pane;

    @FXML
    private Button buttonSolve, buttonReset;

    private final String HOVERED_BUTTON_STYLE = "-fx-background-color: #000; -fx-background-radius: 30;";
    private final String IDLE_BUTTON_STYLE = "-fx-background-color: #282828; -fx-background-radius: 30;";
    private final int SIZE = 9;
    private TextField[][] sudoku = new TextField[SIZE][SIZE];

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.buttonSolve.setOnMouseEntered(e -> this.buttonSolve.setStyle(HOVERED_BUTTON_STYLE));
        this.buttonSolve.setOnMouseExited(e -> this.buttonSolve.setStyle(IDLE_BUTTON_STYLE));

        this.buttonReset.setOnMouseEntered(e -> this.buttonReset.setStyle(HOVERED_BUTTON_STYLE));
        this.buttonReset.setOnMouseExited(e -> this.buttonReset.setStyle(IDLE_BUTTON_STYLE));

        // ======================================================================
        // Create Sudoku Field
        //
        for (int indexY = 0, y = 60; indexY < SIZE; indexY++, y += 45) {
            for (int indexX = 0, x = 15; indexX < SIZE; indexX++, x += 50) {

                this.sudoku[indexY][indexX] = new TextField();
                this.sudoku[indexY][indexX].setStyle("-fx-background-color:#282828; -fx-text-fill:#fff");
                this.sudoku[indexY][indexX].setFont(Font.font("Arial", FontWeight.BOLD, 24));
                this.sudoku[indexY][indexX].setPrefHeight(40);
                this.sudoku[indexY][indexX].setPrefWidth(45);
                this.sudoku[indexY][indexX].setLayoutX(x);
                this.sudoku[indexY][indexX].setLayoutY(y);

                this.sudoku[indexY][indexX].setTextFormatter(new TextFormatter<String>((Change change) -> {
                    String newText = change.getControlNewText();

                    if (newText.length() > 1 || !newText.matches("\\d*") || newText.equals("0")) {
                        return null;
                    }

                    return change;
                }));

                if ((indexX + 1) % 3 == 0) {
                    x += 5;
                }

                pane.getChildren().add(sudoku[indexY][indexX]);
            }

            if ((indexY + 1) % 3 == 0) {
                y += 5;
            }
        }
    }

    @FXML
    void onActionReset() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                this.sudoku[y][x].setText("");
            }
        }
    }

    @FXML
    void onActionSolve() {

        if (!isEmptyField()) {
            showErrorMessage("There is no empty field!", "Can not solve because there is not empty field!");
            return;
        }

        if (!isSolveable()) {
            showErrorMessage("Sudoku error!", "The Sudoku is not solveable!");
            return;
        }

        this.buttonSolve.setOnMouseEntered(e -> this.buttonSolve.setStyle(IDLE_BUTTON_STYLE));
        this.buttonReset.setOnMouseEntered(e -> this.buttonReset.setStyle(IDLE_BUTTON_STYLE));
        this.buttonSolve.setDisable(true);
        this.buttonReset.setDisable(true);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(100), event -> {

                    // ======================================================================
                    // Create a second field with the values
                    //
                    int[][] arr = new int[SIZE][SIZE];
                    boolean goUp = false;

                    for (int i = 0; i < SIZE; i++) {
                        for (int j = 0; j < SIZE; j++) {
                            if (this.sudoku[i][j].getText().isEmpty()) {
                                arr[i][j] = 0;
                            } else {
                                arr[i][j] = Integer.parseInt(this.sudoku[i][j].getText());
                            }
                        }
                    }

                    // ======================================================================
                    // Sudoku Solver Algorithm
                    //
                    for (int indexY = 0, temp = 1; indexY < SIZE; indexY++) {
                        for (int indexX = 0; indexX < SIZE; indexX++) {

                            // ======================================================================
                            // Goes back/up to the row
                            //
                            if (goUp) {
                                goUp = false;
                                indexX = SIZE;
                                indexY -= 2;

                                while (true) {
                                    indexX--;

                                    if (arr[indexY][indexX] == 0) {
                                        temp = Integer.valueOf(this.sudoku[indexY][indexX].getText()) + 1;
                                        this.sudoku[indexY][indexX].setText(String.valueOf(""));

                                        if (temp == 10) {
                                            continue;
                                        }

                                        indexX--;
                                        break;
                                    }
                                }
                                continue;
                            }

                            // ======================================================================
                            // Try many times that the value is correct, when no value is correct
                            // it goes one step back.
                            //
                            if (arr[indexY][indexX] == 0) {

                                this.sudoku[indexY][indexX].setText(String.valueOf(temp));

                                if (isDuplicateNumberRow(indexY, indexX) || isDuplicateNumberColumn(indexY, indexX)
                                        || isDuplicateNumberBlock(indexY, indexX)) {

                                    if (temp == 9) {
                                        this.sudoku[indexY][indexX].setText(String.valueOf(""));

                                        while (true) {
                                            indexX--;

                                            // It goes one step back/up to the row
                                            if (indexX < 0) {
                                                goUp = true;
                                                break;
                                            }

                                            if (arr[indexY][indexX] == 0) {
                                                temp = Integer.valueOf(this.sudoku[indexY][indexX].getText()) + 1;
                                                this.sudoku[indexY][indexX].setText(String.valueOf(""));

                                                if (temp > 9) {
                                                    continue;
                                                }

                                                indexX--; // one less number because loop does one more number
                                                break;
                                            }
                                        }

                                        if (goUp) {
                                            break;
                                        }

                                    } else {
                                        temp++;
                                        indexX--; // one less number because loop does one more number
                                        continue;
                                    }
                                } else {
                                    temp = 1;
                                }
                            }
                        }
                    }

                    this.buttonSolve.setOnMouseEntered(e -> this.buttonSolve.setStyle(HOVERED_BUTTON_STYLE));
                    this.buttonReset.setOnMouseEntered(e -> this.buttonReset.setStyle(HOVERED_BUTTON_STYLE));
                    this.buttonSolve.setDisable(false);
                    this.buttonReset.setDisable(false);
                }));

        timeline.play();
    }

    private boolean isEmptyField() {

        for (int indexY = 0; indexY < SIZE; indexY++) {
            for (int indexX = 0; indexX < SIZE; indexX++) {

                if (this.sudoku[indexY][indexX].getText().isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isSolveable() {

        for (int indexY = 0; indexY < SIZE; indexY++) {
            for (int indexX = 0; indexX < SIZE; indexX++) {

                if (!this.sudoku[indexY][indexX].getText().isEmpty() && (isDuplicateNumberRow(indexY, indexX)
                        || isDuplicateNumberColumn(indexY, indexX) || isDuplicateNumberBlock(indexY, indexX))) {
                    return false;
                }

            }
        }

        return true;
    }

    private boolean isDuplicateNumberRow(int y, int x) {

        for (int indexX = 0; indexX < SIZE; indexX++) {

            if (x == indexX) {
                indexX++;

                if (indexX == 9) {
                    break;
                }
            }

            if (this.sudoku[y][x].getText().equals(this.sudoku[y][indexX].getText())) {
                return true;
            }
        }

        return false;
    }

    private boolean isDuplicateNumberColumn(int y, int x) {

        for (int indexY = 0; indexY < SIZE; indexY++) {

            if (y == indexY) {
                indexY++;

                if (indexY == 9) {
                    break;
                }
            }

            if (this.sudoku[y][x].getText().equals(this.sudoku[indexY][x].getText())) {
                return true;
            }
        }

        return false;
    }

    private boolean isDuplicateNumberBlock(int y, int x) {
        int minX, maxX, minY, maxY;

        if (x < 3) {
            minX = 0;
            maxX = 3;
        } else if (x > 5) {
            minX = 6;
            maxX = 9;
        } else {
            minX = 3;
            maxX = 6;
        }

        if (y < 3) {
            minY = 0;
            maxY = 3;
        } else if (y > 5) {
            minY = 6;
            maxY = 9;
        } else {
            minY = 3;
            maxY = 6;
        }

        for (int tempY = minY; tempY < maxY; tempY++) {
            for (int tempX = minX; tempX < maxX; tempX++) {

                if (tempX == x && tempY == y) {
                    tempX++;

                    if (tempX == maxX) {
                        break;
                    }
                }

                if (this.sudoku[tempY][tempX].getText().equals(this.sudoku[y][x].getText())) {
                    return true;
                }
            }
        }

        return false;
    }

    private void showErrorMessage(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}