import controller.Grammar;
import controller.Parser;
import domain.ParsingTable;

import java.util.ArrayList;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar("g1.txt");
        //System.out.println(grammar.getNonTerminals());
        Parser parser = new Parser(grammar);

//        for (String key: parser.firstList.keySet()) {
//            System.out.println(key);
//            System.out.println(parser.firstList.get(key));
//        }
        parser.parse("input1.txt", "out1.txt");
    }
}
