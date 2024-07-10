package com.example.ghplatinproj;
import java.util.*;

public class Meters {
    public String pattern1;
    public int lines;
    public LinkedList<word> words;
    public List<List<word>> result = new ArrayList<>();

    public Meters(String Latin, String meter) {
        if (meter.equals("Dactylic Hexameter")) {
            pattern1 = "LELELELELSSLA";
            lines = 1;
        } else if (meter.equals("Elegiac Couplet Line 1")) {
            pattern1 = "LELELELELSSLA";
            lines = 2;
        } else if (meter.equals("Elegiac Couplet Line 2")) {
            pattern1 = "LELELLELSSA";
            lines = 2;
        }
        String[] splitLatin = Latin.split(" ");
        word[] wordsArr = new word[splitLatin.length];
        for (int i = 0; i < splitLatin.length; i++) {
            wordsArr[i] = new word(splitLatin[i]);
        }
        words = new LinkedList<>(Arrays.asList(wordsArr));
        matchPattern(words, pattern1, new ArrayList<>(), result);
    }
    public void getLine() {
        for (List<word> combination : result) {
            for (word wrd : combination) {
                System.out.println(wrd.getWord()+" ");
            }
            System.out.println("-----------------------");
        }
    }
    public void getLine(Boolean ye) {
        for (word wrd : words) {
            System.out.println(wrd.toString());
        }
    }

    public void matchPattern(List<word> words, String bigPattern, List<word> current, List<List<word>> result) {
        //System.out.println("New call of Match Pattern. State vars:\nbigPattern: " + bigPattern + "\nCurrent: " + current);
        if (bigPattern.isEmpty()) {
            result.add(new ArrayList<>(current));
            return;
        }

        List<word> wordsCopy = new ArrayList<>(words); // Create a copy for safe iteration

        for (word word : wordsCopy) {
            //System.out.println(word.getWord());
            String wordPattern = word.getScan();
            boolean ellided = false;

            if ("aeiou".contains(word.getWord().substring(word.getWord().length() - 1)) || "amemimomum".contains(word.getWord().substring(word.getWord().length() - 2))) { //Last letter is a vowel.
                if (bigPattern.length() >= wordPattern.length()) {
                    for (word nextWord : wordsCopy) {
                        if (nextWord == word) continue;
                        if ("aeiou".contains(nextWord.getWord().substring(0, 1)) || ("aeiou".contains(nextWord.getWord().substring(1, 2)) && nextWord.getWord().charAt(0) == 'h')) { //If the next word starts with a vowel.
                            String resolvedPattern = wordPattern.substring(0, wordPattern.length() - 1);
                            if (startsWith(resolvedPattern + nextWord.getScan(), bigPattern)) {
                                bigPattern = resolvePattern(resolvedPattern + nextWord.getScan(), bigPattern, true);
                                ellided = true;
                                current.add(new word(word.getWord(), resolvedPattern));
                                current.add(new word(nextWord.getWord(), nextWord.getScan()));
                                words.remove(word);
                                words.remove(nextWord);
                                matchPattern(words, bigPattern.substring((resolvedPattern+nextWord.getScan()).length()), current, result);
                                current.remove(current.size() - 1);
                                current.remove(current.size() - 1);
                                break;
                            }
                        }
                    }
                }
            } 

            if (wordPattern.endsWith("?") && !ellided && startsWith(wordPattern, bigPattern, true)) {
                exploreUnknowns(word, bigPattern, wordsCopy, current);
            } else if (!ellided) {
                if (startsWith(wordPattern, bigPattern)) {
                    bigPattern = resolvePattern(wordPattern, bigPattern, true);
                    current.add(word);
                    words.remove(word);
                    matchPattern(words, bigPattern.substring(wordPattern.length()), current, result);
                    current.remove(current.size() - 1);
                }
            }
        }
    }

