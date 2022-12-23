package com.monstrous;

import java.util.ArrayList;

public class Day22 {


    public enum Dir {
        RIGHT(0, 1, 0, '>'),
        DOWN(1, 0, 1, 'V'),
        LEFT(2, -1, 0, '<'),
        UP(3, 0, -1, '^');
        public final int code;
        public final int dx;
        public final int dy;
        public final char rep;
        Dir(int code, int dx, int dy, char rep) {
            this.code = code;
            this.dx = dx;
            this.dy = dy;
            this.rep = rep;
        }

    }


    static class Player {
        int x;
        int y;
        Dir facing;

        public void advance() {
            x += facing.dx;
            y += facing.dy;
        }


    }

    static class Edge {
        int startx;
        int starty;
        Dir edgeDir;
        int length;
        Dir facing;
        Edge adjoins;
        char label;

        public Edge(int startx, int starty, Dir edgeDir, int len, Dir facing) {
            this.startx = startx;
            this.starty = starty;
            this.edgeDir = edgeDir;
            this.length = len;
            this.facing = facing;
            this.label = '-';
        }
    }

    final int EDGE_LEN;
    final FileInput input = new FileInput("data/day22.txt");
    final ArrayList<Edge> edges = new ArrayList<>();
    int cols;
    final int rows;
    final char [][] grid;
    final char [][] edgeDemo;
    int part;


    public Day22() {
        System.out.print("Day 22\n");
        final long startTime = System.currentTimeMillis();

        cols = 0;
        for(String line : input.lines ) {
            int len = line.length();
            if(len == 0)
                break;
            cols = Math.max(cols, len);
        }
        rows = input.lines.size() - 2;	// -2 for separator line and path
        EDGE_LEN = Math.abs(rows - cols);

        String path = null;
        grid = new char[rows][cols];
        edgeDemo = new char[rows][cols];    // for visualisation

        // clear grid with spaces
        for(int y = 0; y < rows; y++)
            for(int x = 0; x < cols; x++)
                grid[y][x] = ' ';

        for(int y = 0; y < rows; y++)
            for(int x = 0; x < cols; x++)
                edgeDemo[y][x] = ' ';

        int row = 0;
        for(String line : input.lines ) {
            if (line.length() == 0)    // separator line
                continue;
            char k = line.charAt(0);
            if (isDigit(k) || k == 'R' || k == 'L')    // must be the path
                path = line;
            else {	// is maze row
                for(int x = 0; x < line.length(); x++)
                    grid[row][x] = line.charAt(x);
                row++;
            }
        }

        findPivots(grid);



        //printGrid(grid, player);
        //showEdges();
        for(part = 1; part <= 2; part++) {

            // find start
            Player player = new Player();
            player.x = 0;
            player.y = 0;
            player.facing = Dir.RIGHT;

            //printGrid(grid, player);

            while(grid[player.y][player.x] != '.')
                player.x++;

            for (int index = 0; index < path.length(); index++) {
                char k = path.charAt(index);
                if (k == 'R' || k == 'L') {
                    player.facing = turn(k, player.facing);
                } else if (isDigit(k)) {
                    // accumulate digits to make an integer
                    int dist = k - '0';
                    while (index < path.length() - 1 && isDigit(path.charAt(index + 1))) {
                        index++;
                        dist = 10 * dist + path.charAt(index) - '0';
                    }
                    // advance step by step
                    for (int step = 0; step < dist; step++) {
                        int nx = player.x + player.facing.dx;
                        int ny = player.y + player.facing.dy;
                        // are we on the map edge?
                        if (nx >= cols || nx < 0 || ny >= rows || ny < 0 || grid[ny][nx] == ' ') {    // hit the void, teleport
                            boolean teleported;
                            if(part == 1)
                                teleported = teleportPart1(grid, player);
                            else
                                teleported = teleport(grid, player);
                            if (!teleported)
                                break;
                        } else if (grid[ny][nx] != '#') {
                            player.advance();    // go into empty space
                        } else if (grid[ny][nx] == '#') {    // blocked, stop advance loop
                            break;
                        }
                    }
                }
            }

            //printGrid(grid, player);
            //System.out.println("position at end of path: ("+(1+player.x)+" , "+(1+player.y)+") facing: "+player.facing.code);
            int positionCode = 1000 * (1 + player.y) + 4 * (1 + player.x) + player.facing.code;    // check
            System.out.println("Part "+part+": position code at end of path: " + positionCode);
        }

        //  part 1: 31568
        //  part 2: 36540

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }

