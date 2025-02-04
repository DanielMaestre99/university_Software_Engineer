package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.Set;
import edu.uoc.ds.adt.sequential.SetLinkedListImpl;
import edu.uoc.ds.traversal.Iterator;

public class Author {
    private String uniqueCode;
    private String name;
    private String surname;

    private final Set<Book> books;

    public Author(String uniqueCode, String name, String surname) {
        setUniqueCode(uniqueCode);
        setName(name);
        setSurname(surname);
        books = new SetLinkedListImpl<>();
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void updateBook(Book book) {
        books.delete(book);
        books.add(book);
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public Iterator<Book> books() {
        return books.values();
    }

    @Override
    public boolean equals(Object obj) {
        Author author = (Author) obj;
        return this.uniqueCode.equals(author.uniqueCode);
    }
}
