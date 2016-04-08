package mp;

import mp.util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.System.out;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

/** Una classe per fare esempi sugli Stream */
public class TestStreams {

    public static void main(String[] args) throws IOException {
        List<Dipendente> dips = create("Max Ro 1350", "Ugo Gio 1200", "Lia Dea 1100",
                "Lea Gru 1500", "Ciro Espo 1350", "Ugo Bo 1000", "Lola La 1200");

        // Modo tradizionale di ottenere il numero di dipendenti con stipendio >= 1200
        int count = 0;
        for (Dipendente d : dips)
            if (d.getStipendio() >= 1200)
                count++;
        out.println("Numero dipendenti con stidendio >= 1200: "+count);

        // Lo stesso tramite Stream
        out.println("Numero dipendenti con stidendio >= 1200: " +
                dips.stream().filter(d -> d.getStipendio() >= 1200).count());

        out.println("Tutti i dipendenti con stipendio >= 1200: ");
        dips.stream().filter(d -> d.getStipendio() >= 1200).forEach(out::println);

        out.println("Dipendenti ordinati per stipendio: ");
        Stream<Dipendente> sDips = dips.stream();
        sDips.sorted(comparingDouble(Dipendente::getStipendio)).forEachOrdered(out::println);
        //sDips.count();  // ERRORE uno Stream non può essere riusato dopo un'operazione terminale

        out.println("Stipendi:");
        dips.stream().forEach(d -> out.println(d.getStipendio()));

        out.println("Stipendi distinti:");
        dips.stream().map(Dipendente::getStipendio).distinct().forEach(out::println);

        out.println("Stipendi distinti ordinati: ");
        dips.stream().map(Dipendente::getStipendio).distinct().sorted().forEachOrdered(out::println);

        Optional<Dipendente> od = dips.stream().max(comparingDouble(Dipendente::getStipendio));
        out.println("Dipendente con max stipendio: "+
                (od.isPresent() ? od.get() : "non ci sono dipendenti"));

        Path p = Paths.get("files", "alice_it_utf8.txt");
        List<String> lines = Files.readAllLines(p);   // Lista delle linee del file

        String w = "Regina";
        out.println("Numero linee che contengono "+ Utils.q(w)+": "+
                lines.stream().filter(l -> l.contains(w)).count());

        out.println("10 linee che contengono "+Utils.q(w)+":");
        lines.stream().filter(l -> l.contains(w)).limit(10).forEach(out::println);

        String txt = new String(Files.readAllBytes(p));
        out.println("Numero caratteri distinti: "+ Stream.of(txt.split("")).distinct().count());

        String[] ww = txt.split("[^\\p{IsLetter}]+");    // Array di tutte le prole

        Stream<String> ws = Stream.of(ww);               // Stream di tutte le parole
        out.println("Numero totale parole: "+ws.count());

        out.println("Numero parole distinte: "+Stream.of(ww).distinct().count());

        out.println("Numero parole distinte (ignorando M/m): "+
                Stream.of(ww).map(String::toLowerCase).distinct().count());

        out.println("La parola più lunga: "+
                Stream.of(ww).max(comparingInt(String::length)).get());

        out.println("Le 10 parole più lunghe: ");
        Stream.of(ww).sorted(comparingInt(String::length).reversed()).limit(10)
                .forEachOrdered(out::println);

        // Lista di tutte le parole ordinate per lunghezza
        List<String> wLst = Stream.of(ww).sorted(comparingInt(String::length)).collect(toList());

        DoubleSummaryStatistics stats = Stream.of(ww).collect(summarizingDouble(String::length));
        out.println(String.format("Lunghezze: media %.1f  max %.0f  min %.0f",
                stats.getAverage(), stats.getMax(), stats.getMin()));

        out.println("Tutti i caratteri: "+ Stream.of(txt.split("")).distinct().collect(joining()));

        // Mappa dei conteggi delle parole
        Map<String,Integer> wMap = Stream.of(ww).collect(toMap(s -> s, s -> 1, Integer::sum));

        try (Stream<String> ll = Files.lines(p)) {
            out.println(ll.filter(l -> l.contains(w)).count());
        }
    }

    /** Ritorna una lista di dipendenti creati con i dati specificati nelle stringhe
     * nomCogStip ognuna delle quali contiene il nomeCognome e lo stipendio di un
     * dipendente. Esempio,
     * <pre>
     *     create("Mario Rossi 1000", "Ugo Gio 1200", "Lia Dodi 1100");
     * </pre>
     * @param nomCogStip  array coi nomeCognome e stipendio dei dipendenti
     * @return lista di dipendenti creati con i dati specificati */
    public static List<Dipendente> create(String...nomCogStip) {
        List<Dipendente> dips = new ArrayList<>();
        for (String d : nomCogStip) {
            String[] tt = d.split(" ");
            dips.add(new Dipendente(String.join(" ", Arrays.copyOf(tt, tt.length-1)),
                    Double.parseDouble(tt[tt.length-1])));
        }
        return dips;
    }
}
