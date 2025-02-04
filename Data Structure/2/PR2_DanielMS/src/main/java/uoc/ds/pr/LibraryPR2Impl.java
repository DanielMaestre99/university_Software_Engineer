package uoc.ds.pr;

import java.time.LocalDate;

import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;
import uoc.ds.pr.util.BookWareHouse;
import uoc.ds.pr.util.DSArray;
import uoc.ds.pr.util.DSLinkedList;
import uoc.ds.pr.util.OrderedVector;


public class LibraryPR2Impl implements Library {

    private DSArray<Reader> readers;
    private DSArray<Worker> workers;

    private BookWareHouse bookWareHouse;

    private DSLinkedList<CatalogedBook> catalogedBooks;
    private DSLinkedList<Loan> loanList;
    private OrderedVector<Reader> mostFrequentReader;
    private OrderedVector<CatalogedBook> mostReadBook;


    private int numCatalogedBooks;

    public LibraryPR2Impl() {
        readers = new DSArray<>(MAX_NUM_READERS);
        workers = new DSArray<>(MAX_NUM_WORKERS);
        bookWareHouse = new BookWareHouse();
        catalogedBooks = new DSLinkedList<>(CatalogedBook.CMP);
        loanList = new DSLinkedList<>(Loan.CMP);
        mostFrequentReader = new OrderedVector<>(MAX_NUM_READERS,Reader.CMP_V);
        // The number of stored books is unknown and relatively small, in the range of a few hundred,
        mostReadBook = new OrderedVector<>(300, CatalogedBook.CMP_V);
    }

    @Override
    public void addReader(String id, String name, String surname, String docId, LocalDate birthDate, String birthPlace, String address){
        Reader reader = getReader(id);
        if (reader == null) {
            reader = new Reader(id, name, surname, docId, birthDate, birthPlace, address);
            this.readers.put(id, reader);
        }
        else {
            reader.update(name, surname, docId, birthDate, birthPlace, address);
        }
    }

    public void addWorker(String id, String name, String surname) {
        Worker worker = getWorker(id);
        if (worker == null) {
            worker = new Worker(id, name, surname);
            this.workers.put(id, worker);
        }
        else {
            worker.update(name, surname);
        }
    }

    @Override
    public void storeBook (String bookId, String title, String publisher, String edition, int publicationYear, String isbn, String author, String theme) {
        bookWareHouse.storeBook(bookId, title, publisher, edition, publicationYear, isbn, author, theme);
    }

    public CatalogedBook catalogBook(String workerId) throws NoBookException, WorkerNotFoundException {
        Worker worker = getWorker2(workerId);

        if (bookWareHouse.isEmpty()) {
            throw new NoBookException();
        }

       Book b = bookWareHouse.getBookPendingCataloging();
       CatalogedBook catalogedBook = catalogedBooks.get(new CatalogedBook(b));
       if (catalogedBook == null) {
           catalogedBook = new CatalogedBook(b);
           catalogedBooks.insertEnd(catalogedBook);
           worker.catalogBook(catalogedBook);
       }
       else {
           catalogedBook.incCopies();
           worker.incTotalCatalogBooks();
       }
        numCatalogedBooks++;

        return catalogedBook;
    }

    public void lendBook(String loanId, String readerId, String bookId, String workerId, LocalDate date, LocalDate expirationDate)
            throws ReaderNotFoundException, BookNotFoundException, WorkerNotFoundException, NoBookException, MaximumNumberOfBooksException {

        Reader reader = getReader2(readerId);
        Worker worker = getWorker2(workerId);
        CatalogedBook catalogedBook = getCatalogedBook2(bookId);

        if (catalogedBook.numCopies() == 0) {
            throw new NoBookException();
        }

        if (reader.hasMaximumNumberOfBooks()) {
            throw new MaximumNumberOfBooksException();
        }

        Loan loan = reader.addNewLoan(loanId, catalogedBook, date, expirationDate);
        loan.addReader(reader);
        worker.addLoan(loan);
        catalogedBook.decreaseCopies();
        catalogedBook.addLoan(loan);
        loanList.insertEnd(loan);
        mostFrequentReader.update(reader);
        mostReadBook.update(catalogedBook);
    }

    public Loan giveBackBook(String loanId, LocalDate date) throws LoanNotFoundException {
        Loan loan = getLoan2(loanId);
        Worker worker = loan.getWorker();

        if (loan.isDelayed(date)) {
            loan.setState(LoanState.DELAYED);
        }
        else {
            loan.setState(LoanState.COMPLETED);
        }

        worker.addClosedLoan(loan);
        Reader reader = loan.getReader();
        reader.addClosedLoan(loan);

        return loan;
    }

    public int timeToBeCataloged(String bookId, int lotPreparationTime, int bookCatalogTime) throws BookNotFoundException, InvalidLotPreparationTimeException, InvalidCatalogTimeException {
        if (lotPreparationTime<0) {
            throw new InvalidLotPreparationTimeException();
        }
        if (bookCatalogTime<0) {
            throw new InvalidCatalogTimeException();
        }

        BookWareHouse.Position position = bookWareHouse.getPosition(bookId);
        if (position == null) {
            throw new BookNotFoundException();
        }

        int previousStacks = position.getNumStack(); // + 1 ;
        int numberInStack = position.getNum() + 1;

        int t1 = previousStacks * (lotPreparationTime + (MAX_BOOK_STACK * bookCatalogTime));
        int t2 = lotPreparationTime + numberInStack * bookCatalogTime;
        int t = t1 + t2;
        return t;
    }

