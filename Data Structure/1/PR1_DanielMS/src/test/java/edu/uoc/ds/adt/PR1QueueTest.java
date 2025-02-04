package edu.uoc.ds.adt;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import static edu.uoc.ds.adt.util.PeriodicFunction.LEN;
import static edu.uoc.ds.adt.util.PeriodicFunction.f;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PR1QueueTest {
    PR1Queue pr1q;

    private void fillQueue() {

        for (int x = 1; x <= LEN; x++) {
            pr1q.add(f(x));
        }
    }

    @Before
    public void setUp() {
        this.pr1q = new PR1Queue();

        assertNotNull(this.pr1q.getQueue());
        fillQueue();
    }

    @After
    public void release() {
        this.pr1q = null;
    }


    @org.junit.Test
    public void queueTest() {
        assertEquals(this.pr1q.CAPACITY, this.pr1q.getQueue().size());
        Assert.assertEquals(1, pr1q.poll(), 0);
        Assert.assertEquals(4, pr1q.poll(), 0);
        Assert.assertEquals(9, pr1q.poll(), 0);
        Assert.assertEquals(0, pr1q.poll(), 0);
        Assert.assertEquals(1, pr1q.poll(), 0);
        Assert.assertEquals(4, pr1q.poll(), 0);
        Assert.assertEquals(9, pr1q.poll(), 0);
        Assert.assertEquals(0, pr1q.poll(), 0);
        Assert.assertEquals(1, pr1q.poll(), 0);
        Assert.assertEquals(4, pr1q.poll(), 0);
        Assert.assertEquals(9, pr1q.poll(), 0);
        Assert.assertEquals(0, pr1q.poll(), 0);
        Assert.assertEquals(1, pr1q.poll(), 0);
        Assert.assertEquals(4, pr1q.poll(), 0);
        Assert.assertEquals(9, pr1q.poll(), 0);
        assertEquals(0, this.pr1q.getQueue().size());
    }

}
