package com.monstrous;

import java.util.HashMap;

public class Day20 {

    class Node {
        int value;
        Node next;
        Node prev;

        public Node(int value) {
            this.value = value;
            next = null;
            prev = null;
        }
    }
    class CircularList {
        Node head;
        Node tail;

        public CircularList() {
            head = null;
            tail = null;
        }

        public Node addNode(int value){
            Node node = new Node(value);
            if(head == null) {
                head = node;
                tail = node;
            }
            node.prev = tail;   // insert after tail
            tail.next = node;
            node.next = head;
            head.prev = node;
            tail = node;

            return node;
        }

    }

    final FileInput input;

    public Day20() {
        System.out.print("Day 20\n");
        final long startTime = System.currentTimeMillis();

        input = new FileInput("data/day20.txt");

        int num = input.size();
        int max = num-1;
        System.out.println("size of array: "+num);
        int[] initial = new int[ num ];

        int index = 0;
        for(String line: input.lines) {

            int value = Integer.parseInt(line);
            initial[index++] = value;
        }


        int sum = scrambleNew(initial);

        System.out.println("Part 1: sum is "+sum);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nTotal execution time : " + (endTime - startTime) + " ms");
    }




    private void printCircle( CircularList circle ){
        Node n = circle.head;
        do {
            System.out.print(n.value + " ");
            n = n.next;
        } while(n != circle.head);
        System.out.println();

    }


    private int scrambleNew( int start[] ) {
        int num = start.length;
        CircularList circle = new CircularList();
        Node[] initialList = new Node[num];
        Node zeroNode  = null;

        for(int i = 0 ; i < num; i++) {
            Node node = circle.addNode(start[i]);
            initialList[i] = node;
            if(node.value == 0)
                zeroNode = node;
        }

        //printCircle(circle);

        for(int i = 0; i < num; i++) {
            Node node = initialList[i];         // next value from initial sequence

            int value = node.value;

            // unlink from the list
            node.prev.next = node.next;
            node.next.prev = node.prev;
            if(circle.head == node)
                circle.head = node.next;
            if(circle.tail == node)
                circle.tail = node.prev;

            //printCircle(circle);

            Node curr = node;
            if(value > 0) {
                for (int j = 0; j < value; j++)
                    curr = curr.next;
            } else if(value < 0) {
                for (int j = 0; j > value; j--)
                    curr = curr.prev;
                curr = curr.prev;
            } else if(value == 0)
                curr = node.prev;

            // insert node after curr
            //System.out.println("Insert "+value+" between "+curr.value +" and "+curr.next.value);
            node.next = curr.next;
            curr.next = node;
            node.prev = curr;
            node.next.prev = node;

            if(num < 20)
                printCircle(circle);
        }

        int sum = 0;
        Node curr = zeroNode;
        for(int sample = 1; sample <= 3; sample ++)
        {
            for(int j = 0; j < 1000; j++)
                curr = curr.next;
            sum += curr.value;
            //System.out.println("sample is "+curr.value);

        }
        return sum;
    }

}
