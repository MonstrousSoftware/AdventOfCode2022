package com.monstrous;

import java.util.HashMap;

public class Day14 {


    final FileInput input;

    public Day14() {
        System.out.print("Day 14\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day14.txt");

        for(int part = 1; part <= 2; part++) {

            int minx = Integer.MAX_VALUE;
            int maxx = Integer.MIN_VALUE;
            int miny = Integer.MAX_VALUE;
            int maxy = Integer.MIN_VALUE;
            HashMap<Integer, Character> map = new HashMap<>();

//            HashMap<String, Integer> lineCountMap = new HashMap<>();

            for (String line : input.lines) {

//                int lc = 1;
//                if(lineCountMap.containsKey(line))
//                    lc = 1 + lineCountMap.get(line);
//                lineCountMap.put(line, lc + 1);

                // 503,4 -> 502,4 -> 502,9 -> 494,9
                String[] words = line.split("[^0-9]+");

                int prevx = -1;
                int prevy = -1;
                for (int p = 0; p < words.length; p += 2) {
                    int x = Integer.parseInt(words[p]);
                    int y = Integer.parseInt(words[p + 1]);
                    if (p > 0) {
                        addRockLine(map, prevx, prevy, x, y);
                    }
                    prevx = x;
                    prevy = y;
                    minx = Math.min(minx, x);
                    maxx = Math.max(maxx, x);
                    miny = Math.min(miny, y);
                    maxy = Math.max(maxy, y);
                }
            }
            map.put(locationCode(500, 0), '+');
            miny = 0;
            int floor = maxy + 10;
            if(part == 2) {
                int edge = 10;
                maxy += 2;
                floor = maxy;
                addRockLine(map, minx - edge, floor, maxx + edge, floor); // for visualisation only
                minx -= edge;
                maxx += edge;
            }

            //printMap(map, minx, miny, maxx, maxy);

            int count = 0;
            while (true) {
                boolean escaped = dropSand(map, maxy, floor);
                //printMap(map, minx, miny, maxx, maxy);
                if (escaped)
                    break;
                count++;
            }
//            printMap(map, minx, miny, maxx, maxy);

            System.out.println("Part "+part+": Units of sand dropped: " + count);
            // part 1: 885
            // part 2: 28691


//            for(String key : lineCountMap.keySet()) {
//                System.out.println("Appearances : " + lineCountMap.get(key)+" of key: "+key);
//            }

        }
        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime)+" ms");


    }

    private void addRockLine(HashMap<Integer, Character> m, int x1, int y1, int x2, int y2) {
        if(x1 != x2) {
            int min = Math.min(x1, x2);
            int max = Math.max(x1, x2);
            for(int x = min; x <= max; x++)
                m.put(locationCode(x,y1), '#');
        }
        else {
            int min = Math.min(y1, y2);
            int max = Math.max(y1, y2);
            for(int y = min; y <= max; y++)
                m.put(locationCode(x1,y), '#');
        }
    }

    private int locationCode( int x, int y){
        return 100000 * y + x;
    }

    private void printMap(HashMap<Integer, Character> m, int x1, int y1, int x2, int y2) {
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                Character k = m.get(locationCode(x, y));
                if (k == null)
                    k = '.';
                System.out.print(k);
            }
            System.out.println();
        }
        System.out.println();
    }

    private boolean dropSand(HashMap<Integer, Character> m, int bottomy, int floory) {
        int x = 500;
        int y = 0;

        if (m.get(locationCode(x, y)) == 'o') {
//            System.out.println("blocked!");
            return true;
        }

        while(true) {
            if(y+1 == floory) {
                m.put(locationCode(x, y), 'o');
                return false;
            } else  if ( m.get(locationCode(x, y + 1)) == null) {
                y++;
            } else if ( m.get(locationCode(x - 1, y + 1)) == null) {
                x--;
                y++;
            } else if ( m.get(locationCode(x + 1, y + 1)) == null) {
                x++;
                y++;
            } else {
                m.put(locationCode(x, y), 'o');
                return false;
            }
            if (y > bottomy) {
//                System.out.println("escaped!");
                return true;
            }
        }
    }

}
