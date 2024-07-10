package com.example.ghplatinproj;
import java.util.*;

public class tester {
    public static void main(String[] args) {
        Meters test = new Meters("virumque cano Troiae qui pri-mus ab o-ris arma", "Dactylic Hexameter");
        List<List<word>> result = test.getResult();

        System.out.println("Solution: ");
        for (List<word> combination : result) {
            for (word word : combination) {
                System.out.print(word.getWord() + " ");
            }
            System.out.println();
        }
    }
}
