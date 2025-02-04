package edu.uoc.ds.adt;

import edu.uoc.ds.adt.sequential.Queue;
import edu.uoc.ds.adt.sequential.Set;
import edu.uoc.ds.adt.sequential.Stack;
import edu.uoc.ds.adt.sequential.StackArrayImpl;
import edu.uoc.ds.traversal.Iterator;

public class PR1WordSearch {
    enum Direction {
        VERTICAL,
        HORIZONTAL
    }

    static char[][] matrix;
    Stack<Result> out;

    public PR1WordSearch(String input) {
        init(input);
    }
    private  void init(String input) {
        String[] rows = input.split("\n");
        matrix = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            matrix[i] = rows[i].toCharArray();
        }
    }

    private boolean processRows(String word, Stack<Result> out) {
        boolean found = false;
        for (int row = 0; row < matrix.length; row++) { //&& !found; row++) {
            String srow = new String(matrix[row]);
            int col = srow.indexOf(word);
            found = (col != -1);
            if (found) {
                out.push(new Result(Direction.HORIZONTAL, word, row, col));
            }
        }
        return found;
    }

    private void processCols(String word, Stack<Result> out) {
        boolean found = false;
        for (int col = 0; col < matrix[0].length; col++){ //&& !found; col++) {
            StringBuilder sCol = new StringBuilder();
            for (int i = 0; i < matrix.length; i++) {
                sCol.append(matrix[i][col]);
            }
            int row = sCol.indexOf(word);
            found = (row != -1);
            if (found) {
                out.push(new Result(Direction.VERTICAL, word, row, col));
            }
        }
    }

    public Stack<Result> search( Set<String> words) {
        this.out = new StackArrayImpl<>(words.size()+1);
        Iterator<String> it = words.values();
        while (it.hasNext()) {
            boolean found = false;
            String word = it.next();

            found = processRows(word, out);
            if (!found) {
                processCols(word, out);
            }

        }
        return  out;
    }

        public class Result {
            protected Direction direcction;
            protected String word;
            protected int row;
            protected int col;

            protected Result(Direction direction, String word, int row, int col) {
                this.direcction = direction;
                this.word = word;
                this.row = row;
                this.col = col;
            }

        }
    }
