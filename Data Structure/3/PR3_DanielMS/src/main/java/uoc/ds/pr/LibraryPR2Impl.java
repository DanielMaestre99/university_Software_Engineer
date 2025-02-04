package uoc.ds.pr;

import java.time.LocalDate;

import edu.uoc.ds.adt.nonlinear.Dictionary;
import edu.uoc.ds.adt.nonlinear.DictionaryAVLImpl;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.graphs.UnDirectedGraph;
import edu.uoc.ds.adt.nonlinear.graphs.UnDirectedGraphImpl;
import edu.uoc.ds.adt.nonlinear.graphs.Vertex;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;
import uoc.ds.pr.util.BookWareHouse;
import uoc.ds.pr.util.DSArray;
import uoc.ds.pr.util.OrderedVector;

import static uoc.ds.pr.LibraryPR3.MAX_NUM_THEMES;


public class LibraryPR2Impl implements Library {

    private final HashTable<String, Reader> readers;
    private final DSArray<Worker> workers;
    private final BookWareHouse bookWareHouse;

    protected final Dictionary<String, Copy> catalogedCopies;
    protected int unAvailableCopies;
    protected final Dictionary<String, CatalogedBook> catalogedBooks;


    private final Dictionary<String, Loan> loans;
    private final OrderedVector<Reader> mostFrequentReader;
    private final OrderedVector<CatalogedBook> mostReadBook;

    protected final OrderedVector<Reader> bestReaders;
    protected final OrderedVector<CatalogedBook> bestBooks;

    protected HashTable<String, Author> authors;
    protected DSArray<Theme> themes;

    protected LinkedList<Copy> destroyedCopies;

    protected UnDirectedGraph<Book, Integer> recommendationSystem;



    public LibraryPR2Impl() {
        readers = new HashTable<>();
        workers = new DSArray<>(MAX_NUM_WORKERS);
        bookWareHouse = new BookWareHouse();
        catalogedCopies = new DictionaryAVLImpl<>();
        unAvailableCopies = 0;
        catalogedBooks = new DictionaryAVLImpl<>();
        loans = new DictionaryAVLImpl<>();
        mostFrequentReader = new OrderedVector<>(MAX_NUM_READERS, Reader.CMP_V);
        // The number of stored books is unknown and relatively small, in the range of a few hundred,
        mostReadBook = new OrderedVector<>(300, CatalogedBook.CMP_V);

        themes = new DSArray<>(MAX_NUM_THEMES);
        authors = new HashTable<>();
        destroyedCopies = new LinkedList<>();
        bestReaders = new OrderedVector<>(500, Reader.CMP_REQ);
        bestBooks = new OrderedVector<>(500, CatalogedBook.CMP_RATING);
        recommendationSystem = new UnDirectedGraphImpl<>();
    }

    @Override
    public void addReader(String id, String name, String surname, String docId, LocalDate birthDate, String birthPlace, String address, int points) {
        Reader reader = getReader(id);
        if (reader == null) {
            reader = new Reader(id, name, surname, docId, birthDate, birthPlace, address, points);
            this.readers.put(id, reader);
        } else {
            reader.update(name, surname, docId, birthDate, birthPlace, address, points);
        }
    }

    public void addWorker(String id, String name, String surname) {
        Worker worker = getWorker(id);
        if (worker == null) {
            worker = new Worker(id, name, surname);
            this.workers.put(id, worker);
        } else {
            worker.update(name, surname);
        }
    }

    public void storeCopy(String copyId, String title, String publisher, String edition, int publicationYear, String isbn, String author, String theme) throws AuthorNotFoundException, ThemeNotFoundException {
        Author theAuthor = authors.get(author);
        if (theAuthor == null) {
            throw new AuthorNotFoundException();
        }

        Theme theTheme = themes.get(theme);
        if (theTheme == null) {
            throw new ThemeNotFoundException();
        }

        Copy copy = bookWareHouse.storeCopy(copyId, title, publisher, edition, publicationYear, isbn, theAuthor, theTheme);
        Book pendingBook = copy.getPendingBook();

        theAuthor.addBook(pendingBook);
        theTheme.addBook(pendingBook);
    }


    public Copy catalogCopy(String workerId) throws NoBookException, WorkerNotFoundException {
        Worker worker = getWorker2(workerId);

        if (bookWareHouse.isEmpty()) {
            throw new NoBookException();
        }

        Copy copy = bookWareHouse.getBookPendingCataloging();

        CatalogedBook catalogedBook = catalogedBooks.get(copy.getIsbnPendingBook());
        if (catalogedBook == null) {
            Book b = copy.getPendingBook();
            catalogedBook = new CatalogedBook(b);
            worker.incTotalCatalogBooks();
        }
        if (catalogedBook.getVertex()==null){
            Vertex<Book> vertex = null;
            vertex = recommendationSystem.newVertex(catalogedBook);
            catalogedBook.setVertex(vertex);
            copy.getPendingBook().setVertex(vertex);
            Book pending = copy.getPendingBook();
            catalogedBook.getAuthor().updateBook(pending);
        }

        catalogedBook.addCopy(copy);
        catalogedCopies.put(copy.getCopyId(), copy);
        catalogedBooks.put(catalogedBook.getIsbn(), catalogedBook);
        worker.catalogCopy(copy);

        return copy;
    }

