package com.monstrous;

import java.util.ArrayList;
import java.util.Iterator;


// find optimal path between rooms opening valves to maximize the flow in 30 minutes.

public class Day16 {
    final static String FILE_NAME = "data/day16.txt";

    final static int TIME_LIMIT = 30;
    final static int TIME_LIMIT2 = 26;


    static class Link {
        Room neighbour;
        short distance;

        public Link(Room neighbour, short distance) {
            this.neighbour = neighbour;
            this.distance = distance;
        }
    }


    static class Room {
        int index;
        int valveFlag; // 0 for flowrate == 0, else 1,2, 4, 8, ..
        String name;
        int flowRate;
        ArrayList<String> neighbourNames;
        ArrayList<Link> links;
        short distance;

        public Room(int index, int valveFlag, String name, int flowRate) {
            this.index = index;
            this.valveFlag = valveFlag;
            this.name = name;
            this.flowRate = flowRate;
            neighbourNames = new ArrayList<>();
            links = new ArrayList<>();
        }
    }



    final FileInput input;
    ArrayList<Room> rooms;
    int allFlags = 0;
    int sumValves = 0;
    int numValves = 0;
    short distances[][];    // distance between valves

    public Day16() {
        System.out.print("Day 16\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput(FILE_NAME);

        Room start = null;
        int valveFlag = 1;

        int index = 0;
        rooms = new ArrayList<Room>();
        for (String line : input.lines) {
            // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB

            String[] words = line.split("[^0-9A-Z]+");
            int flowRate = Integer.parseInt(words[2]);
            sumValves += flowRate;
            int flag = 0;
            if (flowRate > 0) {
                flag = valveFlag;
                allFlags |= flag;
                valveFlag *= 2;
                numValves++;
            }
            Room room = new Room(index++, flag, words[1], flowRate);
            for (int i = 3; i < words.length; i++)
                room.neighbourNames.add(words[i]);
            rooms.add(room);
            if (room.name.contentEquals("AA"))
                start = room;
        }

        for (Room room : rooms) {
            for (String nborName : room.neighbourNames) {
                for (int i = 0; i < rooms.size(); i++) {
                    Room nbor = rooms.get(i);
                    if (nbor.name.contentEquals(nborName)) {
                        room.links.add(new Link(nbor, (short) 1));
                        break;
                    }
                }
            }
        }

        simplifyGraph();

        calcDistances(rooms);


        int part1 = findBestPath(TIME_LIMIT, start, 0, 0);
        System.out.println("Part 1(new): Flow = " + part1);

        int part2 = getWinningCombination(start);
        System.out.println("Part 2(new): Flow = " + part2);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");

    }


    private void simplifyGraph() {

        for (Room room : rooms) {
            // search for rooms with flow rate 0 connected to exactly 2 other rooms
            // these can be removed
            if (room.flowRate == 0 && room.links.size() == 2) {
                Room room1 = room.links.get(0).neighbour;
                Room room2 = room.links.get(1).neighbour;
                for (int i = 0; i < room1.links.size(); i++) {
                    Link lback = room1.links.get(i);
                    if (lback.neighbour == room) {
                        room1.links.add(new Link(room2, (short) (lback.distance + room.links.get(1).distance)));
                        room1.links.remove(lback);
                    }
                }
                for (int i = 0; i < room2.links.size(); i++) {
                    Link lback = room2.links.get(i);
                    if (lback.neighbour == room) {
                        room2.links.add(new Link(room1, (short) (lback.distance + room.links.get(0).distance)));
                        room2.links.remove(lback);
                    }
                }
                room.links = null;
            }
        }

        // remove rooms that are not linked (orphans)
        // and renumber the remaining rooms
        int index = 0;
        Iterator iter = rooms.iterator();
        while (iter.hasNext()) {
            Room room = (Room) iter.next();
            if (room.links == null)
                iter.remove();
            else {
                room.index = index++;
            }
        }
    }

    // calculate distance matrix
    // uses Dijkstra algorithm
    private void calcDistances(ArrayList<Room> rooms) {

        distances = new short[rooms.size()][rooms.size()];

        for (Room start : rooms) {

            ArrayList<Room> fringe = new ArrayList<>();
            ArrayList<Room> closed = new ArrayList<>();

            for (Room r : rooms)
                r.distance = Short.MAX_VALUE;
            start.distance = 0;
            fringe.add(start);

            while (fringe.size() > 0) {
                short minDist = Short.MAX_VALUE;
                int closest = -1;
                for (int i = 0; i < fringe.size(); i++) {
                    if (fringe.get(i).distance < minDist) {
                        closest = i;
                        minDist = fringe.get(i).distance;
                    }
                }
                Room next = fringe.get(closest);
                fringe.remove(closest);
                closed.add(next);
                for (Link link : next.links) {
                    Room nbor = link.neighbour;
                    if (closed.contains(nbor))
                        continue;
                    short altDist = (short) (next.distance + link.distance);
                    if (altDist < nbor.distance)
                        nbor.distance = altDist;
                    fringe.add(nbor);
                }
            } // while
            for (Room r : rooms) {
                distances[start.index][r.index] = r.distance;
            }
        }
    }

    // BFS search but at each turn considers all the unvisited valve rooms, not just the adjoining ones.

    private int findBestPath(int timeLimit, Room start, int time, int valveFlags) {
        if (time >= timeLimit)
            return 0;

        int bestScore = 0;
        for (Room r : rooms) {
            if ((r.valveFlag & valveFlags) == r.valveFlag)     // already visited?
                continue;
            int timeAtDest = time + 1 + distances[start.index][r.index]; // +1 minute to open the valve
            int timeLeft = timeLimit - timeAtDest;
            int score = r.flowRate * timeLeft + findBestPath(timeLimit, r, timeAtDest, valveFlags | r.valveFlag);
            if (score > bestScore)
                bestScore = score;
        }
        return bestScore;
    }

    // PART 2

    private int getWinningCombination(Room start) {

        int maxScore = -1;

        for (int i = 0; i <= allFlags; i++) {
            int bits = countBits(i);                // select set of half the valves for the human
            if (bits != numValves / 2)
                continue;
            int complement = ~i & allFlags;         // remaining valves for the elephant

            int flow1 = findBestPath(TIME_LIMIT2, start, 0, i);
            int flow2 = findBestPath(TIME_LIMIT2, start, 0, complement);
            int score = flow1+flow2;
            if(score > maxScore)
                maxScore = score;
        }
        return maxScore;
    }

    private int countBits(int nr) {
        int sum = 0;
        for (int i = 0; i <= numValves; i++) {
            sum += (nr % 2);
            nr >>= 1;
        }
        return sum;
    }

}