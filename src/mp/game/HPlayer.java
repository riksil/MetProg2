package mp.game;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

/** Un giocatore per il gioco Find-Treasure che è comandato da eventi di
 * input come i movimenti del mouse. */
public class HPlayer implements Player {
    public HPlayer() {
        shape = new ImageView(getClass().getResource("smiley.png").toString());
    }

    @Override
    public void play() {
        shape.setOnMousePressed(e -> {
            x = e.getSceneX();     // Registra la posizione attuale del mouse
            y = e.getSceneY();
        });
        shape.setOnMouseDragged(e -> {        // La nuova posizione del mouse
            double mx = e.getSceneX(), my = e.getSceneY();
            double tx = shape.getTranslateX(), ty = shape.getTranslateY();
                // Aggiorna la posizione del giocatore tramite lo spostamento
            shape.setTranslateX(tx + mx - x);                    // del mouse
            shape.setTranslateY(ty + my - y);
            x = mx;                  // Registra la nuova posizione del mouse
            y = my;
        });
    }

    @Override
    public Node getNode() { return shape; }

    private final Node shape;     // La forma del giocatore
    private double x, y;          // Mantiene l'ultima posizione del mouse
}
