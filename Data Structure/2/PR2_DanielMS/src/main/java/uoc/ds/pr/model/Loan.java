package uoc.ds.pr.model;

import uoc.ds.pr.Library;

import java.time.LocalDate;
import java.util.Comparator;

public class Loan {
    public static final Comparator<Loan> CMP = (cb1, cb2)->cb1.getLoanId().compareTo(cb2.getLoanId());

    private String loanId;
    private Library.LoanState state;
    private CatalogedBook catalogedBook;
    private LocalDate date;
    private LocalDate expirationDate;
    private Reader reader;
    private Worker worker;

    public Loan(String loanId) {
        this.loanId = loanId;
    }
    public Loan(String loanId, CatalogedBook catalogedBook, LocalDate date, LocalDate expirationDate) {
        this(loanId);
        this.state = Library.LoanState.INPROGRESS;
        this.catalogedBook = catalogedBook;
        this.date = date;
        this.expirationDate = expirationDate;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setState(Library.LoanState state) {
        this.state = state;
    }

    public boolean isDelayed(LocalDate date) {
        return date.compareTo(expirationDate)>0;
    }

    public boolean is(Loan loan) {
        return this.getLoanId().equals(loan.getLoanId());
    }

    public void addReader(Reader reader) {
        this.reader = reader;
    }

    public Reader getReader() {
        return reader;
    }

    public void addWorker(Worker worker) {
        this.worker = worker;
    }

    public Worker getWorker() {
        return worker;
    }

    public Library.LoanState getState() {
        return state;
    }

    public String getTitle() {
        return catalogedBook.getTitle();
    }

    public String getBookId() {
        return catalogedBook.getBookId();
    }
}
