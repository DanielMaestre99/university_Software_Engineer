package edu.uoc.ds.adt;


import edu.uoc.ds.adt.sequential.Queue;
import edu.uoc.ds.adt.sequential.QueueArrayImpl;

import static edu.uoc.ds.adt.util.PeriodicFunction.LEN;

public class PR1Queue {

    public final int CAPACITY = LEN;

    private Queue<Integer> queue;

    public PR1Queue() {
        newQueue();
    }
    public void newQueue() {
        queue = new QueueArrayImpl<>(CAPACITY);
    }

    public Queue<Integer> getQueue() {
        return this.queue;
    }

    public void add(Integer v) {
        this.queue.add(v);
    }

    public Integer poll() {
        return this.queue.poll();
    }
}
