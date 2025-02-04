package uoc.ds.pr.model;

import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.util.DSLinkedList;

import java.util.Comparator;

public class CatalogedBook {
    public static final Comparator<CatalogedBook> CMP = (cb1, cb2)->cb1.getIsbn().compareTo(cb2.getIsbn());
    public static final Comparator<CatalogedBook> CMP_BOOKID = (cb1, cb2)->cb1.getBookId().compareTo(cb2.getBookId());

    public static final Comparator<CatalogedBook> CMP_V = (cb1, cb2) -> Integer.compare(cb1.loanList.size(), cb2.loanList.size());

    public DSLinkedList<Loan> loanList;

    private Book book;

    private int totalCopies;
    private int copies;

    public CatalogedBook(Book book) {
        this.book = book;
        this.copies = 1;
        this.loanList = new DSLinkedList<>(Loan.CMP);
    }

    public CatalogedBook(String bookId) {
        this.book = new Book(bookId);
    }

    public void incCopies() {
        copies++;
        totalCopies++;
    }

    public String getBookId() {
        return (book!=null?book.getBookId():null);
    }

    public String getIsbn() {
        return (book!=null?book.getIsbn():null);
    }
    public String getTitle() {
        return (book!=null?book.getTitle():null);
    }

    public int numCopies() {
        return copies;
    }

    public void decreaseCopies() {
        copies--;
    }

    public int numLoans() {
        return this.loanList.size();
    }

    public void addLoan(Loan loan) {
        loanList.insertEnd(loan);
    }

    public Iterator<Loan> getAllLoans() {
        return loanList.values();
    }

    public Book getBook() {
        return book;
    }
}
