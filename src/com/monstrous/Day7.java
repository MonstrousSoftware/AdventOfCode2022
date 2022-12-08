package com.monstrous;


import java.util.ArrayList;

public class Day7 {

    final FileInput input;

    class Node {
        String name;
        boolean isDir;
        int size;
    }

    class DirNode extends Node {
        ArrayList<Node> contents;
        DirNode parent;

        public DirNode(String name, DirNode parent) {
            this.name = name;
            contents = new ArrayList<>();
            this.parent = parent;
            size = 0;
            isDir = true;
        }
    }

    class FileNode extends Node {
        public FileNode(String name, int size) {
            this.name = name;
            this.size = size;
            isDir = false;
        }
    }

    public Day7() {
        System.out.print("Day 7\n");
        input = new FileInput("data/day7.txt");

        DirNode root = new DirNode("/", null);
        DirNode curDir = null;


        for (String line : input.lines) {

            System.out.println(line);

            String[] words = line.split(" ");

            if(words[0].contentEquals("$")) {   // command
                if(words[1].contentEquals("cd")) {      // change dir
                    String dirName = words[2];
                    //System.out.println("change directory to "+dirName);
                    if(dirName.contentEquals("/"))
                        curDir = root;
                    else if(dirName.contentEquals(".."))
                        curDir = curDir.parent;
                    else {
                        DirNode cd = null;
                        for(Node node : curDir.contents ) {
                            if(node.isDir && node.name.contentEquals(dirName))
                                cd = (DirNode)node;
                        }
                        if(cd == null)
                            System.out.println("subdirectory not found: "+dirName);
                        else
                            curDir = cd;
                    }
                } else if(words[1].contentEquals("ls")) {      // ls
                    //System.out.println("list directory");
                } else {
                    //System.out.println("unrecognized command "+words[1]);
                }
            } else if( words[0].contentEquals("dir")) {
                String dirName = words[1];
                //System.out.println("found directory "+dirName);
                DirNode dir = new DirNode(dirName, curDir);
                curDir.contents.add(dir);
            } else {    // file: size + name
                int size = Integer.parseInt( words[0] );
                String name = words[1];
                //System.out.println("found file "+name+" of size "+size);
                FileNode file = new FileNode(name, size);
                curDir.contents.add(file);
            }
        }

        accumulateDirSize(root);
        //printDir(root, 0);
        int total = sumDirThreshold(root, 100000);
        System.out.println("sum of dir sizes <= 100000 : "+total);

        final int DISK_SIZE = 70000000;
        final int NEEDED = 30000000;
        int unused = DISK_SIZE - root.size;
        //System.out.println("unused space : "+unused);
        int todelete = NEEDED - unused;
        //System.out.println("additional space needed : "+todelete);

        DirNode dir = dirToDelete(root, root, todelete);
        System.out.println("deleting directory "+dir.name+" provides "+dir.size);


    }

    private int accumulateDirSize(DirNode dir) {
        int total = 0;
        for(Node node : dir.contents ) {
            if(node.isDir)
                total += accumulateDirSize((DirNode)node);
            else
                total += node.size;
        }
        dir.size = total;
        return total;
    }

    private void printDir(DirNode dir, int level) {
        System.out.println(level + "   "+dir.name+" (size): "+dir.size);
        for(Node node : dir.contents ) {
            if(node.isDir)
                printDir((DirNode)node, level+1);
            else
                System.out.println((level+1)+"       "+node.name+" size: "+node.size);
        }
    }

    private int sumDirThreshold(DirNode dir, int threshold) {
        int total = 0;
        for(Node node : dir.contents ) {
            if(node.isDir) {
                total += sumDirThreshold((DirNode) node, threshold);
                if(node.size <= threshold)
                    total += node.size;
            }
        }
        return total;
    }

    private DirNode dirToDelete(DirNode dir, DirNode smallestCandidate, int spaceRequired) {

        for(Node node : dir.contents ) {
            if(node.isDir) {
                if(node.size < spaceRequired)
                    continue;
                if (node.size < smallestCandidate.size) {
                    smallestCandidate = (DirNode)node;
                    smallestCandidate = dirToDelete((DirNode) node, smallestCandidate, spaceRequired);
                }
            }
        }
        return smallestCandidate;
    }

}
