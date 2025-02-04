package uoc.ds.pr.model;

import edu.uoc.ds.adt.nonlinear.Dictionary;
import edu.uoc.ds.adt.nonlinear.DictionaryAVLImpl;
import edu.uoc.ds.adt.sequential.Set;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.util.DSLinkedList;

import java.util.Comparator;

public class Theme {
    public static final Comparator<Theme> CMP =  (th1, th2)->th1.getId().compareTo(th2.getId());
    private final String id;
    private String name;
    private final Dictionary<String, Book> books;


    public Theme(String id, String name) {
        this.id = id;
        setName(name);
        books = new DictionaryAVLImpl<>();
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getId() {return id;}

    public String getName() {
        return name;
    }

    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    public Iterator<Book> books() {
        return books.values();
    }


    @Override
    public boolean equals(Object obj) {
        Theme theme = (Theme)obj;
        return this.id.equals(theme.id) && this.name.equals(theme.name);
    }
}
