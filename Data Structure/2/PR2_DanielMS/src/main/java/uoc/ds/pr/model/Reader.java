package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.Set;
import edu.uoc.ds.adt.sequential.SetLinkedListImpl;
import edu.uoc.ds.traversal.Iterator;
import edu.uoc.ds.traversal.IteratorArrayImpl;
import edu.uoc.ds.traversal.MultipleIterator;
import uoc.ds.pr.Library;
import uoc.ds.pr.util.DSLinkedList;

import java.time.LocalDate;
import java.util.Comparator;

import static uoc.ds.pr.Library.MAXIMUM_NUMBER_OF_BOOKS;

public class Reader implements Comparable<Reader> {
    public static final Comparator<Reader> CMP = (r1, r2) -> r1.getId().compareTo(r2.getId());
    public static final Comparator<Reader> CMP_V = (r1, r2)->Double.compare(r1.numAllLoans(), r2.numAllLoans());


    private String id;
    private String name;
    private String surname;
    private String docId;
    private LocalDate birthDate;
    private String birthPlace;
    private String address;

    private DSLinkedList<Loan> closedLoans;
    private Loan[] currentLoans;
    private int numberCurrentLoans;
    public Reader(String id, String name, String surname, String docId,
                  LocalDate birthDate, String birthPlace, String address) {
        setId(id);
        update(name, surname, docId, birthDate, birthPlace, address);
        closedLoans = new DSLinkedList<>(Loan.CMP);
        currentLoans = new Loan[MAXIMUM_NUMBER_OF_BOOKS];
        numberCurrentLoans = 0;
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


    public void update(String name, String surname, String docId,
                       LocalDate birthDate, String birthPlace, String address) {
        setName(name);
        setSurname(surname);
        setDocId(docId);
        setBirthDate(birthDate);
        setBirthPlace(birthPlace);
        setAddress(address);

    }

    public boolean hasMaximumNumberOfBooks() {
        return (numberCurrentLoans == MAXIMUM_NUMBER_OF_BOOKS);
    }

    public Loan addNewLoan(String loanId, CatalogedBook catalogedBook, LocalDate date, LocalDate expirationDate) {
        Loan loan = new Loan(loanId, catalogedBook, date, expirationDate);
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

    public Iterator<Loan> getAllLoans() {
        Set<Loan> loanSet = new SetLinkedListImpl<>();
        Iterator<Loan> it = closedLoans.values();
        while (it.hasNext()) {
            loanSet.add(it.next());
        }

        for (Loan currentLoan: currentLoans) {
            if (currentLoan!=null) loanSet.add(currentLoan);
        }
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

    @Override
    public int compareTo(Reader o) {
        return this.getId().compareTo(o.getId());
    }
}
