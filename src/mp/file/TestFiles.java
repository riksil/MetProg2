package mp.file;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import static java.lang.System.out;

import static mp.util.Utils.getUsedMem;
import static mp.util.Utils.getPeakMem;
import static mp.util.Utils.resetPeakMem;
import static mp.util.Utils.toGMKB;
import static mp.util.Utils.q;
import static mp.util.Utils.randSample;


/**  Una classe per fare test sui file */
public class TestFiles {
    public static void main(String[] args) {
        //info();
        //test_fileTreeToString();
        Path dir = Paths.get("/usr");
        //test_ts("totalSize", Utils::totalSize, dir, 10);
        //test_ts("totalSizeNR", Utils::totalSizeNR, dir, 10);
        //test_ts("totalSizeNaiveConcur FixedThreadPool 500", Utils::totalSizeNaiveConcur, dir, 10);
        //test_ts("totalSizeNaiveConcur CachedThreadPool", Utils::totalSizeNaiveConcur, dir, 10);
        //test_ts("totalSizeNaiveConcur WorkStealingPool", Utils::totalSizeNaiveConcur, dir, 10);
        //test_ts("totalSizeConcur", Utils::totalSizeConcur, dir, 10);
        //test_ts("totalSizeQueue", Utils::totalSizeQueue, dir, 10);
        test_ts("totalSizeForkJoin", Utils::totalSizeForkJoin, dir, 10);
    }

    /** Mette alla prova un metodo che preso in input il percorso di una directory
     * ritorna la somma di tutti i byte dei file regolari contenuti nella
     * directory. Stampa il valore ritornato, il minimo, il massimo e la media dei
     * tempi di esecuzione delle n invocazioni. Inoltre, stampa i picchi del numero
     * di thread addizionali usati e della memoria addizionale usata.
     * @param name  nome del metodo
     * @param ts  il metodo
     * @param p  percorso della directory
     * @param n  numero di volte che il metodo è invocato */
    public static void test_ts(String name, Function<Path,Long> ts, Path p, int n) {
        out.println(name+"  Directory: "+p);
        long max = 0, min = -1, size = 0;
        double average = 0;
        ThreadMXBean tm = ManagementFactory.getThreadMXBean();
        int nt = tm.getThreadCount();   // Numero attuale di thread
        tm.resetPeakThreadCount();
        long mem = getUsedMem();  // Memoria attualmente usata
        resetPeakMem();
        try {
            for (int i = 0; i < n; i++) {    // Invoca il metodo n volte
                long time = System.currentTimeMillis();
                size = ts.apply(p);          // Invoca il metodo
                time = System.currentTimeMillis() - time;
                if (time > max) max = time;
                if (min == -1 || time < min) min = time;
                average += time;
            }
        } catch (Exception ex) { out.println(ex); }
        nt = tm.getPeakThreadCount() - nt;  // Picco numero thread addizionali
        mem = getPeakMem() - mem;     // Picco memoria addizionale
        average /= n;
        out.println(String.format("Size: %s  Time (seconds): min = %.2f "+
                        " max = %.2f ave = %.2f",
                toGMKB(size), min/1000.0, max/1000.0, average/1000.0));
        out.println("Picco numero thread addizionali: "+nt);
        out.println("Picco memoria addizionale: " + toGMKB(mem));
    }

    /** Prova alcuni metodi di {@link java.nio.file.Files} chiedendo un percorso
     * da tastiera e controllando se esiste, se è una directory, ecc. */
    private static void info() {
        Scanner in = new Scanner(System.in);
        out.println("Digita un percorso: ");
        String s = in.nextLine();
        Path p = Paths.get(s);
        p = p.toAbsolutePath();
        out.println("Absolute path: "+p);
        boolean exist = Files.exists(p);
        out.println("Exist? "+exist);
        if (exist) {
            out.println("Directory? "+Files.isDirectory(p));
            out.println("Regular File? "+Files.isRegularFile(p));
            out.println("Link? "+Files.isSymbolicLink(p));
            try {
                infoContent(p);
            } catch(IOException ex) { out.println(ex); }
        }
    }

    /** Se il percorso dato porta a un file regolare con estensione ".txt", stampa
     * il numero di linee del file e anche al più 5 linee random.
     * @param p  il percorso del file
     * @throws IOException se la lettura del file va in errore */
    private static void infoContent(Path p) throws IOException {
        if (!Files.isRegularFile(p) || !p.toString().endsWith(".txt"))
            return;
        List<String> lines = Files.readAllLines(p);
        int n = lines.size();
        out.println("Numero linee: "+n);
        for (int i = 0 ; i < 5 ; i++) {
            int j = (int)Math.floor(Math.random()*n);    // Sceglie una linea random
            out.println(q(lines.get(j)));
        }
    }

    private static void test_fileTreeToString() {
        Scanner input = new Scanner(System.in);
        out.println("Test fileTreeToString: Digita un pathname: ");
        String pathname = input.nextLine();
        Path root = Paths.get(pathname).toAbsolutePath();
        try {
            out.println(Utils.fileTreeToString(root));
        } catch(IOException e) { out.println(e); }
    }

    /** Chiede all'utente di digitare il percorso di un file di testo e il nome
     * di un charset per decodificarne i caratteri e stampa il numero di parole
     * distinte nel file e un campione di al più 10 parole con i relativi
     * conteggi. */
    private static void test_wordMap() {
        Scanner in = new Scanner(System.in);
        out.println("Digita il percorso di un file: ");
        String s = in.nextLine();
        out.println("Digita un charset: ");
        String cs = in.nextLine();
        Path p = Paths.get(s);
        try {
            Map<String,Integer> words = Utils.wordMap(p, cs);
            out.println("Numero di parole: "+words.size());
            out.println("Campione: " + randSample(words, 10));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
