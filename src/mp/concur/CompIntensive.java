package mp.concur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.LongBinaryOperator;

import static java.lang.System.out;

/** Classe per testare implementazioni parallele di task ad alta intensità
 * di calcolo. */
public class CompIntensive {
    /** Ritorna il massimo numero di passi della procedura della congettura
     * di Collatz, per gli interi nell'intervallo [a, b].
     * @param a  inizio intervallo
     * @param b  fine intervallo
     * @return il massimo numero di passi */
    public static long collatz(long a, long b) {
        long max = 0;
        for (long i = a ; i <= b ; i++) {
            long n = i, s = 0;
            while (n != 1) {
                n = n % 2 == 0 ? n/2 : 3*n + 1;
                s++;
            }
            if (s > max) max = s;
        }
        return max;
    }

    /** Ritorna il numero di primi nell'intervallo [a, b].
     * @param a  inizio intervallo
     * @param b  fine intervallo
     * @return il numero di primi nell'intervallo [a, b] */
    public static long numPrimes(long a, long b) {
        long num = 0;
        for (long i = a ; i <= b ; i++) {
            long d = 2;
            double sqrt = Math.sqrt(i);
            while (d <= sqrt && i % d != 0) d++;
            if (d > sqrt) num++;
        }
        return num;
    }

    /** Esegue in multithreading la funzione func per l'intervallo [a, b]
     * partizionando [a, b] in nt sotto-intervalli, eseguendo func su di essi in
     * altrettanti task in modo concorrente su nThreads thread e infine ricombinando
     * i risultati con l'operazione comb.
     * @param nThreads  numero thread
     * @param nt  numero task e numero sotto-intervalli
     * @param func  funzione da calcolare sull'intervallo [a, b]
     * @param a  inizio intervallo
     * @param b  fine intervallo
     * @param comb  operazione per ricombinare i risultati dei sotto-intervalli
     * @return il risultato della funzione func sull'intervallo [a,b] */
    public static long parallel(int nThreads, int nt, LongBinaryOperator func,
                                long a, long b, LongBinaryOperator comb) {
        long size = b - a + 1;
        long nParts = Math.min(size, nt), pSize = size/nParts;
        ExecutorService exec = Executors.newFixedThreadPool(nThreads);
        long res = 0;
        try {
            List<Future<Long>> tasks = new ArrayList<>();    // Lista per i task
            for (int i = 0 ; i < nParts ; i++) {       // Sottomette i task all'esecutore
                long ta = a + i*pSize;                      // Inizio sotto-intervallo
                long tb = (i < nParts-1 ? ta+pSize-1 : b);  // Fine sotto-intervallo
                tasks.add(exec.submit(() -> func.applyAsLong(ta, tb)));
            }
            for (Future<Long> t : tasks)               // Ottiene i risultati relativi ai
                res = comb.applyAsLong(res, t.get());  // sotto-intervalli e li ricombina
        } catch (InterruptedException | ExecutionException e) {
        } finally { exec.shutdown(); }
        return res;
    }

    /** Esegue la funzione func sull'intervallo [a, b] con nThreads thread, nTasks
     * task e altrettanti sotto-intervalli e stampa il risultato e il campionamento
     * dei tempi di esecuzione dei thread che hanno effettuato il calcolo.
     * @param name  nome della funzione
     * @param nThreads  numero thread, se <= 0, è il numero di processori
     * @param nt  numero task e numero sotto-intervalli, se <= 0, è nThreads*abs(nt)
     * @param func  la funzione da eseguire sull'intervallo [a, b]
     * @param a  inizio intervallo
     * @param b  fine intervallo
     * @param comb  operazione per ricombinare i risultati dei sotto-intervalli */
    public static void test(String name, int nThreads, int nt, LongBinaryOperator func,
                            long a, long b, LongBinaryOperator comb) {
        if (nThreads <= 0) nThreads = Runtime.getRuntime().availableProcessors();
        if (nt <= 0) nt = (nt < 0 ? nThreads*Math.abs(nt) : nThreads);
        out.println(String.format("Test %s  Threads %d  Tasks %d", name, nThreads, nt));
        out.println("    interval ["+a+","+b+"]:");
        String p = nThreads == 1 && nt == 1 ? "main" : "pool";
        ThreadsMonitor monitor = new ThreadsMonitor(100,true,5,out,s->s.startsWith(p));
        long res = nThreads == 1 && nt == 1 ? func.applyAsLong(a, b) :
                parallel(nThreads, nt, func, a, b, comb);
        out.println("    Result: "+res);
        monitor.stop();
    }

    public static void main(String[] args) {
        //test("collatz", 1, 1, CompIntensive::collatz, 1, 60_000_000, Math::max);
        //test("collatz", -1, -1, CompIntensive::collatz, 1, 60_000_000, Math::max);
        //test("numPrimes", 1, 1, CompIntensive::numPrimes, 1, 20_000_000, Long::sum);
        //test("numPrimes", -1, -1, CompIntensive::numPrimes, 1, 20_000_000, Long::sum);
        test("numPrimes", -1, -10, CompIntensive::numPrimes, 1, 20_000_000, Long::sum);
    }
}
