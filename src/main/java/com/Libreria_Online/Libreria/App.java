package com.Libreria_Online.Libreria;

import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        
        // 1. Inizializziamo i DAO (Gestori del Database)
        LibroDAO libroDAO = new LibroDAO();
        UtenteDAO utenteDAO = new UtenteDAO(); // <--- NUOVO: Gestore Utenti

        // 2. Avviamo il Server
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost(); 
                });
            });
        }).start(7070);
     // --- FUNZIONALITÀ AVANZATA 7: Gestione delle Eccezioni ---
        app.exception(Exception.class, (e, ctx) -> {
            // 1. Scriviamo l'errore nella console di Eclipse (per te che sviluppi)
            System.out.println("🔥 ERRORE CATTURATO: " + e.getMessage());
            e.printStackTrace();

            // 2. Rispondiamo al Frontend con un messaggio chiaro invece di crashare male
            ctx.status(500); // 500 = Errore Server
            ctx.json("{\"errore\": \"Si è verificato un problema nel server: " + e.getMessage() + "\"}");
        });

        System.out.println("Server pronto su http://localhost:7070");

        // ==========================================
        //              API LIBRI
        // ==========================================
        
        app.get("/api/libri", ctx -> ctx.json(libroDAO.getAllLibri()));

        app.post("/api/libri", ctx -> {
            Libro nuovoLibro = ctx.bodyAsClass(Libro.class);
            Libro libroCreato = libroDAO.createLibro(nuovoLibro);
            ctx.status(201).json(libroCreato);
        });

        app.put("/api/libri/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Libro dati = ctx.bodyAsClass(Libro.class);
            dati.libro_id = id;
            if (libroDAO.updateLibro(dati)) {
                ctx.status(200).json(dati);
            } else {
                ctx.status(404).result("Non trovato");
            }
        });

        app.delete("/api/libri/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (libroDAO.deleteLibro(id)) {
                ctx.status(204);
            } else {
                ctx.status(404).result("Non trovato");
            }
        });

        // ==========================================
        //              API UTENTI (NUOVE)
        // ==========================================

        // 1. GET - Lista utenti
        app.get("/api/utenti", ctx -> ctx.json(utenteDAO.getAllUtenti()));

        // 2. POST - Aggiungi utente
        app.post("/api/utenti", ctx -> {
            Utente nuovo = ctx.bodyAsClass(Utente.class);
            Utente creato = utenteDAO.createUtente(nuovo);
            ctx.status(201).json(creato);
        });

        // 3. PUT - Modifica utente
        app.put("/api/utenti/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Utente dati = ctx.bodyAsClass(Utente.class);
            dati.utente_id = id; // Assicuriamo che l'ID sia quello dell'URL
            
            if (utenteDAO.updateUtente(dati)) {
                ctx.status(200).json(dati);
            } else {
                ctx.status(404).result("Utente non trovato");
            }
        });

        // 4. DELETE - Elimina utente
        app.delete("/api/utenti/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (utenteDAO.deleteUtente(id)) {
                ctx.status(204);
            } else {
                ctx.status(404).result("Utente non trovato");
            }
        });
     // ... (dopo le API Utenti) ...

        // ==========================================
        //              API PRESTITI (NUOVE)
        // ==========================================
        PrestitoDAO prestitoDAO = new PrestitoDAO();

        app.get("/api/prestiti", ctx -> ctx.json(prestitoDAO.getAllPrestiti()));

        app.post("/api/prestiti", ctx -> {
            Prestito nuovo = ctx.bodyAsClass(Prestito.class);
            ctx.status(201).json(prestitoDAO.createPrestito(nuovo));
        });

        app.put("/api/prestiti/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Prestito dati = ctx.bodyAsClass(Prestito.class);
            dati.prestito_id = id;
            if (prestitoDAO.updatePrestito(dati)) {
                ctx.status(200).json(dati);
            } else {
                ctx.status(404).result("Prestito non trovato");
            }
        });

        app.delete("/api/prestiti/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (prestitoDAO.deletePrestito(id)) {
                ctx.status(204);
            } else {
                ctx.status(404).result("Prestito non trovato");
            }
        });

        // ==========================================
        //      FUNZIONALITÀ 5: STATISTICHE 📊
        // ==========================================
        // Deve stare qui in fondo perché deve "vedere" tutti i DAO (libro, utente, prestito)
        
        app.get("/api/stats", ctx -> {
            
            // 1. Conta tutto usando i metodi .size() delle liste
            int totLibri = libroDAO.getAllLibri().size();
            int totUtenti = utenteDAO.getAllUtenti().size();
            int totPrestiti = prestitoDAO.getAllPrestiti().size();

            // 2. Crea una stringa JSON a mano
            String jsonStats = String.format(
                "{\"utenti\": %d, \"libri\": %d, \"prestiti\": %d}", 
                totUtenti, totLibri, totPrestiti
            );
            
            // 3. Restituisci il JSON
            ctx.json(jsonStats);
        });

    } // <--- Fine del main
} // <--- Fine della classe App