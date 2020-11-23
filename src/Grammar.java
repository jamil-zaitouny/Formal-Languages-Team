import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
    public Grammar(String fileName) {
        this.readGrammarFromFile(fileName);
        printSetOfTerminals();
        printSetOfNonTerminals();
        printSetOfProductions();
        printSetOfProductionsNonTerminal("T");
    }
}
