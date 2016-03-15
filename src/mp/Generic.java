package mp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Un oggetto {@code Pair} rappresenta una coppia di valori dello stesso tipo
 * @param <T>  tipo dei valori della coppia */
class Pair<T> {
    /** Crea una coppia di valori.
     * @param a  primo valore della coppia
     * @param b  secondo valore della coppia */
    public Pair(T a, T b) {
        first = a;
        second = b;
    }

    public T getFirst() { return first; }
    public T getSecond() { return second; }

    public void setFirst(T v) { first = v; }
    public void setSecond(T v) { second = v; }


    private T first, second;
}

/** Un oggetto {@code DPair} rappresenta una coppia di valori di tipi anche diversi.
 * @param <S>  tipo del primo valore della coppia
 * @param <T>  tipo del secondo valore della coppia */
class DPair<S,T> {
    /** Crea una coppia di valori.
     * @param a  primo valore della coppia
     * @param b  secondo valore della coppia */
    public DPair(S a, T b) {
        first = a;
        second = b;
    }

    public S getFirst() { return first; }
    public T getSecond() { return second; }

    public void setFirst(S v) { first = v; }
    public void setSecond(T v) { second = v; }


    private S first;
    private T second;
}

class Pair2<T> extends DPair<T,T> {
    public Pair2(T a, T b) { super(a, b); }
}

/** Una classe per fare esempi e test sulla genericità */
public class Generic {
    /** Metodo generico che ritorna il primo indice dell'array in cui si trova
     * il valore, se non è presente ritorna -1.
     * @param a  un array
     * @param v  valore da cercare
     * @param <T>  variabile di tipo
     * @return  il primo indice in cui si trova il valore o -1 */
    public static <T> int find(T[] a, T v) {
        for (int i = 0 ; i < a.length ; i++)
            if (Objects.equals(a[i], v)) return i;
        return -1;
    }

    /** Metodo generico che riempie l'array con il valore dato.
     * @param a  un array
     * @param v  valore di riempimento
     * @param <T>  variabile di tipo */
    public static <T> void fill(T[] a, T v) {
        for (int i = 0 ; i < a.length ; i++)
            a[i] = v;
    }

    /** Metodo generico che ritorna l'elemento dell'array con la più lunga
     * stringa ritornata da {@code toString}.
     * @param a  un array
     * @param <T>  variabile di tipo
     * @return l'elemento dell'array con la più lunga {@code toString} */
    public static <T> T longestStr(T[] a) {
        int max = 0;
        T val = null;
        for (int i = 0 ; i < a.length ; i++)
            if (a[i] != null && a[i].toString().length() >= max) {
                max = a[i].toString().length();
                val = a[i];
            }
        return val;
    }

    public static Pair<String>[] getArr(Pair<String>...a) {
        Pair<String>[] ap = a;     // Array di Pair<String>
        return ap;
    }

    public static <T extends Comparable<T>> T min(T[] a) {
        T m = null;
        for (T v : a)
            if (v != null && (m == null || m.compareTo(v) < 0))
                m = v;
        return m;
    }

    public static <T extends Comparable<? super T>> T min2(T[] a) {
        T m = null;
        for (T v : a)
            if (v != null && (m == null || m.compareTo(v) < 0))
                m = v;
        return m;
    }

    public static void main(String[] args) {
        String[] sA = {"O","B","A"};
        int i = find(sA, "C");  // Il compilatore inferisce il tipo String
        Integer[] iA = {1,2,3};
        i = find(iA, 2);        // Il compilatore inferisce il tipo Integer
        i = find(sA, 2);        // OK ma può essere incongruo
        //i = Generic.<String>find(sA, 2);   // Errore in compilazione
        i = Generic.<Integer>find(iA, 2);    // OK

        fill(sA, "0");          // OK
        fill(iA, 13);           // OK
        //fill(sA, 2);          // Errore in esecuzione: ArrayStoreException
        //Generic.<String>fill(sA, 2);  // Errore in compilazione
        Generic.<String>fill(sA, "A");  // OK

        i = longestStr(iA);             // OK
        String s = longestStr(sA);      // OK
        //s = longestStr(iA);           // Errore in compilazione

        Pair<String> pS = new Pair<>("prima", "seconda");
        s = pS.getFirst();     // OK
        //i = pS.getFirst();   // Errore in compilazione
        pS.setFirst(s);        // OK
        //pS.setFirst(12);     // Errore in compilazione
        Pair<Integer> pI = new Pair<>(1,2);
        Pair<Pair<String>> ppS = new Pair<>(pS, pS);   // OK
        Pair<Dipendente> pD = new Pair<>(new Dipendente("Mario Rossi"),
                                         new Dipendente("Luisa Verdi"));
        Pair<Dirigente> pDir;
        //pD = pDir;          // Errore in compilazione

        //Pair<String>[] aPS = new Pair<String>[3];   // Errore in compilazione
        Pair<String>[] aPS = getArr(pS, pS, pS);      // OK

        Pair2<String> pS2 = new Pair2<>("A","B");
        DPair<String,String> dpS = pS2;               // OK

        s = min(sA);       // OK
        i = min(iA);       // OK
        Dipendente[] dd = {new Dipendente("Mario Rossi"), new Dipendente("Luisa Verdi")};
        Dipendente d = min(dd);         // OK (dopo che Dipendente implementa Comparable)
        Dirigente[] dirs = {new Dirigente("Mario Rossi", 0), new Dirigente("Luisa Verdi", 0)};
        //Dirigente dir = min(dirs);    // Errore in compilazione
        Dirigente dir = min2(dirs);     // OK

        //List<?> da = new ArrayList<?>();      // ERRORE in compilazione

        List<Pair<?>> pairL = new ArrayList<>();
        pairL.add(new Pair<Integer>(1, 2));
        pairL.add(new Pair<String>("a", "b"));
        Pair<Integer> intPair = null;
        //intPair = pairL.get(0);              // ERRORE in compilazione
        Pair<Object> objPair = null;
        //objPair = pairL.get(1);              // ERRORE in compilazione
        Pair<?> unknown = pairL.get(0);        // OK

        List<Pair<? extends Number>> numPairL = new ArrayList<>();
        numPairL.add(new Pair<Integer>(1, 2));
        //numPairL.add(new Pair<String>("a", "b"));    // ERRORE in compilazione
        //intPair = numPairL.get(0);                   // ERRORE in compilazione
        Pair<Number> numPair = null;
        //numPair = numPairL.get(0);                   // ERRORE in compilazione

        Pair<? extends Number> extNumPair = numPairL.get(0);  // OK

        extNumPair = intPair;
        Pair<? super Integer> supIntPair = numPair;
        //supIntPair = extNumPair;                    // ERRORE in compilazione
        Pair<? super Number> supNumPair = null;
        supIntPair = supNumPair;
        //supNumPair = supIntPair;                    // ERRORE in compilazione
        unknown = supIntPair;
        unknown = extNumPair;
        supIntPair = objPair;
    }
}
