package mp.game;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/** App per un gioco Find-Treasure: ricerca di un tesoro in un labirinto */
public class MazeApp extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = createGUI();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Find Treasure");
        primaryStage.show();
    }

    /** Crea la GUI e ne ritorna il nodo radice */
    private Parent createGUI() {
        MenuItem newGame = new MenuItem("New Game");     // Crea un nuovo gioco
        newGame.setOnAction(e -> newGame());
        MenuItem addPlayer = new MenuItem("Add Player"); // Aggiunge un giocatore
        addPlayer.setOnAction(e -> game.add(new HPlayer()));
        Menu game = new Menu("Game",null,newGame,addPlayer);
        MenuBar mBar = new MenuBar(game);  // Aggiunge il menu alla barra
        mBar.setUseSystemMenuBar(true);    // Imposta la barra di menu
        area = new Group(mBar);
        newGame();             // Il gioco iniziale
        return area;
    }

    /** Crea e inizia un nuovo gioco */
    private void newGame() {
        if (game != null)
            area.getChildren().remove(game.getNode());
        game = new MazeGame();
        area.getChildren().add(game.getNode());
    }

    private Group area;      // Area in cui saranno visualizzati i giochi
    private MazeGame game;   // Il gioco corrente
}
