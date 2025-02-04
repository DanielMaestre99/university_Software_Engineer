package uoc.ds.pr;

import edu.uoc.ds.adt.nonlinear.PriorityQueue;
import edu.uoc.ds.adt.nonlinear.graphs.Edge;
import edu.uoc.ds.adt.nonlinear.graphs.UnDirectedEdge;
import edu.uoc.ds.adt.nonlinear.graphs.Vertex;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.adt.sequential.Set;
import edu.uoc.ds.adt.sequential.SetLinkedListImpl;
import edu.uoc.ds.exceptions.InvalidPositionException;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;
import uoc.ds.pr.util.BookLabel;

import java.time.LocalDate;

public class LibraryPR3Impl extends  LibraryPR2Impl implements LibraryPR3 {
    private final PriorityQueue<Request> requests;


    public LibraryPR3Impl() {
        super();
        requests = new PriorityQueue<>();
    }

    @Override
    public void addTheme(String themeId, String name) {
        Theme theme = getTheme(themeId);
        if (theme == null) {
            theme = new Theme(themeId, name);
            this.themes.put(themeId, theme);
        }
        else {
            theme.setName(name);
        }
    }

    @Override
    public void addAuthor(String uniqueCode, String name, String surname) {
        Author author = getAuthor(uniqueCode);
        if (author == null) {
            author = new Author(uniqueCode, name, surname);
            this.authors.put(uniqueCode, author);
        }
        else {
            author.setName(name);
            author.setSurname(surname);
        }

    }

    @Override
    public Iterator<Copy> getCopies(String bookId) throws NoBookException, NoCopiesException {
        CatalogedBook catalogedBook = catalogedBooks.get(bookId);
        if (catalogedBook == null) {
            throw new NoBookException();
        }
        Iterator<Copy> it = catalogedBook.copies();
        if (!it.hasNext()) {
            throw new NoCopiesException();
        }

        return it;
    }

    @Override
    public void createRequest(String readerId, String copyId, LocalDate date) throws ReaderNotFoundException, CopyNotFoundException,
            ReaderAlreadyHasRequest4Copy, ReaderAlreadyHasLoan4Copy {
        Reader reader = getReader(readerId);
        if (reader == null) {
            throw new ReaderNotFoundException();
        }

        Copy copy = catalogedCopies.get(copyId);
        if (copy == null) {
            throw new CopyNotFoundException();
        }

        if (copy.readerAlreadyHasRequest4Copy(readerId)) {
            throw new ReaderAlreadyHasRequest4Copy();
        }

        if (reader.alreadyHasLoan4Copy(copyId)) {
            throw new ReaderAlreadyHasLoan4Copy();
        }

        Request request = new Request(reader, copy, date);
        copy.addRequest(request);


    }

    @Override
    public Iterator<Book> getBooksByTheme(String themeId) throws NoBookException {

        Theme theme = themes.get(themeId);
        Iterator<Book> it = theme.books();
        if (!it.hasNext()) {
            throw new NoBookException();
        }

        return it;
    }

    @Override
    public Iterator<Book> getBooksByAuthor(String uniqueCode) throws NoBookException {
        Author author = authors.get(uniqueCode);
        Iterator<Book> it = author.books();
        if (!it.hasNext()) {
            throw new NoBookException();
        }

        return it;
    }

    @Override
    public Level getReaderLevel(String readerId) throws ReaderNotFoundException {
        Reader reader = getReader(readerId);
        if (reader == null) {
            throw new ReaderNotFoundException();
        }
        return reader.getLevel();
    }

    @Override
    public void addReview(String bookId, String readerId, int rate, String comment)
            throws RateOutOfRangeException, ReaderNotFoundException, BookNotFoundException,
            ReaderNotAssociatedWithBookException, UserAlreadyRatedBookException {
        if (rate < MIN_RATE || rate > MAX_RATE ) {
            throw new RateOutOfRangeException();
        }
        CatalogedBook catalogedBook = getCatalogedBook(bookId);
        if (catalogedBook == null) {
            throw new BookNotFoundException();
        }

        Reader reader = getReader(readerId);
        if (reader == null) {
            throw new ReaderNotFoundException();
        }

        if (!reader.hasLoanByBook(catalogedBook.getIsbn())) {
            throw new ReaderNotAssociatedWithBookException();
        }

        if (reader.hasRating(catalogedBook.getIsbn())){
            throw new UserAlreadyRatedBookException();
        }

        Rating rating = new Rating (reader, catalogedBook, rate, comment );
        catalogedBook.addRating(rating);
        reader.addRating(rating);

        bestBooks.delete(catalogedBook);
        bestBooks.update(catalogedBook);

        if (rate == 5) {
            Set<Book> topBooks = reader.topBooks(catalogedBook);

            Iterator<Book> it = topBooks.values();
            Book book;
            while (it.hasNext()) {
                book = it.next();
                if (!catalogedBook.getIsbn().equals(book.getIsbn())) {
                    UnDirectedEdge<Integer, Book> edge = getEdge(catalogedBook.getIsbn(), book.getIsbn());
                    if (edge==null) {
                        edge = recommendationSystem.newEdge(catalogedBook.getVertex(), book.getVertex());
                        edge.setLabel(1);
                    }
                    else {
                        Integer label = edge.getLabel();
                        edge.setLabel(label + 1);
                    }
                }
            }
        }

    }

