package mp;

import java.math.BigInteger;
import java.util.Scanner;

import static java.lang.System.out;

/** Una classe per fare esempi sulla classi locali */
public class LocalClasses {
    /** Ritorna il coefficiente binomiale n su k. Ovvero, il numero di
     * k-sottoinsiemi di un insieme di n elementi. Calcolo basato sulla formula
     * ricorsiva: C(n, k) = C(n-1, k) + C(n-1, k-1).
     * @param n,k  parametri del coefficiente binomiale
     * @return il coefficiente binomiale n su k */
    public static BigInteger binom(int n, int k) {
        if (k == 0 || k == n) return BigInteger.valueOf(1);
        return binom(n-1,k).add(binom(n-1,k-1));
    }
    
    /** Ritorna il coefficiente binomiale n su k. Ovvero, il numero di
     * k-sottoinsiemi di un insieme di n elementi. Calcolo basato sulla formula
     * ricorsiva: (n, k) = (n-1, k) + (n-1, k-1). La ricorsione è resa efficiente
     * dalla memoizzazione.
     * @param n,k  parametri del coefficiente binomiale
     * @return il coefficiente binomiale n su k */
    public static BigInteger binomM(int n, int k) {
        class Binom {    // Classe locale per mantenere la tabella di memoizzazione
            Binom(int n, int k) {              // Crea una tabella di memoizzazione
                c = new BigInteger[n+1][k+1];  // sufficientemente grande
            }

            BigInteger compute(int n, int k) {
                if (c[n][k] == null) {    // Se non è già stato calcolato...
                    if (k == 0 || k == n) c[n][k] = BigInteger.valueOf(1);
                    else c[n][k] = compute(n-1,k).add(compute(n-1,k-1));
                }
                return c[n][k];
            }

            BigInteger[][] c;    // Tabella di memoizzazione
        }
        Binom b = new Binom(n,k);    // Prepara per la ricorsione memoizzata
        return b.compute(n,k);       // Esegue la ricorsione memoizzata
    }

    /** Implementazione alternativa di {@link mp.LocalClasses#binomM(int,int)} */
    public static BigInteger binomM2(int n, int k) {
        class Binom {
            BigInteger compute(int n, int k) {
                if (c[n][k] == null) {
                    if (k == 0 || k == n) c[n][k] = BigInteger.valueOf(1);
                    else c[n][k] = compute(n-1,k).add(compute(n-1,k-1));
                }
                return c[n][k];
            }

            BigInteger[][] c = new BigInteger[n+1][k+1];
        }
        return new Binom().compute(n,k);
    }

    /** Implementazione alternativa di {@link mp.LocalClasses#binomM2(int,int)} */
    public static BigInteger binomM3(int n, int k) {
         return new Object() {
            BigInteger compute(int n, int k) {
                if (c[n][k] == null) {
                    if (k == 0 || k == n) c[n][k] = BigInteger.valueOf(1);
                    else c[n][k] = compute(n-1,k).add(compute(n-1,k-1));
                }
                return c[n][k];
            }

            BigInteger[][] c = new BigInteger[n+1][k+1];
        }.compute(n,k);
    }


    private static void test_binom() {
        Scanner in = new Scanner(System.in);
        out.print("Digita due interi n e k: ");
        int n = in.nextInt(), k = in.nextInt();
        out.println("C("+n+","+k+") = "+binomM(n, k));
    }

    public static void main(String[] args) {
        test_binom();
    }
}
