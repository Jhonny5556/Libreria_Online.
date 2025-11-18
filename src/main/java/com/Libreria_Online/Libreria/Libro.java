package com.Libreria_Online.Libreria; // <--- Niente .model

public class Libro {
    public int libro_id;
    public String isbn;
    public String titolo;
    public String autore;
    public int anno;
    public String genere_libro;

    public Libro() {}

    public Libro(int libro_id, String isbn, String titolo, String autore, int anno, String genere_libro) {
        this.libro_id = libro_id;
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.anno = anno;
        this.genere_libro = genere_libro;
    }
}