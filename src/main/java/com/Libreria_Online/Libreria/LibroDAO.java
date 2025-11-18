package com.Libreria_Online.Libreria; // <--- Niente .dao

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    // --- CONFIGURAZIONE DB ---
    private static final String URL = "jdbc:postgresql://localhost:5432/Libreria_Online"; 
    private static final String USER = "postgres"; 
    private static final String PASSWORD = "admin"; // <--- LA TUA PASSWORD QUI

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // --- 1. READ ---
    public List<Libro> getAllLibri() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM Libri ORDER BY libro_id";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Libro l = new Libro(
                    rs.getInt("libro_id"),
                    rs.getString("isbn"),
                    rs.getString("titolo"),
                    rs.getString("autore"),
                    rs.getInt("anno"),
                    rs.getString("genere_libro")
                );
                lista.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // --- 2. CREATE ---
    public Libro createLibro(Libro l) {
        String sql = "INSERT INTO Libri (isbn, titolo, autore, anno, genere_libro) VALUES (?, ?, ?, ?, ?) RETURNING libro_id";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.isbn);
            pstmt.setString(2, l.titolo);
            pstmt.setString(3, l.autore);
            pstmt.setInt(4, l.anno);
            pstmt.setString(5, l.genere_libro);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                l.libro_id = rs.getInt("libro_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return l;
    }

    // --- 3. UPDATE ---
    public boolean updateLibro(Libro l) {
        String sql = "UPDATE Libri SET isbn=?, titolo=?, autore=?, anno=?, genere_libro=? WHERE libro_id=?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.isbn);
            pstmt.setString(2, l.titolo);
            pstmt.setString(3, l.autore);
            pstmt.setInt(4, l.anno);
            pstmt.setString(5, l.genere_libro);
            pstmt.setInt(6, l.libro_id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 4. DELETE ---
    public boolean deleteLibro(int id) {
        String sql = "DELETE FROM Libri WHERE libro_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
