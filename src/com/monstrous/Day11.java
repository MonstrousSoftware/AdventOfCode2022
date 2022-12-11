package com.monstrous;

import java.util.ArrayList;

public class Day11 {

   class Monkey {
       int id;
       ArrayList<Long> items;
       char operator;
       int operand;
       boolean onSelf;
       int divisor;
       int onTrue;
       int onFalse;
       int inspections;

       public Monkey(int id) {
           this.id = id;
           items = new ArrayList<>();
           onSelf = false;
           inspections = 0;
       }
   }

   final FileInput input;
   final ArrayList<Monkey> monkeys;
   long modulo;
   Monkey monkey;

    public Day11() {
        System.out.print("Day 11\n");
        input = new FileInput("data/day11.txt");
        monkeys = new ArrayList<>();

        for(int step = 1; step <= 2; step++) {
            monkeys.clear();

            monkey = null;
            modulo = 1;

            for (String line : input.lines) {

                //System.out.println(line);
                String[] words = line.split("[ :,]");

                //System.out.println("words[0] :" + words[0]);

                if (words.length <= 1)
                    continue;
                if (words[0].contentEquals("Monkey")) {
                    int id = Integer.parseInt(words[1]);
                    monkey = new Monkey(id);
                    monkeys.add(monkey);
                } else if (words[2].contentEquals("Starting")) {
                    for (int index = 5; index < words.length; index += 2) {
                        long nr = Integer.parseInt(words[index]);
                        monkey.items.add(nr);
                    }
                } else if (words[2].contentEquals("Operation")) {
                    monkey.operator = words[7].charAt(0);
                    if (words[8].contentEquals(("old")))
                        monkey.onSelf = true;
                    else
                        monkey.operand = Integer.parseInt(words[8]);
                } else if (words[2].contentEquals("Test")) {
                    monkey.divisor = Integer.parseInt(words[6]);
                    modulo *= monkey.divisor;
                } else if (words[5].contentEquals("true")) {
                    monkey.onTrue = Integer.parseInt(words[10]);
                } else if (words[5].contentEquals("false")) {
                    monkey.onFalse = Integer.parseInt(words[10]);
                } else
                    System.out.println("Unrecognized line : " + line);
            }
            //printMonkeys();
            //System.out.println("Modulo : " + modulo);

            int numRounds = 20;
            if(step == 2)
                numRounds = 10000;
            for (int round = 1; round <= numRounds; round++) {


                // round
                for (int i = 0; i < monkeys.size(); i++) {
                    monkey = monkeys.get(i);

                    // per item
                    for (int j = 0; j < monkey.items.size(); j++) {
                        long worry = monkey.items.get(j);
                        monkey.inspections++;
                        long operand = monkey.operand;
                        if (monkey.onSelf)
                            operand = worry;

                        if (monkey.operator == '*')
                            worry *= operand;
                        else if (monkey.operator == '+')
                            worry += operand;
                        else
                            System.out.println("Unknown operator: " + monkey.operator);

                        if (worry < 0) {
                            throw new IndexOutOfBoundsException();
                        }
                        if (step == 1)
                            worry /= 3;
                        else
                            worry %= modulo;

                        int dest;
                        if (worry % monkey.divisor == 0)
                            dest = monkey.onTrue;
                        else
                            dest = monkey.onFalse;
                        monkeys.get(dest).items.add(worry);
                    }
                    monkey.items.clear();
                }
//                if (round == 1 || round == 20 || round % 1000 == 0) {
//                    System.out.println("Round: " + round);
//
//                    printInspections();
//                    //printMonkeys();
//                }

            }
            //printInspections();
            long biz = countMonkeyBusiness();
            System.out.println("Step "+step+" amount of monkey business: " + biz);
            // 58786
            // 14952185856
        }

    }

    private void printMonkeys() {
        for(int i = 0; i < monkeys.size(); i++ ) {
            monkey = monkeys.get(i);
            System.out.print("Monkey "+monkey.id+" : ");

//            if(monkey.onSelf)
//                System.out.print(" operation: "+monkey.operator+" old ");
//            else
//                System.out.print(" operation: "+monkey.operator+" "+monkey.operand+" ");
//            System.out.print(" divisible by: "+monkey.divisor);
//            System.out.print(" on true: "+monkey.onTrue+ "  on false: "+monkey.onFalse);

            System.out.print("\t");
            for(int j = 0; j < monkey.items.size(); j++)
                System.out.print(monkey.items.get(j)+" ");
            System.out.println();
        }
        System.out.println();
    }

    private void printInspections() {
        for (int i = 0; i < monkeys.size(); i++) {
            monkey = monkeys.get(i);
            System.out.println("Monkey " + monkey.id + " inspections : " + monkey.inspections);
        }
    }

    private long countMonkeyBusiness() {
        long max = -1;
        int maxMonkey = -1;
        for (int i = 0; i < monkeys.size(); i++) {
            monkey = monkeys.get(i);
            if(monkey.inspections > max) {
                max = monkey.inspections;
                maxMonkey = i;
            }
        }
        long max2 = -1;
        for (int i = 0; i < monkeys.size(); i++) {
            if(i == maxMonkey)
                continue;
            monkey = monkeys.get(i);
            max2 = Math.max(max2, monkey.inspections);
        }
        return max * max2;
    }
}
