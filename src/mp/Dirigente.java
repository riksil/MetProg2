package mp;

/** Un oggetto {@code Dirigente} rappresenta un dirigente dell'azienda */
public class Dirigente extends Dipendente {
    /** Crea un dirigente con il dato nome e cognome e bonus.
     * @param nomeCognome  nome e cognome del dirigente
     * @param b  bonus del dirigente */
    public Dirigente(String nomeCognome, double b) {
        super(nomeCognome);
        bonus = b;
    }

    /** @return il bonus di questo dirigente */
    public double getBonus() { return bonus; }

    /** Imposta un nuovo bonus per questo dirigente.
     * @param b  l'importo del nuovo bonus */
    public void setBonus(double b) { bonus = b; }

    /** @return lo stipendio di questo dirigente */
    @Override
    public double getStipendio() { return super.getStipendio() + bonus; }

    /** Imposta il supervisore di questo dirigente.
     * @param s  il supervisore
     * @throws IllegalArgumentException se il supervisore non è un dirigente */
    @Override
    public void setSupervisore(Dipendente s) {
        if (!(s instanceof Dirigente))
            throw new IllegalArgumentException("Il supevisore di un dirigente deve essere un dirigente");
        super.setSupervisore(s);
    }


    private double bonus;
}
