package com.monstrous;

public class Day8 {

    final FileInput input;
    int[][] trees;
    int rows;
    int cols;

    public Day8() {
        System.out.print("Day 8\n");
        input = new FileInput("data/day8.txt");

        int i, j;
        int max;
        rows = input.lines.size();
        cols = input.lines.get(0).length();
        trees = new int[rows][cols];
        boolean[][] viz = new boolean[rows][cols];


        int row = 0;
        for (String line : input.lines) {

            //System.out.println(line);
            for (int k = 0; k < line.length(); k++)
                trees[row][k] = line.charAt(k) - '0';
            row++;
        }

//        for (i = 0; i < rows; i++) {
//            for (j = 0; j < cols; j++) {
//                System.out.print(trees[i][j] + " ");
//            }
//            System.out.println();
//        }

        for (i = 0; i < rows; i++)
            for (j = 0; j < cols; j++)
                viz[i][j] = false;

        for (i = 0; i < rows; i++) {
            max = -1;
            for (j = 0; j < cols; j++) {
                if (trees[i][j] > max) {
                    viz[i][j] = true;
                    max = trees[i][j];
                }
            }
            max = -1;
            for (j = cols - 1; j >= 0; j--) {
                if (trees[i][j] > max) {
                    viz[i][j] = true;
                    max = trees[i][j];
                }
            }
        }
        for (j = 0; j < cols; j++) {
            max = -1;
            for (i = 0; i < rows; i++) {
                if (trees[i][j] > max) {
                    viz[i][j] = true;
                    max = trees[i][j];
                }
            }
            max = -1;
            for (i = rows - 1; i >= 0; i--) {
                if (trees[i][j] > max) {
                    viz[i][j] = true;
                    max = trees[i][j];
                }
            }
        }

        int count = 0;

        for (i = 0; i < rows; i++)
            for (j = 0; j < cols; j++)
                if (viz[i][j])
                    count++;

        System.out.println("visible trees: " + count);

        int bestScore = -1;
        for (i = 0; i < rows; i++) {
            for (j = 0; j < cols; j++) {
                int score = scorePosition(i, j);
                if (score > bestScore)
                    bestScore = score;

            }
        }
        System.out.println("highest scenic score: " + bestScore);

    }

    private int scorePosition(int r, int c) {
        int score = 1;
        int len = 0;
        for (int i = r-1; i >= 0; i--) {
            len++;
            if(trees[i][c] >= trees[r][c])
                break;
        }
        score *= len;
        len = 0;
        for (int i = r+1; i < rows; i++) {
            len++;
            if(trees[i][c] >= trees[r][c])
                break;
        }
        score *= len;

        len = 0;
        for (int i = c-1; i >= 0; i--) {
            len++;
            if(trees[r][i] >= trees[r][c])
                break;
        }
        score *= len;
        len = 0;
        for (int i = c+1; i < cols; i++) {
            len++;
            if(trees[r][i] >= trees[r][c])
                break;
        }
        score *= len;
        return score;

    }

}
