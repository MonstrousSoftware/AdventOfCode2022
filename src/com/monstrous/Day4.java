package com.monstrous;

public class Day4 {

    final FileInput input;

    public Day4() {
        System.out.print("Day 4\n");
        input = new FileInput("data/day4.txt");

        int countInclusions = 0;
        int countOverlaps = 0;
        for (String line : input.lines) {

            String[] words = line.split("[^0-9]");
            int[] values = new int[4];

            for(int i = 0; i < 4; i++) {
                values[i] = Integer.parseInt(words[i]);
            }
            boolean containment = false;
            if(values[0] <= values[2] && values[1] >= values[3]) {
                containment = true;
            }
            else if (values[2] <= values[0] && values[3] >= values[1]) {
                containment = true;
            }
            if(containment)
                countInclusions++;


            if(! (values[0] > values[3] || values[1] < values[2]) ){
                countOverlaps++;
            }
        }
        System.out.println("pairs with contained ranges: "+countInclusions);
        System.out.println("pairs with overlaps: "+countOverlaps);
    }



}
