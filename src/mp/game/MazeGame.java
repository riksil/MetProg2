package mp.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static mp.game.MazeGen.Pos;
import static mp.game.Player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Gestisce un gioco del tipo Find-Treasure */
public class MazeGame {
    public MazeGame() {
        maze = new Maze(NR, NC, WALL, CELL, BORDER);
        arena = new Group(maze.draw());
        Node tImg = scale(new ImageView(getClass()
                .getResource("treasure.png").toString()));
        arena.getChildren().add(tImg);
        treasure = maze.rndFreeCell(null);
        move(tImg, treasure);
        anim = new Timeline(new KeyFrame(new Duration(FRAME_MS), e -> update()));
        anim.setCycleCount(Timeline.INDEFINITE);
        anim.play();
    }

    public synchronized void quit() { quit = true; }

    /** @return il Node che contiene il labirinto del gioco */
    public Node getNode() { return arena; }

    /** Aggiunge il giocatore dato al gioco
     * @param p  un giocatore */
    public void add(Player p) {
        Node u = p.getNode();   // Il Node che rappresenta il nuovo giocatore
        if (u == null) {
            // TODO  se il giocatore non ha una forma...
        }
        scale(u);                       // Ridimensiona il Node del giocatore
        arena.getChildren().add(u);     // Lo aggiunge all'arena di gioco
        Set<Pos> pp = controls.stream().map(Control::getPos)    // Posizioni
                .collect(Collectors.toSet());      // correnti dei giocatori
        pp.add(treasure);                          // La posizione del tesoro
        Pos rp = maze.rndFreeCell(pp);  // Sceglie una posizione libera random
        move(u, rp);                    // in cui posizionare il giocatore
        Control c = new Control(this, p, u, rp);  // Crea il controllo per il
        controls.add(c);                          // nuovo giocatore e
        c.start();                                // inizia l'esecuzione
    }

    /** Aggiornamento effettuato ad ogni frame, eseguito nel JavaFX Thread */
    private synchronized void update() {
        Set<Pos> pp = controls.stream().map(Control::getPos)    // Posizioni
                .collect(Collectors.toSet());      // correnti dei giocatori
        Control winner = null;
        for (Control c : controls) {
            Dir d = c.consumeMove(); // La mossa comandata
            if (d == null) continue; // Se non ha comandato una mossa, ignoralo
            Pos p = c.getPos();  // Posizione corrente
            Pos q = p.go(d);     // la nuova posizione
            if (!pp.contains(q) && maze.pass(p, d)) {  // Se non c'è collisione
                move(c.getNode(), q); // Muove il giocatore nella nuova posizione
                pp.remove(p);         // Rimuove la vecchia posizione e
                pp.add(q);            // aggiunge quella nuova
                c.setState(State.GO, q);   // Aggiorna lo stato e la posizione
                if (q.equals(treasure)) winner = c;  // Ha trovato il tesoro
            } else              // Se è in collisione
                c.setState(State.COLLISION, null);
        }
        if (winner != null || quit) {  // Se c'è un vincitore o il gioco è chiuso,
            for (Control c : controls) // comunica la fine del gioco ai giocatori
                c.setState(c == winner ? State.WIN : State.GAME_OVER, null);
            anim.stop();       // Ferma l'esecuzione degli aggiornamenti
        }
        frameCounter++;   // Incrementa il conteggio dei frame
        // Notifica che il frame è stato eseguito. Ogni thread di giocatore
        // che era in attesa è risvegliato e riprenderà l'esecuzione non
        notifyAll();    // appena riotterrà il lock su questo oggetto.
    }

    /** Implementa la console di un giocatore */
    private static class Control implements Console {
        Control(MazeGame boss, Player player, Node pn, Pos p) {
            this.boss = boss;
            node = pn;
            pos = p;
            thread = new Thread(() -> player.play(this));    // Crea il thread
            thread.setDaemon(true);                   // dedicato al giocatore
        }

        void start() { thread.start(); }  // Inizia l'esecuzione del giocatore

        @Override
        public State move(Dir d) {
            synchronized (boss) {    // Sincronizza sul gestore del gioco
                if (State.GAME_OVER.equals(state) || State.WIN.equals(state))
                    return state;    // Se il gioco è terminato
                move = d;
                long frame = boss.frameCounter;       // Il frame corrente
                while (frame == boss.frameCounter) {  // Contro spurious wakeup
                    try {
                        // Aspetta per l'aggiornamento del frame corrente, il
                        // thread rilascia il lock su boss e rimane dormiente
                        boss.wait();  // fino a che è eseguito notifyAll su
                        // boss e il thread riprende il lock su boss.
                    } catch (InterruptedException e) { }
                }
                return state;
            }
        }

        Pos getPos() { return pos; }      // Ritorna la posizione corrente

        Node getNode() { return node; }   // Ritorna il Node del giocatore

        Player.Dir consumeMove() {  // Ritorna la mossa comandata e la consuma
            synchronized (boss) {
                Dir d = move;       // Salva la mossa comandata,
                move = null;        // la consuma e
                return d;           // la ritorna
            }
        }

        void setState(State s, Pos p) {    // Imposta lo stato, che sarà
            synchronized (boss) {          // ritornato dal metodo move,
                state = s;                 // e l'eventuale nuova
                if (p != null) pos = p;    // posizione
            }
        }

        private final MazeGame boss;    // Il gestore del gioco usato per
        private final Node node;        // sincronizzare le mosse dei giocatori
        private final Thread thread;    // Il thread del giocatore
        private volatile Pos pos;       // La posizione, la mossa e lo stato
        private volatile Dir move;      // correnti
        private volatile State state;
    }

    /** Scala le dimensioni del nodo per entrare nei passaggi del labirinto
     * @param u  un nodo
     * @return il nodo stesso */
    private Node scale(Node u) {
        double z = CELL/Math.max(u.getBoundsInParent().getWidth(),
                u.getBoundsInParent().getHeight());
        u.setScaleX(z);
        u.setScaleY(z);
        return u;
    }

    /** Sposta un Node nella posizione specificata del labirinto con una
     * transizione per rendere fluido il movimento.
     * @param node  un Node
     * @param to  una posizione */
    private void move(Node node, Pos to) {
        Point2D p2D = maze.rectUpperLeft(to.row, to.col);
        double tx = node.getTranslateX(), ty = node.getTranslateY();
        double dx = p2D.getX()- node.getBoundsInParent().getMinX();
        double dy = p2D.getY()- node.getBoundsInParent().getMinY();
        TranslateTransition t = new TranslateTransition(Duration.millis(FRAME_MS),node);
        t.setToX(tx+dx);
        t.setToY(ty+dy);
        t.play();
    }

    private volatile boolean quit = false;
    private final long FRAME_MS = 40;      // Durata in millisecondi di un frame
    private final Timeline anim;    // Esecutore periodico per gli aggiornamenti
    private final List<Control> controls = new ArrayList<>(); // I controlli dei giocatori
    private volatile long frameCounter;    // Contatore dei frame
    private final Pos treasure;
    private final List<Pos> takenPos = new ArrayList<>();
    private final int NR = 23, NC = 31, WALL = 6, CELL = 42, BORDER = 10;
    private final Maze maze;
    private final Group arena;      // L'arena di gioco per contenere il
}                                   // labirinto i giocatori
