package mp.tapp;

import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.out;

/** {@code MenuApp} fornisce le funzionalità di base per un'applicazione basata
 * su un menu testuale. Gestisce la stampa del menu e la scelta da parte dell'utente
 * delle voci del menu. La sotto-classe deve fornire i contenuti implementando il
 * metodo astratto {@link mp.tapp.MenuApp#doMenu(int)} che esegue la voce di menu
 * scelta. */
public abstract class MenuApp {
    /** Esegue l'applicazione. L'utente sceglie una voce del menu digitando il
     * numero mostrato a sinistra della voce. L'esecuzione continua fino a che
     * l'utente sceglie "Quit". */
    public void run() {
        Scanner in = new Scanner(System.in);
        boolean quit = false;
        while (!quit) {
            for (int i = 0 ; i < menu.length ; i++)
                out.println((i+1)+". "+menu[i]);
            out.println("Digita un numero tra 1 e "+menu.length);
            while (!in.hasNextInt())
                in.next();    // Scarta qualsiasi input che non è un intero
            int c = in.nextInt();
            if (c >= 1 && c < menu.length) doMenu(c);
            else if (c == menu.length) quit = true;
        }
        out.println("Applicazione terminata");
    }


    /** Invocato dalla sotto-classe per inizializzare tutte le voci del menu,
     * esclusa la voce "Quit" per terminare l'applicazione, che è gestita
     * direttamente da questa classe.
     * @param items  le voci del menu */
    protected MenuApp(String...items) {
        menu = Arrays.copyOf(items, items.length + 1);
        menu[menu.length - 1] = "Quit";
    }

    /** Deve essere implementato dalla sotto-classe per eseguire la voce del menu
     * scelta dall'utente.
     * @param choice  il numero della voce di menu scelta dall'utente */
    protected abstract void doMenu(int choice);

    private final String[] menu;
}
