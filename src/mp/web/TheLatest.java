package mp.web;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mp.web.Utils.clean;
import static mp.web.Utils.loadPage;

/** Un oggetto {@code TheLatest} rappresenta un servizio web per la ricerca e il
 * recupero di informazioni aggiornate. Può essere interrogato circa un argomento
 * specificato in una stringa (vedi {@link TheLatest#get(String)}). Ad esempio,
 * un {@code TheLatest} può rappresentare il servizio di ricerca offerto da un
 * quotidiano. L'attuale implementazione è infatti basata proprio su quest'ultimo
 * tipo di interrogazioni (si veda il costruttore
 * {@link TheLatest#TheLatest(String,Charset,String,String,String,int,int)}). */
public class TheLatest {
    /** Crea un TheLatest tale che l'URL di un'interrogazione {@code q} è la
     * concatenazione {@code uS + q + uE} e il titolo e data della più recente
     * notizia si può estrarre dalla pagina di risposta tramite l'espressione
     * regolare {@code re} dove il numero del gruppo che cattura il titolo è
     * {@code gT} mentre quello per la data è {@code gD}.
     * @param nm  nome del servizio web
     * @param cs  codifica dei caratteri della pagina di risposta
     * @param uS  parte iniziale dell'URL di interrogazione
     * @param uE  parte finale dell'URL di interrogazione
     * @param re  espressione regolare per estrarre titolo e data
     * @param gT  numero del gruppo, in {@code re}, che cattura il titolo
     * @param gD  numero del gruppo, in {@code re}, che cattura la data */
    public TheLatest(String nm, Charset cs, String uS, String uE, String re, int gT, int gD) {
        name = nm;
        chars = cs;
        uStart = uS;
        uEnd = uE;
        regExp = Pattern.compile(re);
        gTitle = gT;
        gDate = gD;
    }

    /** Ritorna la risposta ad un'interrogazione a questo servizio web.
     * @param q  stringa che contiene l'interrogazione
     * @return  la risposta all'interrogazione */
    public String get(String q) {
        String url = uStart+q.replace(" ", "+")+uEnd;
        String s = name+"  ";
        try {
            String page = loadPage(url, chars);
            Matcher m = regExp.matcher(page);
            if (m.find()) {
                s += m.group(gDate).trim()+"  ";
                return s + "<<"+clean(m.group(gTitle).trim())+">>";
            } else
                return s + "No news";
        } catch (Exception e) { return s+"ERROR "+e.getMessage(); }
    }


    private final String name;
    private final Charset chars;
    private final String uStart, uEnd;
    private final Pattern regExp;
    private final int gTitle, gDate;
}