    @Override
    public Request lendCopy(String loanId, String copyId, String workerId, LocalDate date, LocalDate expirationDate)
            throws CopyNotFoundException, WorkerNotFoundException, MaximumNumberOfBooksException, NoRequestException,
            CopyNotAvailableException {

        Worker worker = getWorker2(workerId);
        Copy copy = catalogedCopies.get(copyId);
        if (copy==null) {
            throw new CopyNotFoundException();
        }

        if (!copy.isAvailable()) {
            throw new CopyNotAvailableException();
        }

        CatalogedBook catalogedBook = catalogedBooks.get(copy.getIsbn());

        Request request = copy.firstRequest();

        if (request == null) {
            throw new NoRequestException();
        }

        Reader reader = request.getReader();

        if (reader.hasMaximumNumberOfBooks()) {
            throw new MaximumNumberOfBooksException();
        }

        Loan loan = reader.addNewLoan(loanId, copy, date, expirationDate);
        loan.addReader(reader);
        catalogedBook.addLoan(loan);
        catalogedBook.incUnAvailableCopies();
        worker.addLoan(loan);
        copy.notAvailable();
        copy.addLoan(loan);
        loans.put(loan.getLoanId(), loan);
        mostFrequentReader.update(reader);
        mostReadBook.update(catalogedBook);
        unAvailableCopies++;
        return request;
    }

    public Loan giveBackBook(String loanId, LocalDate date, CopyReturnStatus status) throws LoanNotFoundException {
        Loan loan = getLoan2(loanId);
        Reader reader = loan.getReader();
        Worker worker = loan.getWorker();

        if (loan.isDelayed(date)) {
            loan.setState(LoanState.DELAYED);
            reader.incPoints(POINTS_DELAYED);
        } else {
            loan.setState(LoanState.COMPLETED);
            reader.incPoints(POINTS_COMPLETED);
        }

        Copy copy = loan.getCopy();
        CatalogedBook catalogedBook = copy.getCatalogBook();

        if (status == CopyReturnStatus.DESTROYED) {
            catalogedCopies.delete(copy.getCopyId());
            catalogedBook.removeCopy(copy);
            destroyedCopies.insertEnd(copy);
            reader.incPoints(POINTS_DESTROYED);
        } else if (status == CopyReturnStatus.GOOD) {
            unAvailableCopies--;
            catalogedBook.setAvailable(copy);
            reader.incPoints(POINTS_GOOD);
        } else if (status == CopyReturnStatus.BAD) {
            unAvailableCopies--;
            catalogedBook.setAvailable(copy);
            reader.incPoints(POINTS_BAD);
        }

        worker.addClosedLoan(loan);
        reader.addClosedLoan(loan);

        bestReaders.delete(reader);
        bestReaders.update(reader);

        return loan;
    }

    public int timeToBeCataloged(String bookId, int lotPreparationTime, int bookCatalogTime) throws BookNotFoundException, InvalidLotPreparationTimeException, InvalidCatalogTimeException {
        if (lotPreparationTime < 0) {
            throw new InvalidLotPreparationTimeException();
        }
        if (bookCatalogTime < 0) {
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

    public Iterator<Loan> getAllLoansByCopy(String copyId) throws NoLoansException {
        Copy copy = catalogedCopies.get(copyId);
        Iterator<Loan> it = null;
        if (copy != null) {
            it = copy.getAllLoans();
            if (!it.hasNext()) {
                throw new NoLoansException();
            }
        } else throw new NoLoansException();
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
        return catalogedBook;
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
        return catalogedBooks.size();
    }

    public int numStacks() {
        return bookWareHouse.numStacks();
    }

    protected CatalogedBook getCatalogedBook(String bookId) {
        CatalogedBook catalogedBook = catalogedBooks.get(bookId);
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
        return catalogedBooks.size();
    }

    public int numCopies() {
        return bookWareHouse.numCopies();
    }

    public int numCatalogCopies() {
        return catalogedCopies.size() - unAvailableCopies + numDestroyedLoans();
    }


    public int numCatalogCopiesByWorker(String workerId) {
        Worker worker = this.workers.get(workerId);
        return (worker != null ? worker.numCatalogCopies() : 0);
    }

    public int numCatalogBooksByWorker(String workerId) {
        Worker worker = this.workers.get(workerId);
        return (worker != null ? worker.totalCatalogBooks() : 0);
    }


    public int numCopies(String bookId) {
        CatalogedBook catalogedBook = catalogedBooks.get(bookId);
        return (catalogedBook != null ? catalogedBook.numCopies() : 0);
    }

    public int numAvailableCopies(String bookId) {
        CatalogedBook catalogedBook = catalogedBooks.get(bookId);
        return (catalogedBook != null ? catalogedBook.numAvailableCopies() : 0);
    }

    public Loan getLoan(String loanId) {
        Loan loan = loans.get(loanId);
        return loan;
    }

    public Loan getLoan2(String loanId) throws LoanNotFoundException {
        Loan loan = getLoan(loanId);
        if (loan == null) {
            throw new LoanNotFoundException();
        }
        return loan;
    }

    public int numLoans() {
        return loans.size();
    }

    public int numLoansByWorker(String workerId) {
        Worker worker = getWorker(workerId);
        return (worker != null ? worker.numLoans() : 0);
    }

    public int numClosedLoansByWorker(String workerId) {
        Worker worker = getWorker(workerId);
        return (worker != null ? worker.numClosedLoans() : 0);
    }

    public int numLoansByCopy(String copyId) {
        Copy catalogedCopy = catalogedCopies.get(copyId);
        return (catalogedCopy != null ? catalogedCopy.numLoans() : 0);
    }

    public int numCurrentLoansByReader(String readerId) {
        Reader reader = getReader(readerId);
        return (reader != null ? reader.numLoans() : 0);
    }

    public int numClosedLoansByReader(String readerId) {
        Reader reader = getReader(readerId);
        return (reader != null ? reader.numClosedLoans() : 0);
    }

    public int numDestroyedLoans() {
        return destroyedCopies.size();
    }
}
