package com.monstrous;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Day24 {


    class Blizzard {
        int x;
        int y;
        int startx;
        int starty;
        int dx;
        int dy;
        char rep;

        public Blizzard(int x, int y, int dx, int dy, char rep) {
            this.x = x;
            this.y = y;
            this.startx = x;
            this.starty = y;
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
    int startRow;
    int startCol;
    int exitRow;
    int exitCol;
    int maxSteps;
    Expedition endPoint;
    boolean debug = false;
    HashMap<Integer, Integer> cache; // x+y -> path length to exit


    public Day24() {
        System.out.print("Day 24\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day24.txt");

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

        //System.out.println("Blizzards: "+blizzards.size());
        startRow = 0;
        startCol = 1;
        exitRow = rows -1;
        exitCol = cols -2;
        expedition = new Expedition(startCol, startRow );
        expedition.parent = null;

       maxSteps = 9999999;
       cache = new HashMap<>();

        int cost = findRoute(  0, expedition );
        System.out.println("Part 1: Steps in route: "+cost);
        //printRoute(cost, endPoint);

        expedition.x = exitCol;
        expedition.y = exitRow;
        exitRow = startRow;
        exitCol = startCol;

        maxSteps = 9999999;
        cache.clear();
        int cost2 = findRoute(  cost, expedition );
        System.out.println("Part 2: back to start: "+cost2);


        expedition.x = startRow;
        expedition.y = startRow;
        exitRow = rows -1;
        exitCol = cols -2;
        maxSteps = 9999999;
        cache.clear();
        int cost3 = findRoute(  cost+cost2, expedition );
        System.out.println("Part 2: back to end: "+cost3);

        System.out.println("Part 2: "+(cost+cost2+cost3));




        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }

    // find route to exit, return number of steps
    // called recursively
    //
    private int findRoute( int level, Expedition exp ) {

        int stateCode = stateCode( level, exp.x, exp.y);
        Integer cost = cache.get(stateCode);
        if(cost != null)
            return cost;

        if(exp.x == exitCol && exp.y == exitRow) {  // at exit
            if(level < maxSteps) {     // put upper bound on # steps to cull searches
                maxSteps = level;
                endPoint = exp;             // keep track of best route
                cache.put(stateCode, 0);
            }
            return 0;
        }

        if(level > 1500)     // stuck in endless loop, prune this branch
            return 999999;

        if(level > maxSteps)    // don't need to pursue this branch, there are already faster routes to the exit
            return 999999;

//        System.out.println("Level "+level);
//        placeBlizzards(level);
//        constructGrid(exp);
//        printGrid();

        placeBlizzards(level+1);
        constructGrid(null);

        ArrayList<Expedition> options = new ArrayList<>();
        for(int dir = 0; dir < 5; dir++) {  // 4 directions or stay put
            int nx = exp.x+dx[dir];
            int ny = exp.y+dy[dir];
            if(nx < 0 || ny < 0 || nx >= cols || ny >= rows)
                continue;
            if(grid[ny][nx] == '.' ) { //|| grid[ny][nx] == 'E') {
                Expedition e = new Expedition(nx, ny);
                addManhattanDistance(e);
                e.parent = exp;
                options.add( e );
                //System.out.println("Level "+level+" found option "+e.x+", "+e.y);
             }
        }
        Collections.sort(options);      // sort by Manhattan distance
        int shortestPath = 9999999;
        for(Expedition e : options ) {
            //System.out.println("Level "+level+" trying out option "+e.x+", "+e.y);
            int pathLength = 1 + findRoute(level+1, e);
            if(pathLength < shortestPath) {
                shortestPath = pathLength;
            }
        }
        cache.put(stateCode, shortestPath);
        return shortestPath;
    }

    private void addManhattanDistance(Expedition exp)
    {
        exp.manhattan = Math.abs(exp.x - exitCol) + Math.abs(exp.y - exitRow);
    }

    // use recursion to reverse the list
    private void printRoute( int t, Expedition end ) {
        if(end == null)
            return;
        printRoute(t-1, end.parent);
        System.out.println("Step "+t+" position " + end.x + ", " + end.y);
        placeBlizzards(t);
        constructGrid(end);
        printGrid();

    }

    private void printGrid() {
        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < cols; x++ )
                System.out.print(grid[y][x]);
            System.out.println();
        }
        System.out.println();
    }


    private void placeBlizzards(int t) {
        for(Blizzard blizzard: blizzards) {
            blizzard.x = blizzard.startx + t * blizzard.dx;
            blizzard.y = blizzard.starty + t * blizzard.dy;
            blizzard.x = 1 + mod(blizzard.x-1,  cols-2);
            blizzard.y = 1 + mod(blizzard.y-1, rows-2);
        }
    }

    private boolean collidesWithBlizzard(int x, int y){
        if(x < 1 || x == cols-1 || y < 1 || y == rows-1)
            return true;
        for(Blizzard blizzard: blizzards) {
            if(x == blizzard.x && y == blizzard.y)
                return true;
        }
        return false;
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
        }
        for(int x = 0; x < cols-2; x++) {
            grid[rows-1][x] = '#';
        }
        for(Blizzard blizzard: blizzards) {
            grid[blizzard.y][blizzard.x] = blizzard.rep;
        }

        if(exp != null) {
            if(grid[exp.y][exp.x] != '.')
                System.out.println("Invalid position!");
            else
                grid[exp.y][exp.x] = expedition.rep;
        }
    }

    private int stateCode(int t, int x, int y) {
        if(t >= 2048)
            System.out.println("t overflow "+t);
        if(y >= 64)
            System.out.println("y overflow");
        if(x >= 128)
            System.out.println("x overflow");
        int code = (y << 20) | (x << 12) | t;
        return code; //y * 32768 + x * 512 + t;
    }

}
