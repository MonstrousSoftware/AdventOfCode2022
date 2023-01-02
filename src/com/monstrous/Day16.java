package com.monstrous;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;


// find optimal path between rooms opening valves to maximize the flow in 30 minutes.

public class Day16 {
    final static String FILE_NAME = "data/day16.txt";

    final static int TIME_LIMIT = 30;
    final static int TIME_LIMIT2 = 26;
    Room[] bestPath;
    Room[] bestPath1;
    Room[] bestPath2;
    int combiScores[];

    static class Link {
        Room neighbour;
        short distance;

        public Link(Room neighbour, short distance) {
            this.neighbour = neighbour;
            this.distance = distance;
        }
    }

    static class RoomComparator implements Comparator<Room> {

        @Override
        public int compare(Room o1, Room o2) {
            return o2.flowRate - o1.flowRate;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }
    }

    static class Room {
        int index;
        int valveFlag; // 0 for flowrate == 0, else 1,2, 4, 8, ..
        String name;
        int flowRate;
        ArrayList<String> neighbourNames;
        ArrayList<Link> links;
        boolean opened;
        short distance;
        int flag;

        public Room(int index, int valveFlag, String name, int flowRate) {
            this.index = index;
            this.valveFlag = valveFlag;
            this.name = name;
            this.flowRate = flowRate;
            opened = false;
            neighbourNames = new ArrayList<>();
            links = new ArrayList<>();
        }
    }

    static class Node {
        Node next;
        int step;
        Room current;
        Room current2;
        boolean openingValve;
        boolean openingValve2;
        int flow;
        int flowRate;

        public Node(int step, Room current, Room current2, boolean openingValve, boolean openingValve2, int flow) {
            this.step = step;
            this.current = current;
            this.current2 = current2;
            this.openingValve = openingValve;
            this.openingValve2 = openingValve2;
            this.flow = flow;
            this.flowRate = flowRate;
        }
    }

    class State {
        char timeH;
        char timeE;
        char humanIndex;
        char elephantIndex;
        short roomFlags;

        public State() {

        }

        public State(int timeH, int timeE, Room human, Room elephant, int roomFlags) {
            this.timeH = (char)timeH;
            this.timeE = (char)timeE;
            this.humanIndex = (char)human.index;
            this.elephantIndex = (char)elephant.index;
            this.roomFlags = (short)roomFlags;
        }
    }

    final FileInput input;
    ArrayList<Room> rooms;
    private int highScore;
    HashMap<Integer, Integer> stateMap;
    HashMap<Integer, Node> cache;
    HashMap<State, Integer> cache2;
    int allFlags = 0;
    int sumValves = 0;
    int numValves = 0;
    short distances[][];    // distance between valves
    HashMap<Integer, Integer> flowMap = new HashMap<>();

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
            if(flowRate>0) {
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
            //room.neighbours.add(room);      // add room itself as a 'neighbour'
            for (String nborName : room.neighbourNames) {
                for (int i = 0; i < rooms.size(); i++) {
                    Room nbor = rooms.get(i);
                    if (nbor.name.contentEquals(nborName)) {
                        room.links.add( new Link(nbor, (short)1) );
                        break;
                    }
                }
            }
        }

        simplifyGraph();



        for (Room room : rooms) {
            System.out.print("Room " + room.index+" valveFlag:"+room.valveFlag +" "+room.name + " flow rate " + room.flowRate + " [");
            for (Link link : room.links)
                System.out.print(" " + link.neighbour.name+" ("+link.distance+")");
            System.out.println("]");
        }
        System.out.println("all flags: "+allFlags);

        calcDistances(rooms);


        int part1 = findBestPath(TIME_LIMIT, start, 0, 0);
        System.out.println("Part 1(new): Flow = " + part1);

