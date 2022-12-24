package com.monstrous;

import java.util.ArrayList;
import java.util.HashMap;

public class Day23 {

    final static int MARGIN = 150;   // extra margin in grid around the input values


    class Elf {
        //int firstDirection; // NSWE
        int x;
        int y;
        boolean amAlone;
        int proposalx;
        int proposaly;
        boolean blocked;

        public Elf(int x, int y) {
            firstDirection = 0;
            this.x = x;
            this.y = y;
        }
    }

    final FileInput input;

    // NSWE
    final int dx[] = { 0, 0, -1, 1 };
    final int dy[] = { -1, 1, 0, 0 };
    final int sx[] = { -1, 0, 1, -1, 1, -1, 0, 1 };     // 8 cells surrounding elf
    final int sy[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
    int rows;
    int cols;
    char [][] grid;
    int firstDirection = 0;


    public Day23() {
        System.out.print("Day 23\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day23.txt");

        rows = input.lines.size();
        cols = input.lines.get(0).length();
        grid = new char[rows+2*MARGIN][cols+2*MARGIN];
        for(int y = 0; y < rows+2*MARGIN; y++)
            for(int x = 0; x < cols+2*MARGIN; x++)
                grid[y][x] = '.';

        ArrayList<Elf> elves = new ArrayList<>();


        int row = 0;
        for(String line : input.lines ) {
            for(int x = 0; x < line.length(); x++) {
                char k = line.charAt(x);
                putInGrid(x, row, k);
                if(k == '#')
                    elves.add( new Elf(x, row) );
            }
            row++;
        }

        //printGrid();

        boolean done;
        int round = 1;
        do {
            //System.out.println("Round "+round);
            done = iterate(elves);
            //printGrid();
            if(round == 10) {
                int count = countEmptyCells();
                System.out.println("Part 1: Empty cells after 10 rounds: "+count);
            }
            round++;
        } while(!done );
        round--;
        System.out.println("Part 2: Round where elves stop moving: "+round);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }


    private boolean iterate(ArrayList<Elf> elves) {
        HashMap<Integer, Integer> proposals = new HashMap<>();
        boolean done = true;
        for(Elf elf: elves) {
            done &= proposeMove(elf, proposals);
        }
        firstDirection = (firstDirection + 1) %4;
        for(Elf elf: elves) {
            commitMove(elf, proposals);
        }
        return done;
    }

    private boolean proposeMove( Elf elf, HashMap<Integer, Integer> proposals ) {
        // check if all surrounding cells are empty
        elf.amAlone = true;
        for (int i = 0; i < 8; i++) {
            char k = getFromGrid(elf.x + sx[i], elf.y + sy[i]);
            if ( k == '#') {
                elf.amAlone = false;
                break;
            }
        }

        if (elf.amAlone) { // no elves around, do nothing
            //System.out.println("Elf: " + elf.x + "," + elf.y + " is free!");
            return true;
        }

        boolean blocked = false;
        int dir = firstDirection;

        //elf.firstDirection = (elf.firstDirection + 1) %4;   // this correct?

        for(int i = 0; i < 4; i++) {    // try 4 directions
            blocked = false;
            if(dir == 0) {
                for(int j = -1; j <= 1; j++)
                    if( getFromGrid( elf.x+j, elf.y-1) == '#')  // NE, N, NW
                        blocked = true;
            } else if(dir == 1) { // S
                for(int j = -1; j <= 1; j++)
                    if( getFromGrid( elf.x+j, elf.y+1) == '#')  // SE, S, SW
                        blocked = true;
            } else if(dir == 2) { // W
                for(int j = -1; j <= 1; j++)
                    if( getFromGrid( elf.x-1, elf.y+j) == '#')  //
                        blocked = true;
            } else if(dir == 3) { // E
                for (int j = -1; j <= 1; j++)
                    if( getFromGrid( elf.x+1, elf.y+j) == '#')  //
                        blocked = true;
            }

            if(!blocked)    // 3 cells clear in selected direction
                break;
            //System.out.println("Elf: "+elf.x+","+elf.y+" blocked in dir: "+dir+" trying next dir");
            dir = (dir + 1)%4;
        }

        if(blocked) {// no options found
            elf.blocked = true;
            return false;
        }
        elf.blocked = false;
        // propose move in selected direction

        elf.proposalx = elf.x + dx[dir];
        elf.proposaly = elf.y + dy[dir];
        //System.out.println("Elf: "+elf.x+","+elf.y+" propose move in dir: "+dir+" to "+elf.proposalx+", "+elf.proposaly);


        // add 1 to the proposal count for this location
        int location = encodePosition(elf.proposalx, elf.proposaly);
        int count = 1;
        if(proposals.containsKey(location))
            count += proposals.get(location);
        proposals.put(location, count);
        return false;
    }

    private int encodePosition(int x, int y) {
        return 1000*x + y;
    }

    private char getFromGrid(int x, int y) {
        return grid[y+MARGIN][x+MARGIN];
    }

    private void putInGrid(int x, int y, char k) {
        grid[y+MARGIN][x+MARGIN] = k;
    }

    private void commitMove( Elf elf, HashMap<Integer, Integer> proposals  ) {
        if(elf.amAlone || elf.blocked)
            return;
        int location = encodePosition(elf.proposalx, elf.proposaly);
        Integer count = proposals.get(location);
        if(count == null){
            System.out.println("Elf: "+elf.x+","+elf.y+" ERROR: no proposal for "+elf.proposalx+" , "+elf.proposaly);
        }
        if(count == 1) {
            //System.out.println("Elf: "+elf.x+","+elf.y+" moving to "+elf.proposalx+", "+elf.proposaly);
            putInGrid(elf.x, elf.y, '.');
            elf.x = elf.proposalx;
            elf.y = elf.proposaly;
            putInGrid(elf.x, elf.y, '#');
        }
        else {
            //System.out.println("Elf: "+elf.x+","+elf.y+" proposal blocked to "+elf.proposalx+", "+elf.proposaly);
        }
    }

    private void printGrid() {
        int minx = Integer.MAX_VALUE;
        int maxx = -Integer.MAX_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxy = -Integer.MAX_VALUE;
        for(int y = 0; y < rows+2*MARGIN; y++) {
            for (int x = 0; x < cols + 2 * MARGIN; x++)
                if (grid[y][x] == '#') {
                    minx = Math.min(minx, x);
                    maxx = Math.max(maxx, x);
                    miny = Math.min(miny, y);
                    maxy = Math.max(maxy, y);
                }
        }

        for(int y = miny-MARGIN; y <= maxy-MARGIN; y++) {
            for(int x = minx-MARGIN; x <= maxx-MARGIN; x++) {
                System.out.print(getFromGrid( x, y));
            }
            System.out.println();
        }
        System.out.println();
    }

    private int countEmptyCells() {
        int minx = Integer.MAX_VALUE;
        int maxx = -Integer.MAX_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxy = -Integer.MAX_VALUE;
        for(int y = 0; y < rows+2*MARGIN; y++) {
            for (int x = 0; x < cols + 2 * MARGIN; x++)
                if (grid[y][x] == '#') {
                    minx = Math.min(minx, x);
                    maxx = Math.max(maxx, x);
                    miny = Math.min(miny, y);
                    maxy = Math.max(maxy, y);
                }
        }

        int count = 0;
        for(int y = miny-MARGIN; y <= maxy-MARGIN; y++) {
            for(int x = minx-MARGIN; x <= maxx-MARGIN; x++) {
                if( getFromGrid( x, y) == '.')
                    count++;
            }
        }
        return count;
    }


}
