package edu.uoc.ds.adt;

import edu.uoc.ds.adt.sequential.Set;
import edu.uoc.ds.adt.sequential.SetLinkedListImpl;
import edu.uoc.ds.adt.sequential.Stack;
import edu.uoc.ds.traversal.Iterator;
import org.junit.Assert;
import org.junit.Test;

public class PR1WordSearchTest {


    @Test
    public void wordSearchTest() {
        String in = """
            CSTACKOPTERK
            RSLLMHAOLAQC
            GRAPHIPRFBXA
            GHBINARYTREE
            PRIOSITYQUEU
            LINKEDLISTQZ
            ALBMTUHUGSUT
            MBULISTINGER
            AUPIDCEKTSUO
            XSITWAYANEEI
            HASHTABLERNN
            HGHIUECYCLEE
            """;

        Set<String> words = new SetLinkedListImpl();
        words.add("LIST");
        words.add("HASHTABLE");
        words.add("STACK");
        words.add("BINARYTREE");
        words.add("SET");
        words.add("QUEUE");
        words.add("LINKEDLIST");
        words.add("GRAPH");

        PR1WordSearch wordSearch = new PR1WordSearch(in);
        Stack<PR1WordSearch.Result> out = wordSearch.search(words);

        Iterator<PR1WordSearch.Result> it = out.values();
        PR1WordSearch.Result result = null;

        Assert.assertTrue(it.hasNext());

        PR1WordSearch.Result r = it.next();
        Assert.assertEquals("GRAPH", r.word);
        Assert.assertEquals(2, r.row);
        Assert.assertEquals(0, r.col);
        Assert.assertEquals(PR1WordSearch.Direction.HORIZONTAL, r.direcction);

        r = it.next();
        Assert.assertEquals("LINKEDLIST", r.word);
        Assert.assertEquals(5, r.row);
        Assert.assertEquals(0, r.col);
        Assert.assertEquals(PR1WordSearch.Direction.HORIZONTAL, r.direcction);

        r = it.next();
        Assert.assertEquals("QUEUE", r.word);
        Assert.assertEquals(5, r.row);
        Assert.assertEquals(10, r.col);
        Assert.assertEquals(PR1WordSearch.Direction.VERTICAL, r.direcction);

        r = it.next();
        Assert.assertEquals("SET", r.word);
        Assert.assertEquals(4, r.row);
        Assert.assertEquals(4, r.col);
        Assert.assertEquals(PR1WordSearch.Direction.VERTICAL, r.direcction);

        r = it.next();
        Assert.assertEquals("BINARYTREE", r.word);
        Assert.assertEquals(3, r.row);
        Assert.assertEquals(2, r.col);
        Assert.assertEquals(PR1WordSearch.Direction.HORIZONTAL, r.direcction);

        r = it.next();
        Assert.assertEquals("STACK", r.word);
        Assert.assertEquals(0, r.row);
        Assert.assertEquals(1, r.col);
        Assert.assertEquals(PR1WordSearch.Direction.HORIZONTAL, r.direcction);

        r = it.next();
        Assert.assertEquals("HASHTABLE", r.word);
        Assert.assertEquals(10, r.row);
        Assert.assertEquals(0, r.col);
        Assert.assertEquals(PR1WordSearch.Direction.HORIZONTAL, r.direcction);

        r = it.next();
        Assert.assertEquals("LIST", r.word);
        Assert.assertEquals(7, r.row);
        Assert.assertEquals(3, r.col);
        Assert.assertEquals(PR1WordSearch.Direction.HORIZONTAL, r.direcction);

        Assert.assertTrue(it.hasNext());

        r = it.next();
        Assert.assertEquals("LIST", r.word);
        Assert.assertEquals(5, r.row);
        Assert.assertEquals(6, r.col);
        Assert.assertEquals(PR1WordSearch.Direction.HORIZONTAL, r.direcction);

    }
}
