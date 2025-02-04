package uoc.ds.pr;

import edu.uoc.ds.traversal.Iterator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.Book;
import uoc.ds.pr.model.CatalogedBook;
import uoc.ds.pr.model.Loan;
import uoc.ds.pr.model.Reader;
import uoc.ds.pr.util.BookWareHouseTest;
import uoc.ds.pr.util.BooksData;
import uoc.ds.pr.util.DateUtils;

import static uoc.ds.pr.util.BooksDataHelper.addBooksData;

public class LibraryPR2Test {

    protected Library theLibrary;

    @Before
    public void setUp() throws Exception {
        this.theLibrary = FactoryLibrary.getLibrary();
        Assert.assertEquals(17, this.theLibrary.numReaders());
    }

    @After
    public void tearDown() {
        this.theLibrary = null;
    }


    @Test
    public void addReaderTest() {
        Assert.assertEquals(17, this.theLibrary.numReaders());
        theLibrary.addReader("workerId18", "Jesus", "Linda", "979933A", DateUtils.createLocalDate("21-11-2001"), "Genova", "the street");
        Assert.assertEquals(18, this.theLibrary.numReaders());

        theLibrary.addReader("workerId19", "XXXXX", "Linda", "979933A", DateUtils.createLocalDate("21-11-2001"), "Genova", "the street");
        Assert.assertEquals(19, this.theLibrary.numReaders());

        theLibrary.addReader("workerId19", "Carles", "Fluvià", "97333A", DateUtils.createLocalDate("21-11-2011"), "Napoles", "the street");
        Assert.assertEquals(19, this.theLibrary.numReaders());
    }

    @Test
    public void addWorkerTest() {
        Assert.assertEquals(4, this.theLibrary.numWorkers());
        theLibrary.addWorker("workerId5", "Tato", "y Compañía");
        Assert.assertEquals(5, this.theLibrary.numWorkers());

        theLibrary.addWorker("workerId6", "XXXXX", "YYYYY");
        Assert.assertEquals(6, this.theLibrary.numWorkers());

        theLibrary.addWorker("workerId6", "Tania", "y Compañía");
        Assert.assertEquals(6, this.theLibrary.numWorkers());
    }

    /**
     * @see BooksData
     * @see BookWareHouseTest#storeBookTest()
     */
    @Test
    public void storeBookTest() {

        addBooksData(theLibrary, BooksData.booksData1);
        Assert.assertEquals(15, theLibrary.numBooks());
        Assert.assertEquals(2, theLibrary.numStacks());

        addBooksData(theLibrary, BooksData.booksData2);
        Assert.assertEquals(4, theLibrary.numStacks());
        Assert.assertEquals(31, theLibrary.numBooks());

        addBooksData(theLibrary, BooksData.booksData3);
        Assert.assertEquals(5, theLibrary.numStacks());
        Assert.assertEquals(49, theLibrary.numBooks());

        addBooksData(theLibrary, BooksData.booksData4);
        Assert.assertEquals(7, theLibrary.numStacks());
        Assert.assertEquals(67, theLibrary.numBooks());

        addBooksData(theLibrary, BooksData.booksData5);
        Assert.assertEquals(9, theLibrary.numStacks());
        Assert.assertEquals(86, theLibrary.numBooks());

        addBooksData(theLibrary, BooksData.booksData6);
        Assert.assertEquals(12, theLibrary.numStacks());
        Assert.assertEquals(115, theLibrary.numBooks());

        addBooksData(theLibrary, BooksData.booksData7);
        Assert.assertEquals(13, theLibrary.numStacks());
        Assert.assertEquals(130, theLibrary.numBooks());

        theLibrary.storeBook("FK1", "Die Verwandlung", "Die Weißen Blätter ",
                "First Edition", 1915, "978-84-8256-840-9", "Frank Kafka", "existentialism");

        Assert.assertEquals(14, theLibrary.numStacks());
        Assert.assertEquals(131, theLibrary.numBooks());

    }


