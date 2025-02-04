package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.Set;
import edu.uoc.ds.adt.sequential.SetLinkedListImpl;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.Library;
import uoc.ds.pr.LibraryPR3;
import uoc.ds.pr.util.DSLinkedList;
import uoc.ds.pr.util.LevelHelper;

import java.time.LocalDate;
import java.util.Comparator;

import static uoc.ds.pr.Library.MAXIMUM_NUMBER_OF_BOOKS;

public class Reader implements Comparable<Reader> {
    public static final Comparator<Reader> CMP = (r1, r2) -> r1.getId().compareTo(r2.getId());
    public static final Comparator<Reader> CMP_V = (r1, r2)->Double.compare(r1.numAllLoans(), r2.numAllLoans());

    public static final Comparator<Reader> CMP_REQ = (r2, r1)->Integer.compare(r2.getPoints(), r1.getPoints());


    private String id;
    private String name;
    private String surname;
    private String docId;
    private LocalDate birthDate;
    private String birthPlace;
    private String address;

    private final DSLinkedList<Loan> closedLoans;
    private final Loan[] currentLoans;
    private int numberCurrentLoans;
    private int points;

    private LinkedList<Rating> ratings;


    public Reader(String id, String name, String surname, String docId,
                  LocalDate birthDate, String birthPlace, String address, int points) {
        setId(id);
        update(name, surname, docId, birthDate, birthPlace, address, points);
        closedLoans = new DSLinkedList<>(Loan.CMP);
        currentLoans = new Loan[MAXIMUM_NUMBER_OF_BOOKS];
        numberCurrentLoans = 0;
        this.points = points;
        ratings = new LinkedList<>();
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDocId() {
        return docId;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public String getAddress() {
        return address;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void update(String name, String surname, String docId,
                       LocalDate birthDate, String birthPlace, String address, int points) {
        setName(name);
        setSurname(surname);
        setDocId(docId);
        setBirthDate(birthDate);
        setBirthPlace(birthPlace);
        setAddress(address);
        setPoints(points);

    }

    public boolean hasMaximumNumberOfBooks() {
        return (numberCurrentLoans == MAXIMUM_NUMBER_OF_BOOKS);
    }

    public Loan addNewLoan(String loanId, Copy copy, LocalDate date, LocalDate expirationDate) {
        Loan loan = new Loan(loanId, copy, date, expirationDate);
        currentLoans[numberCurrentLoans++] = loan;
        return loan;
    }

    public int numLoans() {
        return numberCurrentLoans;
    }

    public int numClosedLoans() {
        return closedLoans.size();
    }

    public void addClosedLoan(Loan loan) {
        updateCurrentLoan(loan);
        closedLoans.insertEnd(loan);
    }

    private void updateCurrentLoan(Loan loan) {
        if (this.currentLoans[0].is(loan)) {
            lshift(0);
        }
        else if (this.currentLoans[1].is(loan)) {
            lshift(1);
        }
        else if (this.currentLoans[2].is(loan)) {
            lshift(2);
        }
        this.numberCurrentLoans--;

    }

    private void lshift(int pos) {
        for (int i = pos; i<MAXIMUM_NUMBER_OF_BOOKS-1; i++) {
            this.currentLoans[i]=this.currentLoans[i+1];
        }
    }

    public Set<Loan> getCurrentLoans() {
        Set<Loan> loanSet = new SetLinkedListImpl<>();
        for (Loan currentLoan: currentLoans) {
            if (currentLoan!=null) loanSet.add(currentLoan);
        }
        return loanSet;
    }
    public Iterator<Loan> getAllLoans() {
        Set<Loan> loanSet = new SetLinkedListImpl<>();
        Iterator<Loan> it = closedLoans.values();
        while (it.hasNext()) {
            loanSet.add(it.next());
        }

        loanSet.union(getCurrentLoans());

        return loanSet.values();
    }

    public int numAllLoans() {
        return numClosedLoans()+numLoans();

    }

    public Iterator<Loan> getAllLoansByState(Library.LoanState state) {
        Set<Loan> loanSet = new SetLinkedListImpl<>();
        Iterator<Loan> it = closedLoans.values();
        Loan loan = null;
        while (it.hasNext()) {
            loan = it.next();
            if (state.equals(loan.getState())) {
                loanSet.add(loan);
            }
        }

        for (Loan currentLoan: currentLoans) {
            if (currentLoan!=null && state.equals(currentLoan.getState())) {
                loanSet.add(currentLoan);
            }
        }
        return loanSet.values();
    }

    public LibraryPR3.Level getLevel() {
        return LevelHelper.getLevel(points);
    }


    @Override
    public int compareTo(Reader o) {
        return this.getId().compareTo(o.getId());
    }


    public boolean alreadyHasLoan4Copy(String copyId) {
        Set<Loan> currentLoans = getCurrentLoans();
        Iterator<Loan> it = currentLoans.values();

        boolean found = false;
        Loan loan = null;
        while (!found && it.hasNext()) {
            loan = it.next();
            found = loan.getCopyId().equals(copyId);
        }
        return found;
    }

    public void incPoints(int points) {
        this.points = this.points + points;
    }

    public boolean hasLoanByBook(String isbn) {
        Iterator<Loan> it = closedLoans.values();
        boolean found = false;
        Loan loan = null;
        while (!found && it.hasNext()) {
            loan = it.next();
            found = loan.getIsbn().equals(isbn);
        }
        return found;
    }

    public boolean hasRating(String isbn) {
        Iterator<Rating> it = ratings.values();
        boolean found = false;
        Rating rating = null;
        while (!found && it.hasNext()) {
            rating = it.next();
            found = rating.getIsbn().equals(isbn);
        }
        return found;
    }

    public void addRating(Rating rating) {
        ratings.insertEnd(rating);
    }

    public Set<Book> topBooks(CatalogedBook catalogedBook) {
        Theme theme = catalogedBook.getTheme();
        Author author = catalogedBook.getAuthor();

        Set<Book> topBooks = new SetLinkedListImpl<>();
        Iterator<Rating> it = ratings.values();

        boolean isTheSameBook = false;
        boolean isTheSameTheme = false;
        boolean isTheSameAuthor = false;
        Rating rating = null;
        Theme currentTheme = null;
        Author currentAuthor = null;

        while (it.hasNext()) {
            rating = it.next();
            isTheSameBook = catalogedBook.getIsbn().equals(rating.getIsbn());
            currentTheme = rating.getCatalogBook().getTheme();
            currentAuthor = rating.getCatalogBook().getAuthor();

            isTheSameTheme = currentTheme.equals(theme);
            isTheSameAuthor = rating.getCatalogBook().getAuthor().equals(author);


            if (rating.getRate()==5 && !isTheSameBook &&
                    (isTheSameTheme || isTheSameAuthor )) {
                topBooks.add(rating.getCatalogBook());
            }
        }

        return topBooks;
    }

    public Iterator<Rating> ratings() {
        return ratings.values();
    }

    public Set<Book> booksRead() {
        Set<Book> ret = new SetLinkedListImpl<>();
        Iterator<Rating> it = ratings.values();
        while (it.hasNext()) {
            ret.add(it.next().getCatalogBook());
        }
        return ret;
    }
}
