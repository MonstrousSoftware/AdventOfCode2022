package com.monstrous;

import java.util.HashMap;

public class Day20 {

    final static long DECRYPTION_KEY = 811589153;

    class Node {
        long value;
        Node next;
        Node prev;

        public Node(long value) {
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

        public Node addNode(long value){
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

        int[] initial = new int[ input.size() ];

        int index = 0;
        for(String line: input.lines) {
            int value = Integer.parseInt(line);
            initial[index++] = value;
        }


        long sum = scramble(initial, 1, 1);
        System.out.println("Part 1: sum is "+sum);

        long sum2 = scramble(initial, DECRYPTION_KEY, 10);
        System.out.println("Part 2: sum is "+sum2);

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


    private long scramble( int start[], long key , int rounds) {
        int num = start.length;
        long max = num-1;
        CircularList circle = new CircularList();
        Node[] initialList = new Node[num];
        Node zeroNode  = null;

        for(int i = 0 ; i < num; i++) {
            Node node = circle.addNode((long)start[i] * key);
            initialList[i] = node;
            if(node.value == 0)
                zeroNode = node;
        }

        if(num < 20)
            printCircle(circle);
        for(int round = 0; round < rounds; round++) {
            for (int i = 0; i < num; i++) {
                Node node = initialList[i];         // next value from initial sequence

                long value = node.value;

                // unlink from the list
                node.prev.next = node.next;
                node.next.prev = node.prev;
                if (circle.head == node)
                    circle.head = node.next;
                if (circle.tail == node)
                    circle.tail = node.prev;

                //printCircle(circle);

                Node curr = node;
                value = ((value % max) + max)%max;
                if (value > 0) {
                    for (int j = 0; j < value; j++)
                        curr = curr.next;
                } else if (value < 0) {
                    for (int j = 0; j > value; j--)
                        curr = curr.prev;
                    curr = curr.prev;
                } else if (value == 0)
                    curr = node.prev;

                // insert node after curr
                //System.out.println("Insert "+value+" between "+curr.value +" and "+curr.next.value);
                node.next = curr.next;
                curr.next = node;
                node.prev = curr;
                node.next.prev = node;


            }
            if (num < 20)
                printCircle(circle);
        }

        long sum = 0;
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
