package com.monstrous;

import java.util.ArrayList;

public class Day12 {

    class GridPoint {
        int x,y;

        public GridPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return x * 10000 + y;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null)
                return false;
            if(this == obj)
                return true;
            if(getClass() != obj.getClass())
                return false;
            GridPoint oth = (GridPoint)obj;
            return oth.x == x && oth.y == y;
        }
    }

    final FileInput input;
    char [][] matrix;
    int rows;
    int cols;

    public Day12() {
        System.out.print("Day 12\n");
        input = new FileInput("data/day12.txt");

        matrix = null;
        int row = 0;
        GridPoint start = null;
        GridPoint end = null;

        for (String line : input.lines) {

            //System.out.println(line);
            if(matrix == null) {
                rows = input.lines.size();
                cols = line.length();
                matrix = new char[rows][cols];
            }

            for(int i = 0 ; i < cols; i++) {
                char k = line.charAt(i);
                matrix[row][i] = k;
                if(k == 'S')
                    start = new GridPoint(row, i);
                else if (k == 'E')
                    end = new GridPoint(row, i);
            }
            row++;
        }

        //printState();

        matrix[start.x][start.y] = 'a';
        matrix[end.x][end.y] = 'z';

        int steps = shortestPath(start, end, true, true);
        System.out.println("Steps to reach goal: "+steps);

        int steps2 = shortestPath(end, start, false, false);
        System.out.println("Steps for scenic route from point at level a: "+steps2);
    }


    private int shortestPath(GridPoint start, GridPoint end, boolean goUp, boolean stopOnFind) {
        int dist[][] = new int[rows][cols];
        //GridPoint predecessor[][] = new GridPoint[rows][cols];

        ArrayList<GridPoint> Q = new ArrayList<>();
        ArrayList<GridPoint> nbors = new ArrayList<>();

        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                dist[i][j] = 999;
                //predecessor[i][j] = null;
                Q.add(new GridPoint(i, j));
            }
        }

        dist[start.x][start.y] = 0;

        while(Q.size() > 0) {
            GridPoint closest = null;
            int minDist = 9990;
            for( GridPoint p : Q ) {
                if (dist[p.x][p.y] < minDist) {
                    minDist = dist[p.x][p.y];
                    closest = p;
                }
            }
            Q.remove(closest);
            if(closest.equals(end) && stopOnFind)
                return dist[end.x][end.y];

            GridPoint cand;

            nbors.clear();
            if(closest.x > 0 ) {
                cand = new GridPoint(closest.x-1,closest.y);
                if(Q.contains(cand) && canGoTo(closest, cand, goUp))
                    nbors.add( cand );
            }
            if(closest.x < rows-1) {
                cand = new GridPoint(closest.x+1,closest.y);
                if(Q.contains(cand) && canGoTo(closest, cand, goUp))
                    nbors.add( cand );
            }
            if(closest.y > 0 ) {
                cand = new GridPoint(closest.x,closest.y-1);
                if(Q.contains(cand) && canGoTo(closest, cand, goUp))
                    nbors.add( cand );
            }
            if(closest.y < cols-1) {
                cand = new GridPoint(closest.x,closest.y+1);
                if(Q.contains(cand) && canGoTo(closest, cand, goUp))
                    nbors.add( cand );
            }

            for(GridPoint nbor : nbors ) {
                int altDist = 1 + dist[closest.x][closest.y];
                if(altDist < dist[nbor.x][nbor.y]) {
                    dist[nbor.x][nbor.y] = altDist;
                    //predecessor[nbor.x][nbor.y] = closest;
                }
            }
            //System.out.println("closest "+closest.x+" , "+closest.y+" nbors:"+nbors.size());

        }
//        for(int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                System.out.print(dist[i][j] + "   ");
//            }
//            System.out.println();
//        }

        // now find closest 'a'
        int minDist = Integer.MAX_VALUE;
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(matrix[i][j] == 'a') {
                    if(dist[i][j] < minDist)
                        minDist = dist[i][j];
                }
            }
        }
        return minDist;
    }

    private boolean canGoTo(GridPoint from, GridPoint to, boolean goUp) {
        if(goUp)
            return matrix[to.x][to.y] <= matrix[from.x][from.y]+1;    // not more than one level higher
        else
            return matrix[to.x][to.y] >= matrix[from.x][from.y]-1;    // not more than one level lower
    }

    private void printState() {
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
                System.out.print(matrix[i][j]);
            System.out.println();
        }
        System.out.println();
    }
}
