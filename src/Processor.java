/**
 * This is the processor for dealing with user input
 * includes separating inputs, query, add, remove, additional , update
 * generate result for DictionaryServer
 * Given Name: Jiali
 * Surname: Ying
 * Student ID: 1346717
 */

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processor {
    private Map<String, List<String>> dictionary;
    public Processor(Map<String, List<String>> dictionary) {
        this.dictionary = dictionary;
    }

    // separate command into word, command(old/new meaning)
    public String processCommand(String input){
        String[] parts = input.split(" ", 3);
        if (parts.length < 2) {
            return "Error: Invalid input format";
        }
        List<String> meaning = null;
        String newMeaning = null;
        String command = parts[0].toUpperCase();
        String word = parts[1].toLowerCase();
        String oldMeaning = null;

        if (command.equals("ADD") || command.equals("ADDITIONAL")) {
            // separate meanings
            List<String> meanings = extractMeanings(parts[2]);
            if (meanings == null) {
                return "Error: Invalid meaning format. Must be [\"meaning1\", \"meaning2\", ...]";
            } else {
                meaning = meanings;
            }
        } else if (command.equals("UPDATE")) {
            // get old meaning and new meaning
            String[] meaningParts = extractUpdateMeanings(parts[2]);
            if (meaningParts == null) {
                return "Error: Invalid update format. Must be: UPDATE word \"one old meaning\" \"one new meaning\"";
            } else {
                oldMeaning = meaningParts[0];
                newMeaning = meaningParts[1];
            }
        }

        switch (command) {
            case "QUERY":
                if (parts.length == 3){
                    return "Error: Invalid input format";
                }else {
                    return queryWord(word);
                }
            case "ADD":
                return addWord(word, meaning);
            case "REMOVE":
                if (parts.length == 3){
                    return "Error: Invalid input format";
                }else {
                    return removeWord(word);
                }
            case "ADDITIONAL":
                return additionalWord(word, meaning);
            case "UPDATE":
                return updateWord(word, oldMeaning, newMeaning);
            default:
                return "error: unknown command";
        }
    }

    // Find the meaning of an existing word
    public synchronized String queryWord(String word) {
        if(word == null || word.isEmpty()){
            return "No input word.";
        }
        if (dictionary.containsKey(word)) {
            return String.join(";", dictionary.get(word));
        } else {
            return "Word not found: " + word;
        }
    }
    // Add a non-existing word and meaning
    public synchronized String addWord(String word, List<String> meaning) {
        if(word == null || word.isEmpty()){
            return "No input word.";
        }
        else if (!dictionary.containsKey(word)) {
            if(meaning != null || !meaning.isEmpty()){
                // add word
                dictionary.put(word, new ArrayList<>(meaning));
                return "Word added successfully";
            }else{
                return "Error, no associated meaning";
            }
        } else {
            return "Word already in the dictionary: " + word;
        }
    }
    // remove an existing word
    public synchronized String removeWord(String word) {
        if(word == null || word.isEmpty()){
            return "No input word.";
        }
        else if (dictionary.containsKey(word)) {
            //remove word
            dictionary.remove(word);
            return "Word removed successfully";
        } else {
            return "Word does not exit: " + word;
        }
    }
    // add new meaning(s) to an existing word
    public synchronized String additionalWord(String word, List<String> meanings) {
        if(word == null || word.isEmpty()){
            return "Invalid input word.";
        }else if(meanings == null || meanings.isEmpty()){
            return "Invalid input meaning.";
        }
        else if (dictionary.containsKey(word)) {
            List<String> existingMeanings = new ArrayList<>(dictionary.get(word));
            StringBuilder result = new StringBuilder();
            for (String meaning: meanings){
                if(!existingMeanings.contains(meaning)){
                    // add to list
                    existingMeanings.add(meaning);
                    result.append("Meaning \"").append(meaning).append("\" added successfully\n");
                }else{
                    result.append("Meaning \"").append(meaning).append("\" already existed\n");
                }
            }
            dictionary.put(word, existingMeanings);
            return result.toString().replace("\n", "<br>");

        } else {
            return "Word not found: " + word;
        }
    }

    // update a meaning for an existing word with a new meaning
    public synchronized String updateWord(String word, String oldMeaning, String newMeaning) {
        if (!dictionary.containsKey(word)) return "Error: Word not found: " + word;

        List<String> meanings = dictionary.get(word);
        if (!meanings.contains(oldMeaning)) return "Error: Old meaning not found.";

        meanings.remove(oldMeaning);
        meanings.add(newMeaning);

        return "Meaning updated successfully.";
    }
    public static List<String> extractMeanings(String input) {
        List<String> meanings = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\\"(.*?)\\\"").matcher(input);
        while (matcher.find()) {
            meanings.add(matcher.group(1));
        }
        // nothing then return null
        return meanings.isEmpty() ? null : meanings;
    }

    public static String[] extractUpdateMeanings(String input) {
        Matcher matcher = Pattern.compile("\\\"(.*?)\\\"").matcher(input);
        List<String> meanings = new ArrayList<>();
        while (matcher.find()) {
            meanings.add(matcher.group(1));
        }
        return meanings.size() == 2 ? new String[]{meanings.get(0), meanings.get(1)} : null;
    }
}
