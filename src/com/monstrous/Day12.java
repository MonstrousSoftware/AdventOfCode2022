package com.monstrous;

import java.util.*;

// tweaked for performance:
// solve time ca. 32 ms
// alternative with a PriorityQueue for Q does actually not speed things up
// performance on 1MB input: 1.5 seconds
// on 13MB input: 16.8 seconds


public class Day12 {

    class GridPoint {
        int x,y;
        char z;
        int distance;

        public GridPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return x * 100000 + y;
        }

        @Override
        public boolean equals(Object obj) {

            if(this == obj)
                return true;
            if(obj == null)
                return false;
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
        final long startTime = System.currentTimeMillis();

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
        System.out.println("Steps to reach goal: " + steps);

        int steps2 = shortestPath(end, start, false, false);
        System.out.println("Steps for scenic route from point at level a: " + steps2);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time (ms): " + (endTime - startTime));
    }




    private int shortestPath(GridPoint start, GridPoint end, boolean goUp, boolean stopOnFind) {

        HashSet<GridPoint> closed = new HashSet<>();
        ArrayList<GridPoint> Q = new ArrayList<>();
        HashMap<Integer, GridPoint> map = new HashMap<>();
        ArrayList<GridPoint> nbors = new ArrayList<>();

        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                GridPoint p = new GridPoint(i,j);
                p.z = matrix[i][j];
                if(p.equals(start)) {
                    p.distance = 0;
                    Q.add(p);
                }
                else
                    p.distance = Integer.MAX_VALUE;
                map.put(positionCode(i,j), p);
            }
        }

        while(Q.size() > 0) {

            GridPoint closest = null;
            int minDistance = Integer.MAX_VALUE;
            for( GridPoint p : Q ) {
                if(p.distance < minDistance ) {
                    minDistance = p.distance;
                    closest = p;
                }
            }
            Q.remove(closest);

            if(closest.equals(end) && stopOnFind)
                return closest.distance;
            map.remove(positionCode(closest.x, closest.y));
            closed.add(closest);

            GridPoint cand;

            nbors.clear();
            if(closest.x > 0 ) {
                cand = map.get(positionCode(closest.x-1,closest.y));
                if(cand != null && canGoTo(closest, cand, goUp))
                    nbors.add( cand );
            }
            if(closest.x < rows-1) {
                cand = map.get(positionCode(closest.x+1,closest.y));
                if(cand != null  && canGoTo(closest, cand, goUp))
                    nbors.add( cand );
            }
            if(closest.y > 0 ) {
                cand = map.get(positionCode(closest.x,closest.y-1));
                if(cand != null  && canGoTo(closest, cand, goUp))
                    nbors.add( cand );
            }
            if(closest.y < cols-1) {
                cand = map.get(positionCode(closest.x,closest.y+1));
                if(cand != null  && canGoTo(closest, cand, goUp))
                    nbors.add( cand );
            }

            for(GridPoint nbor : nbors ) {
                int altDist = 1 + closest.distance;
                if(altDist < nbor.distance) {
                    nbor.distance = altDist;
                }
                if(!Q.contains(nbor))
                    Q.add(nbor);

            }
        }
//        for(GridPoint p : closed) {
//            System.out.println("grid ["+p.x+" , "+p.y+"] dist:"+p.distance +" ht:"+p.z);
//        }

        // now find closest 'a'
        int minDist = Integer.MAX_VALUE;
        for(GridPoint p : closed) {
            if(p.z == 'a' && p.distance < minDist)
                minDist = p.distance;
        }
        return minDist;
    }

    private boolean canGoTo(GridPoint from, GridPoint to, boolean goUp) {
        if (goUp)
            return to.z <= from.z + 1;
        else
            return to.z >= from.z - 1;
    }

    private int positionCode( int x, int y ) {
        return rows*y+ x;
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
