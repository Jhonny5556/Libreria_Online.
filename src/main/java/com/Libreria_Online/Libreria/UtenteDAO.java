package com.Libreria_Online.Libreria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

    private static final String URL = "jdbc:postgresql://localhost:5432/Libreria_Online";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin"; 

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public Utente login(String nome, String password, String ruolo) {
        // --- STAMPE DI DEBUG (Guarda la console di Eclipse!) ---
        System.out.println("--- TENTATIVO DI LOGIN ---");
        System.out.println("Cerco Nome: [" + nome + "]");
        System.out.println("Cerco Pass: [" + password + "]");
        System.out.println("Cerco Ruolo: [" + ruolo + "]");
        // -------------------------------------------------------

        String sql = "SELECT * FROM Utenti WHERE nome = ? AND password = ? AND ruolo = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nome);
            pstmt.setString(2, password);
            pstmt.setString(3, ruolo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✅ TROVATO: " + rs.getString("nome")); // Debug
                    return new Utente(
                        rs.getInt("utente_id"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("genere"),
                        rs.getInt("eta"),
                        rs.getString("ruolo"),
                        rs.getString("password")
                    );
                } else {
                    System.out.println("❌ NESSUNA CORRISPONDENZA TROVATA NEL DB"); // Debug
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 1. READ - Aggiornato per leggere anche la password
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
                    rs.getString("ruolo"),
                    rs.getString("password") // <--- Aggiornato
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 2. CREATE - Aggiornato per salvare la password
    public Utente createUtente(Utente u) {
        // Aggiunto campo 'password' nella query
        String sql = "INSERT INTO Utenti (nome, cognome, genere, eta, ruolo, password) VALUES (?, ?, ?, ?, ?, ?) RETURNING utente_id";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.nome);
            pstmt.setString(2, u.cognome);
            pstmt.setString(3, u.genere);
            pstmt.setInt(4, u.eta);
            
            // Gestione Ruolo default
            if (u.ruolo == null || u.ruolo.isEmpty()) {
                pstmt.setString(5, "CLIENTE");
            } else {
                pstmt.setString(5, u.ruolo);
            }

            // Gestione Password default (se non arriva nulla, mettiamo "1234")
            if (u.password == null || u.password.isEmpty()) {
                pstmt.setString(6, "1234");
            } else {
                pstmt.setString(6, u.password);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                u.utente_id = rs.getInt("utente_id");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return u;
    }

    // 3. UPDATE - Aggiornato per modificare anche la password se serve
    public boolean updateUtente(Utente u) {
        String sql = "UPDATE Utenti SET nome=?, cognome=?, genere=?, eta=?, ruolo=?, password=? WHERE utente_id=?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.nome);
            pstmt.setString(2, u.cognome);
            pstmt.setString(3, u.genere);
            pstmt.setInt(4, u.eta);
            pstmt.setString(5, u.ruolo);
            pstmt.setString(6, u.password); // <--- Aggiornato
            pstmt.setInt(7, u.utente_id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // 4. DELETE - Invariato
    public boolean deleteUtente(int id) {
        String sql = "DELETE FROM Utenti WHERE utente_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}