package com.monstrous;

public class Day1 {

    FileInput input;

    public Day1() {
        System.out.print("Day 1\n");
        input = new FileInput("data/day1.txt");

        int top[] = {-1, -1, -1};
        int elfCalories = 0;
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);

            if (line.length() == 0) {
                if (elfCalories > top[0]) {
                    top[2] = top[1];
                    top[1] = top[0];
                    top[0] = elfCalories;
                } else if (elfCalories > top[1]) {
                    top[2] = top[1];
                    top[1] = elfCalories;
                } else if (elfCalories > top[2]) {
                    top[2] = elfCalories;
                }
                elfCalories = 0;
            } else {
                int calories = Integer.parseInt(line);
                elfCalories += calories;
            }
        }
        System.out.print("Max calories of elf: ");
        System.out.println(top[0]);
        System.out.print("Calories of top 3 elves: ");
        System.out.println(top[0] + top[1] + top[2]);
    }
}
