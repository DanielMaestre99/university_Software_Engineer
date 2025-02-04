package uoc.ds.pr;

import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;

import java.time.LocalDate;


public interface Library {

    int MAX_NUM_READERS = 24;
    int MAX_NUM_WORKERS = 18;
    int MAX_BOOK_STACK = 10;
    int MAXIMUM_NUMBER_OF_BOOKS = 3;

    int POINTS_DELAYED = -1;
    int POINTS_COMPLETED = 2;
    int POINTS_GOOD = 5;
    int POINTS_BAD = -10;
    int POINTS_DESTROYED = -100;

    enum LoanState {
        INPROGRESS,
        COMPLETED,
        DELAYED
    }

    enum CopyReturnStatus {
        GOOD (0),
        BAD (1),
        DESTROYED (2);
        private final int value;

        CopyReturnStatus(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    /**
     * To simplify the test suite, the addReader operation allows initializing the points that a 'reader' has
     * */
    public void addReader(String id, String name, String surname, String docId, LocalDate birthDate, String birthPlace, String address, int points);

    public void addWorker(String id, String name, String surname);


    public void storeCopy (String copyId, String title, String publisher, String edition, int publicationYear, String isbn, String author, String theme) throws AuthorNotFoundException, ThemeNotFoundException;


    public Copy catalogCopy(String workerId) throws NoBookException, WorkerNotFoundException;


    public Request lendCopy(String loanId, String copyId, String workerId, LocalDate date, LocalDate expirationDate)
        throws CopyNotFoundException, WorkerNotFoundException, MaximumNumberOfBooksException, NoRequestException, CopyNotAvailableException ;

    public Loan giveBackBook(String loanId, LocalDate date, CopyReturnStatus status) throws LoanNotFoundException;

    public int timeToBeCataloged(String bookId, int lotPreparationTime, int bookCatalogTime) throws BookNotFoundException, InvalidLotPreparationTimeException, InvalidCatalogTimeException;

    public Iterator<Loan> getAllLoansByReader(String readerId) throws NoLoansException;

    public Iterator<Loan> getAllLoansByState(String readerId, LoanState state) throws NoLoansException;

    public Iterator<Loan> getAllLoansByCopy(String copyId) throws NoLoansException;

    public Reader getReaderTheMost() throws NoReaderException;

    public Book getMostReadBook() throws NoBookException;

    /***********************************************************************************/
    /******************** AUX OPERATIONS  **********************************************/
    /***********************************************************************************/


    public Reader getReader(String id);
    public int numReaders();

    public Worker getWorker(String id);
    public int numWorkers();

    //
    // Books warehouse
    //
    public int numCopies();
    public int numStacks();


    public int numCatalogBooks();
    public int numCatalogCopies();
    public int numBooks();
    public int numCatalogCopiesByWorker(String workerId);
    public int numCatalogBooksByWorker(String workerId);
    public int numCopies(String bookId);
    public int numAvailableCopies(String bookId);

    public int numLoans();
    public int numLoansByWorker(String workerId);
    public int numLoansByCopy(String copyId);
    public int numCurrentLoansByReader(String readerId);
    public int numClosedLoansByWorker(String workerId);
    public int numClosedLoansByReader(String readerId);
    public int numDestroyedLoans();

}


