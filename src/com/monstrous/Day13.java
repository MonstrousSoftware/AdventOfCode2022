package com.monstrous;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Day13 {

    class Node {
        ListNode parent;
        boolean isNumber;
    }

    class NumberNode extends Node {
        int value;

        public NumberNode(int value) {
            this.value = value;
            this.isNumber = true;
        }
    }

    class ListNode extends Node {
        ArrayList<Node> list;

        public ListNode(ListNode parent) {
            this.parent = parent;
            list = new ArrayList<>();
            this.isNumber = false;
        }
        public ListNode(int number) {
            this.parent = null;
            list = new ArrayList<>();
            list.add(new NumberNode(number));
            this.isNumber = false;
        }
    }

    class ListComparator implements Comparator<ListNode> {

        @Override
        public int compare(ListNode o1, ListNode o2) {
            return compareList(o1, o2);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }
    }

    final FileInput input;

    public Day13() {
        System.out.print("Day 13\n");
        final long startTime = System.currentTimeMillis();
        input = new FileInput("data/day13.txt");

        int sum = 0;
        int pair = 1;
        ArrayList<ListNode> messageList = new ArrayList<>();

        for(int index = 0; index < input.lines.size(); index+=3, pair++) {
            String left = input.lines.get(index);
            String right = input.lines.get(index+1);

            //System.out.println("compare "+left+" to "+right);
            ListNode leftNode = parse(left);
            ListNode rightNode = parse(right);

            messageList.add(leftNode);
            messageList.add(rightNode);

            int cmp = compareList(leftNode, rightNode);
//            if(cmp < 0)
//                System.out.println("pair "+pair+" compare "+left+" to "+right+" : in the right order");
//            else
//                System.out.println("pair "+pair+" compare "+left+" to "+right+" : NOT in the right order");
            if(cmp < 0)
                sum += pair;

        }
        System.out.println("Sum of pairs in the right order: " + sum);

        ListNode separatorA = new ListNode(null);
        separatorA.list.add( new ListNode(2));
        ListNode separatorB = new ListNode(null);
        separatorB.list.add( new ListNode(6));
        messageList.add(separatorA);
        messageList.add(separatorB);

        messageList.sort( new ListComparator() );

        int product = 1;
        for(int index = 0; index < messageList.size(); index++) {
            ListNode n = messageList.get(index);
            if(n == separatorA || n == separatorB)
                product *= (index+1);
        }
        System.out.println("Product of separator messages: " + product);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time (ms): " + (endTime - startTime));
    }

    private ListNode parse( String string ) {
        ListNode list = new ListNode(null);

        for(int index = 0; index < string.length(); index++) {
            char k = string.charAt(index);

            if (k == '[') {
                ListNode newList = new ListNode(list);
                list.list.add( newList );
                list = newList;
            } else if (k == ']') {
                list = list.parent;
            } else if (Character.isDigit(k)) {
                int val = k - '0';
                while(index < string.length() && Character.isDigit(string.charAt(index+1)) ) {
                    index++;
                    val = 10*val + string.charAt(index) - '0';
                }
                list.list.add(new NumberNode(val));
            } else if (k == ',') {
                ;
            } else
                System.out.println("unrecognized character: "+ k);
        }
        return (ListNode)(list.list.get(0));
    }

    // <0 a < b, 0 a==b, >0 a > b
    private int compareList(ListNode a, ListNode b) {
        Node na, nb;
        int index = 0;
        int cmp = 0;

        for(index = 0; index < a.list.size(); index++) {
            na = a.list.get(index);

            if(index >= b.list.size())   // list a shorter than list b
                return 1;

            nb = b.list.get(index);
            if(na.isNumber && nb.isNumber) {
                NumberNode nna = (NumberNode)na;
                NumberNode nnb = (NumberNode)nb;
                if(nna.value == nnb.value)
                    continue;
                else
                    return nna.value - nnb.value;
            }
            else if (!na.isNumber && !nb.isNumber) {
                cmp = compareList((ListNode)na, (ListNode)nb);
            }
            else if (!na.isNumber && nb.isNumber) {
                cmp = compareList((ListNode)na, new ListNode(((NumberNode)nb).value));
            }
            else if (na.isNumber && !nb.isNumber) {
                cmp =  compareList(new ListNode(((NumberNode)na).value), (ListNode)nb);
            }
            if(cmp != 0)
                return cmp;
        }
        return( a.list.size() - b.list.size());
    }


}
