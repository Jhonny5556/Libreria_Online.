package com.Libreria_Online.Libreria;

public class Prestito {
    public int prestito_id;
    public String data_prestito;
    public String data_restituzione; // Può essere null
    
    // Oggetti annidati (così il JSON include i dettagli)
    public Utente utente; 
    public Libro libro;

    public Prestito() {}

    public Prestito(int prestito_id, String data_prestito, String data_restituzione, Utente utente, Libro libro) {
        this.prestito_id = prestito_id;
        this.data_prestito = data_prestito;
        this.data_restituzione = data_restituzione;
        this.utente = utente;
        this.libro = libro;
    }
}
