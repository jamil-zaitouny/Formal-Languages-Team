import controller.Grammar;
import controller.Parser;

public class Main {
    public static void main(String[] args) {
        Grammar g2 = new Grammar("g2.txt");
        Parser p2 = new Parser(g2);
        p2.parse("PIF.out", "out2.txt");

        System.out.println();
        System.out.println();

        Grammar g1 = new Grammar("g1.txt");
        Parser p1 = new Parser(g1);
        p1.parse("seq.txt", "out1.txt");
    }
}
