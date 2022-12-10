package com.monstrous;

import java.util.HashSet;

public class Day9 {

    final static int MUL = 100000;

    class GridPoint {
        int x,y;

        public GridPoint() {
            x = 0;
            y = 0;
        }
    }

    final FileInput input;
    HashSet<Integer> set;
    GridPoint head;
    GridPoint tail;
    GridPoint[] snake;

    public Day9() {
        System.out.print("Day 9\n");
        input = new FileInput("data/day9.txt");

        for(int part = 1 ; part <=2; part++) {
            int SNAKE_LEN = 2;
            if(part == 2)
                SNAKE_LEN = 10;

            snake = new GridPoint[SNAKE_LEN];
            for (int i = 0; i < SNAKE_LEN; i++)
                snake[i] = new GridPoint();

            head = snake[0];        // reference
            tail = snake[SNAKE_LEN - 1]; // reference
            set = new HashSet<>();


            for (String line : input.lines) {

                GridPoint dH = new GridPoint();


                //System.out.println(line);
                String[] words = line.split(" ");
                switch (words[0].charAt(0)) {
                    case 'L':
                        dH.x = -1;
                        break;
                    case 'R':
                        dH.x = 1;
                        break;
                    case 'U':
                        dH.y = -1;
                        break;
                    case 'D':
                        dH.y = 1;
                        break;
                    default:
                        throw new IllegalStateException();
                }
                int distance = Integer.parseInt(words[1]);

                for (int step = 0; step < distance; step++) {
                    head.x += dH.x;
                    head.y += dH.y;
                    //System.out.print("head at " + head.x + " , " + head.y);

                    for (int k = 1; k < SNAKE_LEN; k++) {
                        if (Math.abs(snake[k - 1].x - snake[k].x) > 1 || Math.abs(snake[k - 1].y - snake[k].y) > 1) {
                            if (snake[k - 1].x > snake[k].x)
                                snake[k].x++;
                            else if (snake[k - 1].x < snake[k].x)
                                snake[k].x--;
                            if (snake[k - 1].y > snake[k].y)
                                snake[k].y++;
                            else if (snake[k - 1].y < snake[k].y)
                                snake[k].y--;
                        }
                    }
                    //System.out.println(" tail at " + tail.x + " , " + tail.y);
                    set.add(tail.x * MUL + tail.y);
                }

            }
            System.out.println("Part "+part+" : Unique grid points visited by tail : " + set.size());
        }
    }

}
