package mp;

import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.System.out;

/** Una classe per esempi e testing */
public class Tests {
    public static void main(String[] args) {
        lezione2();
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
