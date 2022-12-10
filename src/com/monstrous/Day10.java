package com.monstrous;

   public class Day10 {

       final static int COLS = 40;
       final static int ROWS = 6;

    final FileInput input;
    char crt[][];
    int x;
    int cycle;
    int sum;
    int crtX;
    int crtY;


    public Day10() {
        System.out.print("Day 10\n");
        input = new FileInput("data/day10.txt");

        cycle = 0;
        x = 1;
        sum = 0;
        crtX = 0;
        crtY = 0;
        crt = new char[ROWS][COLS];
        for(int r = 0; r < 6; r++)
            for(int c = 0; c < COLS; c++)
                crt[r][c] = '.';

        for (String line : input.lines) {


            //System.out.println(line);
            String[] words = line.split(" ");
            String cmd = words[0];
            if(cmd.contentEquals("noop")) {
                cycle++;
                testCycle();
            } else if (cmd.contentEquals("addx")) {
                int val = Integer.parseInt(words[1]);

                cycle++;
                testCycle();
                cycle++;
                testCycle();
                x += val;


            } else
                System.out.println("Cmd not recognized: "+cmd);

        }
        cycle++;
        testCycle();
        System.out.println("Sum of signal strengths : "+sum);
        showCRT();
    }

    private void testCycle() {
        int strength = cycle * x;
        if (cycle % 40 == 20) {
            sum += strength;
        }

        if(Math.abs(x - crtX) <= 1 ) {
            crt[crtY][crtX] = '#';
        }
        crtX++;
        if(crtX >= COLS) {
            crtX = 0;
            crtY++;
        }
    }

    private void showCRT() {
        System.out.println();
        for(int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++)
                System.out.print(crt[r][c]);
            System.out.println();
        }
    }

}
