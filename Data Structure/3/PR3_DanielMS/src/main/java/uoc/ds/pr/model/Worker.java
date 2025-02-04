package uoc.ds.pr.model;

import uoc.ds.pr.util.DSLinkedList;

public class Worker {
    private String id;
    private String name;
    private String surname;

    private int totalCatalogBooks;

    private final DSLinkedList<Copy> catalogedCopies;
    private final DSLinkedList<Loan> loanList;
    private final DSLinkedList<Loan> closedLoanList;

    public Worker(String id, String name, String surname) {
        setId(id);
        update(name, surname);
        catalogedCopies = new DSLinkedList(CatalogedBook.CMP);
        loanList = new DSLinkedList<>(Loan.CMP);
        closedLoanList = new DSLinkedList<>(Loan.CMP);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void update(String name, String surname) {
        setName(name);
        setSurname(surname);
    }

    public void catalogCopy(Copy catalogedCopy) {
        catalogedCopies.insertEnd(catalogedCopy);
    }

    public int numCatalogCopies() {
        return catalogedCopies.size();
    }

    public int totalCatalogBooks() {
        return totalCatalogBooks;
    }

    public void incTotalCatalogBooks() {
        totalCatalogBooks++;
    }

    public void addLoan(Loan loan) {
        loanList.insertEnd(loan);
        loan.addWorker(this);
    }

    public void addClosedLoan(Loan loan) {
        closedLoanList.insertEnd(loan);
    }

    public int numLoans() {
        return loanList.size();
    }

    public int numClosedLoans() {
        return closedLoanList.size();
    }
}