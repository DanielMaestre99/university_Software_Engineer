package uoc.ds.pr.model;

import java.util.Comparator;

public class Book extends AbstractBook {

    private String bookId;
    private String edition;
    private String publisher;

    public Book (String bookId, String title, String publisher, String edition, int publicationYear, String isbn, String author, String theme) {
        super(title, publicationYear, isbn, author, theme);
        this.bookId = bookId;
        this.edition = edition;
        this.publisher = publisher;
    }

    public Book(String bookId) {
        super(null, 0, null, null, null);
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }
}
