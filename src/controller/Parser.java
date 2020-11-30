package controller;

import domain.Production;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Parser {
    public HashMap<String, ArrayList<String>> firstList;
    public HashMap<String, ArrayList<String>> followList;
    Grammar grammar;

    public Parser(Grammar grammar) {
        this.grammar = grammar;
        this.firstList = firstFlatten();
        this.followList = followFlatten();
    }

    public boolean conflictsExist() {
        return firstConflict();
    }

    public boolean firstConflict() {
        ArrayList<HashSet<String>> uniquenessCheck = new ArrayList<>();
        firstList.forEach((key, list) -> uniquenessCheck.add(new HashSet<>(list)));
        boolean isUnique = true;
        for (int i = 0; i < uniquenessCheck.size() - 1; i++) {
            for (int j = i + 1; j < uniquenessCheck.size(); j++) {
                isUnique = isUnique && uniquenessCheck.get(i).equals(uniquenessCheck.get(j));
            }
        }
        return isUnique;
    }

//    public static boolean followConflict(ArrayList<Production> productions) {
//        HashMap<String, ArrayList<String>> followMap = followFlatten(productions);
//        ArrayList<HashSet<String>> uniquenessCheck = new ArrayList<>();
//        followMap.forEach((key, list) -> uniquenessCheck.add(new HashSet<>(list)));
//        boolean isUnique = true;
//        for (int i = 0; i < uniquenessCheck.size() - 1; i++) {
//            for (int j = i + 1; j < uniquenessCheck.size(); j++) {
//                isUnique = isUnique && uniquenessCheck.get(i).equals(uniquenessCheck.get(j));
//            }
//        }
//        return isUnique;
//    }

    public HashMap<String, ArrayList<String>> first() {
        ArrayList<Production> productions = grammar.productions;
        HashMap<String, ArrayList<String>> first = new HashMap<>();
        for(Production production: productions){
            first.put(production.leftHandSide, new ArrayList<>());
        }
        for (Production production : productions) {
            first.get(production.leftHandSide).add(production.rightHandSide[0]);
        }
        return first;
    }

    public HashMap<String, ArrayList<String>> firstFlatten() {
        AtomicBoolean isFlat = new AtomicBoolean(false);
        HashMap<String, ArrayList<String>> first = first();
        Set<String> listOfKeys = new HashSet<>(first.keySet());
        int counter = 0;
        do{
            for(String key: listOfKeys){
                int size = first.get(key).size();
                for(int i = 0; i < size; i++){
                    isFlat.set(true);
                    if(grammar.isNonTerminal(first.get(key).get(i))){
                        isFlat.set(false);
                        ArrayList<String> terminals = first.get(first.get(key).get(i));
                        first.get(key).addAll(terminals);
                        first.get(key).remove(first.get(key).get(i));
                    }
                }
            }
        }while(!isFlat.get());
        return first;
    }
    public HashMap<String, ArrayList<String>> follow() {
        ArrayList<Production> productions = grammar.productions;
        HashMap<String, ArrayList<String>> follow = new HashMap<>();
        HashMap<String, ArrayList<String>> first = firstFlatten();
        productions.forEach(production -> follow.put(production.leftHandSide, new ArrayList<>()));
        for(Production production: productions){
            for(int i = 0; i <  production.rightHandSide.length; i++){
                if(grammar.isNonTerminal(production.rightHandSide[i])){
                    if(i == production.rightHandSide.length-1){
                        follow.get(production.rightHandSide[i]).add(production.leftHandSide);
                    }else{
                        int currentCounter = 1 + i;
                        String currentNext = production.rightHandSide[currentCounter];
                        while(currentCounter < production.rightHandSide.length){
                            if(!grammar.isNonTerminal(production.rightHandSide[currentCounter])){
                                follow.get(production.rightHandSide[i]).add(production.rightHandSide[currentCounter]);
                                break;
                            }
                            if(currentCounter == production.rightHandSide.length -1){
                                follow.get(production.rightHandSide[i]).addAll(first.get(production.rightHandSide[currentCounter]));
                                break;
                            }
                            ArrayList<String> firstOfCurrentFollow = first.get(production.rightHandSide[currentCounter]);
                            follow.get(production.rightHandSide[i]).addAll(firstOfCurrentFollow);
                            if(!firstOfCurrentFollow.contains("é")){
                                break;
                            }
                            currentCounter++;
                        }
                    }
                }
            }
        }
        follow.get("S").add("é");
        HashMap<String, ArrayList<String>> finalFollow = new HashMap<>();
        for(Map.Entry<String, ArrayList<String>> entry: follow.entrySet()){
            finalFollow.put(entry.getKey(), new ArrayList<>(new HashSet<>(entry.getValue())));
        }
        return finalFollow;
    }

    public HashMap<String, ArrayList<String>> followFlatten() {
        HashMap<String, ArrayList<String>> follow = follow();
        boolean isFlat = false;
        do{
            isFlat = true;
            for(String key: follow.keySet()){
                int size = follow.get(key).size();
                for(int i = 0; i < size; i++){
                   if(grammar.isNonTerminal(follow.get(key).get(i))){
                       isFlat = false;
                       String tokenToReplaceWith = follow.get(key).get(i);
                       follow.get(key).remove(tokenToReplaceWith);
                       follow.get(key).addAll(follow.get(tokenToReplaceWith));
                   }
                }
            }
        }while(!isFlat);
        HashMap<String, ArrayList<String>> finalFollow = new HashMap<>();
        for(Map.Entry<String, ArrayList<String>> entry: follow.entrySet()){
            finalFollow.put(entry.getKey(), new ArrayList<>(new HashSet<>(entry.getValue())));
        }
        return finalFollow;
    }
}
