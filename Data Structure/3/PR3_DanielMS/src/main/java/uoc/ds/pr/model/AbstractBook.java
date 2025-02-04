package uoc.ds.pr.model;

import java.util.Objects;

public abstract class AbstractBook {
    private String isbn;
    private final String title;
    private final int publicationYear;

    private final Author author;
    private final Theme theme;



    public AbstractBook(String title, int publicationYear, String isbn, Author author, Theme theme) {
        this.isbn = isbn;
        this.title = title;
        this.publicationYear = publicationYear;
        this.author = author;
        this.theme = theme;
    }

    public Author getAuthor() {
        return author;
    }

    public Theme getTheme() {
        return theme;
    }

    protected int getPublicationYear() {
        return publicationYear;
    }


    public String getIsbn() {
        return isbn;
    }
    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        AbstractBook that = (AbstractBook) o;
        return isbn.equals(that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    protected void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
