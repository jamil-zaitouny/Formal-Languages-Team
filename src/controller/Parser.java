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
        this.firstList = firstFlatten(grammar.productions);
        this.followList = followFlatten(grammar.productions);
    }

    public static boolean conflictsExist(ArrayList<Production> productions) {
        return Parser.firstConflict(productions);
    }

    public static boolean firstConflict(ArrayList<Production> productions) {
        HashMap<String, ArrayList<String>> firstList = firstFlatten(productions);
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

    public static HashMap<String, ArrayList<String>> first(ArrayList<Production> productions) {
        HashMap<String, ArrayList<String>> first = new HashMap<>();
        for(Production production: productions){
            first.put(production.leftHandSide, new ArrayList<>());
        }
        for (Production production : productions) {
            first.get(production.leftHandSide).add(production.rightHandSide[0]);
        }
        return first;
    }

    public static HashMap<String, ArrayList<String>> firstFlatten(ArrayList<Production> productions) {
        AtomicBoolean isFlat = new AtomicBoolean(false);
        HashMap<String, ArrayList<String>> first = Parser.first(productions);
        Set<String> listOfKeys = new HashSet<>(first.keySet());
        int counter = 0;
        do{
            for(String key: listOfKeys){
                int size = first.get(key).size();
                for(int i = 0; i < size; i++){
                    isFlat.set(true);
                    if(Character.isUpperCase(first.get(key).get(i).charAt(0))){
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
    public static HashMap<String, ArrayList<String>> follow(ArrayList<Production> productions) {
        HashMap<String, ArrayList<String>> follow = new HashMap<>();
        HashMap<String, ArrayList<String>> first = firstFlatten(productions);
        productions.forEach(production -> follow.put(production.leftHandSide, new ArrayList<>()));
        for(Production production: productions){
            for(int i = 0; i <  production.rightHandSide.length; i++){
                if(Character.isUpperCase(production.rightHandSide[i].charAt(0))){
                    if(i == production.rightHandSide.length-1){
                        follow.get(production.rightHandSide[i]).add(production.leftHandSide);
                    }else{
                        int currentCounter = 1 + i;
                        String currentNext = production.rightHandSide[currentCounter];
                        while(currentCounter < production.rightHandSide.length){
                            if(!Character.isUpperCase(production.rightHandSide[currentCounter].charAt(0))){
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

    public static HashMap<String, ArrayList<String>> followFlatten(ArrayList<Production> productions) {
        HashMap<String, ArrayList<String>> follow = Parser.follow(productions);
        boolean isFlat = false;
        do{
            isFlat = true;
            for(String key: follow.keySet()){
                int size = follow.get(key).size();
                for(int i = 0; i < size; i++){
                   if(Character.isUpperCase(follow.get(key).get(i).charAt(0))){
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