        int part2 = getWinningCombination(start);
        System.out.println("Part 2(new): Flow = " + part2);


//        int flow0b = findBestPathRecord(start, 0,0, start.flag);
//        System.out.println("Part 2 max flow for one actor = " + flow0b);
//        findWinningCombination();



//        cache2 = new HashMap<>();
//
//        int flow2 = findBestPath2c(0, 0,start, start, 0, 0, start.flag);
//        System.out.println("Part 2(new): Flow = " + flow2);
//        System.out.println("cache size = " + cache2.size());
//
//        stateMap = new HashMap<>();
//        Room[] path = new Room[TIME_LIMIT + 15];
//        bestPath = new Room[TIME_LIMIT + 15];
//        highScore = 0;
//
//        path[0] = start;
        //Node node = new Node(0, start, null, false, 0, 0);
 //       int flow = findBestRoute(start,  1,  0, 0, path, 0);
 //       System.out.println("Part 1: Flow = " + flow);


        //showBestRoutes(start, start, 0, 0);

        //System.out.println("Pressure release : " + highScore);
        // 1638 in 140 s
        // optimized to 5s

        //printPath(bestPath, highScore);

//        Room[] path1 = new Room[TIME_LIMIT2 + 1];
//        bestPath1 = new Room[TIME_LIMIT2 + 1];
//        Room[] path2 = new Room[TIME_LIMIT2 + 1];
//        bestPath2 = new Room[TIME_LIMIT2 + 1];

//        stateMap.clear();
//
//        cache = new HashMap<>();

//        Node solution = findBestRoutes(start, start,0, 1, 1, 0,0, 0);
//        System.out.println("Part 2: Flow = " + solution.flow);
//
//        System.out.println("Cache size: " + stateMap.size());
//        printNodeList(solution);

//        printPath(bestPath1, highScore);
//        printPath(bestPath2, highScore);

//        for(Integer key : stateMap.keySet()) {
//            System.out.println("cache["+ key +"] = "+stateMap.get(key));
//        }
//        int key = stateCode(start, start, 0, 0, 0, 0);
//        System.out.println("cache["+ key +"] = "+stateMap.get(key));

//        showBestRoutes(start, start, 0, 0);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");

    }


    private void simplifyGraph() {

        for (Room room : rooms) {
            // search for rooms with flow rate 0 connected to exactly 2 other rooms
            // these can be removed
            if(room.flowRate == 0 && room.links.size() == 2) {
                Room room1 = room.links.get(0).neighbour;
                Room room2 = room.links.get(1).neighbour;
                for(int i = 0; i < room1.links.size(); i++) {
                    Link lback = room1.links.get(i);
                    if(lback.neighbour == room) {
                        room1.links.add( new Link(room2,(short)(lback.distance + room.links.get(1).distance) ) );
                        room1.links.remove(lback);
                    }
                }
                for(int i = 0; i < room2.links.size(); i++) {
                    Link lback = room2.links.get(i);
                    if(lback.neighbour == room) {
                        room2.links.add( new Link(room1,(short)(lback.distance + room.links.get(0).distance) ) );
                        room2.links.remove(lback);
                    }
                }
                room.links = null;
            }
        }
        int index = 0;
        int roomFlag = 1;
        Iterator iter = rooms.iterator();
        while( iter.hasNext()) {
            Room room = (Room) iter.next();
            if(room.links == null)
                iter.remove();
            else {
                room.index = index++;
                room.flag = roomFlag;
                roomFlag <<= 1;
            }
        }
    }

    // calculate distance matrix
    // uses Dijkstra algorithm
    private void calcDistances( ArrayList<Room> rooms ) {

        distances = new short [rooms.size()][rooms.size()];

        for(Room start: rooms ) {

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
                //System.out.println("Room " + r.name + " has distance " + r.distance);
                distances[start.index][r.index] = r.distance;
            }
        }
        for(int y = 0; y < rooms.size(); y++){
            for(int x = 0; x < rooms.size(); x++){
                System.out.print(distances[y][x] + "\t");
            }
            System.out.println();
        }
    }

    // BFS search but at each turn considers all the unvisited valve rooms, not just the adjoining ones.

    private int findBestPath(int timeLimit, Room start, int time, int roomFlags){
        ArrayList<Room> options = new ArrayList<>();

        if(time >= timeLimit)
            return 0;

        // worth building this array?
        for(Room r : rooms) {
            if((r.valveFlag & roomFlags) != r.valveFlag )     // not already visited?
                options.add(r);
        }
        //options.sort(new RoomComparator() );    // sort by valve flow

        int bestScore = 0;
        for(Room r : options) {
            int timeAtDest = time+1+ distances[start.index][r.index]; // +1 minute to open the valve
            int timeLeft = timeLimit - timeAtDest;
            int score = r.flowRate * timeLeft + findBestPath(timeLimit, r, timeAtDest, roomFlags | r.valveFlag);
            if(score > bestScore)
                bestScore = score;
        }
        return bestScore;
    }

    // PART 2

