import controller.Grammar;
import controller.Parser;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar("g1.txt");
        Parser parser = new Parser(grammar);
        System.out.println(parser.firstList);
        System.out.println(parser.followList);
    }
}