    public Iterator<Loan> getAllLoansByReader(String readerId) throws NoLoansException {
        Reader reader = getReader(readerId);
        Iterator<Loan> it = reader.getAllLoans();
        if (!it.hasNext()) {
            throw new NoLoansException();
        }
        return it;
    }

    public Iterator<Loan> getAllLoansByState(String readerId, LoanState state) throws NoLoansException {
        Reader reader = getReader(readerId);
        Iterator<Loan> it = reader.getAllLoansByState(state);
        if (!it.hasNext()) {
            throw new NoLoansException();
        }
        return it;
    }

    public Iterator<Loan> getAllLoansByBook(String bookId) throws NoLoansException {
        CatalogedBook catalogedBook = getCatalogedBook(bookId);
        Iterator<Loan> it = null;
        if (catalogedBook!=null) {
            it = catalogedBook.getAllLoans();
            if (!it.hasNext()) {
                throw new NoLoansException();
            }
        }
        else  throw new NoLoansException();
        return it;
    }

    public Reader getReaderTheMost() throws NoReaderException {
        if (mostReadBook.isEmpty()) {
            throw new NoReaderException();
        }
        return mostFrequentReader.elementAt(0);
    }

    public Book getMostReadBook() throws NoBookException {
        if (mostReadBook.isEmpty()) {
            throw new NoBookException();
        }
        CatalogedBook catalogedBook = mostReadBook.elementAt(0);
        return catalogedBook.getBook();
    }


    /***********************************************************************************/
    /******************** AUX OPERATIONS  **********************************************/
    /***********************************************************************************/
    @Override
    public Reader getReader(String id) {
        return readers.get(id);
    }

    private Reader getReader2(String id) throws ReaderNotFoundException {
        Reader reader = getReader(id);
        if (reader == null) {
            throw new ReaderNotFoundException();
        }
        return reader;
    }

    @Override
    public int numReaders() {
        return readers.size();
    }

    @Override
    public Worker getWorker(String id) {
        return workers.get(id);
    }

    private Worker getWorker2(String id) throws WorkerNotFoundException {
        Worker worker = getWorker(id);
        if (worker == null) {
            throw new WorkerNotFoundException();
        }
        return worker;
    }
    @Override
    public int numWorkers() {
        return workers.size();
    }

    public int numBooks() {
        return bookWareHouse.numBooks();
    }
    public int numStacks() {
        return bookWareHouse.numStacks();
    }

    protected CatalogedBook getCatalogedBook(String bookId) {
        CatalogedBook catalogedBook = catalogedBooks.get(new CatalogedBook(bookId), CatalogedBook.CMP_BOOKID);
        return catalogedBook;
    }
    public CatalogedBook getCatalogedBook2(String bookId) throws BookNotFoundException {
        CatalogedBook catalogedBook = getCatalogedBook(bookId);
        if (catalogedBook == null) {
            throw new BookNotFoundException();
        }
        return catalogedBook;
    }
    public int numCatalogBooks() {
        return numCatalogedBooks;
    }
    public int numCatalogBooksInWorker(String workerId) {
        Worker worker = this.workers.get(workerId);
        return (worker!=null? worker.numCatalogBooks():0);
    }

    public int totalCatalogBooksByWorker(String workerId) {
        Worker worker = this.workers.get(workerId);
        return (worker!=null? worker.totalCatalogBooks():0);
    }


    public int numCopies(String bookId) {
        CatalogedBook catalogedBook = catalogedBooks.get(new CatalogedBook(bookId), CatalogedBook.CMP_BOOKID);
        return (catalogedBook!=null?catalogedBook.numCopies():0);
    }

    public Loan getLoan(String loanId) {
        Loan loan = loanList.get(new Loan(loanId));
        return loan;
    }

    public Loan getLoan2(String loanId) throws LoanNotFoundException{
        Loan loan = getLoan(loanId);
        if (loan == null) {
            throw new LoanNotFoundException();
        }
        return loan;
    }

    public int numLoans() {
        return loanList.size();
    }

    public int numLoansByWorker(String workerId) {
        Worker worker = getWorker(workerId);
        return (worker!=null?worker.numLoans():0);
    }

    public int numClosedLoansByWorker(String workerId) {
        Worker worker = getWorker(workerId);
        return (worker!=null?worker.numClosedLoans():0);
    }

    public int numLoansByBook(String bookId) {
        CatalogedBook catalogedBook = getCatalogedBook(bookId);
        return (catalogedBook!=null?catalogedBook.numLoans():0);
    }

    public int numCurrentLoansByReader(String readerId) {
        Reader reader = getReader(readerId);
        return (reader!=null?reader.numLoans():0);
    }

    public int numClosedLoansByReader(String readerId) {
        Reader reader = getReader(readerId);
        return (reader!=null?reader.numClosedLoans():0);
    }
}
