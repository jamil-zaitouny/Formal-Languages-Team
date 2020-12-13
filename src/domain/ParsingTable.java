package domain;

import controller.Grammar;
import controller.Parser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class ParsingTable {
    Parser parser;
    String[][] table;
    Integer[][] numberProductionsTable;
    Grammar grammar;

    Production firstOfProduction(String terminal, Production production) {
        if (production.rightHandSide[0].equals(terminal)) {
            return production;
        }
        if (grammar.getTerminals().contains(production.rightHandSide[0])) {
            return null;
        }
        for (Production productionNext : grammar.getProductions(production.rightHandSide[0])) {
            if (parser.firstList.get(productionNext.leftHandSide).contains(terminal)) {
                return productionNext;
            }
        }
        return null;
    }

    public void printTable() {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                System.out.print(table[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printNumberProductions() {
        for (int i=0; i<numberProductionsTable.length; i++) {
            for (int j=0;j<numberProductionsTable[0].length; j++) {
                System.out.print(numberProductionsTable[i][j] + " ");
            }
            System.out.println();
        }
    }

    public ParsingTable(Parser parser) {
        this.parser = parser;
        grammar = parser.getGrammar();
        table = new String[grammar.getTerminals().size()+grammar.getNonTerminals().size()+1][grammar.getTerminals().size()+1];
        numberProductionsTable = new Integer[grammar.getNonTerminals().size()][grammar.getTerminals().size() + 1];
        for (int i = 0; i<grammar.getNonTerminals().size(); i++) {
            for (int j=0; j<grammar.getTerminals().size() + 1; j++) {
                numberProductionsTable[i][j] = 0;
            }
        }

        for (String[] strings : table) {
            Arrays.fill(strings, "err");
        }

        for (int i = grammar.getNonTerminals().size() + grammar.getTerminals().size() - 1; i>=grammar.getNonTerminals().size(); i--) {
            table[i][i - grammar.getNonTerminals().size()] = "pop";
        }

        table[table.length-1][table[0].length-1] = "acc";

        for(String nonTerminal: grammar.getNonTerminals()){
            ArrayList<String> listOfFirsts = parser.firstList.get(nonTerminal);
            for (String first : listOfFirsts) {
                if (!first.equals("é")) {
                    for (Production production : grammar.getProductions(nonTerminal)) {
                        Production productionInTable = firstOfProduction(first, production);
                        if (productionInTable != null) {
                            table[grammar.nonTerminalId(nonTerminal)][grammar.terminalId(first)] = String.valueOf(grammar.productionId(production));
                            numberProductionsTable[grammar.nonTerminalId(nonTerminal)][grammar.terminalId(first)]++;
                        }
                    }
                }
                else {
                    for (Production production: grammar.getProductions(nonTerminal)) {
                        Production productionInTable = firstOfProduction("é", production);
                        if (productionInTable != null) {
                            ArrayList<String> listOfFollows = parser.followList.get(nonTerminal);
                            //System.out.println(listOfFollows);
                            for (String follow: listOfFollows) {
                                if (follow.equals("é")) {
                                    table[grammar.nonTerminalId(nonTerminal)][grammar.getTerminals().size()] = String.valueOf(grammar.productionId(productionInTable));
                                    numberProductionsTable[grammar.nonTerminalId(nonTerminal)][grammar.getTerminals().size()]++;
                                }
                                else {
                                    table[grammar.nonTerminalId(nonTerminal)][grammar.terminalId(follow)] = String.valueOf(grammar.productionId(productionInTable));
                                    numberProductionsTable[grammar.nonTerminalId(nonTerminal)][grammar.terminalId(follow)]++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void parseWithTable(Stack<String> alfa, String filename) {
        ParserOutput parserOutput = new ParserOutput();
        Stack<String> beta = new Stack<>();
        Stack<Integer> fatherStack = new Stack<>();
        fatherStack.add(-1);
        beta.add("$");
        beta.add("S");
        ArrayList<Integer> pi = new ArrayList<>();
        String result = null;
        boolean go = true;
        while (go) {
            int nonTerminalId = grammar.nonTerminalId(beta.peek());
            int terminalId = grammar.terminalId(alfa.peek());
            if (nonTerminalId != -1 && terminalId != -1 && ! table[nonTerminalId][terminalId].equals("err")) {
                    parserOutput.addNode(beta.peek(), fatherStack.pop());
                    beta.pop();
                    Production production = grammar.getProductionById(Integer.parseInt(table[nonTerminalId][terminalId]));
                    for (int i = production.rightHandSide.length - 1; i>=0; i--) {
                        beta.push(production.rightHandSide[i]);
                        fatherStack.push(parserOutput.getCurrentId());
                    }
                    pi.add(Integer.parseInt(table[nonTerminalId][terminalId]));
            }
            else {
                if (nonTerminalId != -1 && alfa.peek().equals("$") && !table[nonTerminalId][grammar.getTerminals().size()].equals("err")) {
                    parserOutput.addNode(beta.peek(), fatherStack.pop());
                    beta.pop();
                    beta.push("é");
                    fatherStack.push(parserOutput.getCurrentId());
                    pi.add(Integer.parseInt(table[nonTerminalId][grammar.getTerminals().size()]));
                } else {
                    if (beta.peek().equals("é")) {
                        parserOutput.addNode(beta.pop(), fatherStack.pop());
                    }
                    else {
                        if (beta.peek().equals(alfa.peek()) && !beta.peek().equals("$")) {
                            String node = beta.pop();
                            alfa.pop();
                            parserOutput.addNode(node, fatherStack.pop());
                        } else {
                            if (beta.peek().equals("$") && alfa.peek().equals("$")) {
                                go = false;
                                result = "accept";
                            } else {
                                go = false;
                                result = "error";
                            }
                        }
                    }
                }
            }
        }
        if (result.equals("accept")) {
            parserOutput.reorderTree();
            parserOutput.printTable();
            parserOutput.tableToFile(filename);
        }
        else {
            System.out.println("There was an error.");
        }
    }

    public ArrayList<String> conflictSymbols() {
        for (int i=0; i<numberProductionsTable.length; i++) {
            for (int j=0;j<numberProductionsTable[0].length; j++) {
                if (numberProductionsTable[i][j] >= 2) {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(grammar.getNonTerminals().get(i));
                    if (j == numberProductionsTable[0].length) {
                        list.add("$");
                    }
                    else {
                        list.add(grammar.getTerminals().get(j));
                    }
                    return list;
                }
            }
        }
        return null;
    }
}
