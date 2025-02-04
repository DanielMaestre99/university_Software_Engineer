package edu.uoc.ds.adt.util;

public class PeriodicFunction {

    public static int LEN = 15;
    public static int f(int x) {
        return (int)Math.pow((x % 4) , 2);
    }
}
