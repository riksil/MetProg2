package mp;

import mp.tapp.MenuApp;

import java.util.Scanner;

import static java.lang.System.out;

/** Una semplice applicazione con menu testuale per il calcolo di funzioni
 * matematiche. */
public class MathApp extends MenuApp {
    public MathApp() { super("Logaritmo","Radice quadrata"); }

    /** Esegue il calcolo relativo alla voce di menu scelta.
     * @param choice  il numero della voce di menu scelta */
    @Override
    protected void doMenu(int choice) {
        Scanner in = new Scanner(System.in);
        out.println("Digita un numero: ");
        while (!in.hasNextDouble())
            in.next();    // Scarta qualsiasi input che non è un numero
        double x = in.nextDouble();
        switch (choice) {
            case 1:
                out.println("log("+x+") = "+Math.log(x));
                break;
            case 2:
                out.println("sqrt("+x+") = "+Math.sqrt(x));
                break;
        }
    }

    public static void main(String[] args) {   // Mette alla prova l'applicazione
        MathApp app = new MathApp();
        app.run();
    }
}
