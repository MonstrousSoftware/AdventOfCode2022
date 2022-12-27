package com.monstrous;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Day24 {


    class Blizzard {
        int x;
        int y;
        int dx;
        int dy;
        char rep;

        public Blizzard(int x, int y, int dx, int dy, char rep) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.rep = rep;
        }
    }

    static class Expedition implements Comparable<Expedition> {
        int x;
        int y;
        int manhattan;                  // manhattan distance to the exit w/o obstacles
        static char rep = 'E';
        Expedition parent;

        public Expedition(int x, int y) {
            this.x = x;
            this.y = y;
        }


        @Override
        public int compareTo(Expedition o) {
            return manhattan - o.manhattan;
        }
    }

    final FileInput input;

    int rows;
    int cols;
    char [][] grid;
    ArrayList<Blizzard> blizzards = new ArrayList<>();
    Expedition expedition;
    final int dx[] = { 0, 1, 0, -1, 0 };
    final int dy[] = { -1, 0, 1, 0, 0 };
    int exitRow;
    int exitCol;
    int maxSteps;
    Expedition endPoint;


    public Day24() {
        System.out.print("Day 24\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day24b.txt");

        rows = input.lines.size();
        cols = input.lines.get(0).length();
        grid = new char[rows][cols];

        int row = 0;
        for(String line : input.lines ) {
            for(int x = 0; x < line.length(); x++) {
                char k = line.charAt(x);
                grid[row][x] = k;
                switch(k) {
                    case '>':   blizzards.add( new Blizzard(x, row, 1, 0, k)); break;
                    case '<':   blizzards.add( new Blizzard(x, row, -1, 0, k)); break;
                    case '^':   blizzards.add( new Blizzard(x, row, 0, -1, k)); break;
                    case 'v':   blizzards.add( new Blizzard(x, row, 0, 1, k)); break;
                    default: break;
                }
            }
            row++;
        }
        expedition = new Expedition(cols-3, rows-2);
        expedition.parent = null;
        exitRow = rows -1;
        exitCol = cols -2;
        maxSteps = 9999999;

        //printGrid();
        constructGrid(expedition);
        printGrid();

        int cost = findRoute(  0, expedition );
        System.out.println("Steps in route: "+cost);
        printRoute();

//        for(int step = 1; step < 2; step++) {
//            advanceBlizzards();
//            constructGrid();
//            advanceExpedition();
//            printGrid();
//        }

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }

    // find route to exit, return number of steps
    // called recursively
    //
    private int findRoute( int level, Expedition exp ) {
        System.out.println("Level: "+level+" at position "+exp.x+", "+exp.y);
        constructGrid(exp);
        printGrid();
        if(exp.x == exitCol && exp.y == exitRow) {  // at exit
            System.out.println("Found exit!");
            if(level < maxSteps) {     // put upper bound on # steps to cull searches
                maxSteps = level;
                endPoint = exp;             // keep track of best route
            }
            return 0;
        }

        if(level > maxSteps)
            return 999999;

        ArrayList<Expedition> options = new ArrayList<>();
        for(int dir = 0; dir < 4; dir++) {  // 4 directions or stay put
            int nx = exp.x+dx[dir];
            int ny = exp.y+dy[dir];
            if(nx < 0 || ny < 0 || nx >= cols || ny >= rows)
                continue;
            if(grid[ny][nx] == '.' || grid[ny][nx] == 'E') {
                Expedition e = new Expedition(nx, ny);
                addManhattanDistance(e);
                e.parent = exp;
                options.add( e );
             }
        }
        Collections.sort(options);
        int shortestPath = Integer.MAX_VALUE;
        for(Expedition e : options ) {
            //System.out.println("Trying option to position "+e.x+", "+e.y+" manhattan:"+e.manhattan);
            int pathLength = findRoute(level+1, e);
            System.out.println("Option to position "+e.x+", "+e.y+" manhattan:"+e.manhattan+" path:"+pathLength);

            if(pathLength < shortestPath) {
                shortestPath = pathLength;
            }
        }

        return shortestPath+1;
    }

    private void addManhattanDistance(Expedition exp)
    {
        exp.manhattan = Math.abs(exp.x - exitCol) + Math.abs(exp.y - exitRow);
    }

    private void printRoute() {
        System.out.println("Route (in reverse):");
        Expedition e = endPoint;
        while(e != null) {
            System.out.println("position " + e.x + ", " + e.y);
            e = e.parent;
        }
    }

    private void printGrid() {
        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < cols; x++ )
                System.out.print(grid[y][x]);
            System.out.println();
        }
        System.out.println();
    }

    private void advanceBlizzards() {
        for(Blizzard blizzard: blizzards) {
            blizzard.x += blizzard.dx;
            blizzard.y += blizzard.dy;
            blizzard.x = 1 + mod(blizzard.x-1,  cols-2);
            blizzard.y = 1 + mod(blizzard.y-1, rows-2);
        }
    }

    private void advanceExpedition() {
        ArrayList<Expedition> options = new ArrayList<>();
        for(int dir = 0; dir < 5; dir++) {
            int nx = expedition.x+dx[dir];
            int ny = expedition.y+dy[dir];
            if(nx < 0 || ny < 0 || nx >= cols || ny >= rows)
                continue;
            if(grid[ny][nx] == '.' || grid[ny][nx] == 'E') {
                options.add( new Expedition(nx, ny));
                System.out.println("Option "+dir+" to position "+nx+", "+ny);
            }
        }

    }

    private int mod(int a, int b) {
        return (((a % b) + b)%b);
    }

    private void constructGrid(Expedition exp) {
        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < cols; x++ )
                grid[y][x] = '.';
        }
        for(int y = 0; y < rows; y++) {
            grid[y][0] = '#';
            grid[y][cols-1] = '#';
        }
        for(int x = 2; x < cols; x++) {
            grid[0][x] = '#';
            //grid[rows-1][cols-x] = '#';
        }
        for(int x = 0; x < cols-2; x++) {
            grid[rows-1][x] = '#';
        }
//        for(Blizzard blizzard: blizzards) {
//            grid[blizzard.y][blizzard.x] = blizzard.rep;
//        }
        grid[exp.y][exp.x] = expedition.rep;
    }

}
