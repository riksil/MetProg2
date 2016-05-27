package mp.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/** La classe definisce il metodo {@link ObjSize#estimate(Object)} che ritorna la
 * una stima della dimensione in bytes di un oggetto dato. Partendo dalla classe
 * dell'oggetto conteggia la dimensione dell'oggetto base, dei suoi campi e per i
 * campi riferimento conteggia le dimensioni dei valori facendo attenzione a non
 * conteggiare più volte lo stesso valore (oggetto). Per fare ciò esegue una visita
 * a partire dall'oggetto seguendo i puntatori contenuti nei campi riferimento. */
public class ObjSize {
    /** Ritorna una stima della dimensione in byte dell'oggetto specificato.
     * @param o  un oggetto
     * @return una stima della dimensione in byte dell'oggetto specificato */
    public static long estimate(Object o) {
        // Per registrare gli oggetti la cui dimensione è stata già conteggiata
        IdentityHashMap<Object,Void> counted = new IdentityHashMap<>();
        class Size {
            long compute(Object o) {  // Ritorna la dimensione dell'oggetto o
                if (o == null || counted.containsKey(o))  // Se è già stato
                    return 0;            // conteggiato, non lo riconteggia
                long size = 0;
                counted.put(o,null);     // Lo aggiunge agli oggetti conteggiati
                Class<?> c = o.getClass();   // La classe dell'oggetto
                if (c.isArray()) {           // Se è un array
                    // Addiziona la dimensione di Object e del campo length
                    size += align(OBJ_SIZE+primitive(int.class));
                    int len = Array.getLength(o);        // Ottiene la lunghezza e
                    Class<?> cc = c.getComponentType();  // il tipo delle componenti
                    if (cc.isPrimitive()) {  // Se il tipo delle componenti è primitivo
                        // Addiziona le dimensioni degli elementi
                        size += align(len*primitive(cc));
                    } else {          // Se il tipo delle componenti è riferimento
                        // Addiziona le dimensioni delle componenti riferimento
                        size += align(len*REF_SIZE);
                        // Per ogni elemento calcola (ricorsivamente) la dimensione
                        for (int i = 0 ; i < len ; i++) // e l'addiziona
                            size += new Size().compute(Array.get(o, i));
                    }
                } else {                     // Se non è un array
                    CInfo ci = getCInfo(c);  // L'info della classe
                    size += ci.size;         // Addiziona la dimensione del guscio
                    for (Field f : ci.refFields)  // Per ogni campo riferimento
                        try {     // addiziona la dimensione del valore calcolandolo
                            size += new Size().compute(f.get(o));  // ricorsivamente
                        } catch (IllegalAccessException e) {}
                }
                return size;
            }
        }
        return new Size().compute(o);
    }

    /** Info di una classe. I campi statici sono ignorati */
    private static class CInfo {
        /** Dimensione del <i>guscio</i> di una classe, cioè la dimensione di
         * un oggetto della classe esclusa la dimensione dei valori dei campi
         * di tipo riferimento */
        final long size;
        /** Lista di tutti i campi di tipo riferimento */
        final List<Field> refFields;

        CInfo(long size, List<Field> rFF) {
            this.size = size;
            refFields = rFF;
        }
    }

    /** Ritorna l'info della classe specificata. Se non è stata già calcolata,
     * la calcola e prima di ritornarla la registra.
     * @param c  una classe
     * @return l'info della classe specificata */
    private static CInfo getCInfo(Class<?> c) {
        if (!cInfos.containsKey(c)) {   // Se l'info non è già stata calcolata
            CInfo supCI = getCInfo(c.getSuperclass()); // Info della superclasse
            long size = supCI.size;  // La dimensione del guscio comprende quella
                                     // della superclasse e i campi riferimento
                                     // comprendono quelli della superclasse
            List<Field> rFF = new ArrayList<>(supCI.refFields);
            for (Field f : c.getDeclaredFields()) { // Per ogni campo non statico,
                if (Modifier.isStatic(f.getModifiers())) continue;
                Class<?> cf = f.getType();    // ottiene il tipo del campo;
                if (cf.isPrimitive()) {       // se il tipo è primitivo, addiziona
                    size += primitive(cf);    // la dimensione a quella del guscio;
                } else {                      // se non è un tipo primitivo,
                    f.setAccessible(true);    // permette l'accesso al valore e
                    size += REF_SIZE;         // addiziona la dimensione del campo
                    rFF.add(f);               // a quella del guscio e aggiunge il
                }                             // campo alla lista dei campi.
            }                                             // Registra l'info
            cInfos.put(c, new CInfo(align(size), rFF));   // della classe
        }
        return cInfos.get(c);
    }

    /** Ritorna la dimensione in byte del tipo primitivo specificato.
     * @param c  la classe di un tipo primitivo
     * @return la dimensione in byte del tipo primitivo specificato */
    private static long primitive(Class<?> c) {
        if (c == byte.class) return 1;
        else if (c == short.class) return 2;
        else if (c == int.class) return 4;
        else if (c == long.class) return 8;
        else if (c == float.class) return 4;
        else if (c == double.class) return 8;
        else if (c == char.class) return 2;
        else if (c == boolean.class) return 1;
        else throw new IllegalArgumentException();
    }

    /** Ritorna l'aggiustamento della dimensione specificata rispetto alla
     * dimensione di allineamento.
     * @param s  una dimensione in bytes
     * @return l'aggiustamento della dimensione specificata rispetto alla
     * dimensione di allineamento */
    private static long align(long s) {
        long r = s % ALIGN_SIZE;
        return r == 0 ? s : s + ALIGN_SIZE - r;
    }

    private static final long REF_SIZE = 8;   // Dimensione variabile riferimento
    private static final long ALIGN_SIZE = 8; // Dimensione di allineamento
    private static final long OBJ_SIZE = 12;  // Dimensione Object (16)

    /** Mappa per registrare le informazioni delle classi ed evitare così di
     * ricalcolarle più volte. */
    private static final Map<Class<?>, CInfo> cInfos = new HashMap<>();
    static {
        cInfos.put(Object.class,new CInfo(OBJ_SIZE, new ArrayList<>()));
    }
}
