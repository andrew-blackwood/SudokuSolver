package sudoku;

import java.io.File;

/**
 * Filename:    Sudoku.java
 * Purpose:     Implements an application that finds and prints
 *              all possible solutions to any 9x9 sudoku.
 * 
 * @author      Andrew Blackwood
 * @version     1.0, 22/09/2016
 */
public class Sudoku {
   
    public static void main(String[] args) {
                
        CommandLineParser commandLineParser = new CommandLineParser();
        boolean useMultithreading = commandLineParser.checkMultithreading(args);
        String filePath = commandLineParser.getFilePath(args);
        
        File sudokuFile = new File(filePath);
       
        SudokuFileParser sudokuFileParser = new SudokuFileParser(sudokuFile);
        int[][] grid = sudokuFileParser.parseToGrid();
        
        SudokuHandler sudokuHandler = new SudokuHandler(grid);
        sudokuHandler.solve(useMultithreading);
        sudokuHandler.printSolutions();
        
        System.exit(0);
    } 
}
