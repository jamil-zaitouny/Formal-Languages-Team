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
            //String[] listOfTransitions = Files.readString(Path.of(fileName)).split("\r\n");
            String[] listOfTransitions = Files.readString(Path.of(fileName)).split("\n");
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

    public ArrayList<String> getTerminals() {
        return terminals;
    }

    public void setTerminals(ArrayList<String> terminals) {
        this.terminals = terminals;
    }

    public ArrayList<String> getNonTerminals() {
        return nonTerminals;
    }

    public void setNonTerminals(ArrayList<String> nonTerminals) {
        this.nonTerminals = nonTerminals;
    }

    public ArrayList<Production> getProductions(String nonTerminal) {
        ArrayList<Production> selectedProductions = new ArrayList<>();
        productions.forEach(production -> {
            if(nonTerminal.equals(production.leftHandSide)) {
                selectedProductions.add(production);
            }
        });
        return selectedProductions;
    }

    public Production getProductionById(Integer id) {
        return productions.get(id);
    }

    public int nonTerminalId(String nonTerminal) {
        return nonTerminals.indexOf(nonTerminal);
    }

    public int terminalId(String terminal) {
        return terminals.indexOf(terminal);
    }

    public int productionId(Production production) {
        return productions.indexOf(production);
    }

    public boolean isNonTerminal(String string) {
        return nonTerminals.contains(string);
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

    public Grammar(String fileName) {
        this.readGrammarFromFile(fileName);
//        printSetOfTerminals();
//        printSetOfNonTerminals();
//        printSetOfProductions();
//        printSetOfProductionsNonTerminal("T");
    }
}
