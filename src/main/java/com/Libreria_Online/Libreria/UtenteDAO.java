package com.Libreria_Online.Libreria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

    private static final String URL = "jdbc:postgresql://localhost:5432/Libreria_Online";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin"; // <--- CONTROLLA LA TUA PASSWORD

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 1. READ - AGGIORNATO CON "ruolo"
    public List<Utente> getAllUtenti() {
        List<Utente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Utenti ORDER BY utente_id";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Utente(
                    rs.getInt("utente_id"),
                    rs.getString("nome"),
                    rs.getString("cognome"),
                    rs.getString("genere"),
                    rs.getInt("eta"),
                    rs.getString("ruolo") // <--- NUOVO: Leggiamo il ruolo dal DB
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 2. CREATE - AGGIORNATO CON "ruolo"
    public Utente createUtente(Utente u) {
        // Ho aggiunto "ruolo" nella query
        String sql = "INSERT INTO Utenti (nome, cognome, genere, eta, ruolo) VALUES (?, ?, ?, ?, ?) RETURNING utente_id";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.nome);
            pstmt.setString(2, u.cognome);
            pstmt.setString(3, u.genere);
            pstmt.setInt(4, u.eta);
            
            // Se il ruolo non è specificato, mettiamo "CLIENTE" di default
            if (u.ruolo == null || u.ruolo.isEmpty()) {
                pstmt.setString(5, "CLIENTE");
            } else {
                pstmt.setString(5, u.ruolo);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                u.utente_id = rs.getInt("utente_id");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return u;
    }

    // 3. UPDATE - AGGIORNATO CON "ruolo"
    public boolean updateUtente(Utente u) {
        String sql = "UPDATE Utenti SET nome=?, cognome=?, genere=?, eta=?, ruolo=? WHERE utente_id=?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.nome);
            pstmt.setString(2, u.cognome);
            pstmt.setString(3, u.genere);
            pstmt.setInt(4, u.eta);
            pstmt.setString(5, u.ruolo); // <--- Aggiorniamo anche il ruolo
            pstmt.setInt(6, u.utente_id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // 4. DELETE - RIMANE UGUALE (Si cancella per ID)
    public boolean deleteUtente(int id) {
        String sql = "DELETE FROM Utenti WHERE utente_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}