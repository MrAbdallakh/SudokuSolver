import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("SudokuSolver.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Sudoku Solver");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}