    @Override
    public Iterator<Copy> getDestroyedCopies() throws NoBookException {
        Iterator<Copy> it = super.destroyedCopies.values();
        if (!it.hasNext()) {
            throw new NoBookException();
        }
        return it;
    }

    @Override
    public Iterator<Rating> getReviewsByBook(String bookId)
            throws BookNotFoundException, NoReviewsException {
        CatalogedBook catalogedBook = getCatalogedBook2(bookId);
        Iterator<Rating> it = catalogedBook.ratings();
        if (!it.hasNext()) {
            throw new NoReviewsException();
        }

        return it;
    }

    @Override
    public Iterator<CatalogedBook> best5Books() throws NoBookException {
        if (super.bestBooks.isEmpty()) {
            throw new NoBookException();
        }

        Iterator<CatalogedBook> it = super.bestBooks.values();
        return it;
    }

    @Override
    public Iterator<Reader> best5Readers() throws NoReaderException {
        if (super.bestReaders.isEmpty()) {
            throw new NoReaderException();
        }

        Iterator<Reader> it = super.bestReaders.values();
        return it;
    }



    @Override
    public Iterator<Book> getRecommendationsByBook(String bookId) throws NoBookException {
        CatalogedBook book = getCatalogedBook(bookId);

        Iterator<Vertex<Book>> it = recommendationSystem.adjacencyList(book.getVertex());
        PriorityQueue<BookLabel> recommendations = new PriorityQueue<>(BookLabel.CMP);

        Book currentBook;
        while (it.hasNext()) {
            currentBook = it.next().getValue();
            UnDirectedEdge<Integer, Book> edge = recommendationSystem.getEdge(book.getVertex(), currentBook.getVertex());
            recommendations.add(new BookLabel(currentBook, edge.getLabel()));
        }

        if (recommendations.isEmpty()) {
            throw new NoBookException();
        }

        final Iterator<BookLabel> it2 = recommendations.values();
        Iterator<Book> itRes = new Iterator<Book>() {
            @Override
            public boolean hasNext() {
                return it2.hasNext();
            }

            @Override
            public Book next() throws InvalidPositionException {
                return it2.next().getBook();
            }
        };

        return itRes;

    }

    @Override
    public Iterator<Book> getRecommendationsByReader(String readerId) throws ReaderNotFoundException, NoBookException {
        Reader reader = getReader(readerId);
        if (reader == null) {
            throw new ReaderNotFoundException();
        }

        Set<Book> res = new SetLinkedListImpl<>();

        Set<Book> booksRead = reader.booksRead();
        Iterator<Book> it = booksRead.values();
        Book book1 = null;
        Book book2 = null;
        while (it.hasNext()) {
            book1 = it.next();
            Iterator<Vertex<Book>> itAdjacency = recommendationSystem.adjacencyList(book1.getVertex());
            while (itAdjacency.hasNext()) {
                book2 = itAdjacency.next().getValue();
                if (!booksRead.contains(book2)) {
                    res.add(book2);
                }
            }
        }

        if (res.size()==0) {
            throw new NoBookException();
        }
        return res.values();
    }

    @Override
    public Iterator<Author> getRecommendedAuthors(String uniqueCode) throws AuthorNotFoundException, NoAuthorException {
        Author author = getAuthor(uniqueCode);
        if (author==null) {
            throw new AuthorNotFoundException();
        }

        Set<Author> result = new SetLinkedListImpl<>();

        Iterator<Book> it = author.books();
        Book book;
        Vertex<Book> vertex;
        while (it.hasNext()) {
            book = it.next();
            vertex = book.getVertex();
            Iterator<Vertex<Book>> it2 = recommendationSystem.adjacencyList(vertex);

            Vertex<Book> vertex2 = null;
            Book book2 = null;
            while (it2.hasNext()) {
                vertex2 = it2.next();
                book2 = vertex2.getValue();
                if (!book2.getAuthor().getUniqueCode().equals(uniqueCode)) {
                    result.add(book2.getAuthor());
                }
            }
        }

        if (result.isEmpty()) {
            throw new NoAuthorException();
        }

        return result.values();
    }



    public int numThemes() {
        return themes.size();
    }

    public Theme getTheme(String id) {
        return themes.get(id);
    }

    public int numAuthors() {
        return authors.size();
    }

    public Author getAuthor(String id) {
        return authors.get(id);
    }

    @Override
    public UnDirectedEdge<Integer, Book> getEdge(String isbn1, String isbn2) {
        CatalogedBook book1 = getCatalogedBook(isbn1);
        CatalogedBook book2 = getCatalogedBook(isbn2);

        return recommendationSystem.getEdge(book1.getVertex(), book2.getVertex());
    }

}
