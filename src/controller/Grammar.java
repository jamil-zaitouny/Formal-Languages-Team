package controller;

import domain.Production;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Grammar {
    ArrayList<String> terminals = new ArrayList<>();
    ArrayList<String> nonTerminals = new ArrayList<>();
    ArrayList<Production> productions = new ArrayList<>();
    public void readGrammarFromFile(String fileName){
        try {
            String[] listOfTransitions = Files.readString(Path.of(fileName)).split("\r\n");
            String currentRead = "";
            for(String line: listOfTransitions){
                if(line.equals("terminals")){
                    currentRead = "terminals";
                    continue;
                } else if(line.equals("nonterminals")){
                    currentRead = "nonterminals";
                    continue;
                } else if(line.equals("productions")){
                    currentRead = "productions";
                    continue;
                }
                if(currentRead.equals("terminals")){
                    terminals.add(line);
                }else if(currentRead.equals("nonterminals")){
                    nonTerminals.add(line);
                }else if(currentRead.equals("productions")){
                    productions.add(new Production(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void printSetOfTerminals(){
        System.out.println("Set of terminals");
        System.out.println(terminals);
    }
    public void printSetOfNonTerminals(){
        System.out.println("Set of non-terminals");
        System.out.println(nonTerminals);
    }
    public void printSetOfProductions(){
        System.out.println("Set of productions");
        productions.forEach(System.out::println);
    }

    public void printSetOfProductionsNonTerminal(String nonTerminal){
        System.out.println("Set of productions for the nonTerminal " + nonTerminal);
        productions.forEach(production -> {
            if(nonTerminal.equals(production.leftHandSide)){
                System.out.println(production);
            }
        });
    }

    public String[][] parsingTree(){
        String[][] tree = new String[terminals.size()][terminals.size() + nonTerminals.size()];
        //add terminals to the first row
        for(int i = 0; i < terminals.size(); i++){
            tree[0][i] = terminals.get(i);
        }
        //add terminals and non terminals to the list
        for(int i = 0; i < terminals.size() + nonTerminals.size(); i++){
            if(i < nonTerminals.size()){
                tree[i][0] = nonTerminals.get(i);
            }else{
                tree[i][0] = terminals.get(i);
            }
        }

        return tree;
    }
    public boolean hasConflict(){
        return Parser.conflictsExist(productions);
    }

    public HashMap<String, ArrayList<String>> firstFlatten(){
        return Parser.firstFlatten(productions);
    }
    public HashMap<String, ArrayList<String>> followFlatten(){
        return Parser.followFlatten(productions);
    }

    public Grammar(String fileName) {
        this.readGrammarFromFile(fileName);
//        printSetOfTerminals();
//        printSetOfNonTerminals();
//        printSetOfProductions();
//        printSetOfProductionsNonTerminal("T");
        System.out.println(firstFlatten());
        System.out.println(followFlatten());
    }
}
