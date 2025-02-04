package uoc.ds.pr.model;

import edu.uoc.ds.adt.nonlinear.graphs.Vertex;

import java.util.Comparator;
import java.util.Objects;

public class Book extends AbstractBook {

    private String edition;
    private String publisher;

    protected Vertex<Book> vertex;

    public Book (String title, String publisher, String edition, int publicationYear, String isbn, Author author, Theme theme) {
        super(title, publicationYear, isbn, author, theme);
        this.edition = edition;
        this.publisher = publisher;
    }

    public Book(String isbn) {
        super(null, 0, null, null, null);
        super.setIsbn(isbn);
    }

    protected String getPublisher() {
        return this.publisher;
    }

    public String getEdition() {
        return edition;
    }

    public void setVertex(Vertex<Book> vertex) {
        this.vertex = vertex;
    }

    public Vertex<Book> getVertex() {
        return vertex;
    }



}
