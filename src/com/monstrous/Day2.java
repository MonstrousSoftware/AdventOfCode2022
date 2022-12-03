package com.monstrous;

public class Day2 {

    FileInput input;

    public Day2() {
        System.out.print("Day 2\n");
        input = new FileInput("day2.txt");
        int total = 0;
        int total2 = 0;

        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);

            char them = line.charAt(0);
            char you = line.charAt(2);
            int points = score(them, you);

            total += points;
            //System.out.println("them : "+ them + " you: " + you + " score: "+points);

            you = choosePlay(them, you);
            points = score(them, you);
            total2 += points;

            //System.out.println("them : "+ them + " you: " + you + " score: "+points);

        }
        System.out.println("total score (part 1): "+ total );
        System.out.println("total score (part 2): "+ total2 );

    }

    private int score(char them, char you ) {
        int base = 0;
        switch(you) {
            case 'X': base = 1; break;
            case 'Y': base = 2; break;
            case 'Z': base = 3; break;
            default: throw new IllegalStateException("You: "+you);
        }

        int win = 0; // lose
        if((them == 'A' && you == 'X') || (them == 'B' && you == 'Y') || (them == 'C' && you == 'Z'))
            win = 3;    // draw
        else if ((them == 'A' && you == 'Y') || (them == 'B' && you == 'Z') || (them == 'C' && you == 'X'))
            win = 6; // win

        return base + win;
    }

    private char choosePlay( char them, char goal ) {
        char play = ' ';
        if(goal == 'X') {// lose
            switch(them) {
                case 'A': play = 'Z'; break;
                case 'B': play = 'X'; break;
                case 'C': play = 'Y'; break;
                default: throw new IllegalStateException("them: "+them);
            }
        }
        else if(goal == 'Y') {// draw
            switch(them) {
                case 'A': play = 'X'; break;
                case 'B': play = 'Y'; break;
                case 'C': play = 'Z'; break;
                default: throw new IllegalStateException("them: "+them);
            }
        }
        else if(goal == 'Z') {// win
            switch(them) {
                case 'A': play = 'Y'; break;
                case 'B': play = 'Z'; break;
                case 'C': play = 'X'; break;
                default: throw new IllegalStateException("them: "+them);
            }
        }
        else
            throw new IllegalStateException("goal: "+goal);
        return play;
    }


}
