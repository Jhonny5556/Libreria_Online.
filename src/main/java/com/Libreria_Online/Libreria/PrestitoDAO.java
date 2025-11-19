package com.Libreria_Online.Libreria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestitoDAO {

    private static final String URL = "jdbc:postgresql://localhost:5432/Libreria_Online";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin"; 

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 1. READ - MODIFICATO PER GESTIRE LA PASSWORD
    public List<Prestito> getAllPrestiti() {
        List<Prestito> lista = new ArrayList<>();
        
        // Query che unisce le tabelle
        String sql = "SELECT p.*, " +
                     "u.nome, u.cognome, u.genere, u.eta, u.ruolo, " + 
                     "l.isbn, l.titolo, l.autore, l.anno, l.genere_libro " +
                     "FROM Prestiti p " +
                     "JOIN Utenti u ON p.utente_rif = u.utente_id " +
                     "JOIN Libri l ON p.libro_rif = l.libro_id " +
                     "ORDER BY p.prestito_id DESC"; 

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // QUI ERA L'ERRORE: Dobbiamo creare l'Utente con il NUOVO costruttore (con password)
                // Passiamo "N/A" (o stringa vuota) come password perché qui non ci serve
                Utente u = new Utente(
                    rs.getInt("utente_rif"),
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("genere"),
                    rs.getInt("eta"),
                    rs.getString("ruolo"),
                    "N/A" // <--- AGGIUNTA QUESTA RIGA FONDAMENTALE
                );

                Libro l = new Libro(
                    rs.getInt("libro_rif"),
                    rs.getString("isbn"),
                    rs.getString("titolo"),
                    rs.getString("autore"),
                    rs.getInt("anno"),
                    rs.getString("genere_libro")
                );

                Prestito p = new Prestito(
                    rs.getInt("prestito_id"),
                    rs.getString("data_prestito"),
                    rs.getString("data_restituzione"),
                    u,
                    l
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            // Se c'è un errore, lo stampiamo in console per capire
            System.out.println("ERRORE LETTURA PRESTITI: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // 2. CREATE
    public Prestito createPrestito(Prestito p) {
        String sql = "INSERT INTO Prestiti (utente_rif, libro_rif, data_prestito, data_restituzione) VALUES (?, ?, ?, ?) RETURNING prestito_id";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, p.utente.utente_id);
            pstmt.setInt(2, p.libro.libro_id);
            
            if (p.data_prestito == null || p.data_prestito.isEmpty()) {
                pstmt.setDate(3, new Date(System.currentTimeMillis()));
            } else {
                pstmt.setDate(3, Date.valueOf(p.data_prestito));
            }
            
            if (p.data_restituzione != null && !p.data_restituzione.isEmpty()) {
                pstmt.setDate(4, Date.valueOf(p.data_restituzione));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                p.prestito_id = rs.getInt("prestito_id");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return p;
    }

    // 3. DELETE
    public boolean deletePrestito(int id) {
        String sql = "DELETE FROM Prestiti WHERE prestito_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public boolean updatePrestito(Prestito p) { return false; } 
}