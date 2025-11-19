package com.Libreria_Online.Libreria;

import java.util.List;
import java.util.Random;

public class GeneratoreDati {
    
    private String[] nomi = {"Giovanni" , "Ferdinando", "Gianpaolo", "Mattia", "Walter", "Mario", "Giulia", "Elena", "Francesco"};
    private String[] cognomi = {"Sergio" , "Russo", "Abbrescia", "Caponio", "Dimauro", "Pesce", "Paradiso", "Porto", "Gabbani"};
    private String[] generiUtente={"M", "F"};
    
    private String[] titoliLibro = {"Il Signore degli Anelli", "Harry Potter", "La Divina Commedia", "1984", "Il Piccolo Principe", "Don Chisciotte", "Guerra e Pace", "Moby Dick", "Orgoglio e Pregiudizio", "Il Grande Gatsby"};
    private String[] autori = {"J.R.R. Tolkien", "J.K. Rowling", "Dante Alighieri", "George Orwell", "Antoine de Saint-Exupéry", "Miguel de Cervantes", "Lev Tolstoj", "Herman Melville", "Jane Austen", "F. Scott Fitzgerald"};
    private String[] generiLibro = {"Fantasy", "Romanzo", "Classico", "Fantascienza", "Avventura", "Storico"};

    private Random random = new Random();
    private UtenteDAO utenteDAO = new UtenteDAO();
    private LibroDAO libroDAO = new LibroDAO();
    private PrestitoDAO prestitoDAO = new PrestitoDAO(); // <--- AGGIUNTO QUESTO

    public void popolaDatabase(int numeroUtenti , int numeroLibri) {
        System.out.println("Inizio generazione dati..");
        
        // 1. Generiamo UTENTI
        for (int i = 0; i < numeroUtenti; i++) {
            String nome = nomi[random.nextInt(nomi.length)];
            String cognome = cognomi[random.nextInt(cognomi.length)];
            String genere = generiUtente[random.nextInt(generiUtente.length)];
            int eta = 18 + random.nextInt(60);
            String ruolo = (i % 10 == 0) ? "BIBLIOTECARIO" : "CLIENTE";
            
            // Aggiungiamo password di default
            Utente u = new Utente(0, nome, cognome, genere, eta, ruolo, "1234");
            utenteDAO.createUtente(u);
        }

        // 2. Generiamo LIBRI
        for (int i = 0; i < numeroLibri; i++) {
            String titolo = titoliLibro[random.nextInt(titoliLibro.length)] + " Vol." + (i+1);
            String autore = autori[random.nextInt(autori.length)];
            String genere = generiLibro[random.nextInt(generiLibro.length)];
            int anno = 1900 + random.nextInt(124);
            String isbn = "ISBN-" + (10000 + i); 

            Libro l = new Libro(0, isbn, titolo, autore, anno, genere);
            libroDAO.createLibro(l);
        }

        // 3. Generiamo PRESTITI (NOVITÀ)
        // Dobbiamo prima leggere chi esiste nel DB per collegarli
        List<Utente> utentiEsistenti = utenteDAO.getAllUtenti();
        List<Libro> libriEsistenti = libroDAO.getAllLibri();

        if (!utentiEsistenti.isEmpty() && !libriEsistenti.isEmpty()) {
            // Creiamo 10 prestiti a caso
            for (int i = 0; i < 10; i++) {
                Utente uRandom = utentiEsistenti.get(random.nextInt(utentiEsistenti.size()));
                Libro lRandom = libriEsistenti.get(random.nextInt(libriEsistenti.size()));
                
                String dataPrestito = "2024-01-" + (10 + i); // Date fittizie
                String dataRestituzione = (i % 2 == 0) ? "2024-02-01" : null; // Metà restituiti, metà no

                Prestito p = new Prestito(0, dataPrestito, dataRestituzione, uRandom, lRandom);
                prestitoDAO.createPrestito(p);
            }
            System.out.println("Creati anche 10 prestiti di prova!");
        }

        System.out.println("Dati generati con successo!");
    }
}