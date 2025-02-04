package uoc.ds.pr.model;

public abstract class AbstractBook {
    private String isbn;
    private String title;
    private int publicationYear;
    private String author;
    private String theme;

    public AbstractBook(String title, int publicationYear, String isbn, String author, String theme) {
        this.isbn = isbn;
        this.title = title;
        this.publicationYear = publicationYear;
        this.author = author;
        this.theme = theme;


    }


    public String getIsbn() {
        return isbn;
    }
    public String getTitle() {
        return title;
    }

}
