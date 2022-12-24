package com.monstrous;

public class Day4 {

    final FileInput input;

    public Day4() {
        System.out.print("Day 4\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day4.txt");

        int countInclusions = 0;
        int countOverlaps = 0;
        for (String line : input.lines) {

            String[] words = line.split("[^0-9]");
            int[] values = new int[4];

            for(int i = 0; i < 4; i++) {
                    values[i] = Integer.parseInt(words[i]);
            }

            if( (values[0] <= values[2] && values[1] >= values[3]) ||
                (values[2] <= values[0] && values[3] >= values[1])    ){
                countInclusions++;
            }


            if(! (values[0] > values[3] || values[1] < values[2]) ){
                countOverlaps++;
            }
        }
        System.out.println("pairs with contained ranges: "+countInclusions);
        System.out.println("pairs with overlaps: "+countOverlaps);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }



}
