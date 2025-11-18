package com.Libreria_Online.Libreria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestitoDAO {

    private static final String URL = "jdbc:postgresql://localhost:5432/Libreria_Online";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin"; // <--- LA TUA PASSWORD!

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 1. READ (JOIN per ottenere nomi e titoli)
    public List<Prestito> getAllPrestiti() {
        List<Prestito> lista = new ArrayList<>();
        // Questa query unisce le 3 tabelle
        String sql = "SELECT p.*, " +
                     "u.nome, u.cognome, u.genere, u.eta, " +
                     "l.isbn, l.titolo, l.autore, l.anno, l.genere_libro " +
                     "FROM Prestiti p " +
                     "JOIN Utenti u ON p.utente_rif = u.utente_id " +
                     "JOIN Libri l ON p.libro_rif = l.libro_id " +
                     "ORDER BY p.prestito_id DESC"; // I più recenti prima

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Ricostruiamo l'oggetto Utente
                Utente u = new Utente(
                    rs.getInt("utente_rif"),
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("genere"),
                    rs.getInt("eta"),
                    rs.getString("ruolo")
                );

                // Ricostruiamo l'oggetto Libro
                Libro l = new Libro(
                    rs.getInt("libro_rif"),
                    rs.getString("isbn"),
                    rs.getString("titolo"),
                    rs.getString("autore"),
                    rs.getInt("anno"),
                    rs.getString("genere_libro")
                );

                // Creiamo il Prestito con gli oggetti dentro
                Prestito p = new Prestito(
                    rs.getInt("prestito_id"),
                    rs.getString("data_prestito"),
                    rs.getString("data_restituzione"),
                    u,
                    l
                );
                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 2. CREATE
    public Prestito createPrestito(Prestito p) {
        String sql = "INSERT INTO Prestiti (utente_rif, libro_rif, data_prestito, data_restituzione) VALUES (?, ?, ?, ?) RETURNING prestito_id";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Prendiamo gli ID dagli oggetti annidati
            pstmt.setInt(1, p.utente.utente_id);
            pstmt.setInt(2, p.libro.libro_id);
            
            // Convertiamo le date String in SQL Date
            pstmt.setDate(3, Date.valueOf(p.data_prestito.substring(0, 10))); // "YYYY-MM-DD"
            
            if (p.data_restituzione != null && !p.data_restituzione.isEmpty()) {
                pstmt.setDate(4, Date.valueOf(p.data_restituzione.substring(0, 10)));
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

    // 3. UPDATE
    public boolean updatePrestito(Prestito p) {
        String sql = "UPDATE Prestiti SET utente_rif=?, libro_rif=?, data_prestito=?, data_restituzione=? WHERE prestito_id=?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, p.utente.utente_id);
            pstmt.setInt(2, p.libro.libro_id);
            pstmt.setDate(3, Date.valueOf(p.data_prestito.substring(0, 10)));
            
            if (p.data_restituzione != null && !p.data_restituzione.isEmpty()) {
                pstmt.setDate(4, Date.valueOf(p.data_restituzione.substring(0, 10)));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            
            pstmt.setInt(5, p.prestito_id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // 4. DELETE
    public boolean deletePrestito(int id) {
        String sql = "DELETE FROM Prestiti WHERE prestito_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
