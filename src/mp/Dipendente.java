package mp;

import java.util.Objects;

/** Un oggetto {@code Dipendente} rappresenta un dipendente dell'azienda */
public class Dipendente implements Comparable<Dipendente> {

    /** Mantiene i contatti di un dipendente come indirizzo, telefono, ecc. */
    public static class Contatti {
        /** @return  l'indirizzo del dipendente */
        public String getIndirizzo() { return indirizzo; }

        /** @return  il recapito telefonico del dipendente */
        public String getTelefono() { return telefono; }

        private Contatti() {
            indirizzo = "";
            telefono = "";
        }

        private String indirizzo;
        private String telefono;
    }

    /** Crea un dipendente con i dati specificati. Da usarsi solamente se al
     * dipendente è già stato assegnato un codice.
     * @param nomeCognome  nome e cognome del dipendente
     * @param stipendio  stipendio del dipendente
     * @param codice  codice del dipendente */
    public Dipendente(String nomeCognome, double stipendio, long codice) {
        this.nomeCognome = nomeCognome;
        this.stipendio = stipendio;
        contatti = new Contatti();
        this.codice = codice;
        codiceUsato(codice);    // Comunica che il codice è usato
    }

    /** Crea un dipendente con i dati specificati.
     * @param nomeCognome  nome e cognome del dipendente
     * @param stipendio  stipendio del dipendente */
    public Dipendente(String nomeCognome, double stipendio) {
        this(nomeCognome, stipendio, nuovoCodice());
    }

    /** Crea un dipendente con il dato nome e cognome e lo stipendio a zero.
     * @param nomeCognome  nome e cognome del dipendente */
    public Dipendente(String nomeCognome) { this(nomeCognome, 0.0); }

    @Override
    public int compareTo(Dipendente o) {
        return (codice < o.codice ? -1 : (codice > o.codice ? 1 : 0));
    }

    /** @return il nome e cognome di questo dipendente */
    public String getNomeCognome() { return nomeCognome; }

    /** @return lo stipendio di questo dipendente */
    public double getStipendio() { return stipendio; }

    /** Imposta un nuovo stipendio per questo dipendente.
     * @param stip  l'importo del nuovo stipendio
     * @throws IllegalArgumentException  se lo stipendio è negativo */
    public void setStipendio(double stip) {
        if (stip < 0)
            throw new IllegalArgumentException("Stipendio non può essere negativo");
        stipendio = stip;
    }

    /** @return il codice di questo dipendente */
    public long getCodice() { return codice; }

    /** @return i contatti di questo dipendente */
    public Contatti getContatti() { return contatti; }

    /** Imposta l'indirizzo di questo dipendente.
     * @param ind  il nuovo indirizzo
     * @throws NullPointerException  se indirizzo è null */
    public void setIndirizzo(String ind) {
        Objects.requireNonNull(ind, "Indirizzo non può essere null");
        contatti.indirizzo = ind;
    }

    /** Imposta il recapito telefonico di questo dipendente.
     * @param tel  il nuovo numero di telefono */
    public void setTelefono(String tel) { contatti.telefono = tel; }

    /** @return il supervisore di questo dipendente */
    public Dipendente getSupervisore() { return supervisore; }

    /** Imposta il supervisore di questo dipendente.
     * @param s  il supervisore */
    public void setSupervisore(Dipendente s) { supervisore = s; }

    @Override
    public String toString() {
        //return getClass().getName()+"["+nomeCognome+","+codice+","+stipendio+"]";
        return mp.reflect.Utils.toString(this);
    }




    private static long ultimoCodice;    // Ultimo codice usato

    private static long nuovoCodice() {  // Ritorna un nuovo codice
        ultimoCodice++;
        return ultimoCodice;
    }

    // Aggiorna la generazione dei codici tenendo conto che il dato codice è in uso
    private static void codiceUsato(long codice) {
        ultimoCodice = Math.max(ultimoCodice, codice);
    }

    private String nomeCognome;
    private double stipendio;
    private Contatti contatti;
    private Dipendente supervisore;  // Inizialmente null
    private final long codice;       // Il codice del dipendente
}
