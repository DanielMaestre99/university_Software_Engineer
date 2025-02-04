package uoc.ds.pr.util;

import uoc.ds.pr.Library;
import uoc.ds.pr.model.Book;

public class BooksDataHelper {

    public static void addBooksData(Library theLibrary, String[][] booksData) {
        Book book = null;
        for (String[] bookData: booksData) {
            theLibrary.storeBook(bookData[0], bookData[1], bookData[2], bookData[3], Integer.parseInt(bookData[4]),
                    bookData[5], bookData[6], bookData[7]);
        }
    }
}
