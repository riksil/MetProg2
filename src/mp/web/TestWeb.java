package mp.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

import static java.lang.System.out;
import static mp.web.Utils.*;

/** Classe per testare operazioni relative al web */
public class TestWeb {
    /** Ritorna una mappa che associa ad ogni interrogazione data la lista delle
     * risposte ottenute dai servizi web specificati. L'implementazione è
     * sequenziale nel thread di invocazione.
     * @param lts  i servizi web da interrogare
     * @param qq  le interrogazioni
     * @return  una mappa con le risposte alle interrogazioni */
    public static Map<String,List<String>> get(TheLatest[] lts, String...qq) {
        Map<String,List<String>> res = new HashMap<>();
        for (String q : qq) {
            List<String> r = new ArrayList<>();
            for (TheLatest lt : lts)
                r.add(lt.get(q));
            res.put(q, r);
        }
        return res;
    }

    /** Ritorna una mappa che associa ad ogni data interrogazione la lista delle
     * risposte ottenute dai servizi web specificati. L'implementazione è
     * multithreading ed usa il numero di thread specificato.
     * @param nt  numero thread
     * @param lts  i servizi web da interrogare
     * @param qq  le interrogazioni
     * @return  una mappa con le risposte alle interrogazioni */
    public static Map<String, List<String>> get(int nt, TheLatest[] lts, String...qq) {
        ExecutorService exec = Executors.newFixedThreadPool(nt);    // Esecutore
        Map<String,List<Future<String>>> futures = new HashMap<>();
        for (String q : qq) {   // Sottomette i task di tutte le interrogazioni
            List<Future<String>> list = new ArrayList<>();
            for (TheLatest lt : lts)     // Sottomette i task di una interrogazione
                list.add(exec.submit(() -> lt.get(q)));           // ai servizi web
            futures.put(q, list);
        }
        Map<String,List<String>> results = new HashMap<>();
        for (String q : qq) {  // Ottiene i risultati dei task delle interrogazioni
            List<String> list = new ArrayList<>();
            for (Future<String> f : futures.get(q))          // Ottiene i risultati
                try {              // dei task di una interrogazione ai servizi web
                    list.add(f.get());
                } catch (InterruptedException | ExecutionException e) {
                    list.add("ERROR: "+e.getMessage());
                }
            results.put(q, list);
        }
        exec.shutdown();
        return results;
    }

    public static void main(String[] args) {
        //test_loadPage();
        // I servizi web da interrogare
        TheLatest[] lts = {new TheLatest("Repubblica", StandardCharsets.UTF_8,
                "http://ricerca.repubblica.it/ricerca/repubblica?query=",
                "&sortby=ddate&mode=phrase",
                "<h1>[^<]*<[^>]*>([^<]*)<[^<]*</h1>([^<]*<[^t][^<]*)*<time[^>]*>([^<]*)</time>", 1, 3),
                new TheLatest("Corriere", StandardCharsets.ISO_8859_1,
                        "http://sitesearch.corriere.it/forward.jsp?q=", "#",
                        "<span class=\"hour\">\\D*([^<]*)</span>([^<]*<[^h][^<]*)*<h1>[^<]*<[^>]*>([^<]*)<[^<]*</h1>", 3, 1),
                new TheLatest("Il Sole 24ore", StandardCharsets.ISO_8859_1,
                        "http://www.ricerca24.ilsole24ore.com/fc?cmd=static&chId=30&path=%2Fsearch%2Fsearch_engine.jsp&keyWords=%22",
                        "%22&orderByString=Data+desc",
                        "<a[^>]*>([^<]*)</a></div></div><div class=\"box_autore\"><div class=\"autore_text\">[^<0-9]*([^<]*)</div>", 1, 2)};
// Le interrogazioni
        String[] qq = {"Carlo Padoan","Matteo Renzi","Sofia Loren","Totti",
                "Belen","Barack Obama","Informatica","Donald Trump",
                "Federica Pellegrini","Beppe Grillo","emigranti",
                "debito pubblico","Università","Trivelle","Referendum"};

        //test_TheLatest(lts, qq, TestWeb::get);
        test_TheLatest(lts, qq, (l,q) -> get(10,l,q));
    }

    private static void test_TheLatest(TheLatest[] lts, String[] qq,
                                       BiFunction<TheLatest[],String[],Map<String,List<String>>> get) {
        out.println("Servizi: "+lts.length+"  Interrogazioni: "+qq.length);
        long time = System.currentTimeMillis();
        Map<String, List<String>> results = get.apply(lts, qq);
        out.println(String.format("Tempo: %.2f secondi",
                (System.currentTimeMillis() - time)/1000.0));
        results.forEach((k, l) -> {
            out.println(k);
            l.forEach(out::println);
        });
    }

    private static void test_loadPage()  {
        try {
            String p = loadPage("https://www.python.org", StandardCharsets.UTF_8);
            out.println("Page length: "+p.length());
        } catch (IOException e) { e.printStackTrace(); }
    }
}
