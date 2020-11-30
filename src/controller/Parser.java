package controller;

import domain.Production;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


//btw, you might know better, is having static methods like this a bad idea, from a design point of view?
public class Parser {
    //returns true if there's a first conflict or a follow conflict
    public static boolean conflictsExist(ArrayList<Production> productions) {
        return Parser.firstConflict(productions) || Parser.followConflict(productions);
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
    public static boolean followConflict(ArrayList<Production> productions) {
        HashMap<String, ArrayList<String>> followMap = followFlatten(productions);
        ArrayList<HashSet<String>> uniquenessCheck = new ArrayList<>();
        followMap.forEach((key, list) -> uniquenessCheck.add(new HashSet<>(list)));
        boolean isUnique = true;
        for (int i = 0; i < uniquenessCheck.size() - 1; i++) {
            for (int j = i + 1; j < uniquenessCheck.size(); j++) {
                isUnique = isUnique && uniquenessCheck.get(i).equals(uniquenessCheck.get(j));
            }
        }
        return isUnique;
    }

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
            System.out.println("F" + counter);
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
            counter++;
        }while(!isFlat.get());
        return first;
    }
    public static HashMap<String, ArrayList<String>> follow(ArrayList<Production> productions) {
        HashMap<String, ArrayList<String>> follow = new HashMap<>();
        for (Production production : productions) {
            follow.put(production.leftHandSide, new ArrayList<>());
        }
        HashMap<String, ArrayList<String>> first = first(productions);
        boolean isNext = false;
        for (Production production : productions) {
            ArrayList<String> followList = follow.get(production.leftHandSide);
            for (Map.Entry<String, ArrayList<String>> entry : first.entrySet()) {
                if(entry.getKey().equals(production.leftHandSide)){
                    followList.add(entry.getValue().get(entry.getValue().size() - 1));
                }
                for (String token : entry.getValue()) {
                    if (token.equals(production.leftHandSide)) {
                        isNext = true;
                        continue;
                    }
                    if (isNext) {
                        followList.add(token);
                        follow.replace(production.leftHandSide, followList);
                    }
                    isNext = false;
                }
            }
        }
        return follow;
    }

    public static HashMap<String, ArrayList<String>> followFlatten(ArrayList<Production> productions) {
        HashMap<String, ArrayList<String>> follow = Parser.follow(productions);
        HashMap<String, ArrayList<String>> firstFlatten = Parser.firstFlatten(productions);
        boolean isFlat;
        do{
            isFlat = true;
            for(Map.Entry<String, ArrayList<String>> entry: follow.entrySet()){
                ArrayList<String> followList = follow.get(entry.getKey());
                for(String token: entry.getValue()){
                    if(Character.isUpperCase(token.charAt(0))){
                        if(firstFlatten.get(token).contains("e")){
                            String leftHandSide = Parser.getLeftHandSide(entry.getKey(), token, productions);
                            followList.addAll(firstFlatten.get(leftHandSide));
                            follow.remove(token);
                            follow.replace(entry.getKey(), followList);
                        }else{
                            followList.addAll(follow.get(token));
                            followList.remove(token);
                            follow.replace(entry.getKey(), followList);
                        }
                        isFlat = false;
                    }
                }
            }
        }while(!isFlat);
        return follow;
    }

    public static String getLeftHandSide(String key, String token, ArrayList<Production> productions) {
        String leftHandSide = "e";
        for(Production production: productions){
            boolean isNext = false;
            for(String rightHandToke: production.rightHandSide){
                if(rightHandToke.equals(key)){
                    isNext = true;
                    continue;
                }if(isNext){
                    if(rightHandToke.equals(token)){
                        leftHandSide.equals(production.leftHandSide);
                        break;
                    }
                }
                isNext = false;
            }
        }
        return leftHandSide;
    }

}
