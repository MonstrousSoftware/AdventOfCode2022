package com.monstrous;

public class Day3 {

    FileInput input;

    public Day3() {
        System.out.print("Day 3\n");
        input = new FileInput("data/day3.txt");
        int total = 0;

        for (String line : input.lines) {
            int count[] = new int[53];
            int len = line.length();
            for (int i = 0; i < len / 2; i++) {
                count[priority( line.charAt(i))]++;
            }
            for (int i = len/2; i < len; i++) {
                int prio = priority( line.charAt(i));
                if( count[prio] > 0 ) {
                    //System.out.println("dupe char: " + line.charAt(i) + " " + prio);
                    total += prio;
                    break;
                }
            }
        }
        System.out.println("sum of priorities: "+total);

        int total2 = 0;
        for(int group = 0; group < input.lines.size()/3; group++) {
            int count[] = new int[53];
            int flag = 1;
            for (int elf = 0; elf < 3; elf++) {
                String line = input.get(3 * group + elf);
                for (int i = 0; i < line.length(); i++) {
                    count[priority(line.charAt(i))] |= flag;
                }
                flag *= 2;
            }
            for (int i = 1; i < 53; i++) {
                if (count[i] == 7) { // 1+2+4
                    //System.out.println("group badge prio: " + i);
                    total2 += i;
                    break;
                }
            }

        }
        System.out.println("sum of priority of badges: "+total2);
    }

    private int priority( char ch ) {
        if(ch >= 'a' && ch <= 'z')
            return 1 + ch - 'a';
        if(ch >= 'A' && ch <= 'Z')
            return 27 + ch - 'A';
        throw new IllegalStateException("priority: "+ch);
    }



}
