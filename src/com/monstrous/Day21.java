package com.monstrous;

import java.util.HashMap;

public class Day21 {


    class Monkey {
        String name;
        boolean isLeaf;
        boolean isHumanDependent;

        public Monkey(String name) {
            this.name = name;
            isHumanDependent = false;
        }
    }

    class NodeMonkey extends Monkey {
        char operator;
        Monkey left;
        Monkey right;
        String leftName;
        String rightName;

        public NodeMonkey(String name, char op, String leftName, String rightName) {
            super(name);
            isLeaf = false;
            operator = op;
            this.leftName = leftName;
            this.rightName = rightName;
        }
    }

    class LeafMonkey extends Monkey {
        long value;

        public LeafMonkey(String name, long value) {
            super(name);
            isLeaf = true;
            this.value = value;
        }
    }

    final HashMap<String, Monkey> monkeyMap = new HashMap<>();		// symbol table
    final FileInput input;

    public Day21() {
        System.out.print("Day 21\n");
        final long startTime = System.currentTimeMillis();

        input = new FileInput("data/day21.txt");

        for(String line: input.lines) {

            String[] words = line.split("[: ]+");
            String name = words[0];
            Monkey monkey;
            if(words.length == 2) {
                monkey = new LeafMonkey(name, Integer.parseInt( words[1] ));
            } else {
                monkey = new NodeMonkey(name,words[2].charAt(0), words[1], words[3] );
            }
            monkeyMap.put(name, monkey);
        }

        // note we don't have to look for the root monkey, but for the monkey named "root"
        NodeMonkey root = (NodeMonkey)monkeyMap.get("root");
        fixUp( root );

        long value =  eval( root );
        System.out.println("Part 1: root monkey shouts: "+value);

        LeafMonkey human = (LeafMonkey)monkeyMap.get("humn");
        human.isHumanDependent = true;
        markDependents(root);

        root.operator = '=';
        setHumanValue(root, 0);

        System.out.println("Part 2: HUMN has to shout: " + human.value);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }



    // replace symbolic references by monkey references using the symbol table
    private void fixUp(Monkey monkey) {
        if(monkey.isLeaf)
            return;
        NodeMonkey node = (NodeMonkey)monkey;
        node.left = monkeyMap.get(node.leftName);
        node.right = monkeyMap.get(node.rightName);
        fixUp(node.left);
        fixUp(node.right);
    }

    // recursive descent evaluator
    private long eval(Monkey monkey) {
        if(monkey.isLeaf)
            return ((LeafMonkey)monkey).value;
        NodeMonkey node = (NodeMonkey)monkey;
        long left = eval(node.left);
        long right = eval(node.right);
        long result = 0;
        switch(node.operator) {
            case '+': result = left + right; break;
            case '-': result = left - right; break;
            case '*': result = left * right; break;
            case '/': result = left / right; break;
            case '=': result = (left == right? 1 : 0); break;
            default: System.out.println("ERROR unknown operator: "+node.operator);
        }
        return result;
    }

    // flow up dependence on HUMN node
    private void markDependents(Monkey monkey) {
        if(monkey.isLeaf)
            return;
        NodeMonkey node = (NodeMonkey)monkey;
        markDependents(node.left);
        markDependents(node.right);
        node.isHumanDependent = (node.left.isHumanDependent || node.right.isHumanDependent );
    }

    //
    private void setHumanValue(Monkey monkey, long value) {
        if(monkey.isLeaf) {
            ((LeafMonkey) monkey).value = value;
            //System.out.println("HUMN := "+value);
            return;
        }
        NodeMonkey node = (NodeMonkey)monkey;
        if(node.left.isHumanDependent && !node.right.isHumanDependent) {
            long right = eval(node.right);
            long left = 0;
            switch(node.operator) {
                case '+': left = value - right; break;
                case '-': left = value + right; break;
                case '*': left = value / right; break;
                case '/': left = value * right; break;
                case '=': left = right; break;
            }
            setHumanValue(node.left, left);
        } else if(!node.left.isHumanDependent && node.right.isHumanDependent) {
            long left = eval(node.left);
            long right = 0;
            switch(node.operator) {
                case '+': right = value - left; break;
                case '-': right = left - value; break;
                case '*': right = value / left; break;
                case '/': right = left / value; break;
                case '=': right = left; break;
            }
            setHumanValue(node.right, right);
        }
        else
            System.out.println("Both branches depend on HUMN");
    }

}
