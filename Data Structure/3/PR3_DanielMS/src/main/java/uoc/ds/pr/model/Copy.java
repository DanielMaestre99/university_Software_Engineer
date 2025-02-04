package uoc.ds.pr.model;

import edu.uoc.ds.adt.nonlinear.PriorityQueue;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.traversal.Iterator;

import java.util.Comparator;

public class Copy {
    public static final Comparator<Copy> CMP =  (th1, th2)->th1.getCopyId().compareTo(th2.getCopyId());

    private final String copyId;

    private final Book pendingBook;
    private CatalogedBook catalogedBook;

    private boolean isAvailable;

    private PriorityQueue<Request> requests;

    private LinkedList<Loan> loans;

    public Copy(String copyId, Book book) {
        this.copyId = copyId;
        this.pendingBook = book;
        this.requests = new PriorityQueue<>(Request.CMP_LEVEL);
        this.isAvailable = true;
        loans = new LinkedList<>();
    }

    public void setCatalogedBook(CatalogedBook catalogedBook) {
        this.catalogedBook = catalogedBook;
    }

    public Book getPendingBook() {
        return pendingBook;
    }


    public String getCopyId() {
        return copyId;
    }

    public String getIsbnPendingBook() {
        return (this.pendingBook!=null?pendingBook.getIsbn():null);
    }

    public String getIsbn() {
        return (catalogedBook!=null?catalogedBook.getIsbn():null);
    }

    public String getTitle() {
        return (catalogedBook!=null?catalogedBook.getTitle():null);
    }

    public Request firstRequest() {
        return (!requests.isEmpty()?requests.poll():null);
    }

    public void addRequest(Request request) {
        requests.add(request);
    }

    public boolean readerAlreadyHasRequest4Copy(String readerId) {
        Iterator<Request> it = requests.values();
        boolean found = false;
        Request request = null;
        while (!found && it.hasNext())  {
            request = it.next();
            found = request.getReader().getId().equals(readerId);
        }

        return found;
    }

    public void notAvailable() {
        isAvailable = false;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public int numLoans() {
        return loans.size();
    }

    public void addLoan(Loan loan) {
        loans.insertEnd(loan);
    }

    public Iterator<Loan> getAllLoans() {
        return loans.values();
    }

    public CatalogedBook getCatalogBook() {
        return catalogedBook;
    }

    public void setAvailable() {
        isAvailable = true;
    }
}
