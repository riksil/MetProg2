package mp.game;

import java.util.*;

import static mp.game.Player.Dir;

/** Classe di utilità per la costruzione di labirinti */
public class MazeGen {
    /** Una posizione all'interno di un labirinto */
    public static class Pos {
        public final int row, col;  // Riga e colonna della posizione

        /** Crea una posizione con la riga e la colonna specificate.
         * @param r  numero di riga
         * @param c  numero di colonna */
        public Pos(int r, int c) {
            row = r;
            col = c;
        }

        /** La posizione della cella in cui si arriva se da questa posizione ci
         * si muove di un passo nella direzione specificata. Si assume che
         * questa posizione sia quella di una cella.
         * @param d  una direzione
         * @return la posizione della cella di arrivo */
        public Pos go(Dir d) {
            for (int i = 0 ; i < 4 ; i++)
                if (Dir.values()[i].equals(d))
                    return new Pos(movR(row, i), movC(col, i));
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            return row == ((Pos)o).row && col == ((Pos)o).col;
        }

        @Override
        public int hashCode() { return Objects.hash(row, col); }
    }

    /** Ritorna un labirinto con le specificate dimensioni. Il labirinto è
     * rappresentato con una matrice di boolean. Ogni elemento della matrice
     * corrisponde a un rettangolo identificato dalle coordinate di riga e
     * colonna e che può essere di uno dei seguenti tre tipi: bordo, muro e
     * cella. La matrice ritornata m è tale che m[r][c] è true se e solo se il
     * rettangolo di coordinate (r, c) è percorribile (o libero). Tutte le celle
     * sono percorribili. La disposizione iniziale dei vari tipi di rettangoli è
     * la seguente:
     * <pre>
     *     B B B B B . . . B B B B
     *     B C W C W . . . C W C B
     *     B W W W W . . . W W W B
     *     B C W C W . . . C W C B
     *     B W W W W . . . W W W B
     *     B C W C W . . . C W C B
     *     . . . . . . . . . . . .
     *     B W W W W . . . W W W B
     *     B C W C W . . . C W C B
     *     B B B B B . . . B B B B
     * </pre>
     * Dove B è un bordo, W un muro e C una cella. Per rispettare questo schema
     * sia il numero di righe che quello delle colonne deve essere un intero
     * dispari. Il labirinto è costruito tramite una visita in profondità (DFS)
     * random. Più precisamente, la visita inizia in una cella random e ad ogni
     * passo sceglie in modo random una delle celle vicine e se non è stata
     * ancora visitata apre il muro tra le due celle e continua la visita da
     * quella cella. I labirinti costruiti in questo modo non hanno cicli.
     * @param nr  numero righe (intero dispari)
     * @param nc  numero colonne (intero dispari)
     * @return  un labirinto con le specificate dimensioni */
    public static boolean[][] genMaze(int nr, int nc) {
        boolean[][] maze = new boolean[nr][nc];
        class DFS {
            void visit(int r, int c) {
                maze[r][c] = true;
                int[] ind = rndIndices(4);
                for (int h = 0 ; h < 4 ; h++) {
                    int rr = movR(r, ind[h]), cc = movC(c, ind[h]);
                    if (inside(rr, cc, nr, nc) && !maze[rr][cc]) {
                        maze[passI(r, rr)][passI(c, cc)] = true;
                        new DFS().visit(rr, cc);
                    }
                }
            }
        }
        int row = 2*RND.nextInt(nr/2)+1, col = 2*RND.nextInt(nc/2)+1;
        new DFS().visit(row, col);
        return maze;
    }

    /** Ritorna la coordinata del rettangolo di passaggio tra due posizioni
     * adiacenti
     * @param k1  prima posizione
     * @param k2  seconda posizione
     * @return la coordinata del rettangolo di passaggio tra le due posizioni */
    static int passI(int k1, int k2) { return k1 + (k2 - k1)/2; }

    /** Ritorna la riga della cella adiacente in una data direzione
     * @param r  una coordinata di riga
     * @param i  una direzione
     * @return la riga della cella adiacente in una data direzione */
    static int movR(int r, int i) { return r + moves[i][0]; }

    /** Ritorna la colonna della cella adiacente in una data direzione
     * @param c  una coordinaya di colonna
     * @param i  una direzione
     * @return la colonna della cella adiacente in una data direzione */
    static int movC(int c, int i) { return c + moves[i][1]; }

    /** Ritorna true se le coordinate specificate sono all'interno del
     * labirinto di dimensioni date.
     * @param r  riga
     * @param c  colonna
     * @param nr  numero righe
     * @param nc  numero colonne
     * @return true se le coordinate specificate sono all'interno del
     * labirinto di dimensioni date */
    static boolean inside(int r, int c, int nr, int nc) {
        return r > 0 && r < nr && c > 0 && c < nc;
    }

    /** Ritorna un array di interi di lunghezza n che contiene gli indici
     * 0,1,...n-1 disposti in modo random.
     * @param n  la lunghezza dell'array
     * @return un array contenente gli indici 0,1,...n-1 in ordine random */
    private static int[] rndIndices(int n) {
        int[] indices = new int[n];
        List<Integer> indList = new ArrayList<>();
        for (int i = 0 ; i < n ; i++) indList.add(i);
        for (int i = 0 ; i < n ; i++)
            indices[i] = indList.remove(RND.nextInt(indList.size()));
        return indices;
    }

    private static final int[][] moves = {{-2, 0}, {0, 2}, {2, 0}, {0, -2}};
    private static final Random RND = new Random();
}