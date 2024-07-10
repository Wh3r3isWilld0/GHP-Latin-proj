package com.example.ghplatinproj;

public class word {
    private String word;
    private String wordWithLongs;
    private String scans;

    public word(String word) {
        this.word = word;
        word = word.toLowerCase();
        wordWithLongs = word;
        word = withoutChar("h");
        word = withoutChar("-");

        scans = markup(word);
        scans = addNaturals(wordWithLongs, scans);
    }

    public word(String word, String Scans) {
        this.word = word;
        scans = Scans;
    }

    public String withoutChar(String chr) {
        String nonhs = "";
        for (String letter : word.split("|")) {
            if (letter.equals(chr)) {
                continue;
            }
            nonhs+=letter;
        }
        return nonhs;
    }

    public String addNaturals(String word, String scans) {
        String toret = scans;
        String[] chars = word.split("|");
        int scanInt = -1;

        for (int i = 0; i < chars.length; i++) {
            if ("aeiou".contains(chars[i])) {
                scanInt+=1;
            } else if(chars[i].equals("-")) {
                if (scanInt > 0 && scanInt+1<toret.length()) {
                    toret = toret.substring(0,scanInt) + "L" + toret.substring(scanInt+1);
                } else if (scanInt+1<toret.length()) {
                    toret = "L" + toret.substring(scanInt+1);
                } else if (scanInt > 0) {
                    toret = toret.substring(0, scanInt) + "L";
                } else {
                    toret = "L";
                }
            }
        }
        return toret;
    }

    public String markup(String word) {
        String toret = "";
        String[] chars = word.split("|");
        String nextTwo;

        for (int i = 0; i < chars.length; i++) {
            if (i+2 < chars.length) {
                nextTwo = chars[i+1] + chars[i+2];
            } else {
                nextTwo = "end";
            }
            if ((chars[i].charAt(0)=='q' || chars[i].charAt(0)=='g')&& nextTwo.charAt(0)=='u') {
                chars[i] = "k";
                chars[i+1]="w";
                continue;
            }
            if ("aeiou".contains(chars[i])) { //Checks if letter is a vowel
                if (nextTwo.equals("end")) {
                    if (i+1<word.length() && ("aeiou".contains(word.substring(word.length()-1)))) {
                        if (isDip(chars[i]+chars[i+1])) {
                            toret+="L";
                        } else {
                            toret+="S";
                        }
                    } else {
                        toret += "?";
                    }
                } else if (chars[i].equals("i")) { //Checks for i cases
                    if (!("aeiou".contains(nextTwo.substring(0,1)) && i==0)) { //Checks if i isn't a j
                        toret += vowelLongCheck(chars[i], nextTwo);

                    }
                } else {
                    toret += vowelLongCheck(chars[i], nextTwo);

                }
                if (i+1<chars.length && isDip(chars[i]+chars[i+1])) {
                    i += 1;
                }
            }
        }
        return toret;
    }

    public String vowelLongCheck(String curr, String next) {
        if ("xz".contains(next.substring(0,1))) {
            return "L";
        }
        if("aeiou".contains(next.substring(0,1))) {
            if (isDip(curr+next.substring(0,1))) {
                return "L";
            }
            return "S"; 
        } 
        if ("aeiou".contains(next.substring(1))) {
            return "S"; //Next letter is a consanant, second is a vowel => Must be short. (Kinda ignoring long by nature right now)
        }
        return "L";
    }

    public boolean isDip(String pair) {  //Diphthong Test!
        String[] dips = new String[] {"ae", "au", "ei", "eu", "oe", "ui"};
        for (String dip : dips) {
            if (dip.equals(pair)) {
                return true;
            }
        }
        return false;
    }

    public String getWord() {
        return word;
    }

    public String getScan() {
        return scans;
    }

    public String toString() {
        return "Word: " + word + "\nScansion Markings: " + scans;
    }

    public void setScan(String newScan) {
        scans = newScan;
    }
}
