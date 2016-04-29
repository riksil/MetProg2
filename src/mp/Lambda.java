package mp;

import mp.file.Utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import static java.lang.System.out;

/** Una classe per fare esempi sulle espressioni lambda */
public class Lambda {

    public static void main(String[] args) {
        Predicate<String> pred = new Predicate<String>() {    // Classe anonima
            @Override
            public boolean test(String s) {
                return true;
            }
        };
        pred = (String s) -> true;    // Equivale alla definizione tramite classe anonima
        pred = (s) -> true;
        pred = s -> true;             // Parentesi si possono omettere se c'è un solo parametro

        BiPredicate<String,Integer> bpred = (s,n) -> { return s.length() == n;};
        bpred = (s, i) -> s.length() == i;                   // OK
        bpred = (String s, Integer i) -> s.length() == i;    // OK
        //bpred = (Integer i, s) -> s.length() == i;         // ERRORE: o tutti o nessuno

        BiPredicate<String,String> eq = (s1,s2) -> s1.equals(s2);
        eq = String::equals;       // Equivalente con riferimento a metodo dell'istanza
        eq = Objects::equals;      // Equivalente con riferimento a metodo statico

        List<Dipendente> dLst = new ArrayList<>();
        dLst.forEach(d -> out.println(d));
        dLst.forEach(out::println);

        List<String> sLst = new ArrayList<>();
        sLst.removeIf(s -> s == null);
        sLst.removeIf(Objects::isNull);


        List<Dipendente> dd = createList(Dipendente::new,   // Riferimento a costruttore
                "Mario Rossi", "Ugo Verdi", "Lia Gialli");
        dd.forEach(out::println);

        Dipendente[] dArr = toArray(dd, n -> new Dipendente[n]);
        dArr = toArray(dd, Dipendente[]::new);   // Equivalente con riferimento a costruttore di array

        String[] fruits = {"Pera","Mela","uva","aranci","banana"};
        Arrays.sort(fruits);                                 // Ordinamento naturale
        out.println(Arrays.toString(fruits));
        Arrays.sort(fruits, String::compareToIgnoreCase);    // Ignora maiuscole/minuscole
        out.println(Arrays.toString(fruits));
        Arrays.sort(fruits, Comparator.comparingInt(String::length));  // Ordina per lunghezza
        out.println(Arrays.toString(fruits));
        Arrays.sort(fruits, Comparator.comparingInt(String::length)    // Prima ordina per lunghezza
                .thenComparing(String::compareToIgnoreCase));          // e poi ignorando le maiuscole/minuscole
        out.println(Arrays.toString(fruits));


        List<String> fLst = Arrays.asList(fruits);
        fLst.replaceAll(String::toUpperCase);
        out.println(fLst);

        try {
            Map<String,Integer> wm = Utils.wordMap(Paths.get("files",
                    "alice_it_utf8.txt"), "utf8");
            out.println(wm.size());
            out.println(sum(wm.values()));
            Map<String,Double> wMap = wordMapLowerCase(wm);
            out.println(wMap.size());
            out.println(sum(wMap.values()));
        } catch (IOException ex) {
            out.println(ex);
        }
    }

    /** Ritorna la lista dei risultati prodotti dall'applicazione della funzione f
     * ad ognuno dei valori dell'array specificato.
     * @param f  una funzione da applicare ai valori dell'array
     * @param arr  un array di valori
     * @param <E>  tipo dei risultati
     * @param <T>  tipo dei valori
     * @return la lista dei risultati della funzione applicata ai valori dell'array */
    @SafeVarargs
    public static <E,T> List<E> createList(Function<T,E> f, T...arr) {
        List<E> lst = new ArrayList<>();
        for (T e : arr)
            lst.add(f.apply(e));
        return lst;
    }

    /** Ritorna un array che contiene tutti gli elementi della collezione.
     * @param coll  una collezione
     * @param gen  funzione che crea un array di tipo E[]
     * @param <E>  tipo degli elementi dell'array
     * @return un array che contiene tutti gli elementi della collezione */
    public static <E> E[] toArray(Collection<? extends E> coll,
                                  IntFunction<E[]> gen) {
        E[] array = gen.apply(coll.size());
        int i = 0;
        for (E e : coll)
            array[i++] = e;
        return array;
    }

    /** Ritorna una mappa che ha come chiavi le stringhe della mappa wm ridotte
     * in minuscolo e i conteggi sono aggiornati sommando quelli che sono
     * relativi a chiavi di wm che sono uguali se ridotte in minuscole.
     * @param wm  una mappa da stringhe a numeri
     * @return una mappa che ha solo chiavi minuscole ma conteggi preservati */
    public static <N extends Number> Map<String,Double> wordMapLowerCase(Map<String,N> wm) {
        Map<String,Double> wMap = new HashMap<>();
        wm.forEach((s,i) -> wMap.merge(s.toLowerCase(), i.doubleValue(), Double::sum));
        return wMap;
    }

    /** Ritorna la somma dei numeri della collezione.
     * @param coll  una collezione di numeri
     * @return la somma dei numeri della collezione */
    public static double sum(Collection<? extends Number> coll) {
        double[] count = {0.0};
        coll.forEach(n -> count[0] += n.doubleValue());
        return count[0];
    }
}
