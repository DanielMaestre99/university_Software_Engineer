package uoc.ds.pr.model;

public class Rating implements Comparable<Rating> {
    private CatalogedBook catalogedBook;
    private Reader reader;
    private int rate;
    private String comment;

    public Rating(Reader reader, CatalogedBook catalogedBook, int rate, String comment) {
        this.reader = reader;
        this.catalogedBook = catalogedBook;
        this.rate = rate;
        this.comment = comment;
    }


    public String getIsbn() {
        return catalogedBook.getIsbn();
    }

    public Reader getReader() {
        return reader;
    }

    public String getComment() {
        return comment;
    }

    public int getRate() {
        return rate;
    }

    public CatalogedBook getCatalogBook() {
        return this.catalogedBook;
    }

    @Override
    public int compareTo(Rating o) {
        int ret = reader.compareTo(o.reader);
        if (ret == 0) {
            ret = catalogedBook.compareTo(o.catalogedBook);
            if (ret == 0) {
                ret = Integer.compare(rate, o.rate);
            }
        }
        return ret;
    }
}
