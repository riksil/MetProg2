package mp.game;

import javafx.scene.Node;

/** Interfaccia che deve essere implementata da un giocatore di Find-Treasure */
public interface Player {
    /** Le quattro direzioni di movimento */
    enum Dir { UP, RIGHT, DOWN, LEFT }

    /** Lo stato del gioco nel frame in cui il giocatore comanda una mossa.
     * Quando lo stato diventa {@link State#GAME_OVER} o {@link State#WIN},
     * il giocatore deve terminare la propria esecuzione. */
    enum State {
        /** Mossa eseguita, il gioco continua */
        GO,
        /** Mossa non eseguita causa collisione, il gioco continua */
        COLLISION,
        /** Gioco terminato e il giocatore non ha vinto */
        GAME_OVER,
        /** Gioco terminato con la vittoria del giocatore */
        WIN
    }

    /** Il tipo dell'oggetto per comandare le mosse che è passato al giocatore
     * quando inizia il gioco */
    interface Console {
        /** Comanda la mossa di muoversi nella direzione data e ritorna lo stato
         * del gioco del frame corrente. È bloccante fino al completo
         * aggiornamento del frame corrente.
         * @param d  una direzione
         * @return lo stato del gioco del frame corrente */
        State move(Dir d);
    }

    /** Invocato solamente quando inizia il gioco in un thread dedicato. Il
     * giocatore decide le sue mosse in questo metodo tramite un ciclo che
     * deve interrompersi non appena il comando di una mossa ritorna uno
     * {@link State} che segnala la fine del gioco.
     * @param c  l'oggetto per comandare le mosse */
    void play(Console c);

    /** @return un nodo che rappresenta il giocatore o null */
    default Node getNode() { return null; }
}
