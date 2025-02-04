package uoc.ds.pr;

import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;

import java.time.LocalDate;


public interface Library {

    public static final int MAX_NUM_READERS = 24;
    public static final int MAX_NUM_WORKERS = 18;
    public static final int MAX_BOOK_STACK = 10;
    public static final int MAXIMUM_NUMBER_OF_BOOKS = 3;

    public enum LoanState {
        INPROGRESS,
        COMPLETED,
        DELAYED
    }


    public void addReader(String id, String name, String surname, String docId, LocalDate birthDate, String birthPlace, String address);

    public void addWorker(String id, String name, String surname);

    public void storeBook (String bookId, String title, String publisher, String edition, int publicationYear, String isbn, String author, String theme);

    public CatalogedBook catalogBook(String workerId) throws NoBookException, WorkerNotFoundException;

    public void lendBook(String loanId, String readerId, String bookId, String workerId, LocalDate date, LocalDate expirationDate)
        throws ReaderNotFoundException, BookNotFoundException, WorkerNotFoundException, NoBookException, MaximumNumberOfBooksException;

    public Loan giveBackBook(String loanId, LocalDate date) throws LoanNotFoundException;

    public int timeToBeCataloged(String bookId, int lotPreparationTime, int bookCatalogTime) throws BookNotFoundException, InvalidLotPreparationTimeException, InvalidCatalogTimeException;

    public Iterator<Loan> getAllLoansByReader(String readerId) throws NoLoansException;

    public Iterator<Loan> getAllLoansByState(String readerId, LoanState state) throws NoLoansException;

    public Iterator<Loan> getAllLoansByBook(String bookId) throws NoLoansException;

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
    public int numBooks();
    public int numStacks();


    public int numCatalogBooks();
    public int numCatalogBooksInWorker(String workerId);
    public int totalCatalogBooksByWorker(String workerId);
    public int numCopies(String bookId);

    public int numLoans();
    public int numLoansByWorker(String workerId);
    public int numLoansByBook(String bookId);
    public int numCurrentLoansByReader(String readerId);
    public int numClosedLoansByWorker(String workerId);
    public int numClosedLoansByReader(String readerId);

}


