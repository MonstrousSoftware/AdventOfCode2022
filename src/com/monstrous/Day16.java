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

//    class State {
//        Room location;
//        Room location2;
//        int valveFlags;
//    }

    final FileInput input;
    ArrayList<Room> rooms;
    private int highScore;
    HashMap<Integer, Integer> stateMap;
    int allFlags = 0;

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
                allFlags |= flag;
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
            //room.neighbours.add(room);      // add room itself as a 'neighbour'
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

        int flow = findBestRoute(start, 0,  path, 0);
        System.out.println("Flow : " + flow);
        //System.out.println("Pressure release : " + highScore);
        // 1638 in 140 s
        // optimized to 5s

        //printPath(bestPath, highScore);

//        Room[] path1 = new Room[TIME_LIMIT2 + 1];
//        bestPath1 = new Room[TIME_LIMIT2 + 1];
//        Room[] path2 = new Room[TIME_LIMIT2 + 1];
//        bestPath2 = new Room[TIME_LIMIT2 + 1];

        stateMap.clear();


        int flow2 = findBestRoutes(start, start,0, path, 0);
        System.out.println("Part 2: Flow : " + flow2);
        //printPath(bestPath, highScore);
        //printPath(bestPath2, highScore);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");

    }






    private int findBestRoute( Room current, int step,  Room[] path, int valvesOpened) {

        int cachedFlow  = findState(current, current, valvesOpened, step);
        if(cachedFlow >= 0)
            return cachedFlow;


        int bestFlow = 0;

        path[step] = current;
        if(step == TIME_LIMIT) {
            bestFlow = 0;
        }
        else if(valvesOpened == allFlags)    // we can stop when all valves are opened
            bestFlow = 0;
        else {
            bestFlow = 0;
            if ((current.valveFlag & valvesOpened) == 0) {
                bestFlow = current.flowRate * (TIME_LIMIT - (step + 1));
                bestFlow += findBestRoute(current, step + 1, path, valvesOpened | current.valveFlag);
            }
            for (Room nbor : current.neighbours) {
                if (step > 0 && nbor == path[step - 1])  // avoid double backs
                    continue;
                int flow = findBestRoute(nbor, step + 1, path, valvesOpened);
                if (flow > bestFlow)
                    bestFlow = flow;
            }
        }
        addState(current, current, valvesOpened, step, bestFlow);
        return bestFlow;
    }

    private int stateCode(Room room, Room room2, int valvesOpened, int steps) {
        return valvesOpened*1000000 + room.index*10000 + room2.index*100 + steps;
    }


    private int findState(Room room, Room room2, int valvesOpened, int steps) {
        int code = stateCode(room, room2, valvesOpened, steps);
        Integer flow = stateMap.get(code);
        if(flow == null)
            return -1;
        else {
            //System.out.println("cache: "+room.name+" flags: "+valvesOpened+" code:" + code + " flow: "+flow);
            return flow;
        }
    }

    private void addState(Room room, Room room2,int valvesOpened, int steps, int flow) {

        int code = stateCode(room, room2, valvesOpened, steps);
        Integer cachedFlow = stateMap.get(code);
        if(cachedFlow == null) {
            stateMap.put(code, flow);
            System.out.println("add to cache: (" + room.name + "+"+room2.name+" flags: " + valvesOpened +  " steps:" + steps +") code:" + code +" flow: " + flow);
        }
        else if (flow > cachedFlow) {
            stateMap.put(code, flow);
            System.out.println("update cache: (" + room.name +"+"+room2.name+ " flags: " + valvesOpened +  " steps:" + steps +") code:" + code + " flow: " + flow);
        }

    }

    private int findBestRoutes( Room current, Room current2, int step,  Room[] path, int valvesOpened) {

        int cachedFlow  = findState(current, current2, valvesOpened, step);
        if(cachedFlow >= 0)
            return cachedFlow;


        int bestFlow = 0;

        path[step] = current;
        if(step == TIME_LIMIT2) {
            bestFlow = 0;
        }
        else if(valvesOpened == allFlags)    // we can stop when all valves are opened
            bestFlow = 0;
        else {
            bestFlow = 0;
            if (current.flowRate > 0 && (current.valveFlag & valvesOpened) == 0) {
                int flow  = current.flowRate * (TIME_LIMIT2 - (step + 1));
                for (Room nbor2 : current2.neighbours) {
                    int flow2 = findBestRoutes(current, nbor2, step + 1,  path, valvesOpened| current.valveFlag);
                    if (flow + flow2 > bestFlow)
                        bestFlow = flow+flow2;
                }
                //bestFlow += findBestRoutes(current, current2, step + 1, total + bestFlow, path, valvesOpened | current.valveFlag);
            }
            if (current2.flowRate > 0 && (current2.valveFlag & valvesOpened) == 0) {
                int flow = current2.flowRate * (TIME_LIMIT2 - (step + 1));
                for (Room nbor : current.neighbours) {
                    int flow2 = findBestRoutes(nbor, current2, step + 1,  path, valvesOpened| current2.valveFlag);
                    if (flow + flow2 > bestFlow)
                        bestFlow = flow+flow2;
                }
                //flow += findBestRoutes(current, current2, step + 1, total + flow, path, valvesOpened | current2.valveFlag);
            }
            if (current.flowRate > 0 && (current.valveFlag & valvesOpened) == 0 &&
                    current2.flowRate > 0 && (current2.valveFlag & valvesOpened) == 0) {
                int flow  = current.flowRate * (TIME_LIMIT2 - (step + 1));
                int flow2 = current2.flowRate * (TIME_LIMIT2 - (step + 1));
                int flow3 = findBestRoutes(current, current2, step + 1,  path, valvesOpened| current.valveFlag | current2.valveFlag);
                if (flow + flow2 +flow3 > bestFlow)
                    bestFlow = flow+flow2+flow3;
            }
            for (Room nbor : current.neighbours) {
                for (Room nbor2 : current2.neighbours) {
                    int flow = findBestRoutes(nbor, nbor2, step + 1, path, valvesOpened);
                    if (flow > bestFlow)
                        bestFlow = flow;
                }
            }
        }
        addState(current, current2, valvesOpened, step, bestFlow);
        return bestFlow;
    }


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
