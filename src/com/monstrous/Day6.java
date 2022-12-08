package com.monstrous;


public class Day6 {

    final FileInput input;

    public Day6() {
        System.out.print("Day 6\n");
        input = new FileInput("data/day6.txt");

        for (String line : input.lines) {

            //System.out.println(line);
            findMarker(4, line);
            findMarker(14, line);
        }
    }

    private void findMarker(int len, String line) {
        for(int i = len; i < line.length(); i++) {
            boolean dupe = false;
            int[] count = new int[128];
            count[ line.charAt(i) ] = 1;
            for(int j = 1; j < len; j++) {
                int k = line.charAt(i - j);
                if (count[k] == 0)
                    count[k] = 1;
                else
                    dupe = true;
            }
            if(!dupe) {
                System.out.println("marker of " + len+" different characters found at: "+(i+1));
                return;
            }
        }
    }
}
