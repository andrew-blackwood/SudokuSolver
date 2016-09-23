package sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Filename:    SudokuFileParser.java
 * Purpose:     Parse a sudoku file given to the Sudoku application
 *              and return it as a 2D array of integers.
 * 
 * @author      Andrew Blackwood
 * @version     1.0, 22/09/2016
 */
public class SudokuFileParser {
    
    /* --------------------------
       -- Sudoku File Settings --
       --------------------------*/
    private static final char FILE_DELIMITER = ',';
    
    /* Accepted characters in source file representing an empty square
     * (aside from empty character and 0, which are included by default).
     */
    private static final char[] FILE_CHARS_EMPTY_SQUARE = {'x','X'};
    
    /* --------------------------
       -- 9x9 Sudoku constants --
       --------------------------*/
    private static final int ROW_COUNT = 9;
    private static final int DELIMITER_COUNT = 8;
    
    private final File file;
    private ArrayList<String> fileLines;
    private int[][] grid;
    private boolean parsedLines = false;
    private boolean cleaned = false;
    private boolean validated = false;
       
    public SudokuFileParser(File file) {  
        this.file = file;
    }
    
    // Parse sudoku file lines
    private ArrayList<String> parseFileLines() {
                
        this.fileLines = new ArrayList<>();
        
        try {
            Scanner reader = new Scanner(this.file);
            while (reader.hasNextLine()){
                this.fileLines.add(reader.nextLine());
            }
        }
        catch (FileNotFoundException e){
            ErrorHandler error = new ErrorHandler();
            error.printAndExit("Error: Source file not found.", true, 1);
        } 
        
        this.parsedLines = true;
        return this.fileLines;
    }
    
    private void removeWhiteSpace() {
        for (int line = 0; line < this.fileLines.size(); line++) {
            this.fileLines.set(line, 
                    this.fileLines.get(line).replaceAll("\\s+",""));
        }
    } 
    
    private void removeEmptyLines() {
        this.fileLines.removeAll(Arrays.asList(""));
    }
    
    private void clean(){  
        
        if (!this.parsedLines) {
            this.parseFileLines();
        }  
        
        this.removeWhiteSpace();
        this.removeEmptyLines();
        
        this.cleaned = true;
    }
    
    private void validateLineCount() {   
        if (this.fileLines.size() != ROW_COUNT){
            ErrorHandler error = new ErrorHandler();
            error.printAndExit("Error: Too many non-empty rows"
                    + " in source file.", true, 0);
        }
    }
    
    private int countChars(String string, char value) {
      
        int count = 0;
        
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == value) {
                count++;
            } 
        }
        
        return count;
    }
    
    private void validateDelimiterCounts() {       
        for (int l = 1; l <= ROW_COUNT; l++) {
            if (this.countChars(this.fileLines.get(l-1),FILE_DELIMITER) 
                    != ROW_COUNT - 1) {
                ErrorHandler error = new ErrorHandler();
                error.printAndExit("Error: Wrong number of delimiters"
                        + " on line " + l, true, 0);
            }   
        }
    }
    
    private String getLinePattern() {
        
        String regexEmptyChars = "";
        
        if (FILE_CHARS_EMPTY_SQUARE.length > 0) {
       
            regexEmptyChars = Character.toString(FILE_CHARS_EMPTY_SQUARE[0]);
            
            if (FILE_CHARS_EMPTY_SQUARE.length > 1) {            
                for (int c = 1; c < FILE_CHARS_EMPTY_SQUARE.length; c++) {
                    regexEmptyChars = regexEmptyChars + "|" 
                            + FILE_CHARS_EMPTY_SQUARE[c];
                }   
            }    
        }
      
        String pattern = "(([0-9]|" + regexEmptyChars + ")?" + FILE_DELIMITER 
                + "){" + DELIMITER_COUNT + "}?([0-9]|" + regexEmptyChars + ")?";
                
        return pattern;
    }
    
    private void validateLinePattern() {
        
        String validPattern = getLinePattern();
        
        for (int line = 1; line <= ROW_COUNT; line++){            
            if (!this.fileLines.get(line-1).matches(validPattern)){
                ErrorHandler error = new ErrorHandler();
                error.printAndExit("Error: Issue with source file"
                        + " on line " + line, true, 0);
            }
        }    
    }
        
    private void validate() {
        
        if (!this.cleaned) {
           this.clean(); 
        }
        
        this.validateLineCount();
        this.validateDelimiterCounts();
        this.validateLinePattern();
        
        this.validated = true;
    }
    
    private boolean isEmptySquareChar(char c) {
        
        for (char test : FILE_CHARS_EMPTY_SQUARE) {
            if (c == test) {
                return true;
            }
        }
        
        return c == '0';
    }
    
    public int[][] parseToGrid() {
        
        if (!this.validated) {
            this.validate();
        }
        
        this.grid = new int[ROW_COUNT][ROW_COUNT];
        
        for (int i = 1; i <= ROW_COUNT; i++) {
            
            String line = this.fileLines.get(i-1);
            int col = 0;
            boolean lastWasDelimiter = true;
            
            for (int j = 1; j <= line.length(); j++) {
                
                char c = line.charAt(j-1);
                
                if (c != FILE_DELIMITER) {                     
                    
                    if (isEmptySquareChar(c)) {
                        this.grid[i-1][col] = 0;
                    }
                    else {  
                        this.grid[i-1][col] = Character.getNumericValue(c);
                    }  
                    
                    col++;
                    lastWasDelimiter = false;
                }
                else {      
                    if (lastWasDelimiter) {
                        this.grid[i-1][col] = 0;
                        col++;
                    }
                    else {
                        lastWasDelimiter = true;
                    }    
                }
            }
        }
        
        return this.grid;
    }
}
