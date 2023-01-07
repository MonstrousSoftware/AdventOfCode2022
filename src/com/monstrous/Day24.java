package com.monstrous;

import java.util.*;

public class Day24 {


    static class Blizzard {
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


    final FileInput input;

    int rows;
    int cols;
    char [][][] superGrid;
    List<Blizzard> blizzards = new ArrayList<>();
    final int dx[] = { 0, 1, 0, -1, 0 };
    final int dy[] = { -1, 0, 1, 0, 0 };
    int startRow;
    int startCol;
    int exitRow;
    int exitCol;
    int period;



    public Day24() {
        System.out.print("Day 24\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day24.txt");

        rows = input.lines.size();
        cols = input.lines.get(0).length();

        int row = 0;
        for(String line : input.lines ) {
            for(int x = 0; x < line.length(); x++) {
                char k = line.charAt(x);
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

        // period before blizzard cycle repeats
        period = lcm(rows-2, cols-2);

        // now build 3 dimensional grid of blizzard positions over time
        superGrid = new char[period][rows][cols];
        for(int time = 0; time < period; time++){
            placeBlizzards(time+1);
            constructGrid(superGrid[time]);
        }
        //printSuperGrid();

        //System.out.println("Blizzards: "+blizzards.size());
        startRow = 0;
        startCol = 1;
        exitRow = rows -1;
        exitCol = cols -2;


        int steps = searchPath(   startCol, startRow,  exitCol, exitRow, 0 );
        System.out.println("Part 1: Steps in route: "+steps);
        int steps2 = searchPath(  exitCol, exitRow,  startCol, startRow, steps );
        int steps3 = searchPath(   startCol, startRow, exitCol, exitRow, steps2 );
        System.out.println("Part 2: Steps in route: "+steps3);

        // part 1 : 290
        // part 2 : 842
        // 470 ms

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }


    private int coord(int x, int y){
        return (y << 8) + x;
    }


    // Simplified type of BFS. For each time step go one slice down the 3 dimensional grid and make a set
    // of cells that can be reached from the possible positions from the previous slice.
    // The 3rd dimension is equivalent to time, so we don't have to keep track of steps taken or what the
    // shortest path is.
    // Note that the maze structure repeats every 'period' steps.
    //
    private int searchPath(int x, int y, int endx, int endy, int time) {
        Set<Integer> sliceSet = new HashSet<>();
        Set<Integer> nextSliceSet = new HashSet<>();

        sliceSet.add(coord(x,y));   // first slice (T = time) only has the start position in the set

        while(true) {
            //System.out.println("time: "+time+" set size: "+sliceSet.size());
            time++;
            for(int cell : sliceSet ) {

                if (cell == coord(endx, endy))
                    return time+1;

                int cx = cell % 256;
                int cy = cell >> 8;
                //System.out.println("Step "+time+" Removing closest: " + cx + ", " + cy + " in fringe:" + fringe.size());

                // create neighbours and add them to set for next time step
                for (int dir = 0; dir < 5; dir++) {  // 4 directions or stay put
                    int nx = cx + dx[dir];
                    int ny = cy + dy[dir];

                    if (nx < 0 || ny < 0 || nx >= cols || ny >= rows)   // skip positions outside of grid
                        continue;
                    if (superGrid[(time + 1) % period][ny][nx] == '.') {    // viable option; empty cell
                        nextSliceSet.add( coord(nx, ny) );
                    }
                }
            }
            sliceSet = nextSliceSet;
            nextSliceSet = new HashSet<>();
        }
    }

    private void printSuperGrid(){
        for(int y = 0; y < rows; y++) {
            for(int t = 0; t < 5; t++) {
                for (int x = 0; x < cols; x++)
                    System.out.print(superGrid[t][y][x]);
                System.out.print("   ");
            }
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

    private int mod(int a, int b) {
        return (((a % b) + b)%b);
    }

    private void constructGrid(char[][]grid) {
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
    }

    private int lcm(int a, int b) {
        int max = Math.max(a,b);
        int min = Math.min(a,b);

        while(true) {
            if (max % min == 0)
                break;
            max += Math.max(a,b);
        }
        return max;
    }

}