//    private int findBestPath2(Room start, int time, int roomFlags){
//        ArrayList<Room> options = new ArrayList<>();
//
//        if(time >= TIME_LIMIT2)
//            return 0;
//
//        // worth building this array?
//        for(Room r : rooms) {
//            if((r.valveFlag & roomFlags) != r.valveFlag )     // not already visited?
//                options.add(r);
//        }
//        //options.sort(new RoomComparator() );    // sort by valve flow
//
//        int bestScore = 0;
//        for(Room r : options) {
//            int timeAtDest = time+1+ distances[start.index][r.index]; // +1 minute to open the valve
//            int timeLeft = TIME_LIMIT2 - timeAtDest;
//            int score = r.flowRate * timeLeft + findBestPath2(r, timeAtDest, roomFlags | r.valveFlag);
//            if(score > bestScore)
//                bestScore = score;
//        }
//        return bestScore;
//    }




    private int getWinningCombination(Room start) {

        combiScores = new int[allFlags+1];

        for(int i = 0; i <= allFlags; i++) {
//            int bits = countBits(i);
//            if(bits != numValves/2)
//                continue;
            int complement = ~i & allFlags;
            //System.out.println("human: "+i+" elephant:"+complement);

            int flow1 = findBestPath(TIME_LIMIT2, start, 0, i );
            int flow2 = findBestPath(TIME_LIMIT2, start, 0, complement );
            combiScores[i] = flow1+flow2;
        }

        int max = -1;
        int maxi = 0;
        for(int i = 0; i <= allFlags; i++)
            if(combiScores[i] > max) {
                max = combiScores[i];
                maxi = i;
            }
        int complement = ~maxi & allFlags;
        System.out.println("human: "+maxi+" elephant:"+complement+" scores: "+max);
        int flow1 = findBestPath(TIME_LIMIT2, start, 0, maxi );
        int flow2 = findBestPath(TIME_LIMIT2, start, 0, complement );
        System.out.println("human: "+flow1+" elephant:"+flow2);
        return max;
    }

    private int countBits(int nr )
    {
        int sum = 0;
        for(int i = 0; i <= numValves; i++) {
            sum += (nr % 2);
            nr >>= 1;
        }
        return sum;
    }

    private int findWinningCombination() {
        for(int flags : flowMap.keySet()){
            System.out.println("key "+flags+" value: "+flowMap.get(flags));
        }
        for(int h : flowMap.keySet()){
            for(int e: flowMap.keySet()){
                if((h & e) == 0)
                    System.out.println("Combi "+h+" x "+e);
            }

        }
        return 0;
    }


    private int findBestPath2(int time, Room human, Room elephant, int timeH, int timeE, int roomFlags){


        if(time >= TIME_LIMIT2)
            return 0;

        ArrayList<Room> options = new ArrayList<>();
        for(Room r : rooms) {
            if((r.flag & roomFlags) != r.flag )     // not already visited?
                options.add(r);
        }
        options.sort(new RoomComparator() );    // sort by valve flow
        int bestScore = 0;
        if(time == timeH) { // human to choose next path
            for (Room r : options) {
                int timeAtDest = time + 1 + distances[human.index][r.index]; // +1 minute to open the valve
                int timeLeft = TIME_LIMIT2 - timeAtDest;
                int score = r.flowRate * timeLeft;
                int timeNext = Math.min(timeAtDest, timeE);
                score += findBestPath2(timeNext, r, elephant, timeAtDest, timeE, roomFlags | r.flag);
                if (score > bestScore)
                    bestScore = score;
            }
        }
        if(time == timeE) { // elephant to choose
            for (Room r : options) {
                int timeAtDest = time + 1 + distances[elephant.index][r.index]; // +1 minute to open the valve
                int timeLeft = TIME_LIMIT2 - timeAtDest;
                int score = r.flowRate * timeLeft;
                int timeNext = Math.min(timeAtDest, timeH);
                score += findBestPath2(timeNext, human, r, timeH, timeAtDest, roomFlags | r.flag);
                if (score > bestScore)
                    bestScore = score;
            }
        }
        return bestScore;
    }

    State state2 = new State();
    State state3 = new State();

    int bestScoreSoFar = -1;

    //with cache
    private int findBestPath2c(int time, int totalFlow, Room human, Room elephant, int timeH, int timeE, int roomFlags){

        if(time >= TIME_LIMIT2)
            return 0;

        if(totalFlow + sumValves * (TIME_LIMIT2-time) < bestScoreSoFar) {
            //System.out.println("Prune at time "+time);
            return 0;
        }

        //state = new State(timeH, timeE, human, elephant, roomFlags);
        state2.timeE = (char)timeE;
        state2.timeH = (char)timeH;
        state2.humanIndex = (char)human.index;
        state2.elephantIndex = (char)elephant.index;
        state2.roomFlags = (short)roomFlags;
        Integer cachedFlow = cache2.get(state2);
        if(cachedFlow != null)
            return cachedFlow;
        state3.timeE = (char)timeH;
        state3.timeH = (char)timeE;
        state3.humanIndex = (char)elephant.index;
        state3.elephantIndex = (char)human.index;
        state3.roomFlags = (short)roomFlags;
        Integer cachedFlow2 = cache2.get(state3);
        if(cachedFlow2 != null)
            return cachedFlow2;

//        ArrayList<Room> options = new ArrayList<>();
//        for(Room r : rooms) {
//            if((r.flag & roomFlags) != r.flag )     // not already visited?
//                options.add(r);
//        }
//        options.sort(new RoomComparator() );    // sort by valve flow



        int bestScore = 0;
        if(time == timeH) { // human to choose next path
            for (Room r : rooms) {
                if((r.flag & roomFlags) == r.flag )     // already visited?
                    continue;

                int timeAtDest = time + 1 + distances[human.index][r.index]; // +1 minute to open the valve
                int timeLeft = TIME_LIMIT2 - timeAtDest;
                int score = r.flowRate * timeLeft;
                int timeNext = Math.min(timeAtDest, timeE);
                score += findBestPath2c(timeNext,score+totalFlow,  r, elephant, timeAtDest, timeE, roomFlags | r.flag);
                if (score > bestScore)
                    bestScore = score;
            }
        }
        if(time == timeE) { // elephant to choose
            for (Room r : rooms) {
                if((r.flag & roomFlags) == r.flag )     // already visited?
                    continue;
                int timeAtDest = time + 1 + distances[elephant.index][r.index]; // +1 minute to open the valve
                int timeLeft = TIME_LIMIT2 - timeAtDest;
                int score = r.flowRate * timeLeft;
                int timeNext = Math.min(timeAtDest, timeH);
                score += findBestPath2c(timeNext, score+totalFlow, human, r, timeH, timeAtDest, roomFlags | r.flag);
                if (score > bestScore)
                    bestScore = score;
            }
        }



        State state = new State(timeH, timeE, human, elephant, roomFlags);
        cache2.put(state, bestScore);

        if(bestScore > bestScoreSoFar) {
            bestScoreSoFar = bestScore;
            System.out.println("Best score so far: "+bestScore);
        }

        return bestScore;
    }



    private int bestScore = -1;



    private int findBestRoute( Room current, int step, int flowRate, int totalFlow, Room[] path, int valvesOpened) {
        path[step] = current;

        int code = stateCode(current, current, 0,0,valvesOpened, step);
        Integer cachedFlow = stateMap.get(code);
        if(cachedFlow != null)
            return cachedFlow;

        int bestFlow = 0;

        if(step >= TIME_LIMIT) {            // time's up, don't branch any further
            bestFlow = 0;
        }
        else if(valvesOpened == allFlags) {  // we can stop when all valves are opened
            bestFlow = 0;
        } else {
            bestFlow = 0;
            // option 1: stay in same room to open the valve, takes one minute
            if (current.flowRate > 0 && (current.valveFlag & valvesOpened) == 0) {  // found closed valve in current room
                // note: the flow doesn't count for the minute we're opening the valve
                bestFlow = current.flowRate * (TIME_LIMIT - step);    // flow contribution for remaining time
                int flow =  findBestRoute(current, step+ 1, flowRate+ current.flowRate, totalFlow+bestFlow, path, valvesOpened | current.valveFlag);
                //System.out.println("after opening "+current.name+(bestFlow+flow)+" = "+bestFlow+"("+current.flowRate+"*"+(TIME_LIMIT-step)+")+"+flow+ " in minute "+step);
                bestFlow += flow;
            }
            // other options: go to adjoining room, takes N minutes depending on distance
            for (Link link : current.links) {
//                if (step > 0 && link.neighbour == path[step - 1])  // avoid double backs
//                    continue;

                int flow = findBestRoute(link.neighbour, step+link.distance, flowRate, totalFlow, path, valvesOpened);
                if (flow > bestFlow)    // and use the best option
                    bestFlow = flow;

            }
        }
        stateMap.put(code, bestFlow);
        return bestFlow;
    }



    private int stateCode(Room room, Room room2, int travel1, int travel2, int valvesOpened, int steps) {
        if(steps >= 100)
            System.out.println("steps overflow "+steps);
        if(room2.index >= 100)
            System.out.println("room2 overflow "+room2);
        if(room.index >= 100)
            System.out.println("room overflow "+room);

        return valvesOpened*10000000 + travel1* 1000000 + travel2*100000+room.index*10000 + room2.index*100 + steps;
    }


    private void Log( int t, String message ) {
        if(t < 10)
            System.out.println("t="+t+": "+message);
    }


    // find best route for 2 actors working together
    
    private Node findBestRoutes( Room current, Room current2, int step, int travel1, int travel2, int flowRate, int totalFlow,  int valvesOpened) {

        step++;
        travel1--;      // reaches 0 when arriving at destination
        travel2--;

        int code = stateCode(current, current2, travel1, travel2, valvesOpened, step);

            Node cachedNode = cache.get(code);
            if (cachedNode != null) {
                Log(step," from cache ("+current.name+","+current2.name+", cached flow="+cachedNode.flow);
                return cachedNode;
            }
            int code2 = stateCode(current2, current, travel2, travel1, valvesOpened, step);   // use symmetry
            cachedNode = cache.get(code);
            if (cachedNode != null) {
                Log(step, " from cache (" + current.name + "," + current2.name + ", cached flow=" + cachedNode.flow);
                return cachedNode;
            }


        int bestFlow = 0;

//        if(totalFlow + (TIME_LIMIT2 - step)*sumValves < bestTotalFlow)  // prune if no time left to beat the record
//            return 0;

        Node node = new Node(step-1, current, current2, false, false, 0);
        node.flowRate = flowRate;
        if(step >= TIME_LIMIT2) {
            bestFlow = 0;
            node.next = null;
            Log(step," time's up, total flow="+totalFlow);
        }
        else if(valvesOpened == allFlags) {   // we can stop when all valves are opened
            Log(+step," all valves open");
            node.next = null;
            bestFlow = 0;
        } else {

            bestFlow = 0;
            if(travel1 > 0 && travel2 > 0) {
                Node nextNode = findBestRoutes(current, current2, step, travel1, travel2,  flowRate , totalFlow,valvesOpened);
                bestFlow = nextNode.flow;
                node.openingValve = false;
                node.openingValve2 = false;
                node.flow = bestFlow;
                node.next = nextNode;
            }
            if (travel1 == 0 && travel2 == 0 && current != current2 &&
                    current.flowRate > 0 && (current.valveFlag & valvesOpened) == 0 &&  // both opening valves
                    current2.flowRate > 0 && (current2.valveFlag & valvesOpened) == 0) {
                int flow  = current.flowRate * (TIME_LIMIT2 - step);
                int flow2 = current2.flowRate * (TIME_LIMIT2 - step);
                Log(step," open valve "+current.name+flow+" and open valve "+current2.name+flow2);
                Node nextNode = findBestRoutes(current, current2, step, 1, 1,  flowRate+current.flowRate +current2.flowRate , totalFlow+flow+flow2,valvesOpened| current.valveFlag | current2.valveFlag);
                bestFlow = flow+flow2+nextNode.flow;
                node.openingValve = true;
                node.openingValve2 = true;
                node.flow = bestFlow;
                node.next = nextNode;
            }
            if (travel1 == 0 && current.flowRate > 0 && (current.valveFlag & valvesOpened) == 0) {  // open valve in current room
                int flow  = current.flowRate * (TIME_LIMIT2 - step);
                if(travel2 == 0) {
                    for (Link link2 : current2.links) {
                        Log(step, "open valve "+current.name+flow+" elephant goes to "+link2.neighbour.name);
                        Node nextNode = findBestRoutes(current, link2.neighbour, step, 1, link2.distance, flowRate+current.flowRate, totalFlow + flow, valvesOpened | current.valveFlag);
                        if (nextNode.flow + flow > bestFlow) {
                            bestFlow = nextNode.flow + flow;
                            node.openingValve = true;
                            node.openingValve2 = false;
                            node.flow = bestFlow;
                            node.next = nextNode;
                        }
                    }
                }
                else {
                    Log(step, " open valve "+current.name+flow+" elephant traveling ");
                    Node nextNode = findBestRoutes(current, current2, step, 1, travel2,flowRate+current.flowRate,  totalFlow + flow, valvesOpened | current.valveFlag);
                    if (nextNode.flow + flow > bestFlow) {
                        bestFlow = nextNode.flow + flow;
                        node.openingValve = true;
                        node.openingValve2 = false;
                        node.flow = bestFlow;
                        node.next = nextNode;
                    }
                }
             }
             if (travel2 == 0 && current2.flowRate > 0 && (current2.valveFlag & valvesOpened) == 0) { // elephant opens valve in his room
                int flow = current2.flowRate * (TIME_LIMIT2 - step);
                if(travel1 == 0) {
                    for (Link link1 : current.links) {
                        Log(step, " go to "+link1.neighbour.name+" elephant opens valve "+current2.name+flow);
                        Node nextNode = findBestRoutes(link1.neighbour, current2, step, link1.distance, 1, flowRate+current2.flowRate, totalFlow + flow, valvesOpened | current2.valveFlag);
                        if (nextNode.flow + flow > bestFlow) {
                            bestFlow = nextNode.flow + flow;
                            node.openingValve = false;
                            node.openingValve2 = true;
                            node.flow = bestFlow;
                            node.next = nextNode;
                        }
                    }
                }
                else {
                    Log(step, " travelling, elephant opens valve "+current2.name+flow);
                    Node nextNode = findBestRoutes(current, current2, step, travel1, 1, flowRate+current2.flowRate, totalFlow + flow, valvesOpened | current2.valveFlag);
                    if (nextNode.flow + flow > bestFlow) {
                        bestFlow = nextNode.flow + flow;
                        node.openingValve = false;
                        node.openingValve2 = true;
                        node.flow = bestFlow;
                        node.next = nextNode;
                    }
                }
             }
            if(travel1 == 0 && travel2 == 0) {
                for (Link link : current.links) {
                    for (Link link2 : current2.links) {
                        if (link.neighbour == link2.neighbour)
                            continue;
                        Log(step, " goto "+link.neighbour.name+" and to "+link2.neighbour.name);
                        Node nextNode = findBestRoutes(link.neighbour, link2.neighbour, step, link.distance, link2.distance, flowRate,  totalFlow, valvesOpened);
                        if (nextNode.flow > bestFlow) {
                            bestFlow = nextNode.flow;
                            node.openingValve = false;
                            node.openingValve2 = false;
                            node.flow = bestFlow;
                            node.next = nextNode;
                        }
                    }
                }
            } else if(travel1 == 0 && travel2 > 0) {
                for (Link link : current.links) {
                    Log(step, " goto "+link.neighbour.name+" elephant travelling");
                    Node nextNode = findBestRoutes(link.neighbour, current2, step, link.distance, travel2, flowRate, totalFlow, valvesOpened);
                    if (nextNode.flow > bestFlow) {
                        bestFlow = nextNode.flow;
                        node.openingValve = false;
                        node.openingValve2 = false;
                        node.flow = bestFlow;
                        node.next = nextNode;
                    }
                }
            } else if(travel1 > 0 && travel2 == 0) {
                for (Link link2 : current2.links) {
                    Log(step, " travelling, elephant goes to "+link2.neighbour.name);
                    Node nextNode = findBestRoutes(current, link2.neighbour, step, travel1, link2.distance, flowRate, totalFlow, valvesOpened);
                    if (nextNode.flow > bestFlow) {
                        bestFlow = nextNode.flow;
                        node.openingValve = false;
                        node.openingValve2 = false;
                        node.flow = bestFlow;
                        node.next = nextNode;
                    }
                }
            }
        }

        if(bestFlow < 0)
            System.out.println("Negative best flow");

        Log(step, " best flow "+bestFlow);


            cache.put(code, node);
        return node;
    }

