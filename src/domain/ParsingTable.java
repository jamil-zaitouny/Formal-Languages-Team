package domain;

import controller.Grammar;
import controller.Parser;

import java.util.ArrayList;
import java.util.Arrays;

public class ParsingTable {
    Parser parser;
    String[][] table;
    Grammar grammar;

    Production firstOfProduction(String terminal, Production production) {
        if (production.rightHandSide[0].equals(terminal)) {
            return production;
        }
        if (grammar.getTerminals().contains(production.rightHandSide[0])) {
            return null;
        }
        for (Production productionNext: grammar.getProductions(production.rightHandSide[0])) {
            if (firstOfProduction(terminal, productionNext) != null) {
                return productionNext;
            }
        }
        return null;
    }

    public ParsingTable(Parser parser) {
        this.parser = parser;
        grammar = parser.getGrammar();
        table = new String[grammar.getTerminals().size()+grammar.getNonTerminals().size()+1][grammar.getTerminals().size()+1];
        //initalizing the matrix

        for(int i = 0; i < table.length; i++){
                Arrays.fill(table[i], "err");
            }

        //Adding the pop for the terminals
        for(int i = grammar.getNonTerminals().size(); i < grammar.getNonTerminals().size() + grammar.getTerminals().size();i++){
            for(int j = 0; j < grammar.getTerminals().size(); j++){
                table[i][j] = "pop";
            }
        }

        //setting the empty stack
        table[table.length-1][table[0].length-1] = "acc";

        for(String nonTerminal: grammar.getNonTerminals()){
            ArrayList<String> listOfFirsts = parser.firstList.get(nonTerminal);
            for(int i = 0; i < listOfFirsts.size(); i++){
                String first = listOfFirsts.get(i);
                if (!first.equals("é")) {
                    for (Production production: grammar.getProductions(nonTerminal)) {
                        Production productionInTable = firstOfProduction(first, production);
                        if (productionInTable != null) {
                            table[grammar.nonTerminalId(nonTerminal)][grammar.terminalId(first)] = String.valueOf(grammar.productionId(production));
                        }
                    }
                }
                if(listOfFirsts.get(i).equals("é")){
                    ArrayList<String> follows = parser.followList.get(nonTerminal);

                }else{

                }
            }
        }
    }

    public ArrayList<ArrayList<String>> constructTable(){

    }

}
