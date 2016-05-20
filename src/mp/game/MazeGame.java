package mp.game;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import static mp.game.MazeGen.Pos;

import java.util.ArrayList;
import java.util.List;

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
        takenPos.add(treasure);
    }

    /** @return il Node che contiene il labirinto del gioco */
    public Node getNode() { return arena; }

    /** Aggiunge il giocatore dato al gioco
     * @param p  un giocatore */
    public void add(Player p) {
        Node u = p.getNode();
        if (u != null) {
            scale(u);
            arena.getChildren().add(u);
            Pos rp = maze.rndFreeCell(takenPos);
            move(u, rp);
            takenPos.add(rp);
        } else {
            // TODO  se il giocatore non ha una forma...
        }
        p.play();
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

    /** Sposta un Node nella posizione specificata del labirinto.
     * @param node  un Node
     * @param to  una posizione */
    private void move(Node node, Pos to) {
        Point2D p2D = maze.rectUpperLeft(to.row, to.col);
        double tx = node.getTranslateX(), ty = node.getTranslateY();
        double dx = p2D.getX() - node.getBoundsInParent().getMinX();
        double dy = p2D.getY() - node.getBoundsInParent().getMinY();
        node.setTranslateX(tx + dx);
        node.setTranslateY(ty + dy);
    }

    private final Pos treasure;
    private final List<Pos> takenPos = new ArrayList<>();
    private final int NR = 23, NC = 31, WALL = 6, CELL = 42, BORDER = 10;
    private final Maze maze;
    private final Group arena;      // L'arena di gioco per contenere il
}                                   // labirinto i giocatori
