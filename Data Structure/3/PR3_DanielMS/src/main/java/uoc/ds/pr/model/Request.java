package uoc.ds.pr.model;

import java.time.LocalDate;
import java.util.Comparator;

public class Request {
    private Reader reader;
    private Copy copy;
    private LocalDate date;

    public static final Comparator<Request> CMP_LEVEL =
            (Request r1, Request r2)-> Integer.compare(r2.getReader().getPoints(), r1.getReader().getPoints());
    public static final Comparator<Request> CMP_ID =
            (Request r1, Request r2)-> r2.getCopyId().compareTo(r1.getCopyId());

    public Request (Reader reader, Copy copy, LocalDate date) {
        this.reader = reader;
        this.copy = copy;
        this.date = date;
    }

    public Reader getReader() {
        return reader;
    }

    public String getCopyId() {
        return (copy!=null?copy.getCopyId():null);
    }
}
