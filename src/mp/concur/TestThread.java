package mp.concur;

import java.math.BigInteger;
import java.util.Scanner;

import static java.lang.System.out;

/** Classe per semplici esempi sui threads */
public class TestThread {
    /** Ritorna una stringa con informazioni relative al thread specificato.
     * @param th  un thread
     * @return una stringa con informazioni relative al thread */
    public static String threadInfo(Thread th) {
        ThreadGroup group = th.getThreadGroup();
        return String.format("Id %2d  Daemon %s  %-13s  Group %-10s  Name %s",
                th.getId(), (th.isDaemon() ? "Y" : "N"), th.getState(),
                (group != null ? group.getName() : "-"), th.getName());
    }

    /** Ritorna una stringa con informazioni sui thread attualmente attivi.
     * @param stackDepth  la massima profondità delle stack trace
     * @return una stringa con informazioni sui thread attualmente attivi */
    public static String liveThreadInfo(int stackDepth) {
        String[] s = {""};    // Per aggirare il vincolo dell'effettivamente final
        Thread.getAllStackTraces().forEach((t,st) -> {
            s[0] += threadInfo(t)+"\n";
            for (int i = 0 ; i < st.length && i < stackDepth ; i++)
                s[0] += "    "+st[i]+"\n";
        });
        return s[0];
    }

    public static void main(String[] args) {
        //test_first();
        //test_interrupt_1();
        //test_interrupt_2();
        //fibonacci();
        //out.println(liveThreadInfo(5));
        test_liveThreadInfo();
    }

    /** Un semplice test per mostrare come si possono intrecciare le esecuzioni
     * di due thread */
    private static void test_first() {
        Thread t = new Thread(() -> {    // Crea un nuovo thread
            for (int i = 0 ; i < 50 ; i++)  // Task da eseguire nel nuovo thread
                out.println("nuovo "+i);
        });
        t.start();                          // Inizia l'esecuzione del nuovo thread
        for (int i = 0 ; i < 50 ; i++)
            out.println("MAIN "+i);
    }

    /** Aspetta che sia passato il numero di millisecondi specificato
     * @param millis  numero di millisecondi di attesa */
    private static void waitFor(long millis) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < millis) ;
    }

    /** Un test per l'interruzione di un thread */
    private static void test_interrupt_1() {
        Thread t = new Thread(() -> {
            long start = System.currentTimeMillis();
            while (true) {        // Ogni secondo stampa il numero di millisecondi
                waitFor(1000);    // passati dalla partenza del thread
                out.println(System.currentTimeMillis() - start);
                if (Thread.currentThread().isInterrupted()) break;
            }
            out.println("Conteggio terminato");
        });
        t.start();
        waitFor(4000);
        t.interrupt();
    }

    /** Un test per l'interruzione di un thread con uso di sleep */
    private static void test_interrupt_2() {
        Thread t = new Thread(() -> {
            long start = System.currentTimeMillis();
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { break; }
                out.println(System.currentTimeMillis()-start);
            }
            out.println("Conteggio terminato");
        });
        t.start();
        waitFor(4000);
        t.interrupt();
    }

    /** Piccolo esempio di applicazione con UI testuale che usa multithreading
     * per mantenere la reattività all'input dell'utente anche quando sta
     * eseguendo un'operazione che richiede tempi lunghi di elaborazione. */
    private static void fibonacci() {
        Scanner in = new Scanner(System.in);
        out.println("Digita n per calcolare F_n o 0 per terminare");
        Thread comp = null;
        while (true) {
            long n = in.nextLong();
            if (n <= 0) break;
            if (comp != null) comp.interrupt();
            comp = new Thread(() -> {
                BigInteger a = BigInteger.valueOf(1);
                BigInteger b = BigInteger.valueOf(1);
                for (long i = 1 ; i < n ; i++) {
                    BigInteger c = a.add(b);
                    a = b;
                    b = c;
                    if (Thread.interrupted()) {
                        out.println("Interrotto calcolo di F_"+n);
                        return;
                    }
                }
                out.println("F_"+n+" = "+b);
            });
            comp.start();
        }
    }

    /** Test di liveThreadInfo con nuovi thread */
    private static void test_liveThreadInfo() {
        new Thread(() -> waitFor(10_000), "Nuovo 1").start();
        new Thread(() -> {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {}
        }, "Nuovo 2").start();
        out.println(liveThreadInfo(1));
        new Thread(() -> out.println(liveThreadInfo(1)), "Nuovo 3").start();
    }
}
