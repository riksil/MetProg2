package mp.game;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static mp.game.MazeGen.*;
import static mp.game.Player.Dir;

import java.util.Collection;
import java.util.Random;

/** Un labirinto */
public class Maze {
    /** Crea un labirinto con le date dimensioni (vedi
     * {@link MazeGen#genMaze(int, int)}).
     * @param nr  numero righe (intero dispari)
     * @param nc  numero colonne (intero dispari)
     * @param wW  larghezza muri
     * @param cW  larghezza celle (o passaggi)
     * @param bW  larghezza bordi */
    public Maze(int nr, int nc, int wW, int cW, int bW) {
        maze = MazeGen.genMaze(nr, nc);
        this.nr = nr;
        this.nc = nc;
        wallW = wW;
        cellW = cW;
        bordW = bW;
        wallP = Color.SADDLEBROWN;
        cellP = Color.FLORALWHITE;
        bordP = Color.BLACK;
    }

    /** @return il Node che contiene l'immagine del labirinto */
    public Node draw() {
        int width = 2*bordW + ((nc - 2)/2)*(wallW + cellW)+cellW;
        int height = 2*bordW + ((nr - 2)/2)*(wallW + cellW)+cellW;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(wallP);
        gc.fillRect(0, 0, width, height);
        gc.setFill(bordP);
        gc.fillRect(0, 0, width, bordW);
        gc.fillRect(0, height - bordW, width, bordW);
        gc.fillRect(0, 0, bordW, height);
        gc.fillRect(width - bordW, 0, bordW, height);
        gc.setFill(cellP);
        for (int r = 0 ; r < nr ; r++)
            for (int c = 0 ; c < nc ; c++)
                if (maze[r][c]) {
                    Point2D p = rectUpperLeft(r, c);
                    Dimension2D d = rectSize(r, c);
                    gc.fillRect(p.getX(), p.getY(), d.getWidth(), d.getHeight());
                }
        return canvas;
    }

    /** Ritorna true se la posizione della cella a cui si arriva dalla cella p
     * con un passo nella direzione d è libera e il passaggio tra le due celle
     * è aperto, altrimenti ritorna false.
     * @param p  la posizione di una cella
     * @param d  una direzione
     * @return true se si può andare dalla cella in posizione p alla cella
     * adiacente nella direzione d */
    public boolean pass(Pos p, Dir d) {
        Pos q = p.go(d);
        if (!inside(q.row, q.col, nr, nc) || !maze[q.row][q.col]) return false;
        int r = passI(p.row, q.row), c = passI(p.col, q.col);
        return maze[r][c];
    }

    /** Ritorna la posizione di una cella scelta in modo random tra quelle
     * libere e la cui posizione non è tra quelle date.
     * @param taken  una collezione di posizioni di celle o null
     * @return la poszione di una cella libera random */
    public Pos rndFreeCell(Collection<Pos> taken) {
        Random rnd = new Random();
        for (int t = 0; t < 100; t++) {
            Pos p = new Pos(2 * rnd.nextInt(nr / 2) + 1, 2 * rnd.nextInt(nc / 2) + 1);
            if (maze[p.row][p.col] && (taken == null || !taken.contains(p)))
                return p;
        }
        return null;
    }

    /** Ritorna le coordinate rispetto all'immagine del labirinto dell'angolo
     * superiore sinistro del rettangolo con la riga e la colonna specificate.
     * @param r numero di riga
     * @param c numero di colonna
     * @return le coordinate dell'angolo superiore sinistro del rettangolo */
    public Point2D rectUpperLeft(int r, int c) {
        int x = (c > 0 ? bordW + (c/2)*(cellW+wallW) + (c % 2 == 0 ? -wallW : 0) : 0);
        int y = (r > 0 ? bordW + (r/2)*(cellW+wallW) + (r % 2 == 0 ? -wallW : 0) : 0);
        return new Point2D(x, y);
    }

    /** Ritorna le dimensioni del rettangolo del labirinto con le specificate
     * riga e colonna.
     * @param r  numero di riga
     * @param c  numero di colonna
     * @return le dimensioni del rettangolo con le date riga e colonna */
    private Dimension2D rectSize(int r, int c) {
        int w, h;
        if (r == 0 || r == nr - 1) h = bordW;
        else if (r % 2 == 1) h = cellW;
        else h = wallW;
        if (c == 0 || c == nc - 1) w = bordW;
        else if (c % 2 == 1) w = cellW;
        else w = wallW;
        return new Dimension2D(w, h);
    }

    private final boolean[][] maze;
    private final int nr, nc, wallW, cellW, bordW;
    private final Paint wallP, cellP, bordP;
}