package com.monstrous;

import java.util.ArrayList;
import java.util.HashMap;

public class Day18 {

    class Point {
        int x,y,z;
        boolean stone;

        public Point(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            stone = true;
        }
        public Point add(Point b) {
            this.x += b.x;
            this.y += b.y;
            this.z += b.z;
            return this;
        }
    }
    final FileInput input;
    HashMap<String, Point> pointMap;
    Point[] delta;

    public Day18() {
        System.out.print("Day 18\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day18.txt");

        pointMap = new HashMap<>();
        Point min = new Point(999,999,999);
        Point max = new Point(-999,-999,-999);

        for(String line : input.lines ) {

            String[] words = line.split(",");

            Point point = new Point(Integer.parseInt(words[0]), Integer.parseInt(words[1]), Integer.parseInt(words[2]));
            String key = point.x+","+point.y+","+point.z;
            pointMap.put(key, point);

            min.x = Math.min(min.x, point.x);
            min.y = Math.min(min.y, point.y);
            min.z = Math.min(min.z, point.z);
            max.x = Math.max(max.x, point.x);
            max.y = Math.max(max.y, point.y);
            max.z = Math.max(max.z, point.z);
        }

//        for(Point p : pointMap.values() ) {
//            System.out.println("(" + p.x + " , " + p.y + " , " + p.z + ")");
//        }
//        System.out.println("min: (" + min.x + " , " + min.y + " , " + min.z + ")");
//        System.out.println("max: (" + max.x + " , " + max.y + " , " + max.z + ")");

        delta = new Point[6];
        delta[0] = new Point(1,0,0);
        delta[1] = new Point(-1, 0, 0);
        delta[2] = new Point(0,1,0);
        delta[3] = new Point(0,-1,0);
        delta[4] = new Point(0,0,1);
        delta[5] = new Point(0,0,-1);

        int sumFaces = 0;
        for(Point p : pointMap.values() ) {
            int faces = 6;
            for(int face = 0; face < 6; face++) {
                Point n = new Point(p.x, p.y, p.z);

                n.add(delta[face]);
                String key = n.x + "," + n.y + "," + n.z;
                if (pointMap.containsKey(key))
                    faces--;

            }
//            String key = p.x + "," + p.y + "," + p.z;
//            System.out.println(key+" free faces: "+faces);
            sumFaces += faces;
        }
        System.out.println("Part 1: Sum faces:"+sumFaces);


        min.x--;min.y--; min.z--;
        max.x++;max.y++; max.z++;
        int fills = floodFill(min, max, min);
//        System.out.println("Fills:"+fills);

        sumFaces = 0;
        for(Point p : pointMap.values() ) {
            if(!p.stone)
                continue;
            int faces = 0;
            for(int face = 0; face < 6; face++) {
                Point n = new Point(p.x, p.y, p.z);
                n.add(delta[face]);
                if(n.x < min.x || n.y < min.y || n.z < min.z)   // outside the fill cube
                    continue;
                if(n.x > max.x || n.y > max.y || n.z > max.z)   // idem
                    continue;
                String key = n.x + "," + n.y + "," + n.z;
                Point nb = pointMap.get(key);
                if (nb != null && !nb.stone )
                    faces++;
            }
//            String key = p.x + "," + p.y + "," + p.z;
//            System.out.println(key+" air touching faces: "+faces);
            sumFaces += faces;
        }
        System.out.println("Part 2: Sum faces:"+sumFaces);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }

    private int floodFill(Point min, Point max, Point start) {
        ArrayList<Point> fillList = new ArrayList<>();
        fillList.add(start);
        int sum = 0;
        while(fillList.size() > 0) {
            start = fillList.get(0);
            fillList.remove(0);
            for (int face = 0; face < 6; face++) {
                // any neigbouring cube (inside the fill cube)
                Point n = new Point(start.x, start.y, start.z);
                n.add(delta[face]);
                if (n.x < min.x || n.y < min.y || n.z < min.z)   // outside the fill cube
                    continue;
                if (n.x > max.x || n.y > max.y || n.z > max.z)   // idem
                    continue;
                String key = n.x + "," + n.y + "," + n.z;
                if (!pointMap.containsKey(key)) {       // empty?
                    n.stone = false;                    // fill with air
                    pointMap.put(key, n);
                    sum++;
                    fillList.add(n);
                    //sum += floodFill(min, max, n);
                }
            }
        }
        return sum;
    }
}
