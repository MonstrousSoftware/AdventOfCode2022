package com.monstrous;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Day19 {
    final String FILE_NAME =  "data/day19.txt";

    final static int STEPS1 = 24;
    final static int STEPS2 = 32;

    static class Mineral {
        String name;
        int id;
    }
    static class Ingredient {
        int amount;
        Mineral mineral;

        public Ingredient(int amount, Mineral mineral) {
            this.amount = amount;
            this.mineral = mineral;
        }
    }
    static class RobotRecipe {
        Mineral robotType;
        ArrayList<Ingredient> ingredients;

        public RobotRecipe(Mineral robotType) {
            this.robotType = robotType;
            ingredients = new ArrayList<>();
        }
    }
    static class BluePrint {
        int number;
        ArrayList<RobotRecipe> recipes;

        public BluePrint(int number) {
            this.number = number;
            recipes = new ArrayList<>();
        }
    }
    static class StateVector {
        char stepsLeft;
        char numRobots[];
        short unitsCollected[];
        char couldHaveBuilt;          // botFlags to indicate the bots that could have been built instead of waiting

        public StateVector() {
            numRobots = new char[4];
            unitsCollected = new short[4];
            couldHaveBuilt = 0;
        }
        public StateVector( StateVector oth ) {
            this.stepsLeft = oth.stepsLeft;
            for(int i = 0; i < 4; i++){
                this.numRobots[i] = oth.numRobots[i];
                this.unitsCollected[i] = oth.unitsCollected[i];
            }
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj)
                return true;
//            if (!(obj instanceof StateVector))
//                return false;
            StateVector oth = (StateVector)obj;
            if(stepsLeft != oth.stepsLeft)
                return false;
            for(int i = 0; i < 4; i++){
                if(oth.numRobots[i] != numRobots[i] || oth.unitsCollected[i] != unitsCollected[i])
                    return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(stepsLeft, unitsCollected[0], unitsCollected[1], unitsCollected[2], unitsCollected[3], numRobots[0], numRobots[1], numRobots[2], numRobots[3]);
        }
    }

    final FileInput input;
    ArrayList<Mineral> minerals;
    ArrayList<BluePrint> bluePrints;
    int robotCosts[][] = new int[4][4];     // [robot type][ingredients]
    int maxRobots[];
    int steps;
    HashMap<StateVector, Integer> cacheMap = new HashMap<>();
    private int maxGeodesCracked = -1;
    private char path[] = new char [STEPS2];
    int cacheHits = 0;
    int statesVisited = 0;
    char botFlags[] = { 1, 2, 4, 8 };   // bit flag for bot type 0 .. 3

    public Day19() {
        System.out.print("Day 19\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput(FILE_NAME);

        minerals = new ArrayList<>();
        bluePrints = new ArrayList<>();

        for(String line : input.lines) {

            String[] words = line.split("[ .:]+");

            int index = 0;
            int nr = Integer.parseInt(words[1]);
            BluePrint bp = new BluePrint(nr);


            do {
                index+=3;    // Each
                Mineral min = findMineral(words[index]);      // ore robot costs
                RobotRecipe recipe = new RobotRecipe(min);

                do {    // 4 clay and
                    index += 3;
                    int amount = Integer.parseInt(words[index]);
                    Mineral ingr = findMineral(words[index + 1]);
                    Ingredient ingredient = new Ingredient(amount, ingr);
                    recipe.ingredients.add(ingredient);
                } while (index + 2 < words.length && words[index + 2].contentEquals("and"));
                bp.recipes.add(recipe);
            } while (index + 2 < words.length && words[index + 2].contentEquals("Each"));
            bluePrints.add(bp);
        }

//        for(Mineral m : minerals )
//            System.out.println(" mineral "+m.id +" "+m.name);


        int sum = 0;
        steps = STEPS1;

        for(BluePrint bp : bluePrints ) {
           // BluePrint bp = bluePrints.get(0); if(false) {

            //printBluePrint(bp);

            fillRobotCosts(bp);
            cacheMap.clear();
            maxRobots = new int[4]; // max robots to build per type
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 3; j++) {    // ingredients
                    if(robotCosts[i][j] > maxRobots[j])
                        maxRobots[j] = robotCosts[i][j];
                }
            }

            maxRobots[3] = steps;  // i.e. no limit
            maxGeodesCracked = -1;
            cacheMap.clear();
            StateVector start = new StateVector();
            start.stepsLeft = STEPS1;
            for(int j = 0; j < 4; j++){
                start.unitsCollected[j] = 0;
                start.numRobots[j] = 0;
            }
            start.numRobots[0] = 1;
            maxGeodesCracked = -1;
            cacheHits = 0;
            statesVisited = 0;
            maxGeodesCracked = 0;

            int geodes = simulate(start);
            int quality = geodes*bp.number;
            sum += quality;
            //System.out.println( " cache:" + cacheMap.size() + " hits:" + cacheHits+ "states visited: "+statesVisited);


            //System.out.println("Nr of geodes: "+geodes);
            //System.out.println("Quality level: "+quality);

        }

        System.out.println("Part 1: Sum of Quality levels: "+sum);

        int product = 1;
        steps = STEPS2;
        for(int bpi = 0; bpi < 3  && bpi < bluePrints.size(); bpi++) {      // phase 2: consider only the first 3 blueprints
            BluePrint bp = bluePrints.get(bpi);

            //printBluePrint(bp);

            fillRobotCosts(bp);

            maxRobots = new int[4]; // max robots to build per type
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 3; j++) {    // ingredients
                    if(robotCosts[i][j] > maxRobots[j])
                        maxRobots[j] = robotCosts[i][j];
                }
            }

            maxRobots[3] = steps;  // i.e. no limit

            StateVector start = new StateVector();
            start.stepsLeft = (char)steps;
            for(int j = 0; j < 4; j++){
                start.unitsCollected[j] = 0;
                start.numRobots[j] = 0;
            }
            start.numRobots[0] = 1;
            maxGeodesCracked = -1;
            cacheMap.clear();
            cacheHits = 0;
            statesVisited = 0;
            maxGeodesCracked = 0;

            int geodes = simulate(start);
            product *= geodes;

            //System.out.println( " cache:" + cacheMap.size() + " hits:" + cacheHits+ "states visited: "+statesVisited);


            //System.out.println("Nr of geodes: "+geodes);
            //System.out.println("Quality level: "+quality);

        }
        System.out.println("Part 2: Product of geodes: "+product);

        // part 1: 1294
        // part 2: 13640
        // duration 1.5 s

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }


    // recursive function
    // returns max number of cracked geodes based on minerals and robots and time left
    private int simulate( StateVector state ) {

        //System.out.println((int)state.stepsLeft + " " + state.unitsCollected[3] + " cache:" + cacheMap.size() + " hits:" + cacheHits);
        // end condition
        if (state.stepsLeft == 0) {        // time's up
            statesVisited++;
            if (state.unitsCollected[3] > maxGeodesCracked) {          // nr of geodes cracked, new record?
//                printState(state);
//                for (int i = 0; i < steps; i++)
//                    System.out.print(path[i]);
//                System.out.println();
                maxGeodesCracked = state.unitsCollected[3];
            }
            return state.unitsCollected[3];   // geodes cracked
        }

        if(upperBoundGeodes(state) <= maxGeodesCracked)   // prune if we cannot possibly beat the number of geode bots in time left
            return 0;

        // check cache to avoid revisiting branches already visited
        Integer cachedNumGeodes = cacheMap.get(state);
        if (cachedNumGeodes != null) {
            cacheHits++;
            return cachedNumGeodes;
        }


        int bestScore = -1;

        // try building a robot in this step
        char canBuildBots = 0;
        for(int botType = 3; botType >= 0; botType--) {       // try to make each robot type, try most expensive ones first
            if (state.numRobots[botType] > maxRobots[botType])
                continue;

            if (botType == 3 && state.stepsLeft <= 1)
                continue;
            if (botType == 2 && state.stepsLeft <= 2)
                continue;
            if (botType == 1 && state.stepsLeft <= 3)
                continue;

            // do we have resources to build this type of robot?
            int[] costs = robotCosts[botType];
            if (costs[0] > state.unitsCollected[0])
                continue;
            if (costs[1] > state.unitsCollected[1])
                continue;
            if (costs[2] > state.unitsCollected[2])
                continue;

            canBuildBots |= botFlags[botType];  // keep track of the robot types we can build

            if((state.couldHaveBuilt & botFlags[botType]) != 0) // don't build a bot we could have built a minute ago
                continue;

            StateVector branch = new StateVector();
            branch.stepsLeft = (char)(state.stepsLeft-1);
            for(int j = 0; j < 4; j++ ){
                // subtract expenditure on building a bot, add profit from gathering
                branch.unitsCollected[j] = (short)(state.unitsCollected[j] - costs[j] + state.numRobots[j]);
                branch.numRobots[j] = state.numRobots[j];
            }
            branch.numRobots[botType]++;

            int score = simulate(branch);
            if (score > bestScore)
                bestScore = score;

            if(botType == 3)      {   // if we're able to make a geode bot, don't even consider making another bot
                break;
            }
        }

        // if we can already build each type of robot, there is no point in saving up
        // if we can make a geode bot, don't save up.
        if( (canBuildBots & botFlags[3]) == 0) {
            // or try waiting a step (just gather resources)

            StateVector branch = new StateVector();
            branch.stepsLeft = (char)(state.stepsLeft-1);
            for(int j = 0; j < 4; j++ ){
                branch.unitsCollected[j] = (short)(state.unitsCollected[j]  + state.numRobots[j]);
                branch.numRobots[j] = state.numRobots[j];
            }
            branch.couldHaveBuilt = canBuildBots;   // which bots could we have built instead of waiting?
            int score = simulate( branch );
            if (score > bestScore)
                bestScore = score;
        }

        cacheMap.put(state, bestScore);

        return bestScore;
    }


    private int upperBoundGeodes( StateVector state ){
        int sum = state.unitsCollected[3];
        int bots = state.numRobots[3];
        for(int i = state.stepsLeft; i > 0; i--){
            sum += bots;
            bots++;
        }
        return sum;
    }



    private void printState(StateVector state) {
        System.out.print("Minutes left "+(int)state.stepsLeft +" : ");
        for(int i = 0; i < 4; i++)
            System.out.print("    "+minerals.get(i).name+":"+(int)state.unitsCollected[i]);
        for(int i = 0; i < 4; i++)
            System.out.print("    "+minerals.get(i).name+" robots: "+(int)state.numRobots[i]);
        System.out.println();
    }

    private void printStatus(int t, int unitsCollected[], int numRobots[]) {
//        if(t < 20)
//            return;
        System.out.print("Minutes left "+t +" : ");
        for(int i = 0; i < 4; i++)
            System.out.print("    "+minerals.get(i).name+":"+unitsCollected[i]);
        for(int i = 0; i < 4; i++)
            System.out.print("    "+minerals.get(i).name+" robots: "+numRobots[i]);
        System.out.println();
    }

    private void printStats(int stepsLeft, int units0, int units1, int units2, int units3, int bots0, int bots1, int bots2, int bots3 ) {
        System.out.print("Steps left "+stepsLeft +" : ");

        System.out.print("  "+minerals.get(0).name+":"+units0);
        System.out.print("  "+minerals.get(1).name+":"+units1);
        System.out.print("  "+minerals.get(2).name+":"+units2);
        System.out.print("  "+minerals.get(3).name+":"+units3);
        System.out.print("  "+minerals.get(0).name+" robots: "+bots0);
        System.out.print("  "+minerals.get(1).name+" robots: "+bots1);
        System.out.print("  "+minerals.get(2).name+" robots: "+bots2);
        System.out.print("  "+minerals.get(3).name+" robots: "+bots3);
        System.out.println();
    }




        private Mineral findMineral( String name ) {
        for(Mineral m : minerals )
            if(m.name.contentEquals(name))
                return m;
        Mineral m = new Mineral();
        m.name = name;
        m.id = minerals.size();
        minerals.add(m);
        return m;
    }

    private String mineralName( int id ) {
        return minerals.get(id).name;
    }

    private void printBluePrint( BluePrint bp ){
        System.out.println("BluePrint "+bp.number+":");
        for( RobotRecipe r : bp.recipes ) {
            System.out.println("  Each "+r.robotType.name+" robot costs");
            for( Ingredient in : r.ingredients )
                System.out.println("       "+in.amount+" "+in.mineral.name);
        }
        System.out.println();
    }

    private void fillRobotCosts( BluePrint bp ){
        for(int i = 0; i < 4; i++)
            for(int j = 0; j < 4; j++)
                robotCosts[i][j] = 0;

        for( RobotRecipe r : bp.recipes ) {
            int i = r.robotType.id;
            for( Ingredient in : r.ingredients ) {
                int j = in.mineral.id;
                robotCosts[i][j] = in.amount;
            }
        }
    }
}
