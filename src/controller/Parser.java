package controller;

import domain.ParsingTable;
import domain.Production;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Parser {
    public HashMap<String, ArrayList<String>> firstList;
    public HashMap<String, ArrayList<String>> followList;
    Grammar grammar;

    public Parser(Grammar grammar) {
        this.grammar = grammar;
        this.buildFirst();
        this.buildFollow();
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void parse(String inputFilename, String outputFilename) {
        try {
            ParsingTable parsingTable = new ParsingTable(this);
            ArrayList<String> conflict = parsingTable.conflictSymbols();
            if (conflict != null) {
                System.out.println("There is a conflict in the parsing table at cell " + conflict);
            }
            else {
                String[] listOfLines = Files.readString(Path.of(inputFilename)).split("\n");
                ArrayList<String> tokens = new ArrayList<>();
                for (String line: listOfLines) {
                    line = line.replace("\t", " ");
                    String[] words = line.split("\\s+");
                    for (String token: words) {
                        tokens.add(token);
                    }
                }
                Stack<String> alfa = new Stack<>();
                alfa.push("$");
                for (int i=tokens.size() - 1; i>=0; i--) {
                    alfa.push(tokens.get(i));
                }
                parsingTable.parseWithTable(alfa, outputFilename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, ArrayList<String>> deepCopyHashMap(HashMap<String, ArrayList<String>> hashMap) {
        HashMap<String, ArrayList<String>> newHashMap = new HashMap<>();
        for (String key: hashMap.keySet()) {
            newHashMap.put(key, new ArrayList<>());
            for (String string: hashMap.get(key)) {
                newHashMap.get(key).add(string);
            }
        }
        return newHashMap;
    }

    private static int hashMapSize(HashMap<String, ArrayList<String>> hashMap) {
        int size = 0;
        for (String key: hashMap.keySet()) {
            size += hashMap.get(key).size();
        }
        return size;
    }

    private void buildFirst() {
        HashMap<String, ArrayList<String>> nonTerminalsForFirst = new HashMap<>();
        firstList = new HashMap<>();
        for (String nonTerminal: grammar.getNonTerminals()) {
            firstList.put(nonTerminal, new ArrayList<>());
            nonTerminalsForFirst.put(nonTerminal, new ArrayList<>());
        }
        for (Production production: grammar.productions) {
            String token = production.rightHandSide[0];
            if (grammar.isNonTerminal(token)) {
                nonTerminalsForFirst.get(production.leftHandSide).add(token);
            }
            else {
                firstList.get(production.leftHandSide).add(token);
            }
        }
        HashMap<String, ArrayList<String>> firstListOld = new HashMap<>();
        while (hashMapSize(firstListOld) != hashMapSize(firstList)) {
            firstListOld = deepCopyHashMap(firstList);
            for (String nonTerminal: grammar.getNonTerminals()) {
                for (String firstNonTerminal: nonTerminalsForFirst.get(nonTerminal)) {
                    ArrayList<String> toAddList = firstList.get(firstNonTerminal);
                    for (String toAdd: toAddList) {
                        if (!firstList.get(nonTerminal).contains(toAdd)) {
                            firstList.get(nonTerminal).add(toAdd);
                        }
                    }
                }
            }
        }
    }

    private void buildFollow() {
        followList = new HashMap<>();
        HashMap<String, ArrayList<String>> followListOld = new HashMap<>();
        for (String nonTerminal: grammar.getNonTerminals()) {
            followList.put(nonTerminal, new ArrayList<>());
        }
        followList.get("S").add("é");
        for (Production production: grammar.productions) {
            String[] rightHandSide = production.rightHandSide;
            for (int i=0; i<rightHandSide.length - 1; i++) {
                String nonTerminal = rightHandSide[i];
                String nextToken = rightHandSide[i + 1];
                if (grammar.isNonTerminal(nonTerminal)) {
                    if (grammar.isNonTerminal(nextToken)) {
                        for (String first: firstList.get(nextToken)) {
                            if (!first.equals("é") && !followList.get(nonTerminal).contains(first)) {
                                followList.get(nonTerminal).add(first);
                            }
                        }
                    }
                    else {
                        if (!followList.get(nonTerminal).contains(nextToken)) {
                            followList.get(nonTerminal).add(nextToken);
                        }
                    }
                }
            }
        }
        while (hashMapSize(followListOld) != hashMapSize(followList)) {
            followListOld = deepCopyHashMap(followList);
            for (Production production: grammar.productions) {
                String[] rightHandSide = production.rightHandSide;
                String leftHandSide = production.leftHandSide;
                String nonTerminal = rightHandSide[rightHandSide.length - 1];
                if (grammar.isNonTerminal(nonTerminal)) {
                    for (String follow: followList.get(leftHandSide)) {
                        if (!followList.get(nonTerminal).contains(follow)) {
                            followList.get(nonTerminal).add(follow);
                        }
                    }
                }
            }
            for (Production production: grammar.productions) {
                String[] rightHandSide = production.rightHandSide;
                String leftHandSide = production.leftHandSide;
                int rightHandSideIndex = rightHandSide.length - 1;
                while(rightHandSideIndex >= 0 && grammar.isNonTerminal(rightHandSide[rightHandSideIndex]) && firstList.get(rightHandSide[rightHandSideIndex]).contains("é")) {
                    rightHandSideIndex --;
                }
                rightHandSideIndex ++;
                if (rightHandSideIndex < rightHandSide.length && rightHandSideIndex > 0) {
                    String nonTerminalNext = rightHandSide[rightHandSideIndex];
                    String nonTerminal = rightHandSide[rightHandSideIndex - 1];
                    for (String first: firstList.get(nonTerminalNext)) {
                        if (!first.equals("é") && !followList.get(nonTerminal).contains(first)) {
                            followList.get(nonTerminal).add(first);
                        }
                    }
                    for (String follow: followList.get(leftHandSide)) {
                        if (!followList.get(nonTerminal).contains(follow)) {
                            followList.get(nonTerminal).add(follow);
                        }
                    }
                }
            }
        }
    }
}
