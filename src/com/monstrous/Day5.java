package com.monstrous;

import java.util.ArrayList;

public class Day5 {

    final FileInput input;
    private ArrayList<Character>[] towers = null;

    public Day5() {
        System.out.print("Day 5\n");
        input = new FileInput("data/day5.txt");

        for(int part = 1; part <= 2; part++) {

            int phase = 1;
            for (String line : input.lines) {

                //System.out.print(line);

                if (line.length() == 0) {
                    phase = 2;
                    //dumpTowers();
                    continue;
                }

                if (phase == 1) {

                    int cols = (line.length() + 1) / 4;
                    if (towers == null) {
                        towers =  new ArrayList[cols + 1];
                        for (int i = 1; i <= cols; i++)
                            towers[i] = new ArrayList<>();
                    }

                    for (int col = 0; col < cols; col++) {
                        char crate = line.charAt(4 * col + 1);
                        if (crate >= 'A' && crate <= 'Z') {
                            towers[col + 1].add(crate);
                        }
                    }
                } else {

                    String[] words = line.split(" ");
                    int[] values = new int[3];

                    for (int i = 0; i < 3; i++) {
                        values[i] = Integer.parseInt(words[1 + 2 * i]);
                    }
                    //System.out.println(" move : "+values[0]+" from "+values[1]+" to "+values[2]);

                    if(part == 1)
                        move(values[0], towers[values[1]], towers[values[2]]);
                    else
                        moveSubStack(values[0], towers[values[1]], towers[values[2]]);

                    //dumpTowers();

                }
            }
            //dumpTowers();
            printStackTops(part);
        }

    }

    private void dumpTowers() {
        for(int i = 1; i < towers.length; i++) {
            System.out.print(i + " : ");
            for(int j = 0; j < towers[i].size(); j++)
                System.out.print(towers[i].get(j));
            System.out.println();
        }
        System.out.println();
    }

    private void move(int count, ArrayList<Character> from, ArrayList<Character> to ) {
        for(int i = 0; i < count; i++)
            moveOne(from, to);
    }

    private void moveOne( ArrayList<Character> from, ArrayList<Character> to ) {
        char crate = from.get(0);
        from.remove(0);
        to.add(0,crate);
    }

    private void moveSubStack(int count, ArrayList<Character> from, ArrayList<Character> to ) {
        for(int i = 0; i < count; i++) {
            char crate = from.get(i);
            to.add(i, crate);
        }
        for(int i = 0; i < count; i++)
            from.remove(0);
    }


    private void printStackTops(int part) {
        System.out.print("part "+part+" crates at top of stacks : ");
        for(int i = 1; i < towers.length; i++) {
            System.out.print( towers[i].get(0));
        }
        System.out.println();
    }



}
