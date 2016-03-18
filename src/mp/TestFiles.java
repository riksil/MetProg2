package mp;

import mp.util.Utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.out;

/**  Una classe per fare test sui file */
public class TestFiles {

    public static void main(String[] args) {
        //info();
        test_wordMap();

    }

    /** Prova alcuni metodi di {@link java.nio.file.Files} chiedendo un percorso
     * da tastiera e controllando se esiste, se è una directory, ecc. */
    public static void info() {
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
    public static void infoContent(Path p) throws IOException {
        if (!Files.isRegularFile(p) || !p.toString().endsWith(".txt"))
            return;
        List<String> lines = Files.readAllLines(p);
        int n = lines.size();
        out.println("Numero linee: "+n);
        for (int i = 0 ; i < 5 ; i++) {
            int j = (int)Math.floor(Math.random()*n);    // Sceglie una linea random
            out.println(Utils.q(lines.get(j)));
        }
    }

    /** Ritorna una mappa che ad ogni parola del file specificato associa il
     * numero di occorrenze. Per parola si intende una sequenza di lettere
     * (riconosciute dal metodo {@link java.lang.Character#isLetter(char)}) di
     * lunghezza massimale. Le parole sono sensibili alle maiuscole/minuscole.
     * @param p  il percorso del file
     * @param cs  il charset per decodificare i caratteri
     * @return  una mappa che conta le occorenze delle parole */
    public static Map<String,Integer> wordMap(Path p, String cs) throws IOException {
        Map<String,Integer> map = new HashMap<>();
        try (Scanner scan = new Scanner(p, cs)) {
            scan.useDelimiter("[^\\p{IsLetter}]+");    // Caratteri che non sono lettere
            while (scan.hasNext()) {
                String w = scan.next();
                Integer n = map.get(w);
                map.put(w, (n != null ? n+1 : 1));
            }
        }
        return map;
    }

    /** Chiede all'utente di digitare il percorso di un file di testo e il nome
     * di un charset per decodificarne i caratteri e stampa il numero di parole
     * distinte nel file e un campione di al più 10 parole con i relativi
     * conteggi. */
    public static void test_wordMap() {
        Scanner in = new Scanner(System.in);
        out.println("Digita il percorso di un file: ");
        String s = in.nextLine();
        out.println("Digita un charset: ");
        String cs = in.nextLine();
        Path p = Paths.get(s);
        try {
            Map<String,Integer> words = wordMap(p, cs);
            out.println("Numero di parole: "+words.size());
            out.println("Campione: " + Utils.randSample(words, 10));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