    @Test
    public void catalogBookTest() throws DSException {
        Assert.assertThrows(WorkerNotFoundException.class, () ->
                theLibrary.catalogBook("XXXX"));


        Assert.assertThrows(NoBookException.class, () ->
                theLibrary.catalogBook("workerId1"));

        storeBookTest();

        Assert.assertEquals(14, theLibrary.numStacks());

        CatalogedBook b = null;
        b = theLibrary.catalogBook("workerId1");
        Assert.assertEquals("978-0451530960", b.getIsbn());
        Assert.assertEquals("Twenty Thousand Leagues Under the Sea", b.getTitle());
        Assert.assertEquals("JV2c", b.getBookId());

        Assert.assertEquals(1, b.numCopies());

        b = theLibrary.catalogBook("workerId2");
        Assert.assertEquals("978-0451530960", b.getIsbn());
        Assert.assertEquals("Twenty Thousand Leagues Under the Sea", b.getTitle());
        Assert.assertEquals(2, b.numCopies());
        Assert.assertEquals("JV2c", b.getBookId());

        Assert.assertEquals(1, theLibrary.numCatalogBooksInWorker("workerId1"));
        Assert.assertEquals(0, theLibrary.numCatalogBooksInWorker("workerId2"));
        Assert.assertEquals(1, theLibrary.totalCatalogBooksByWorker("workerId1"));
        Assert.assertEquals(1, theLibrary.totalCatalogBooksByWorker("workerId2"));


        b = theLibrary.catalogBook("workerId1");
        Assert.assertEquals("978-1853260257", b.getIsbn());
        Assert.assertEquals("The Adventures of Captain Hatteras", b.getTitle());
        Assert.assertEquals("JV5", b.getBookId());
        Assert.assertEquals(1, b.numCopies());


        b = theLibrary.catalogBook("workerId2");
        Assert.assertEquals("978-1605062234", b.getIsbn());
        Assert.assertEquals("The Steam House", b.getTitle());
        Assert.assertEquals("JV7c", b.getBookId());
        Assert.assertEquals(1, b.numCopies());

        b = theLibrary.catalogBook("workerId2");
        Assert.assertEquals("978-1103325575", b.getIsbn());
        Assert.assertEquals("The Begum's Fortune", b.getTitle());
        Assert.assertEquals("JV8", b.getBookId());
        Assert.assertEquals(1, b.numCopies());

        b = theLibrary.catalogBook("workerId2");
        Assert.assertEquals("978-1435149408", b.getIsbn());
        Assert.assertEquals("The Mysterious Island", b.getTitle());
        Assert.assertEquals("JV4c", b.getBookId());
        Assert.assertEquals(1, b.numCopies());

        b = theLibrary.catalogBook("workerId2");
        Assert.assertEquals("978-1516887907", b.getIsbn());
        Assert.assertEquals("Around the World in Eighty Days", b.getTitle());
        Assert.assertEquals("JV3", b.getBookId());
        Assert.assertEquals(1, b.numCopies());

        b = theLibrary.catalogBook("workerId1");
        Assert.assertEquals("978-0486268685", b.getIsbn());
        Assert.assertEquals("Journey to the Center of the Earth", b.getTitle());
        Assert.assertEquals("JV1", b.getBookId());
        Assert.assertEquals(1, b.numCopies());

        b = theLibrary.catalogBook("workerId3");
        Assert.assertEquals("978-1435149408", b.getIsbn());
        Assert.assertEquals("The Mysterious Island", b.getTitle());
        Assert.assertEquals("JV4c", b.getBookId());
        Assert.assertEquals(2, b.numCopies());

        b = theLibrary.catalogBook("workerId3");
        Assert.assertEquals("978-1605062234", b.getIsbn());
        Assert.assertEquals("The Steam House", b.getTitle());
        Assert.assertEquals("JV7c", b.getBookId());
        Assert.assertEquals(2, b.numCopies());

        Assert.assertEquals(3, theLibrary.numCatalogBooksInWorker("workerId1"));
        Assert.assertEquals(4, theLibrary.numCatalogBooksInWorker("workerId2"));
        Assert.assertEquals(0, theLibrary.numCatalogBooksInWorker("workerId3"));
        Assert.assertEquals(3, theLibrary.totalCatalogBooksByWorker("workerId1"));
        Assert.assertEquals(5, theLibrary.totalCatalogBooksByWorker("workerId2"));
        Assert.assertEquals(2, theLibrary.totalCatalogBooksByWorker("workerId3"));

        Assert.assertEquals(10, theLibrary.numCatalogBooks());
        Assert.assertEquals(13, theLibrary.numStacks());
    }


