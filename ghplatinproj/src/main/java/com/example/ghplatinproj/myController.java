package com.example.ghplatinproj;

import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class myController {

    @PostMapping("/processInput")
    public ResponseEntity<String> processInput(@RequestBody InputData inputData) {
        try {
            String input = inputData.getInput();
            String dropdownValue = inputData.getDropdownValue();
    
            // Validate input
            if (input == null || input.trim().isEmpty() || input.length() > 100) {
                return ResponseEntity.badRequest().body("Invalid input");
            }
    
            String output = runYourJavaPrograms(input, dropdownValue);
            return ResponseEntity.ok(output);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    private String runYourJavaPrograms(String inputLatin, String inputMeter) {
        System.out.println(inputLatin + inputMeter);
        Meters newMeter = new Meters(inputLatin, inputMeter);

        List<List<word>> result = newMeter.getResult();
        String toret = "Sorry. No combinations found.";
        if (!result.isEmpty()) {
            toret= "";
        }
        for (List<word> combination : result) {
            for (word wrd : combination) {
                toret += wrd.getWord() + " ";
            }
            toret += "\n";
        }
        System.out.println("Evaluated java programs.");
        newMeter.getLine();
        return toret;
    }

    static class InputData {
        private String input;
        private String dropdownValue;

        // Getters and setters
        public String getInput() { return input; }
        //public void setInput(String input) { this.input = input; }
        public String getDropdownValue() { return dropdownValue; }
        //public void setDropdownValue(String dropdownValue) { this.dropdownValue = dropdownValue; }
    }
}
