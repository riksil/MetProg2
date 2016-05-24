package mp.game;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

/** Un giocatore per il gioco Find-Treasure che può essere controllato sia con
 * il mouse che con i tasti freccia. */
public class HPlayer implements Player {
    public HPlayer() {
        shape = new ImageView(getClass().getResource("smiley.png").toString());
    }

    @Override
    public Node getNode() { return shape; }

    @Override
    public void play(Console c) {
        Platform.runLater(() -> {
            shape.setOnMousePressed(e -> {
                x = e.getSceneX();     // Registra la posizione attuale del mouse
                y = e.getSceneY();
                disp.reset();          // Lo spostamento è inizializzato a zero
                shape.requestFocus();  // Richiede il focus per i tasti
            });
            shape.setOnMouseDragged(e -> {        // La nuova posizione del mouse
                double mx = e.getSceneX(), my = e.getSceneY();
                disp.update(mx - x, my - y);  // Aggiorna lo spostamento corrente
                x = mx;                  // Registra la nuova posizione del mouse
                y = my;
            });
            shape.setOnKeyPressed(e -> key = e.getCode());
            length = Math.max(shape.getBoundsInParent().getWidth(),
                    shape.getBoundsInParent().getHeight());
        });
        while (true) {
            State s = null;
            if (disp.gte(length)) {     // Se lo spostamento supera la unghezza
                Dir d = disp.getDir();     // della forma, calcola la direzione
                s = c.move(d);
                switch (s) {            // Aggiorna lo spostamento dopo la mossa
                    case GO: disp.update(d, length); break;
                    case COLLISION: disp.reset(); break;
                }
            } else {          // Se non c'è movimento con il mouse, controlla se
                try {         // è premuto un tasto freccia
                    Dir d = Dir.valueOf(key.toString());  // I tasti freccia
                    s = c.move(d);                        // hanno i nomi delle
                    key = null;                           // direzioni
                } catch (Exception e) {}
            }
            if (State.GAME_OVER.equals(s) || State.WIN.equals(s))
                break;
        }
    }

    private static class Disp {       // Gestisce lo spostamento del mouse
        synchronized void reset() { dx = 0; dy = 0; }
        synchronized void update(double sx, double sy) { dx += sx; dy += sy; }
        synchronized void update(Dir d, double len) {
            switch (d) {
                case UP: dy += len; dx = 0; break;
                case RIGHT: dx -= len; dy = 0; break;
                case DOWN: dy -= len; dx = 0; break;
                case LEFT: dx += len; dy = 0; break;
            }
        }
        synchronized boolean gte(double len) {  // Ritorna true se una delle due
            return Math.abs(dx) >= len || Math.abs(dy) >= len;     // componenti
        }                                           // è maggiore o uguale a len
        synchronized Dir getDir() {      // Ritorna la direzione che corrisponde
            double[] dd = {-dy, dx, dy, -dx};       // allo spostamento, cioè la
            int indMax = 0;                       // direzione lungo la quale lo
            for (int i = 0 ; i < dd.length ; i++)   // spostamento ha la massima
                if (dd[i] > dd[indMax]) indMax = i;                // proiezione
            return Dir.values()[indMax];
        }

        private volatile double dx = 0, dy = 0;
    }

    private final Node shape;        // La forma del giocatore
    private volatile double x, y;    // L'ultima posizione del mouse
    private volatile double length;  // Lunghezza della forma nel labirinto
    private final Disp disp = new Disp();  // Lo spostamento del mouse
    private volatile KeyCode key;    // L'eventuale tasto premuto
}