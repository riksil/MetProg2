package mp.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import static java.util.stream.Collectors.joining;

/** Metodi di utilità per il Web */
public class Utils {
    /** Ritorna la stringa ottenuta decodificando la sequenza di bytes di un
     * flusso tramite una codifica specificata. I fine linea sono sostituiti
     * con '\n'.
     * @param in  un flusso di bytes
     * @param cs  codifica per i caratteri
     * @return una stringa con i caratteri decodificati dal flusso */
    public static String read(InputStream in, Charset cs) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, cs));
        return br.lines().collect(joining("\n"));
    }

    /** Ritorna una stringa con il contenuto della pagina localizzata da un URL
     * usando una codifica per i caratteri specificata.
     * @param url  una stringa contenente un URL
     * @param cs  codifica per i caratteri della pagina
     * @return  il contenuto della pagina come stringa
     * @throws IOException se accade un errore durante la connessione remota */
    public static String loadPage(String url, Charset cs) throws IOException {
        URLConnection urlC = new URL(url).openConnection();
        urlC.setRequestProperty("User-Agent", "Mozilla/5.0");
        urlC.setRequestProperty("Accept", "text/html;q=1.0,*;q=0");
        urlC.setRequestProperty("Accept-Encoding", "identity;q=1.0,*;q=0");
        urlC.setConnectTimeout(5000);
        urlC.setReadTimeout(10000);
        urlC.connect();
        return read(urlC.getInputStream(), cs);
    }

    /** Ritorna la stringa ripulita, cioè ottenuta sostituendo le sequenze di
     * whitespaces consecutivi con un singolo spazio e le più comuni HTML
     * character references con i relativi caratteri.
     * @param s  una stringa, tipicamente tratta da una pagina HTML
     * @return la stringa ripulita */
    public static String clean(String s) {
        s = s.replaceAll("\\s+", " ");
        for (String[] cr : CHAR_REFS)
            s = s.replace("&"+cr[0]+";", cr[1]);
        return s;
    }

    /** Sostituzioni per le più comuni HTML character references */
    private static final String[][] CHAR_REFS = {{"amp","&"},{"laquo","\""},
            {"raquo","\""},{"quot","\""},{"egrave","è"},{"Egrave","È"},
            {"eacute","é"},{"agrave","à"},{"ograve","ò"},{"igrave","ì"},
            {"ugrave","ù"},{"deg","°"}};
}
