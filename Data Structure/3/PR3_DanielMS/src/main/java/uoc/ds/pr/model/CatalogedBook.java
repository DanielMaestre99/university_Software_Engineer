package uoc.ds.pr.model;

import edu.uoc.ds.adt.helpers.Position;
import edu.uoc.ds.adt.nonlinear.AVLTree;
import edu.uoc.ds.adt.nonlinear.Dictionary;
import edu.uoc.ds.adt.nonlinear.graphs.Vertex;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.traversal.Iterator;
import edu.uoc.ds.traversal.Traversal;
import uoc.ds.pr.util.DSLinkedList;

import java.util.Comparator;

public class CatalogedBook extends Book implements Comparable<CatalogedBook> {
    public static final Comparator<CatalogedBook> CMP = (cb1, cb2)->cb1.getIsbn().compareTo(cb2.getIsbn());
    public static final Comparator<CatalogedBook> CMP_BOOKID = (cb1, cb2)->cb1.getIsbn().compareTo(cb2.getIsbn());

    public static final Comparator<CatalogedBook> CMP_V = (cb1, cb2) -> Integer.compare(cb1.loanList.size(), cb2.loanList.size());
    public static final Comparator<CatalogedBook> CMP_RATING =  (cb1, cb2) -> Double.compare(cb1.getGlobalRate(), cb2.getGlobalRate());;

    protected DSLinkedList<Loan> loanList;

    protected LinkedList<Copy> copies;

    private AVLTree<Rating> ratings;
    private int sumRate;
    private int globalRate;
    private int unAvailableCopies;

    public CatalogedBook(String title, String publisher, String edition, int publicationYear, String isbn, Author author, Theme theme) {
        super(title, publisher, edition, publicationYear, isbn, author, theme);
        this.loanList = new DSLinkedList<>(Loan.CMP);
        this.copies = new LinkedList<>();
        this.ratings = new AVLTree<>();
        this.sumRate = 0;
        this.globalRate = 0;
        this.unAvailableCopies = 0;
    }

    public CatalogedBook(Book b) {
        this(b.getTitle(), b.getPublisher(),  b.getEdition(), b.getPublicationYear(), b.getIsbn(), b.getAuthor(), b.getTheme());
    }


    public String getIsbn() {
        return super.getIsbn();
    }
    public String getTitle() {
        return super.getTitle();
    }

    public int numCopies() {
        return copies.size();
    }


    public void addLoan(Loan loan) {
        loanList.insertEnd(loan);
    }

    public void addCopy(Copy copy) {
        copy.setCatalogedBook(this);
        copies.insertEnd(copy);
    }

    public Iterator<Copy> copies() {
        return copies.values();
    }


    public int numAvailableCopies() {
        LinkedList<Copy> availableCopies = new LinkedList<>();
        Iterator<Copy> it = copies.values();
        Copy copy = null;
        while (it.hasNext()) {
            copy = it.next();
            if (copy.isAvailable()) {
                availableCopies.insertEnd(copy);
            }
        }
        return availableCopies.size();
    }

    public void removeCopy(Copy copy) {
        Traversal<Copy> traversal = copies.positions();
        boolean found = false;
        Position<Copy> position = null;
        while (!found && traversal.hasNext()) {
            position = traversal.next();
            found = (position.getElem().equals(copy));
        }
        if (found) {
            copies.delete(position);
        }
    }

    public void addRating(Rating rating) {
        this.sumRate+=rating.getRate();
        ratings.add(rating);
    }

    public double getGlobalRate() {
        return sumRate / ratings.size();
    }
    public Iterator<Rating> ratings() {
        return ratings.values();
    }

    public void incUnAvailableCopies() {
        unAvailableCopies++;
    }

    public void decUnAvailableCopies() {
        unAvailableCopies--;
    }

    public void setAvailable(Copy copy) {
        boolean found = false;
        Iterator<Copy> it = copies.values();
        Copy copyAux = null;
        while (it.hasNext() && !found) {
            copyAux = it.next();
            found = copyAux.getCopyId().equals(copy.getCopyId());
        }
        if (found) {
            copy.setAvailable();
        }
    }

    @Override
    public int compareTo(CatalogedBook o) {
        return CMP.compare(this, o);
    }

}