    // player is standing facing the void, go in opposite direction until you meet the
    // void at the other side of the island

    private boolean teleportPart1(char [][]grid, Player player) {
        int rows = grid.length;
        int cols = grid[0].length;
        int nx = player.x - player.facing.dx;	// move in reverse
        int ny = player.y - player.facing.dy;

        while(nx >= 0 && nx < cols && ny >= 0 && ny < rows && grid[ny][nx] != ' ') {
            //
            nx -= player.facing.dx;	// move in reverse
            ny -= player.facing.dy;
        }
        nx += player.facing.dx;	// move in reverse
        ny += player.facing.dy;
        if(grid[ny][nx] == '#')    // but don't teleport into a wall
            return false;
        player.x = nx;
        player.y = ny;
        return true;
    }


    private boolean isDigit(char k) {
        return k >= '0' && k <= '9';
    }

    private void printGrid(char [][]grid, Player player){
        int rows = grid.length;
        int cols = grid[0].length;

        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < cols; x++) {
                if(x == player.x && y == player.y)
                    System.out.print(player.facing.rep);
                else
                    System.out.print(grid[y][x]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private void showEdges() {

        for(Edge edge : edges ){
            for(int i = 0; i < EDGE_LEN; i++) {
                int x = edge.startx + i*edge.edgeDir.dx;
                int y = edge.starty + i*edge.edgeDir.dy;
                edgeDemo[y][x] = edge.label;
            }
            System.out.println("Edge "+edge.label+" ("+edge.startx+","+edge.starty+") dir:"+edge.edgeDir.rep+" normal:"+edge.facing);
            label++;
        }
        for(int y = 0; y < rows; y++) {
            for(int x = 0; x < cols; x++) {
                    System.out.print(edgeDemo[y][x]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private char label = 'A';

    private void linkEdges(Edge edge1, Edge edge2) {
        edge1.adjoins = edge2;
        edge2.adjoins = edge1;
        edges.add(edge1);
        edges.add(edge2);
        edge1.label = label++;
        edge2.label = label++;

//        System.out.println("join edges "+edge1.startx+","+edge1.starty+" "+edge1.edgeDir.rep+ " with "+
//                                        edge2.startx+","+edge2.starty+" "+edge2.edgeDir.rep);
    }
    private void pivotEdges(int pivx, int pivy, Dir eDir1, Dir eDir2, Dir dir1, Dir dir2 ) {
        //System.out.println("Pivot "+pivx+" , "+pivy);
        Edge edge1 = new Edge(pivx + eDir1.dx, pivy + eDir1.dy, eDir1, EDGE_LEN, dir1);
        Edge edge2 = new Edge(pivx + eDir2.dx, pivy + eDir2.dy, eDir2, EDGE_LEN, dir2);
        linkEdges(edge1, edge2);

        boolean void1, void2;
        do {
            int x1 = edge1.startx + EDGE_LEN * edge1.edgeDir.dx;
            int y1 = edge1.starty + EDGE_LEN * edge1.edgeDir.dy;
            int x2 = edge2.startx + EDGE_LEN * edge2.edgeDir.dx;
            int y2 = edge2.starty + EDGE_LEN * edge2.edgeDir.dy;
            //System.out.println("follow up " + x1 + "," + y1 + "  vs " + x2 + "," + y2);
            void1 = false;
            if (x1 < 0 || x1 >= cols || y1 < 0 || y1 >= rows || grid[y1][x1] == ' ') {
                //System.out.println("x1, y1 in void");
                void1 = true;
            }
            void2 = false;
            if (x2 < 0 || x2 >= cols || y2 < 0 || y2 >= rows || grid[y2][x2] == ' ') {
                //System.out.println("x2, y2 in void");
                void2 = true;
            }
            if (void1 && !void2) {
                x1 = edge1.startx + (EDGE_LEN-1) * edge1.edgeDir.dx;        // end of edge1
                y1 = edge1.starty + (EDGE_LEN-1) * edge1.edgeDir.dy;

                edge1 = new Edge(x1, y1, edge1.facing, EDGE_LEN, opposite(edge1.edgeDir)); // turn 90 degrees into the block
                edge2 = new Edge(x2, y2, edge2.edgeDir, EDGE_LEN, edge2.facing); // extend edge
                linkEdges(edge1, edge2);
            }
            else if (void2 && !void1) {
                x2 = edge2.startx + (EDGE_LEN-1) * edge2.edgeDir.dx;        // end of edge2
                y2 = edge2.starty + (EDGE_LEN-1) * edge2.edgeDir.dy;
                edge2 = new Edge(x2, y2, edge2.facing, EDGE_LEN, opposite(edge2.edgeDir));
                edge1 = new Edge(x1, y1, edge1.edgeDir, EDGE_LEN, edge1.facing);
                linkEdges(edge1, edge2);
            }
            else
                break;
        } while(true);

    }

    private void findPivots(char [][]grid) {
        // pivots are on block boundaries where 3 blocks meet and the 4th block is void
        // the pivot point is in the corner opposite the void.
        for(int x = EDGE_LEN-1; x < cols-EDGE_LEN; x += EDGE_LEN) {
            for(int y = EDGE_LEN-1; y < rows-EDGE_LEN; y += EDGE_LEN) {
                if(grid[y][x] == ' ' && grid[y][x+1] != ' ' && grid[y+1][x] != ' ' && grid[y+1][x+1] != ' ')
                    pivotEdges(x+1, y+1, Dir.UP, Dir.LEFT, Dir.RIGHT, Dir.DOWN);
                if(grid[y][x] != ' ' && grid[y][x+1] == ' ' && grid[y+1][x] != ' ' && grid[y+1][x+1] != ' ')
                    pivotEdges(x, y+1, Dir.UP, Dir.RIGHT, Dir.LEFT, Dir.DOWN);
                if(grid[y][x] != ' ' && grid[y][x+1] != ' ' && grid[y+1][x] == ' ' && grid[y+1][x+1] != ' ')
                    pivotEdges(x+1, y, Dir.DOWN, Dir.LEFT, Dir.RIGHT, Dir.UP);
                if(grid[y][x] != ' ' && grid[y][x+1] != ' ' && grid[y+1][x] != ' ' && grid[y+1][x+1] == ' ')
                    pivotEdges(x, y, Dir.DOWN, Dir.RIGHT, Dir.LEFT, Dir.UP);
            }
        }
    }


    private boolean teleport(char [][] grid, Player player) {

        for( Edge edge : edges ){
            int endx = edge.startx + (edge.length-1) * edge.edgeDir.dx;
            int endy = edge.starty + (edge.length-1) * edge.edgeDir.dy;
            int minx = Math.min(edge.startx, endx);
            int maxx = Math.max(edge.startx, endx);
            int miny = Math.min(edge.starty, endy);
            int maxy = Math.max(edge.starty, endy);
            // be careful: corners may be on two edges
            // so look at the player direction
            if(player.x >= minx && player.x <= maxx && player.y >= miny && player.y <= maxy && player.facing == opposite(edge.facing)) {  // are we on this edge?
                int offset = Math.max(Math.abs(player.x - edge.startx), Math.abs(player.y - edge.starty));  // relative position on edge
                if(offset < 0 || offset >= EDGE_LEN)
                    System.out.println("ERROR: Offset out of bounds");
                Edge newEdge = edge.adjoins;    // teleport edge
                int newx = newEdge.startx + offset*newEdge.edgeDir.dx;  // corresponding position on matching edge
                int newy = newEdge.starty + offset*newEdge.edgeDir.dy;
                if(grid[newy][newx] == '#')
                    return false;
                player.x = newx;
                player.y = newy;
                player.facing = newEdge.facing;
                return true;
            }
        }
        System.out.println("ERROR: Not on an edge to teleport! "+player.x+" , "+player.y+" facing: "+player.facing.rep);
        return false;
    }


    public Dir turn(char turnDir, Dir facing) {
        if (turnDir == 'R') {
            switch (facing) {
                case RIGHT:
                    return Dir.DOWN;
                case DOWN:
                    return Dir.LEFT;
                case LEFT:
                    return Dir.UP;
                case UP:
                    return Dir.RIGHT;
            }
        } else {
            switch (facing) {
                case RIGHT:
                    return Dir.UP;
                case DOWN:
                    return Dir.RIGHT;
                case LEFT:
                    return Dir.DOWN;
                case UP:
                    return Dir.LEFT;
            }
        }
        return facing;
    }

    public Dir opposite(Dir facing) {
        switch (facing) {
            case RIGHT:
                return Dir.LEFT;
            case DOWN:
                return Dir.UP;
            case LEFT:
                return Dir.RIGHT;
            case UP:
                return Dir.DOWN;
        }
        return facing;
    }

}
