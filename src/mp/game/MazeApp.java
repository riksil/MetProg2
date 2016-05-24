package mp.game;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

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
        Menu addPlayer = new Menu("Add Player");
        MenuItem mi = new MenuItem("HPlayer");
        mi.setOnAction(e -> game.add(new HPlayer()));
        addPlayer.getItems().add(mi);
        mi = new MenuItem("RPlayer");
        mi.setOnAction(e -> game.add(new RPlayer()));
        addPlayer.getItems().add(mi);
        // Permette di caricare un nuovo giocatore scegliendo un class file con
        // l'implementazione di un giocatore (cioè, una classe che implementa
        // PlayerC). Si assume che la classe appartenga al package mp.game.
        MenuItem addCPlayer = new MenuItem("Add Player...");
        addCPlayer.setOnAction(e -> addCPlayer());
        Menu menu = new Menu("Game", null, newGame, addPlayer, addCPlayer);
        MenuBar mBar = new MenuBar(menu);  // Aggiunge il menu alla barra
        mBar.setUseSystemMenuBar(true);    // Imposta la barra di menu
        area = new Group(mBar);
        newGame();             // Il gioco iniziale
        return area;
    }

    /** Permette all'utente di scegliere un class file che implementa un
     * giocatore e di aggiungerlo al gioco. Assume che la classe appartenga a
     * un package mp.game e che abbia un costruttore senza parametri. */
    private void addCPlayer() {
        Window owner = area.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Player Class");
        fc.getExtensionFilters().add(new FileChooser
                .ExtensionFilter("Class Files", "*.class"));
        File selectedFile = fc.showOpenDialog(owner);
        if (selectedFile == null) return;
        try {
            String fn = selectedFile.getName();
            String className = "mp.game." + fn.substring(0, fn.indexOf("."));
            URL url = selectedFile.getParentFile().getParentFile()
                    .getParentFile().toURI().toURL();
            // Crea un loader di class file che usa lo specificato URL per
            // trovare la classe da caricare e le eventuali altre classi o
            // risorse. L'URL specificato deve puntare alla directory che
            // contiene la directory del package di base della classe che
            // si vuole caricare.
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url});
            // Usa il caricatore di class file per caricare il class file
            // scelto dall'utente. È necessario che il class file sia
            // effettivamente un'implementazione della classe PlayerC.
            Class<? extends Player> playerClass = classLoader
                    .loadClass(className).asSubclass(PlayerC.class);
            if (game != null)
                game.add(playerClass.newInstance());
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Crea e inizia un nuovo gioco. Se c'era un gioco in esecuzione lo termina */
    private void newGame() {
        if (game != null) {
            game.quit();
            area.getChildren().remove(game.getNode());
        }
        game = new MazeGame();
        area.getChildren().add(game.getNode());
    }

    private Group area;      // Area in cui saranno visualizzati i giochi
    private MazeGame game;   // Il gioco corrente
}
