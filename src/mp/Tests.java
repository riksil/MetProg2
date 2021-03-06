package mp;

import mp.util.HTree;

import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.System.out;


class LPoint {    // Punto del piano con etichetta
    public final int x, y;
    public final String label;

    public LPoint(int x, int y, String lab) {
        this.x = x;
        this.y = y;
        label = lab;
    }

    /** Ritorna true se l'oggetto dato non è null, è di tipo LPoint ed ha le stesse
     * coordinate e la stessa etichetta di questo punto.
     * @param o  un oggetto
     * @return true se l'oggetto è un LPoint con lo stesso stato di questo oggetto */
    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) return false;
        LPoint p = (LPoint)o;
        return x == p.x && y == p.y && Objects.equals(label, p.label);
    }

    @Override
    public String toString() {
        return getClass().getName()+"["+x+","+y+","+label+"]";
    }
}

/** Una classe per esempi e testing */
public class Tests {
    public static void main(String[] args) {
        //lezione2();
        //lezione3();
        //lezione4();
        lezione7();
    }

    private static void test_HTree() {
        HTree<String> tree = new HTree<>("Computer Science");
        tree.add("Computer Science", "Software", "Hardware");
        tree.add("Hardware", "Memory", "Processor", "Architecture");
        tree.add("Software", "Operating System", "Data Base", "Word Processing",
                "Image Processing", "Algorithms", "Languages");
        tree.add("Languages", "Procedural", "Functional", "Object Oriented");
        tree.add("Procedural", "C", "Pascal");
        tree.add("Object Oriented", "C++", "Java", "Smalltalk");
        tree.add("Data Base", "SQL", "Data Mining");
        tree.add("Operating System", "Unix", "Linux", "MacOS X");
        out.println(tree.toFullString());
    }

    private static void lezione7() {
        test_HTree();
    }

    private static void lezione4() {
        Dirigente dir = new Dirigente("Luisa Gialli", 300.0);
        dir.setStipendio(2000.0);
        Dipendente dip = new Dipendente("Mario Rossi", 1000.0);
        for (Dipendente d : new Dipendente[] {dir, dip}) {
            out.println(d.getNomeCognome()+" "+d.getStipendio());
        }
        Dipendente[] dd = new Dipendente[3];
        dd[0] = dip;
        dd[1] = dir;
        dd[2] = new Dipendente("Ugo Verdi");
        ((Dirigente)dd[1]).setBonus(100);
        if (dd[2] instanceof Dirigente)
            ((Dirigente)dd[2]).setBonus(100);    // Cast

        LPoint p1 = new LPoint(0,0,"origin");
        LPoint p2 = new LPoint(0,0,"origin");
        if (!p1.equals(p2))
            out.println("Sono diversi");
        else
            out.println("Sono uguali");

        out.println(p1);
        out.println(dip);
        out.println(dir);
    }

    private static void lezione3() {
        Dipendente d1 = new Dipendente("Mario Rossi", 1000.0);
        out.println("Dipendente: "+d1.getNomeCognome()+" stipendio: "
                +d1.getStipendio()+" codice:"+d1.getCodice());
        d1.setStipendio(2000.0);
        out.println("Dipendente: "+d1.getNomeCognome()+" stipendio: "
                +d1.getStipendio());
        Dipendente d2 = new Dipendente("Ciro Verdi", 1500.0);
        out.println("Dipendente: "+d2.getNomeCognome()+" stipendio: "
                +d2.getStipendio()+" codice:"+d2.getCodice());

        Dipendente rossi = new Dipendente("Mario Rossi", 1000.0);
        rossi.setIndirizzo("Roma, via Verdi, 76");
        //rossi.setIndirizzo(null);
        Dipendente verdi = new Dipendente("Ugo Verdi", 1200.0);
        verdi.setTelefono("06 79879887");
        for (Dipendente d : new Dipendente[] {rossi, verdi}) {
            out.println("Dipendente: "+d.getNomeCognome());
            Dipendente.Contatti con = d.getContatti();
            out.println("Indirizzo: "+con.getIndirizzo());
            out.println("Telefono: "+con.getTelefono());
        }
    }

    private static void lezione2() {
        /* Esempi relativi ad array */
        int[] interi = new int[10];
        interi[1] = 13;
        String[] stringhe;
        stringhe = new String[5];
        String[] frutta = {"mela","pera","pesca"};
        int lunghezza = frutta.length;
        for (int i = 0 ; i < frutta.length ; i++)
            out.println(frutta[i]);

        /* Esempi relativi ad array multidimensionali */
        int[][] matInteri = new int[5][];
        int[][][] cubo = new int[3][3][3];
        for (int i = 0 ; i < matInteri.length ; i++)
            matInteri[i] = new int[10];
        float[][] matFloat = {{1.2f,3.5f,0.0f},{0.01f,12.3f},{0.23f}};
        printMatrix(matFloat);
        int[] vals = new int[0];
        float ave = average(vals);

        /* Esempi relativi a numero variabile di argomenti */
        max(2, 3);
        out.println("max(2,3,4,35,0) = "+max(2,3,4,35,0));
        out.println("max(2,3,0) = "+max(2,3,0));

        /* Esempi relativi ad eccezioni */
        /*
        String s = null;
        int n = s.length();
        int num = 12, d = 0;
        float q = num/d;
        */
        eccezioni();
    }


    /** Stampa una matrice di float
     * @param mat matrice di float */
    private static void printMatrix(float[][] mat) {
        for (int i = 0 ; i < mat.length ; i++) {
            for (int j = 0 ; j < mat[i].length ; j++)
                out.printf("%8.2f ", mat[i][j]);
            out.println();
        }
    }

    /** Ritorna la media degli interi nel array.
     * @param vals array di interi
     * @return media dei valori nell'array */
    private static float average(int[] vals) {
        float s = 0.0f;
        for (int v : vals)
            s += v;
        return s/vals.length;
    }

    /** Ritorna il massimo degli interi passati come argomenti.
     * @param first primo degli interi
     * @param others array contenente gli altri eventuali interi
     * @return il massimo degli interi */
    private static int max(int first, int...others) {
        int m = first;
        for (int n : others)
            if (n > m) m = n;
        return m;
    }

    /** Metodo per provare le eccezioni e il costrutto try-catch */
    private static void eccezioni() {
        Scanner input = new Scanner(System.in);
        try {
            out.print("Inserire due interi: ");
            int n = input.nextInt();    // Può lanciare InputMismatchException
            int m = input.nextInt();    // Può lanciare InputMismatchException
            int quoziente = n/m;        // Può lanciare ArithmeticException
            out.println(n+" / "+m+"  fa  "+quoziente);
            out.print("Inserire una parola e una posizione: ");
            String p = input.next();
            int i = input.nextInt(); // Può lanciare InputMismatchException
            char c = p.charAt(i-1);  // Può lanciare StringIndexOutOfBoundsException
            out.println("Il carattere in pos. "+i+" di \""+p+"\" è "+c);
        } catch (InputMismatchException ex) {
            out.print("Eccezione InputMismatchException: ");
            out.println(ex.getMessage());
        } catch (ArithmeticException ex) {
            out.print("Eccezione ArithmeticException: ");
            out.println(ex.getMessage());
            throw ex;
        } finally {
            out.println("Questa stampa viene sempre eseguita");
        }
        out.println("Il programma è terminato normalmente");
    }
}
