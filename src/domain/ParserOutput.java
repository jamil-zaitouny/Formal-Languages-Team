package domain;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ParserOutput {
    private static class ParserOutputNode {
        public int id;
        public String token;
        public int father;
        public int sibling;

        public ParserOutputNode(int id, String token, int father, int sibling) {
            this.id = id;
            this.token = token;
            this.father = father;
            this.sibling = sibling;
        }

        @Override
        public String toString() {
            return id + " " + token + " " + father + " " + sibling;
        }
    }

    public int getCurrentId() {
        return treeNodes.size() - 1;
    }

    ArrayList<ParserOutputNode> treeNodes = new ArrayList<>();

    private int getSiblingId(Integer fatherId) {
        for (int i=treeNodes.size() - 1; i>=0; i--) {
            if (treeNodes.get(i).father == fatherId) {
                return treeNodes.get(i).id;
            }
        }
        return -1;
    }

    public void addNode(String token, Integer fatherId) {
        int siblingId = getSiblingId(fatherId);
        treeNodes.add(new ParserOutputNode(treeNodes.size(), token, fatherId, siblingId));
    }

    public void printTable() {
        for (ParserOutputNode node: treeNodes) {
            System.out.println(node);
        }
    }

    public void tableToFile(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            for (ParserOutputNode node: treeNodes) {
                writer.write(node + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reorderTree() {
        ArrayList<ParserOutputNode> levelNodes = new ArrayList<>();
        levelNodes.add(treeNodes.get(0));
        ArrayList<ParserOutputNode> newTreeNodes = new ArrayList<>();
        newTreeNodes.add(treeNodes.get(0));
        while (levelNodes.size() > 0) {
            ArrayList<ParserOutputNode> newLevelNodes = new ArrayList<>();
            for (ParserOutputNode levelNode: levelNodes) {
                for (ParserOutputNode node : treeNodes) {
                    if (node.father == levelNode.id) {
                        newLevelNodes.add(node);
                        newTreeNodes.add(node);
                    }
                }
            }
            levelNodes = newLevelNodes;
        }
        treeNodes = newTreeNodes;
        for (int index1=0; index1<treeNodes.size(); index1++) {
            int index2 = treeNodes.get(index1).id;
            for (ParserOutputNode nodeToChange: treeNodes) {
                if (nodeToChange.id == index1 || nodeToChange.id == index2) {
                    if (nodeToChange.id == index1) {
                        nodeToChange.id = index2;
                    }
                    else {
                        nodeToChange.id = index1;
                    }
                }
                if (nodeToChange.father == index1 || nodeToChange.father == index2) {
                    if (nodeToChange.father == index1) {
                        nodeToChange.father = index2;
                    }
                    else {
                        nodeToChange.father = index1;
                    }
                }
                if (nodeToChange.sibling == index1 || nodeToChange.sibling == index2) {
                    if (nodeToChange.sibling == index1) {
                        nodeToChange.sibling = index2;
                    }
                    else {
                        nodeToChange.sibling = index1;
                    }
                }
            }
        }
    }
}
