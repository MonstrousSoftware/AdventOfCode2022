package com.monstrous;

import java.util.ArrayList;
import java.util.Comparator;

public class Day15 {

    class GridPoint {
        int x;
        int y;

        public void print() {
            System.out.print("["+x+","+y+"]");
        }
    }
    class Sensor {
        int id;
        GridPoint location;
        GridPoint beacon;
        int distanceToBeacon;
    }
    class Range {
        int start;
        int end;
    }
    class RangeComparator implements Comparator<Range> {

        @Override
        public int compare(Range o1, Range o2) {
            if(o1.start == o2.start)
                return o1.end - o2.end;
            else
              return o1.start - o2.start;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }
    }


    final FileInput input;

    public Day15() {
        System.out.print("Day 15\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day15.txt");

        int SIZE = 4000000; // 20 for the example


        ArrayList<Sensor> sensors = new ArrayList<>();

        int id = 1;
        for (String line : input.lines) {
            // Sensor at x=14, y=17: closest beacon is at x=10, y=16

            String[] words = line.split("[^\\-0-9]+");

            Sensor sensor = new Sensor();
            GridPoint sensorLoc = new GridPoint();
            GridPoint beacon = new GridPoint();
            sensorLoc.x = Integer.parseInt(words[1]);
            sensorLoc.y = Integer.parseInt(words[2]);
            beacon.x = Integer.parseInt(words[3]);
            beacon.y = Integer.parseInt(words[4]);

            //            System.out.print("sensor: "+id+" ");
            //            sensorLoc.print();
            //            beacon.print();

            sensor.id = id++;
            sensor.location = sensorLoc;
            sensor.beacon = beacon;
            sensor.distanceToBeacon = manhattan(sensorLoc, beacon);
            sensors.add(sensor);

            //            System.out.println(" dist: "+sensor.distanceToBeacon);

        }
        int y = SIZE/2;
        int scanned = scanLine(y, sensors, false, 0, 0);
        System.out.println("Part 1: Line "+y+" does not contain beacons in "+scanned+" positions");

        // just brute force part 2
        for(y = 0; y <= SIZE; y++) {
            scanned = scanLine(y, sensors, true, 0, SIZE);
            if (scanned >= 0) {
                System.out.println("Part 2: Uncovered spot in line " + y + " at x = " + scanned + "; frequency = " + ((long)SIZE * scanned + y));
                break;
            }
        }

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime)+" ms");


    }

    private int manhattan(GridPoint a, GridPoint b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private int scanLine(int y, ArrayList<Sensor> sensors, boolean limited, int minx, int maxx) {

        ArrayList<Range> ranges = new ArrayList();
        for(Sensor sensor : sensors) {
            int distY = Math.abs(sensor.location.y - y);
            if(distY > sensor.distanceToBeacon) {       // too far from sensor
                //System.out.println("sensor "+sensor.id+" no intersect , y distance = "+distY+" and manhattan="+sensor.distanceToBeacon);
                continue;
            }
            int distX = sensor.distanceToBeacon - distY;
            Range range = new Range();

            range.start = sensor.location.x - distX;
            range.end = sensor.location.x + distX;
            //System.out.println("sensor "+sensor.id+" cells scanned from "+range.start+ " to "+range.end);
            //sum += 1 + maxX - minX;
            ranges.add(range);

        }

        ranges.sort(new RangeComparator());

//        for(Range range : ranges ){
//            System.out.println("range "+range.start+ " to "+range.end);
//        }

        int sum = 0;

        Range spanningRange = ranges.get(0);
        for(Range range : ranges ){

            if(range.start > spanningRange.end+1){
                sum += 1 + spanningRange.end - spanningRange.start;
//                System.out.println("spanned range ["+spanningRange.start+" to "+spanningRange.end+" ] values: "+(1 + spanningRange.end - spanningRange.start));
//                System.out.println("line "+y+": gap "+ (spanningRange.end+1) +" to "+(range.start-1));
                int gapx =  (spanningRange.end+1);
                if(limited && gapx >= minx &&  gapx <= maxx)
                    return gapx;
            }
            else if (range.end > spanningRange.end) {
                spanningRange.end = range.end;
            }
        }
        if(limited)
            return -1;

        sum += 1 + spanningRange.end - spanningRange.start;
//        System.out.println("spanned range ["+spanningRange.start+" to "+spanningRange.end+" ] values: "+(1 + spanningRange.end - spanningRange.start));

//        System.out.println("sum "+sum);
        int prevBeaconX = Integer.MAX_VALUE;
        for(Sensor sensor : sensors) {
            if(sensor.beacon.y == y && sensor.beacon.x != prevBeaconX) {       // assuming at most 1 beacon on a line!
                sum--;
                prevBeaconX = sensor.beacon.x;
            }
        }
//        System.out.println("sum after subtracting known beacons: "+sum);


        return sum;
    }


}
