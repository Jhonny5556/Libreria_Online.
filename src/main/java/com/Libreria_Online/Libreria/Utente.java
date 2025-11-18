package com.Libreria_Online.Libreria;

public class Utente {
    public int utente_id;
    public String nome;
    public String cognome;
    public String genere;
    public int eta;
    public String ruolo;

    public Utente() {}

    public Utente(int utente_id, String nome, String cognome, String genere, int eta, String ruolo) {
        this.utente_id = utente_id;
        this.nome = nome;
        this.cognome = cognome;
        this.genere = genere;
        this.eta = eta;
        this.ruolo = ruolo;
    }
}