    public void exploreUnknowns(word fword, String bigPattern, List<word> wordsCopy, List<word> current) {
        String nonEditedBigPattern = bigPattern;
        String patToRet = bigPattern;
        boolean end = false;
        String twordPattern = fword.getScan();
        List<word> wordsList = new ArrayList<>();
        wordsList.add(fword);
        //System.out.println(wordsList.get(0).getWord());

        while (nonEditedBigPattern.length() >= twordPattern.length() && end == false) {
            //System.out.println("0. In exploreUnknowns while-statement. Current word: "+wordsList.get(wordsList.size()-1)+" Current wordsList: "+ wordsList);
            twordPattern = "";
            word word = wordsList.get(wordsList.size()-1);
            String wordPattern = word.getScan();
            boolean foundNextWord = false;
            boolean lastWord = true;
            for (word nextWord : wordsCopy) {
                if (!wordsList.contains(nextWord)) {
                    lastWord = false;
                }
            }
            if (lastWord) {
                /**bigPattern = resolvePattern(resolvedPattern + nextWord.getScan(), bigPattern, true);
                word.setScan(resolvedPattern); */
                foundNextWord = true;
                for (word wrd : wordsList) {
                    current.add(new word(wrd.getWord(), wrd.getScan()));
                    words.remove(wrd);
                    twordPattern += wrd.getScan();
                }
                String totalPat = "";
                totalPat = twordPattern; //Edit later if need be.
                patToRet = resolvePattern(totalPat, patToRet, true);
                end = true;
                //System.out.println("5.Words coming out of '?' fixer: " + current);
                //System.out.println("totalPat: "+totalPat + " BigPattern before substring: "+bigPattern);
                matchPattern(words, patToRet.substring(totalPat.length()), current, result);
            }

            for (word nextWord : wordsCopy) {
                if (wordsList.contains(nextWord)) continue;
                //System.out.println("Possible next word: " + nextWord);
                char resolvedChar = resolveUnknowns(word, nextWord);
                String resolvedPattern = wordPattern.substring(0, wordPattern.length() - 1) + resolvedChar;
                //System.out.println("1.Word: " +word.getWord() + " Scans: " + word.getScan() + " resolved Char: " + resolvedChar);
                //System.out.println("2.Testing:" + word.getWord() + " with nextWord: "+nextWord.getWord()+" resPat: "+resolvedPattern);
                if ("aeiou".contains(word.getWord().substring(word.getWord().length()-1)) && "aeiou".contains(nextWord.getWord().substring(0,1))) {
                    resolvedPattern = wordPattern.substring(0,wordPattern.length()-1);
                }
                //System.out.println("3.About to test if it works: "+(resolvedPattern + nextWord.getScan()) + " big Pattern: " + bigPattern);
                if (nextWord.getScan().contains("?") && startsWith(resolvedPattern + nextWord.getScan(), bigPattern, true)) {
                    bigPattern = resolvePattern(resolvedPattern + nextWord.getScan(), bigPattern, true);
                    word.setScan(resolvedPattern);
                    //System.out.print("4.In mediator adder. Current word: " + word.getWord() + "Adding a word: "+nextWord.getWord()+" with a scan: "+nextWord.getScan());
                    wordsList.add(nextWord);
                    bigPattern = bigPattern.substring(wordPattern.length());
                    //System.out.println("bigPattern: " + bigPattern);
                    foundNextWord = true;
                    break;
                } else if (((! nextWord.getScan().contains("?")) && startsWith(resolvedPattern + nextWord.getScan(), bigPattern)) || (nextWord.getScan().length() == bigPattern.length() && startsWith(resolvedPattern + nextWord.getScan(), bigPattern))) {
                    //System.out.println("4.In resolving adder. Current word: " + word.getWord() + " Adding a word: "+nextWord.getWord()+" with a scan: "+nextWord.getScan());
                    bigPattern = resolvePattern(resolvedPattern + nextWord.getScan(), bigPattern, true);
                    word.setScan(resolvedPattern);
                    wordsList.add(nextWord);
                    foundNextWord = true;
                    for (word wrd : wordsList) {
                        current.add(new word(wrd.getWord(), wrd.getScan()));
                        words.remove(wrd);
                        twordPattern += wrd.getScan();
                    }
                    String totalPat = "";
                    /*for (int i = 0; i < twordPattern.length(); i++) {
                        if ((twordPattern.charAt(i)=='S')&& (twordPattern.charAt(i+1)=='S')) {
                            totalPat+="E";
                            i++;
                        } else {
                            totalPat += twordPattern.substring(i,i+1);
                        }
                    }*/
                    totalPat = twordPattern; //Edit later if need be.
                    patToRet = resolvePattern(totalPat, patToRet, true);
                    end = true;
                    //System.out.println("5.Words coming out of '?' fixer: " + current);
                    //System.out.println("totalPat: "+totalPat + " BigPattern before substring: "+bigPattern);
                    matchPattern(words, patToRet.substring(totalPat.length()), current, result);
                    for (word wrd : wordsList) {
                        current.remove(current.size() -1);
                    }
                    break;
                }
            }
            if (!foundNextWord) {
                break;
            }
            for (word wrd : wordsList) {
                twordPattern += wrd.getScan();
            }
        }
    }

