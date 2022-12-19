package com.monstrous;

public class Day17 {

    final static int NUM_DROPS1 = 2022;
    final static long NUM_DROPS2 = 1000000000000L;

    final static int WIDTH = 9;
    final static int DEPTH = 150000;

    class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    class Shape {
        Point [] points;
        int height;
    }

    final FileInput input;
    Shape[] shapes;


    public Day17() {
        System.out.print("Day 17\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day17.txt");
        boolean jetLeft[];

        String line = input.lines.get(0);
            // >>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>

        int len = line.length();
        jetLeft = new boolean[len];
        for(int i = 0; i < len; i++) {
            char k = line.charAt(i);
            if(k == '<')
                jetLeft[i] = true;
            else if(k == '>')
                jetLeft[i] = false;
            else
                System.out.println("ERROR unexp char: "+k);
        }

        //System.out.println("Jets: "+jetLeft.length);

        initShapes();
        char[][] grid = new char[DEPTH][WIDTH];

        long height = getTowerHeight(grid, jetLeft, NUM_DROPS1);
        System.out.println("Part 1: Tower height="+height);

        height = getTowerHeight(grid, jetLeft, NUM_DROPS2);
        System.out.println("Part 2: Tower height="+height);
        // 1565242165201



        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }


    private long getTowerHeight(char [][] grid, boolean jetLeft[], long numDrops) {

        long topLevel = 0;
        int shapeNr = 0;
        int jetIndex = 0;
        long prevHeight = 0;
        long prevDrop = 0;
        long t = 0;
        long multiple = shapes.length * jetLeft.length;
        long heightPerIteration;
        long middlePart = 0L;

        clearGrid(grid);
        for(long drop = 0; drop < numDrops; drop++) {
            int x = 3;
            int y = (int)topLevel+4;
            Shape shape = shapes[shapeNr++];
            if(shapeNr >= shapes.length)
                shapeNr = 0;
            addShape(grid, shape, x, y, '@');
            //printGrid(grid, y+shape.height-1);

            while(true) {
                removeShape(grid, shape, x, y);
                t++;

                if(t % multiple == 0) {
                    heightPerIteration = (topLevel - prevHeight);
                    long dropsPerIteration = (drop-prevDrop);
                    //System.out.println("tower height " + topLevel + " at t=" + t + " delta=" + heightPerIteration+" drops:"+(drop-prevDrop));
                    if(prevHeight > 0) {
                        long iters = (numDrops - drop)/dropsPerIteration;
                        drop += iters*dropsPerIteration;
                        middlePart = iters * heightPerIteration;
                        //System.out.println("to add: "+ middlePart);
                    }
                    prevHeight = topLevel;
                    prevDrop = drop;
                    //printGrid(grid, topLevel+4 +shape.height);
                }

                int dx = 1;
                if(jetLeft[jetIndex++])
                    dx=-1;
                if(jetIndex >= jetLeft.length) {
                    jetIndex = 0;
                }


                boolean ok = addShape(grid, shape, x+dx, y, '@');
                if(!ok)
                    addShape(grid, shape, x, y, '@');
                else
                    x+=dx;
                removeShape(grid, shape, x, y);
                y--;
                ok = addShape(grid, shape, x, y, '@');
                if(!ok) {
                    addShape(grid, shape, x, y + 1, '#');    // comes to rest
                    //printGrid(grid, y+shape.height);
                    topLevel = Math.max(topLevel, y+shape.height);
                    break;
                }
                //printGrid(grid,y+shape.height);
            }
        }
        //System.out.println("Tower height: "+topLevel+" + "+middlePart+" = "+(topLevel+middlePart));
        return topLevel+middlePart;
    }


    private boolean addShape(char [][]grid, Shape shape, int x, int y, char k) {
        for(int i = 0; i < shape.points.length; i++) {
            if(grid[y-shape.points[i].y][x+shape.points[i].x] != '.')
                return false;
        }
        for(int i = 0; i < shape.points.length; i++) {
            grid[y-shape.points[i].y][x+shape.points[i].x] = k;
        }
        return true;
    }
    private void removeShape(char [][]grid, Shape shape, int x, int y) {
        for(int i = 0; i < shape.points.length; i++) {
            grid[y-shape.points[i].y][x+shape.points[i].x] = '.';
        }
    }

    private void clearGrid(char [][]grid) {
        for(int y = 1; y < grid.length; y++){
            grid[y][0] = '|';
            for(int x = 1; x < grid[y].length-1; x++)
                grid[y][x] = '.';
            grid[y][grid[y].length-1] = '|';
        }
        for(int x = 0; x < grid[0].length; x++)
            grid[0][x] = '=';
    }

    private void printGrid(char [][]grid, int top) {
        for(int y = top; y >= 0; y--){
            for(int x = 0; x < grid[y].length; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private void initShapes() {
        shapes = new Shape[5];
        shapes[0] = new Shape();
        shapes[0].points = new Point[4];
        shapes[0].points[0] = new Point(0,0);
        shapes[0].points[1] = new Point(1,0);
        shapes[0].points[2] = new Point(2,0);
        shapes[0].points[3] = new Point(3,0);
        shapes[0].height = 1;

        shapes[1] = new Shape();
        shapes[1].points = new Point[5];
        shapes[1].points[0] = new Point(1,-2);
        shapes[1].points[1] = new Point(0,-1);
        shapes[1].points[2] = new Point(1,-1);
        shapes[1].points[3] = new Point(2,-1);
        shapes[1].points[4] = new Point(1,0);
        shapes[1].height = 3;

        shapes[2] = new Shape();
        shapes[2].points = new Point[5];
        shapes[2].points[0] = new Point(2,-2);
        shapes[2].points[1] = new Point(2,-1);
        shapes[2].points[2] = new Point(2,0);
        shapes[2].points[3] = new Point(0,0);
        shapes[2].points[4] = new Point(1,0);
        shapes[2].height = 3;

        shapes[3] = new Shape();
        shapes[3].points = new Point[4];
        shapes[3].points[0] = new Point(0,-3);
        shapes[3].points[1] = new Point(0,-2);
        shapes[3].points[2] = new Point(0,-1);
        shapes[3].points[3] = new Point(0,0);
        shapes[3].height = 4;

        shapes[4] = new Shape();
        shapes[4].points = new Point[4];
        shapes[4].points[0] = new Point(0,-1);
        shapes[4].points[1] = new Point(0,0);
        shapes[4].points[2] = new Point(1,-1);
        shapes[4].points[3] = new Point(1,0);
        shapes[4].height = 2;
    }


}