//    private void showBestRoutes( Room current, Room current2, int step,   int valvesOpened) {
//        System.out.println("Time "+step+": room "+current.name +" and room "+current2.name+" valves: "+valvesOpened);
//        int code = stateCode(current, current2, valvesOpened, step);
//        Integer cachedFlow = stateMap.get(code);
//        System.out.println("Flow:  "+cachedFlow+"key: "+code);
//
//        if(step == TIME_LIMIT2) {
//            return;
//        }
//        int bestFlow = -1;
//        Room bestNbor = null;
//        Room bestNbor2 = null;
//        int valves = valvesOpened;
//        for (Link link : current.links) {
//            for (Link link2 : current2.links) {
//                int code2 = stateCode(link.neighbour, link2.neighbour, valvesOpened, step+1);
//                Integer flow = stateMap.get(code2);
//                if(flow!=null && flow > bestFlow) {
//                    bestFlow = flow;
//                    bestNbor = link.neighbour;
//                    bestNbor2 = link2.neighbour;
//                }
//            }
//        }
//        for (Link link2 : current2.links) {
//            int code2 = stateCode(current, link2.neighbour, valvesOpened|current.valveFlag, step+1);
//            Integer flow = stateMap.get(code2);
//            if(flow!=null && flow > bestFlow) {
//                bestFlow = flow;
//                bestNbor = current;
//                bestNbor2 = link2.neighbour;
//                valves = valvesOpened|current.valveFlag;
//            }
//        }
//        for (Link link : current.links) {
//            int code2 = stateCode(link.neighbour, current2, valvesOpened|current2.valveFlag, step+1);
//            Integer flow = stateMap.get(code2);
//            if(flow!=null && flow > bestFlow) {
//                bestFlow = flow;
//                bestNbor = link.neighbour;
//                bestNbor2 = current2;
//                valves = valvesOpened|current2.valveFlag;
//            }
//        }
//        showBestRoutes(bestNbor, bestNbor2, step + 1, valves);
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


    private void printNodeList( Node node ){

        while(node != null) {
            System.out.print(" t="+node.step+":");
            if(node.openingValve)
                System.out.print("opening ");
            System.out.print(node.current.name);
            System.out.print(" x ");
            if(node.openingValve2)
                System.out.print("opening ");
            System.out.print(node.current2.name);
            System.out.print(" flowRate="+node.flowRate);
            System.out.print(" flow="+node.flow);
            System.out.println();
            node = node.next;
        }
        System.out.println();
    }
}


// t=0:AA x AA flowRate=0 flow=1707
//         t=1:opening DD x II flowRate=0 flow=1707
//         t=2:DD x opening JJ flowRate=20 flow=1227
//         t=3:EE x JJ flowRate=41 flow=744
//         t=4:FF x II flowRate=41 flow=744
//         t=5:GG x AA flowRate=41 flow=744
//         t=6:opening HH x opening BB flowRate=41 flow=744
//         t=7:HH x BB flowRate=76 flow=79
//         t=8:GG x opening CC flowRate=76 flow=79
//         t=9:FF x CC flowRate=78 flow=45
//         t=10:opening EE x DD flowRate=78 flow=45
//         t=11:EE x CC flowRate=81 flow=0
