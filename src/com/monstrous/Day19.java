package com.monstrous;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Day19 {

    final static int STEPS1 = 24;
    final static int STEPS2 = 32;

    class Mineral {
        String name;
        int id;
    }
    class Ingredient {
        int amount;
        Mineral mineral;

        public Ingredient(int amount, Mineral mineral) {
            this.amount = amount;
            this.mineral = mineral;
        }
    }
    class RobotRecipe {
        Mineral robotType;
        ArrayList<Ingredient> ingredients;

        public RobotRecipe(Mineral robotType) {
            this.robotType = robotType;
            ingredients = new ArrayList<>();
        }
    }
    class BluePrint {
        int number;
        ArrayList<RobotRecipe> recipes;

        public BluePrint(int number) {
            this.number = number;
            recipes = new ArrayList<>();
        }
    }
    class StateVector {
        char t;
        char numRobots[];
        short unitsCollected[];

        public StateVector() {
            numRobots = new char[4];
            unitsCollected = new short[4];
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj)
                return true;
            if (!(obj instanceof StateVector))
                return false;
            StateVector oth = (StateVector)obj;
            if(t != oth.t)
                return false;
            for(int i = 0; i < 4; i++){
                if(oth.numRobots[i] != numRobots[i] || oth.unitsCollected[i] != unitsCollected[i])
                    return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(t, unitsCollected[0], unitsCollected[1], unitsCollected[2], unitsCollected[3], numRobots[0], numRobots[1], numRobots[2], numRobots[3]);
        }


    }

    final FileInput input;
    ArrayList<Mineral> minerals;
    ArrayList<BluePrint> bluePrints;
    int unitsCollected[] = new int [4];
    int numRobots[] = new int[4];
    int robotCosts[][] = new int[4][3];     // [robot type][ingredients]
    int maxRobots[];
    int bestPerStep[];
    int steps;
    HashMap<StateVector, Integer> cacheMap = new HashMap<>();


    public Day19() {
        System.out.print("Day 19\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day19a.txt");

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


        for(Mineral m : minerals )
            System.out.println(" mineral "+m.id +" "+m.name);


        int sum = 0;
        //for(BluePrint bp : bluePrints ) {
        BluePrint bp = bluePrints.get(1); {

            //printBluePrint(bp);

            for(int i = 0; i < 4; i++) {
                unitsCollected[i] = 0;
                numRobots[i] = 0;
            }
            numRobots[0] = 1;
            fillRobotCosts(bp);
            cacheMap.clear();
            maxRobots = new int[4]; // max robots to build per type
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 3; j++) {    // ingredients
                    if(robotCosts[i][j] > maxRobots[j])
                        maxRobots[j] = robotCosts[i][j];
                }
            }
            steps = STEPS1;
            maxRobots[3] = steps;  // i.e. no limit
            best = -1;

            int geodes = simulate(steps, unitsCollected, numRobots);
            int quality = geodes*bp.number;
            sum += quality;


            System.out.println("Nr of geodes: "+geodes);
            System.out.println("Quality level: "+quality);
        }

        System.out.println("Sum of Quality levels: "+sum);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }

    private int best = -1;
    private char path[] = new char [STEPS2];
    //private StateVector key = new StateVector();

    // returns number of cracked geodes
    private int simulate(int t, int unitsCollected[], int numRobots[] ) {


        StateVector key = new StateVector();
        key.t = (char)t;
        for(int i = 0 ; i < 4; i++){
            key.unitsCollected[i] = (short)unitsCollected[i];
            key.numRobots[i] = (char)numRobots[i];
        }


        //int key = makeKey(t, unitsCollected, numRobots);
        if(cacheMap.containsKey(key))
            return cacheMap.get(key);


        int bestScore = -1;

        //printStatus(t, unitsCollected, numRobots);
        if(t == 0) {
            if(unitsCollected[3] > best) {
                printStatus(t, unitsCollected, numRobots);
                for(int i = 0; i < steps; i++)
                    System.out.print(path[i]);
                System.out.println();
                best = unitsCollected[3];
            }
            return unitsCollected[3];   // geodes cracked
        }

//        if(unitsCollected[3] < bestPerStep[t] - 18)
//            return 0;

        int unitsAtStart[] = new int[3];        // remember what we had at start of the time slot
        for(int i = 0; i < 3; i++)
            unitsAtStart[i] = unitsCollected[i];

        // gathering
        for(int i = 0; i < 4; i++) {
            unitsCollected[i] += numRobots[i];
        }

        boolean madeGeodeBot = false;
        for(int i = 3; i >= 0; i--) {       // try to make each robot type
            boolean canMake = true;
            for(int j = 0; j < 3; j++ ) {
                if (unitsAtStart[j] < robotCosts[i][j])
                    canMake = false;
            }
            if(canMake && numRobots[i] < maxRobots[i]) {
                if(i == 3)
                    madeGeodeBot = true;
                numRobots[i]++;

                for(int j = 0; j < 3; j++) {
                    unitsCollected[j] -= robotCosts[i][j];
                }

                path[steps - t] = (char) ('1' + i);
                int score = simulate(t - 1, unitsCollected, numRobots);
                if (score > bestScore)
                    bestScore = score;

                // undo
                numRobots[i]--;
                for(int j = 0; j < 3; j++)
                    unitsCollected[j] += robotCosts[i][j];
                //break;
            }


        }

        if(!madeGeodeBot) {
            //printStatus(t, unitsCollected, numRobots);
            path[steps - t] = '0';
            int score = simulate(t - 1, unitsCollected, numRobots);
            if (score > bestScore)
                bestScore = score;
        }
        // undo gathering
        for(int i = 0; i < 4; i++) {
            unitsCollected[i] -= numRobots[i];
        }

        cacheMap.put(key, bestScore);

        return bestScore;
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
            for(int j = 0; j < 3; j++)
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
