package com.monstrous;

import java.util.ArrayList;
import java.util.HashMap;

public class Day16 {

    final static int TIME_LIMIT = 30;
    final static int TIME_LIMIT2 = 26;
    Room[] bestPath;
    Room[] bestPath1;
    Room[] bestPath2;

    class Room {
        int index;
        int valveFlag; // 0 for flowrate == 0, else 1,2, 4, 8, ..
        String name;
        int flowRate;
        ArrayList<String> neighbourNames;
        ArrayList<Room> neighbours;
        boolean opened;

        public Room(int index, int flag, String name, int flowRate) {
            this.index = index;
            this.valveFlag = flag;
            this.name = name;
            this.flowRate = flowRate;
            opened = false;
            neighbourNames = new ArrayList<>();
            neighbours = new ArrayList<>();
        }
    }

    class State {
        Room location;
        int valveFlags;
    }

    final FileInput input;
    ArrayList<Room> rooms;
    private int highScore;
    HashMap<Integer, Integer> stateMap;

    public Day16() {
        System.out.print("Day 16\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day16a.txt");



        Room start = null;
        int valveFlag = 1;
        int index = 0;
        rooms = new ArrayList<Room>();
        for (String line : input.lines) {
            // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB

            String[] words = line.split("[^0-9A-Z]+");
            int flowRate = Integer.parseInt(words[2]);
            int flag = 0;
            if(flowRate>0) {
                flag = valveFlag;
                valveFlag *= 2;
            }
            Room room = new Room(index++, flag, words[1], flowRate);
            for (int i = 3; i < words.length; i++)
                room.neighbourNames.add(words[i]);
            rooms.add(room);
            if (room.name.contentEquals("AA"))
                start = room;
        }

        for (Room room : rooms) {
            room.neighbours.add(room);      // add room itself as a 'neighbour'
            for (String nborName : room.neighbourNames) {
                for (int i = 0; i < rooms.size(); i++) {
                    Room nbor = rooms.get(i);
                    if (nbor.name.contentEquals(nborName)) {
                        room.neighbours.add(nbor);
                        break;
                    }
                }
            }

        }

        for (Room room : rooms) {
            System.out.print("Room " + room.index+" flag:"+room.valveFlag +" "+room.name + " flow rate " + room.flowRate + " [");
            for (Room nbor : room.neighbours)
                System.out.print(" " + nbor.name);
            System.out.println("]");
        }

        stateMap = new HashMap<>();
        Room[] path = new Room[TIME_LIMIT + 1];
        bestPath = new Room[TIME_LIMIT + 1];
        highScore = 0;

        int flow = findBestRoute(start, 0, 0, path, 0);
        System.out.println("Flow : " + flow);
        System.out.println("Pressure release : " + highScore);
        // 1638 in 140 s
        // optimized to 5s

        printPath(bestPath, highScore);

//        Room[] path1 = new Room[TIME_LIMIT2 + 1];
//        bestPath1 = new Room[TIME_LIMIT2 + 1];
//        Room[] path2 = new Room[TIME_LIMIT2 + 1];
//        bestPath2 = new Room[TIME_LIMIT2 + 1];
//        for(int i = 0; i < numValves; i++)
//            opened[i] = false;
//        findBestRoutes(start, start,0, 0, 0, path1, path2);
//        printPath(bestPath1, highScore);
//        printPath(bestPath2, highScore);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");

    }






    private int findBestRoute( Room current, int step,  int total, Room[] path, int valvesOpened) {

        int bestFlow = 0;

        path[step] = current;
        if(step == TIME_LIMIT) {
//            printPath(path, total);
            if(total > highScore) {
                highScore = total;
                for(int i = 0; i<path.length;i++)
                    bestPath[i] = path[i];
            }
            return bestFlow;
        }

//        int cachedFlow  = findState(current, valvesOpened, step);
//        if(cachedFlow >= 0)
//            return cachedFlow;

        for (Room nbor : current.neighbours) {
            int flow = 0;
            if(nbor == current) {
                if((current.valveFlag & valvesOpened) == current.valveFlag)
                    continue;
                flow = current.flowRate * (TIME_LIMIT-(step+1));
                flow += findBestRoute(nbor, step+1,  total+flow, path, valvesOpened|current.valveFlag);
            } else {
                if (step > 0 && nbor == path[step - 1])  // avoid double backs
                    continue;
                flow = findBestRoute(nbor, step + 1,  total, path, valvesOpened);
            }
            if(flow > bestFlow)
                bestFlow = flow;
        }
        addState(current, valvesOpened, bestFlow, step);
        return bestFlow;
    }

    private int stateCode(Room room, int valvesOpened, int steps) {
        return valvesOpened*10000+room.index*100+steps;
    }


    private int findState(Room room, int valvesOpened, int steps) {
        int code = stateCode(room, valvesOpened, steps);
        Integer flow = stateMap.get(code);
        if(flow == null)
            return -1;
        else {
            System.out.println("cache: "+room.name+" flags: "+valvesOpened+" code:" + code + " flow: "+flow);
            return flow;
        }
    }

    private void addState(Room room, int valvesOpened, int steps, int flow) {

        int code = stateCode(room, valvesOpened, steps);
//        System.out.println("add to cache: "+room.name+" flags: "+valvesOpened+" code:" + code + " flow: "+flow);
        stateMap.put(code, flow);
    }

//    private void findBestRoutes( Room current, Room current2, int step, int flow, int total, Room[] path1, Room[] path2){
//
//        path1[step] = current;
//        path2[step] = current2;
//        if(step == TIME_LIMIT2) {
//            if(total > highScore) {
//                highScore = total;
//
//                for(int i = 0; i<path1.length;i++)
//                    bestPath1[i] = path1[i];
//                for(int i = 0; i<path2.length;i++)
//                    bestPath2[i] = path2[i];
//                printPath(bestPath, highScore);
//                printPath(bestPath2, highScore);
//                System.out.println();
//            }
//            return;
//        }
//
//        total += flow;
//
//
//        for (Room nbor : current.neighbours) {
//            int flow1 = 0;
//            if (nbor == current) {
//                if (current.valveFlag < 0 || opened[current.valveFlag] ) // valve is already open, or flow rate is 0
//                    continue;                                               // skip
//                opened[current.valveFlag] = true;   // open valve of this room
//                flow1 = current.flowRate;
//            }
//            if (step > 0 && nbor == path1[step - 1])  // avoid double backs
//                continue;
//            for (Room nbor2 : current2.neighbours) {
//
//                int flow2 = 0;
//                if (nbor2 == current2) {
//                    if (current2.valveFlag < 0 || opened[current2.valveFlag] ) // valve is already open, or flow rate is 0
//                        continue;                                               // skip
//                    opened[current2.valveFlag] = true;   // open valve of this room
//                    flow2 =  current2.flowRate;
//                }
//                if (step > 0 && nbor2 == path2[step - 1])  // avoid double backs
//                    continue;
//                findBestRoutes(nbor, nbor2, step + 1, flow + flow1+flow2, total, path1, path2);
//                if (nbor2 == current2)
//                    opened[current2.valveFlag] = false; // undo
//            }
//            if (nbor == current)
//                opened[current.valveFlag] = false; // undo
//        }
//    }


    private void printPath( Room[] path, int score) {

        System.out.print(""+score+" : ");
        for(int i = 0; i<path.length;i++)
            if(path[i] == null)
                System.out.print(" ("+i+"): null");
            else
                System.out.print(" ("+i+"): "+path[i].name);
        System.out.println(" : "+score);
    }
}
