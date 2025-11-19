package com.Libreria_Online.Libreria;
	
import java.util.Random;
public class GeneratoreDati {
	//Creiamo una lista per mischiare
	
	private String[] nomi = {"Giovanni" , "Ferdinando", "Gianpaolo", "Mattia", "Walter", "Mario", "Giulia", "Elena", "Francesco"};
	private String [] cognomi = {"Sergio" , "Russo", "Abbrescia", "Caponio", "Dimauro", "Pesce", "Paradiso", "Porto", "Gabbani"};
	private String []generiUtente={"M", "F"};
	
	private String[] titoliLibro = {"Il Signore degli Anelli", "Harry Potter", "La Divina Commedia", "1984", "Il Piccolo Principe", "Don Chisciotte", "Guerra e Pace", "Moby Dick", "Orgoglio e Pregiudizio", "Il Grande Gatsby"};
    private String[] autori = {"J.R.R. Tolkien", "J.K. Rowling", "Dante Alighieri", "George Orwell", "Antoine de Saint-Exupéry", "Miguel de Cervantes", "Lev Tolstoj", "Herman Melville", "Jane Austen", "F. Scott Fitzgerald"};
    private String[] generiLibro = {"Fantasy", "Romanzo", "Classico", "Fantascienza", "Avventura", "Storico"};

    //Qui gestiamo gli strumenti
    
    private Random random = new Random();
    private UtenteDAO utenteDAO = new UtenteDAO();
    private LibroDAO libroDAO = new LibroDAO();
    
    // Generiamo tot utenti e tot Libri
    
    public void popolaDatabase(int numeroUtenti , int numeroLibri) {
    	System.out.println("Inizio generazione dati..");
    	
    	for (int i = 0; i < numeroUtenti; i++) {
            String nome = nomi[random.nextInt(nomi.length)];
            String cognome = cognomi[random.nextInt(cognomi.length)];
            String genere = generiUtente[random.nextInt(generiUtente.length)];
            int eta = 18 + random.nextInt(60); // Età tra 18 e 78
            
            // Qui applichiamo la logica che 1 su 10 sia il bibliotecario il resto clienti
            String ruolo = (i % 10 == 0) ? "BIBLIOTECARIO" : "CLIENTE";

            Utente u = new Utente(0, nome, cognome, genere, eta, ruolo);
            utenteDAO.createUtente(u);
        }

        // 2. Generiamo i LIBRI
        for (int i = 0; i < numeroLibri; i++) {
            String titolo = titoliLibro[random.nextInt(titoliLibro.length)] + " Vol." + (i+1);
            String autore = autori[random.nextInt(autori.length)];
            String genere = generiLibro[random.nextInt(generiLibro.length)];
            int anno = 1900 + random.nextInt(124);
            String isbn = "ISBN-" + (10000 + i); 

            Libro l = new Libro(0, isbn, titolo, autore, anno, genere);
            libroDAO.createLibro(l);
        }

        System.out.println("Dati generati con successo!");
    }
    
    }