    @Test
    public void lendBookTest() throws DSException {
        Assert.assertThrows(ReaderNotFoundException.class, () ->
                theLibrary.lendBook("LOAN1", "XXXX", "JV2a", "workerId1",
                        DateUtils.createLocalDate("01-11-2024"), DateUtils.createLocalDate("15-11-2024")));

        Assert.assertThrows(WorkerNotFoundException.class, () ->
                theLibrary.lendBook("LOAN1", "readerId1", "JV2a", "xxxx",
                        DateUtils.createLocalDate("01-11-2024"), DateUtils.createLocalDate("15-11-2024")));

        Assert.assertThrows(BookNotFoundException.class, () ->
                theLibrary.lendBook("LOAN1", "readerId1", "XXXX", "workerId1",
                        DateUtils.createLocalDate("01-11-2024"), DateUtils.createLocalDate("15-11-2024")));

        catalogBookTest();

        Assert.assertEquals(2, theLibrary.numCopies("JV2c"));
        theLibrary.lendBook("LOAN1", "readerId1", "JV2c", "workerId1",
                DateUtils.createLocalDate("01-11-2024"), DateUtils.createLocalDate("15-11-2024"));
        Assert.assertEquals(1, theLibrary.numCopies("JV2c"));

        theLibrary.lendBook("LOAN2", "readerId2", "JV2c", "workerId1",
                DateUtils.createLocalDate("04-11-2024"), DateUtils.createLocalDate("17-11-2024"));
        Assert.assertEquals(0, theLibrary.numCopies("JV2c"));

        theLibrary.lendBook("LOAN3", "readerId2", "JV1", "workerId1",
                DateUtils.createLocalDate("04-11-2024"), DateUtils.createLocalDate("17-11-2024"));
        Assert.assertEquals(0, theLibrary.numCopies("JV1"));

        Assert.assertThrows(NoBookException.class, () ->
            theLibrary.lendBook("LOAN4", "readerId3", "JV2c", "workerId2",
                DateUtils.createLocalDate("07-11-2024"), DateUtils.createLocalDate("27-11-2024")));

        theLibrary.lendBook("LOAN5", "readerId2", "JV7c", "workerId1",
                DateUtils.createLocalDate("06-11-2024"), DateUtils.createLocalDate("18-11-2024"));
        Assert.assertEquals(1, theLibrary.numCopies("JV7c"));

        Assert.assertThrows(MaximumNumberOfBooksException.class, () ->
                theLibrary.lendBook("LOAN6", "readerId2", "JV7c", "workerId2",
                        DateUtils.createLocalDate("07-11-2024"), DateUtils.createLocalDate("27-11-2024")));


        Assert.assertEquals(4, theLibrary.numLoansByWorker("workerId1"));
        Assert.assertEquals(1, theLibrary.numCurrentLoansByReader("readerId1"));
        Assert.assertEquals(3, theLibrary.numCurrentLoansByReader("readerId2"));
        Assert.assertEquals(0, theLibrary.numCurrentLoansByReader("readerId3"));
        Assert.assertEquals(4, theLibrary.numLoans());
        Assert.assertEquals(2, theLibrary.numLoansByBook("JV2c"));
        Assert.assertEquals(1, theLibrary.numLoansByBook("JV7c"));
        Assert.assertEquals(1, theLibrary.numLoansByBook("JV1"));



    }


    @Test
    public void giveBackBookTest() throws DSException {
        Assert.assertThrows(LoanNotFoundException.class, () ->
                theLibrary.giveBackBook("XXXX",  DateUtils.createLocalDate("10-11-2024")));


        lendBookTest();

        Assert.assertEquals(3, theLibrary.numCurrentLoansByReader("readerId2"));
        Loan loan = theLibrary.giveBackBook("LOAN5",
                DateUtils.createLocalDate("10-11-2024"));

        Assert.assertEquals(Library.LoanState.COMPLETED, loan.getState());
        Assert.assertEquals(1, theLibrary.numClosedLoansByReader("readerId2"));
        Assert.assertEquals(1, theLibrary.numClosedLoansByWorker("workerId1"));
        Assert.assertEquals(2, theLibrary.numCurrentLoansByReader("readerId2"));

        loan = theLibrary.giveBackBook("LOAN3",
                DateUtils.createLocalDate("20-11-2024"));

        Assert.assertEquals(Library.LoanState.DELAYED, loan.getState());
        Assert.assertEquals(2, theLibrary.numClosedLoansByReader("readerId2"));
        Assert.assertEquals(2, theLibrary.numClosedLoansByWorker("workerId1"));
        Assert.assertEquals(1, theLibrary.numCurrentLoansByReader("readerId2"));
    }

