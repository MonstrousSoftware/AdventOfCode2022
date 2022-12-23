package com.monstrous;

import java.util.ArrayList;

public class Day23 {


    class Elf {
        int firstDirection; // NSWE
        int x;
        int y;
        boolean amAlone;

        public Elf(int x, int y) {
            firstDirection = 0;
            this.x = x;
            this.y = y;
        }
    }

    final FileInput input;
    final char [][] grid;
    // NSWE
    final int dx[] = { 0, 0, -1, 1 };
    final int dy[] = { 1, -1, 0, 0 };
    final int sx[] = { -1, 0, 1, -1, 1, -1, 0, 1 };     // 8 cells surrounding elf
    final int sy[] = { -1, -1, -1, 0, 0, 1, 1, 1 };



    public Day23() {
        System.out.print("Day 23\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day23a.txt");

        int rows = input.lines.size();
        int cols = input.lines.get(0).length();
        grid = new char[rows][cols];

        ArrayList<Elf> elves = new ArrayList<>();

        int row = 0;
        for(String line : input.lines ) {
            for(int x = 0; x < line.length(); x++) {
                char k = line.charAt(x);
                grid[row][x] = k;
                if(k == '#')
                    elves.add( new Elf(x, row) );
            }
            row++;
        }

        printGrid(grid);
        for(Elf elf : elves )
            System.out.println("Elf at "+elf.x+", "+elf.y);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }

    private void step( Elf elf ) {
        // check if all surrounding cells are empty
        elf.amAlone = true;
        for(int i = 0; i < 8; i++) {
            if(grid[elf.y+sy[i]][elf.x+sx[i]] != ' ') {
                elf.amAlone = false;
                break;
            }
        }

        if(elf.amAlone)
            return;

        int dir = elf.firstDirection;
        for(int i = 0; i < 4; i++) {
            if(dir == 0) {
                boolean blocked = false;
                for(int j = -1; j <= 1; j++)
                    if( grid[elf.y-1][elf.x+j] == '#')  // NE, N, NW
                        blocked = true;
                if(!blocked)


            }


            dir = (dir + 1)%4;
        }
    }

    private void printGrid(char [][]grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < cols; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
        System.out.println();
    }


}
