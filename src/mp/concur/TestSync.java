package mp.concur;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

/** Classe per testare la sincronizzazione di threads */
public class TestSync {
    /** Un generatore di interi distinti */
    public static interface Gen {
        /** @return un intero sempre differente */
        int getNext();
    }

    /** Implementazione semplice di {@link Gen} */
    public static class SimpleGen implements Gen {
        @Override
        public int getNext() { return counter++; }

        private int counter = 0;
    }

    /** Implementazione thread safe di {@link Gen} che usa la sincronizzazione */
    public static class SyncGen implements Gen {
        public synchronized int getNext() { return counter++; }

        private int counter = 0;
    }

    /** Implementazione thread safe di {@link Gen} che usa una variabile atomica */
    public static class AtomGen implements Gen {
        @Override
        public int getNext() { return counter.getAndIncrement(); }

        private final AtomicInteger counter = new AtomicInteger(0);
    }

    /** Mette alla prova un generatore con un dato numero di thread e task.
     * @param g  un generatore
     * @param nThreads  numero thread
     * @param nTasks  numero task */
    public static void test_Gen(Gen g, int nThreads, int nTasks) {
        out.println("Threads: "+nThreads+"  Tasks: "+nTasks);
        ExecutorService exec = Executors.newFixedThreadPool(nThreads);
        List<Future<Integer>> tasks = new ArrayList<>();
        Set<Integer> vals = new HashSet<>();
        for (int i = 0; i < nTasks; i++)
            tasks.add(exec.submit(g::getNext));
        for (Future<Integer> t : tasks)
            try {
                vals.add(t.get());
            } catch (InterruptedException | ExecutionException e) {}
        exec.shutdown();
        out.println("Valori ripetuti: " + (nTasks - vals.size()));
    }

    public static class Do {
        public void doSomething() {
            for (long i = 0 ; i < 1_000_000_000 ; i++) ;  // Esegue un qualche task
            done = true;                       // Segnala che ha completato il task
            out.println("Done!");
        }

        public void waitForDone() {
            out.println("Start waiting");
            while (!done) ;             // Aspetta che il task sia stato completato
            out.println("End waiting");
        }

        private /* volatile */ boolean done = false;
    }

    public static void test_Do() {
        Do d = new Do();
        ThreadsMonitor monitor = new ThreadsMonitor(50,true,5,out,s->s.startsWith("*"));
        new Thread(d::doSomething, "*doSomething").start();
        new Thread(d::waitForDone, "*waitForDone").start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {}
        monitor.stop();
    }



    public static void main(String[] args) {
        //test_Gen(new SimpleGen(), 1, 1_000_000);
        //test_Gen(new SimpleGen(), 2, 10_000);
        //test_Gen(new SyncGen(), 1000, 1_000_000);
        //test_Gen(new AtomGen(), 1000, 1_000_000);
        test_Do();
    }
}
