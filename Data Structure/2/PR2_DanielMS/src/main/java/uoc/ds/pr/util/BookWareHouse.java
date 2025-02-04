package uoc.ds.pr.util;

import edu.uoc.ds.adt.sequential.Stack;
import edu.uoc.ds.adt.sequential.StackArrayImpl;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.exceptions.NoBookException;
import uoc.ds.pr.model.Book;


import static uoc.ds.pr.Library.MAX_BOOK_STACK;

public class BookWareHouse {

    QueueLinkedList<StackArrayImpl<Book>> storedBooks;
    StackArrayImpl<Book> lastStack;

    int numFullStacks;

    public BookWareHouse() {
        storedBooks = new QueueLinkedList<>();
        lastStack = null;
        numFullStacks = 0;
    }

    public StackArrayImpl newStack() {
        lastStack = new StackArrayImpl<Book>(MAX_BOOK_STACK);
        storedBooks.add(lastStack);
        numFullStacks++;
        return lastStack;
    }

    public void storeBook (String bookId, String title, String publisher, String edition, int publicationYear, String isbn, String author, String theme) {
        storeBook(new Book(bookId, title, publisher, edition, publicationYear, isbn, author, theme));
    }
    public void storeBook(Book book) {
        if (( lastStack == null) || (lastStack.isFull()) ) {
            lastStack = newStack();
        }
        lastStack.push(book);
    }

    public Position getPosition(String bookId) {
        Iterator<StackArrayImpl<Book>> it = this.storedBooks.values();
        boolean found = false;
        int numStack = 0;
        int num = 0;

        while (it.hasNext() && !found) {
            num = processStack(bookId, it.next());
            found = num >= 0;
            if (!found) {
                numStack++;
            }
        }
        return (found?new Position(numStack, num):null);
    }

    protected int processStack(String bookId, StackArrayImpl<Book> stack) {
        int num = -1;
        boolean found = false;
        Iterator<Book> it = stack.values();
        Book book = null;
        while (it.hasNext() && !found) {
            book = it.next();
            found = book.getBookId().equals(bookId);
            num++;
        }
        return (found?num:-1);


    }

    public Book getBookPendingCataloging() {
        Book pending = null;
        Stack<Book> firstStack = this.storedBooks.peek();
        if (firstStack.size()>1) {
            pending = firstStack.pop();
        }
        else {
            pending = storedBooks.peek().pop();
            storedBooks.deleteFirst();
        }
        return pending;
    }

    public int numBooks() {
        int numBooks = 0;
        Iterator<StackArrayImpl<Book>> it = storedBooks.values();
        while (it.hasNext()) {
            numBooks+=it.next().size();
        }

        return (numBooks);
    }

    public boolean isEmpty() {
        return (this.numBooks()==0);
    }

    public int numStacks() {
        return this.storedBooks.size();
    }


    public class Position {
        private int numStack;
        private int num;

        public Position(int numStack, int num) {
            this.numStack = numStack;
            this.num = num;
        }

        public int getNumStack() {
            return numStack;
        }

        public int getNum() {
            return num;
        }
    }
}
