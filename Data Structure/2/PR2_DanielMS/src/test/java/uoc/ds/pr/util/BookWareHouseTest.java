package uoc.ds.pr.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uoc.ds.pr.model.Book;
import uoc.ds.pr.util.BookWareHouse;
import uoc.ds.pr.util.BooksData;

public class BookWareHouseTest {


    private BookWareHouse bookWareHouse;

    @Before
    public void setUp() {
        bookWareHouse = new BookWareHouse();
    }

    @Test
    public void storeBookTest() {
        Assert.assertEquals(0, bookWareHouse.numBooks());


        addBooksData(BooksData.booksData1);
        Assert.assertEquals(15, bookWareHouse.numBooks());
        Assert.assertEquals(2, bookWareHouse.numStacks());

        addBooksData(BooksData.booksData2);
        Assert.assertEquals(4, bookWareHouse.numStacks());
        Assert.assertEquals(31, bookWareHouse.numBooks());

        addBooksData(BooksData.booksData3);
        Assert.assertEquals(5, bookWareHouse.numStacks());
        Assert.assertEquals(49, bookWareHouse.numBooks());

        addBooksData(BooksData.booksData4);
        Assert.assertEquals(7, bookWareHouse.numStacks());
        Assert.assertEquals(67, bookWareHouse.numBooks());

        addBooksData(BooksData.booksData5);
        Assert.assertEquals(9, bookWareHouse.numStacks());
        Assert.assertEquals(86, bookWareHouse.numBooks());

        addBooksData(BooksData.booksData6);
        Assert.assertEquals(12, bookWareHouse.numStacks());
        Assert.assertEquals(115, bookWareHouse.numBooks());

        addBooksData(BooksData.booksData7);
        Assert.assertEquals(13, bookWareHouse.numStacks());
        Assert.assertEquals(130, bookWareHouse.numBooks());

        bookWareHouse.storeBook("FK1", "Die Verwandlung", "Die Weißen Blätter ",
                "First Edition", 1915, "978-84-8256-840-9", "Frank Kafka", "existentialism");

        Assert.assertEquals(14, bookWareHouse.numStacks());
        Assert.assertEquals(131, bookWareHouse.numBooks());
    }

    @Test
    public void catalogingTest() {
        storeBookTest();

        Book b1 = bookWareHouse.getBookPendingCataloging();
        Assert.assertEquals(14, bookWareHouse.numStacks());
        Assert.assertEquals(130, bookWareHouse.numBooks());

        Book b2 = bookWareHouse.getBookPendingCataloging();
        Assert.assertEquals(14, bookWareHouse.numStacks());
        Assert.assertEquals(129, bookWareHouse.numBooks());

        Book b = null;
        for (int i=0; i<8; i++) {
            b = bookWareHouse.getBookPendingCataloging();
        }

        Assert.assertEquals(13, bookWareHouse.numStacks());
        Assert.assertEquals(121, bookWareHouse.numBooks());
    }

    @Test
    public void getPositionTest() {
        storeBookTest();

        Assert.assertEquals(14, bookWareHouse.numStacks());
        Assert.assertEquals(131, bookWareHouse.numBooks());

        BookWareHouse.Position position = bookWareHouse.getPosition("JV2c");
        Assert.assertEquals(0, position.getNumStack());
        Assert.assertEquals(0, position.getNum());

        position = bookWareHouse.getPosition("JV4c");
        Assert.assertEquals(0, position.getNumStack());
        Assert.assertEquals(5, position.getNum());

        position = bookWareHouse.getPosition("JV7d");
        Assert.assertEquals(0, position.getNumStack());
        Assert.assertEquals(9, position.getNum());

        position = bookWareHouse.getPosition("HP6c");
        Assert.assertEquals(1, position.getNumStack());
        Assert.assertEquals(0, position.getNum());

        position = bookWareHouse.getPosition("JV2a");
        Assert.assertEquals(1, position.getNumStack());
        Assert.assertEquals(9, position.getNum());

        position = bookWareHouse.getPosition("HP6f");
        Assert.assertEquals(2, position.getNumStack());
        Assert.assertEquals(0, position.getNum());

        position = bookWareHouse.getPosition("HP4a");
        Assert.assertEquals(2, position.getNumStack());
        Assert.assertEquals(9, position.getNum());

        position = bookWareHouse.getPosition("JA10");
        Assert.assertEquals(12, position.getNumStack());
        Assert.assertEquals(0, position.getNum());

        position = bookWareHouse.getPosition("JA5");
        Assert.assertEquals(12, position.getNumStack());
        Assert.assertEquals(9, position.getNum());


        position = bookWareHouse.getPosition("FK1");
        Assert.assertEquals(13, position.getNumStack());
        Assert.assertEquals(0, position.getNum());



    }


    private void addBooksData(String[][] booksData) {
        Book book = null;
        for (String[] bookData: booksData) {
            book = new Book(bookData[0], bookData[1], bookData[2], bookData[3], Integer.parseInt(bookData[4]),
                    bookData[5], bookData[6], bookData[7]);
            bookWareHouse.storeBook(book);
        }
    }
}
