package uoc.ds.pr.util;

import uoc.ds.pr.model.Book;
import uoc.ds.pr.model.Reader;

import java.util.Comparator;

public class BookLabel implements Comparable<BookLabel> {

    public static final Comparator<BookLabel> CMP = (b1, b2)-> b2.compareTo(b1);
    private Book book;
    private int label;

    public BookLabel(Book book, int label) {
        this.book = book;
        this.label = label;
    }

    @Override
    public int compareTo(BookLabel b) {
        return Integer.compare(label, b.label);
    }

    public Book getBook() {
        return book;
    }
}