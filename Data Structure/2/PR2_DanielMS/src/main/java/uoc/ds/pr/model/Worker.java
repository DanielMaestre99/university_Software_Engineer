package uoc.ds.pr.model;

import uoc.ds.pr.util.DSLinkedList;

public class Worker {
    private String id;
    private String name;
    private String surname;

    private int totalCatalogBooks;

    private DSLinkedList<CatalogedBook> catalogedBooks;
    private DSLinkedList<Loan> loanList;
    private DSLinkedList<Loan> closedLoanList;

    public Worker(String id, String name, String surname) {
        setId(id);
        update(name, surname);
        catalogedBooks = new DSLinkedList(CatalogedBook.CMP);
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

    public void catalogBook(CatalogedBook catalogedBook) {
        incTotalCatalogBooks();
        catalogedBooks.insertEnd(catalogedBook);
    }

    public int numCatalogBooks() {
        return catalogedBooks.size();
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