    @Test
    public void timeToBeCatalogedTest() throws DSException {
        Assert.assertThrows(BookNotFoundException.class, () ->
                theLibrary.timeToBeCataloged("XXXX",  12, 3));

        catalogBookTest();

        Assert.assertThrows(InvalidLotPreparationTimeException.class, () ->
                theLibrary.timeToBeCataloged("JV8",  -1, 3));

        Assert.assertThrows(InvalidCatalogTimeException.class, () ->
                theLibrary.timeToBeCataloged("JV8",  12, -3));

        Assert.assertThrows(InvalidCatalogTimeException.class, () ->
                theLibrary.timeToBeCataloged("JV8",  12, -3));

        int t = theLibrary.timeToBeCataloged("HP4a",  8, 12);
//        Assert.assertEquals(244, t);
        Assert.assertEquals(256, t);


    }


    @Test
    public void getAllLoansByReaderTest() throws DSException {
        Assert.assertThrows(NoLoansException.class, () ->
            theLibrary.getAllLoansByReader("readerId8"));

        giveBackBookTest();

        Iterator<Loan> it = theLibrary.getAllLoansByReader("readerId2");
        Loan l = null;

        Assert.assertTrue(it.hasNext());
        l = it.next();

        Assert.assertEquals("JV7c", l.getBookId());
        Assert.assertEquals("The Steam House", l.getTitle());
        Assert.assertEquals("LOAN5", l.getLoanId());
        Assert.assertEquals(Library.LoanState.COMPLETED, l.getState());


        Assert.assertTrue(it.hasNext());
        l = it.next();

        Assert.assertEquals("JV1", l.getBookId());
        Assert.assertEquals("Journey to the Center of the Earth", l.getTitle());
        Assert.assertEquals("LOAN3", l.getLoanId());
        Assert.assertEquals(Library.LoanState.DELAYED, l.getState());


        Assert.assertTrue(it.hasNext());
        l = it.next();

        Assert.assertEquals("Twenty Thousand Leagues Under the Sea", l.getTitle());
        Assert.assertEquals("LOAN2", l.getLoanId());
        Assert.assertEquals(Library.LoanState.INPROGRESS, l.getState());

    }

    @Test
    public void getAllLoansByStateTest() throws DSException {
        Assert.assertThrows(NoLoansException.class, () ->
                theLibrary.getAllLoansByReader("readerId8"));

        giveBackBookTest();

        Iterator<Loan> it = theLibrary.getAllLoansByState("readerId2", Library.LoanState.DELAYED);
        Loan l = null;

        Assert.assertTrue(it.hasNext());
        l = it.next();

        Assert.assertEquals("Journey to the Center of the Earth", l.getTitle());
        Assert.assertEquals("LOAN3", l.getLoanId());
        Assert.assertEquals(Library.LoanState.DELAYED, l.getState());

        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void getAllLoansByBookTest() throws DSException {
        Assert.assertThrows(NoLoansException.class, () ->
                theLibrary.getAllLoansByBook("JV1"));

        giveBackBookTest();

        Iterator<Loan> it = theLibrary.getAllLoansByBook("JV1");

        Loan l = null;

        Assert.assertTrue(it.hasNext());
        l = it.next();

        Assert.assertEquals("Journey to the Center of the Earth", l.getTitle());
        Assert.assertEquals("LOAN3", l.getLoanId());
        Assert.assertEquals(Library.LoanState.DELAYED, l.getState());

        Assert.assertFalse(it.hasNext());

    }


    @Test
    public void getReaderTheMost() throws DSException {
        Assert.assertThrows(NoReaderException.class, () ->
                theLibrary.getReaderTheMost());

        giveBackBookTest();

        Reader reader = theLibrary.getReaderTheMost();
        Assert.assertEquals("readerId2", reader.getId());

    }

    @Test
    public void getMostReadBook() throws DSException {
        Assert.assertThrows(NoBookException.class, () ->
                theLibrary.getMostReadBook());

        giveBackBookTest();

        Book book = theLibrary.getMostReadBook();
        Assert.assertEquals("JV2c", book.getBookId());

    }

}