    public boolean startsWith(String toCompare, String bigPattern, boolean unk) { //Ignores final '?'.
        //System.out.println("In the check '?' varifier.");
        int scanCounter = 0;
        for (int i = 0; i < toCompare.length()-1; i++) {
            if (i < bigPattern.length() && scanCounter < toCompare.length()-1) {
                if (bigPattern.charAt(i) == toCompare.charAt(scanCounter)) {
                    scanCounter += 1;
                    continue;
                } else if (bigPattern.charAt(i) == 'E') {
                    if (toCompare.charAt(scanCounter) == 'L') {
                        scanCounter += 1;
                        continue;
                    } else if (toCompare.charAt(scanCounter) == 'S' && toCompare.charAt(scanCounter + 1) == 'S') {
                        bigPattern = bigPattern.substring(0,i) + "SS"+ bigPattern.substring(i+1);

                        scanCounter += 2;
                        i+=1;
                        continue;
                    } else {
                        return false;
                    }
                } else if (bigPattern.charAt(i) == 'S' && toCompare.charAt(scanCounter) == 'S') {
                    scanCounter += 1;
                    continue;
                } else if (bigPattern.charAt(i) == 'A') {
                    scanCounter += 1;
                    continue;
                } else {
                    return false;
                }
            }
        }
        //System.out.println("In startsWith tester. toCompare: " + toCompare + " bigPattern: " + bigPattern + "returns: " + true);
        return true;
    }

    public String resolvePattern(String toCompare, String bPattern, boolean unk) { //Ignores final '?'.
    int scanCounter = 0;
    for (int i = 0; i < toCompare.length()-1; i++) {
        if (i < bPattern.length() && scanCounter < toCompare.length()-1) {
            if (bPattern.charAt(i) == toCompare.charAt(scanCounter)) {
                scanCounter += 1;
                continue;
            } else if (bPattern.charAt(i) == 'E') {
                if (toCompare.charAt(scanCounter) == 'L') {
                    scanCounter += 1;
                    continue;
                } else if (toCompare.charAt(scanCounter) == 'S' && toCompare.charAt(scanCounter + 1) == 'S') {
                    bPattern = bPattern.substring(0,i) + "SS"+ bPattern.substring(i+1);
                    scanCounter += 2;
                    i+=1;
                    continue;
                } else {
                    continue;
                }
            } else if (bPattern.charAt(i) == 'A') {
                scanCounter += 1;
                continue;
            } else {
                continue;
            }
        }
    }
    return bPattern;
}

    public boolean startsWith(String toCompare, String bigPattern) {
        int scanCounter = 0;
        for (int i = 0; i < toCompare.length(); i++) {
            if (i < bigPattern.length() && scanCounter < toCompare.length()) {
                if (bigPattern.charAt(i) == toCompare.charAt(scanCounter)) {
                    scanCounter += 1;
                    continue;
                } else if (bigPattern.charAt(i) == 'E') {
                    if (toCompare.charAt(scanCounter) == 'L') {
                        scanCounter += 1;
                        continue;
                    } else if (scanCounter+1<toCompare.length() && toCompare.charAt(scanCounter) == 'S' && toCompare.charAt(scanCounter + 1) == 'S') {
                        scanCounter += 2;
                        continue;
                    } else {
                        return false;
                    }
                } else if (bigPattern.charAt(i) == 'A') {
                    scanCounter += 1;
                    continue;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static char resolveUnknowns(word word, word nextWord) {
        String latin = word.getWord();
        boolean nextCons = isConsonant(nextWord.getWord().charAt(0));
        if (!nextCons) {
            return 'S';
        } else if (isConsonant(latin.charAt(latin.length() - 1))) {
            return 'L';
        } else if (isConsonant(nextWord.getWord().charAt(1))) {
            return 'L';
        }
        return 'S';
    }

    public static boolean isConsonant(char c) {
        c = Character.toLowerCase(c);
        return "qwrtyplkjgfdszxcvbnm".indexOf(c) != -1;
    }

    public List<List<word>> getResult() {
        return result;
    }
}
