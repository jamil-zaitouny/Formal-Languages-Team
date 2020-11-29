package domain;

public class Production {
    public String production;
    public String leftHandSide;
    public String[] rightHandSide;

    @Override
    public String toString() {
        return production;

    }

    public Production(String production) {
        this.production = production;
        leftHandSide = production.split(" -> ")[0];
        rightHandSide = production.split(" -> ")[1].split(" ");
    }
}
