package mp.game;

import javafx.scene.Node;

/** Interfaccia che deve essere implementata da un giocatore per il
 * gioco Find-Treasure */
public interface Player {
    /** Invocato solamente quando inizia il gioco */
    void play();

    /** Ritorna eventualmente un nodo che rappresenta il giocatore.
     * @return il nodo del giocatore o null */
    default Node getNode() { return null; }
}
