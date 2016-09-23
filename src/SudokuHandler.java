package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Filename:    SudokuHandler.java
 * Purpose:     Find and print solutions to a given sudoku grid.
 * 
 * @author      Andrew Blackwood
 * @version     1.0, 22/09/2016
 */

public class SudokuHandler {
    
    private int[][] grid;
    private int[][] initialGrid;
    private ArrayList<int[][]> gridList;
    private boolean[][] initialClue = new boolean[9][9];
    private ArrayList<int[][]> solutions = new ArrayList<>();
    
    public SudokuHandler(int[][] grid) {
        
        this.grid = grid;
        
        if (this.grid.length != 9) {
            ErrorHandler error = new ErrorHandler();
            error.printAndExit("Unexpected Error: Parsed sudoku grid passed to"
                    + " SudokuHandler is not the correct size", true, -1);
        }
        
        for (int[] col : this.grid) {
            if (col.length != 9) {
                ErrorHandler error = new ErrorHandler();
                error.printAndExit("Unexpected Error: Parsed sudoku grid passed"
                    + " to SudokuHandler is not the correct size", true, -1);
            }
        }
    }
    
    public int[][] cloneIntGrid(int[][] original) {
        
        int[][] clone = new int[original.length][original.length];
        
        for (int i = 0; i < original.length; i++) {
            clone[i] = Arrays.copyOf(original[i], original[i].length);
        }
        
        return clone;
    }
    
    private boolean acceptClue(int row, int col, int val) {
        
        // Check row and column
        for (int i = 0; i < 9; i++ ) {
            if (this.grid[row][i] == val && i != col) {
                return false;
            }
            if (this.grid[i][col] == val && i != row) {
                return false;
            }
        }
        
        // Check box
        int boxStartRow = 3 * (row / 3);
        int boxStartCol = 3 * (col / 3);
        
        for (int i = boxStartRow; i < boxStartRow + 3; i++) {
            for (int j = boxStartCol; j < boxStartCol + 3; j++) {
		if (this.grid[i][j] == val && i!= row && i!= col) {
                    return false;
		}
            }
        }
        
        return true;
    }
    
    private void logAndValidateInitialClues() {   
        
        int clueCount = 0;
        
        for (int r = 0; r < this.initialGrid.length; r++) {
            for (int c = 0; c < this.initialGrid.length; c++) {
                if (this.initialGrid[r][c] != 0) {
                    this.initialClue[r][c] = true;
                    clueCount++;
                    if (!this.acceptClue(r, c, this.initialGrid[r][c])) {
                        ErrorHandler error = new ErrorHandler();
                        error.printAndExit("Error: Values in the initial"
                                + " grid are not valid.", true, 1);
                    }
                }
            }
        }  
        
        // Exit if there are not more than 16 clues given in initial grid
        if (clueCount < 17) {
            ErrorHandler error = new ErrorHandler();
            error.printAndExit("Error: Need minimum of 17 clues in the initial"
                    + " sudoku grid in order for this program to be able to"
                    + " solve the sudoku", true, 1);
        }
    }
    
    public boolean[][] cloneBooleanGrid(boolean[][] original) {
        
        boolean[][] clone = new boolean[original.length][original.length];
        
        for (int i = 0; i < original.length; i++) {
            clone[i] = Arrays.copyOf(original[i], original[i].length);
        }
        
        return clone;
    }
    
    public boolean isSolved(int[][] testGrid) {
        
        int count = 0;
        
        for (int[] col : testGrid) {
            for (int c = 0; c < testGrid.length; c++) {
                if (col[c] != 0) {
                    count++;
                }
            }       
        }
            
        return count == 81;
    }
    
    // Actions before attempting to solve sudoku
    private void preSolve() {
        
        // Save initial grid
        this.initialGrid = this.cloneIntGrid(this.grid);
        
        // Log locations of initial clues and check validity
        this.logAndValidateInitialClues();
        
        // Check not already a full solution
        if (isSolved(this.initialGrid)) {
            ErrorHandler error = new ErrorHandler();
            error.printAndExit("Source file sudoku grid is already solved.",
                               true, 1);
        }  
    }
    
    private int[] getFirstEmptyCell() {
        
        int[] emptyCell = new int[2];
        
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (!this.initialClue[r][c]) {
                    emptyCell[0] = r;
                    emptyCell[1] = c;
                    return emptyCell;
                }
            }
        }
        
        return emptyCell;   
    }

    private boolean acceptMT(int row, int col, int val, int threadValue) {
        
        int[][] thisGrid = this.gridList.get(threadValue-1);
                
        // Check row and column
        for (int i = 0; i < 9; i++ ) {
            if (thisGrid[row][i] == val || thisGrid[i][col] == val) {
                return false;
            }
        }
        
        // Check box
        int boxStartRow = 3 * (row / 3);
        int boxStartCol = 3 * (col / 3);
        
        for (int i = boxStartRow; i < boxStartRow + 3; i++) {
            for (int j = boxStartCol; j < boxStartCol + 3; j++) {
		if (thisGrid[i][j] == val) {
                    return false;
		}
            }
        }
        
        return true;
    }
    
    private int[][] makeNewSolution(int[][] grid) {
        
        int[][] solution = new int[9][9];
        
        for (int i = 0; i < grid.length; i++) {
            solution[i] = Arrays.copyOf(grid[i], grid[i].length);
        }
        
        return solution;
    }
    
    public void solveFromCellMT(int row, int col, int threadValue, 
                                                     boolean threadCall) {
                        
        if (row == 9) {          
            
            int[][] newSolution = makeNewSolution(
                    this.gridList.get(threadValue-1));
            this.solutions.add(newSolution);
            
            return;
        }
                               
        if (this.initialClue[row][col]) {
           if (col == 8) {
              solveFromCellMT(row + 1, 0, threadValue, false);
              return;
           }     
           solveFromCellMT(row, col + 1, threadValue, false);
           return;
        }
        
        int startValue;
        int endValue;
        
        if (threadCall) {
            startValue = threadValue;
            endValue = threadValue;
        }
        else {
            startValue = 1;
            endValue = 9;
        }
            
        for (int val = startValue; val <= endValue; val++) {        
            
            if (this.acceptMT(row, col, val, threadValue)) {

                this.gridList.get(threadValue-1)[row][col] = val;
                
                if (col == 8) {
                    solveFromCellMT(row + 1, 0, threadValue, false);
                }
                else {
                    solveFromCellMT(row, col + 1, threadValue, false);
                }  
            }
        }
        
        this.gridList.get(threadValue-1)[row][col] = 0;
    }
    
    public void waitForAllSolverThreads(ExecutorService executor) {
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                ErrorHandler error = new ErrorHandler();
                error.printAndExit("Error: Sudoku solver did not"
                        + " finish finding all solutions.", true, 1);
            }
        } 
        catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            ErrorHandler error = new ErrorHandler();
            error.printAndExit("Error: Sudoku solver did not"
                        + " finish finding all solutions.", true, 1);
        }  
    }
    
    private void solveUsingMultithreading() {
        
        // Set up a thread pool the same size as the processor count
        int processorCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processorCount);

        // Get the first empty cell
        int[] emptyCell = this.getFirstEmptyCell();
        int row = emptyCell[0];
        int col = emptyCell[1];

        // Set up a list to store grids for different solver threads
        this.gridList = new ArrayList<>();

        // Try solving for each value from the first empty cell
        for (int i = 1; i <= 9; i++) {

            int[][] newGrid = this.cloneIntGrid(this.grid);
            this.gridList.add(newGrid);

            SudokuSolverRunnable solverThread 
                    = new SudokuSolverRunnable(this, row, col, i);
            executor.execute(solverThread);
        }

        executor.shutdown();
        this.waitForAllSolverThreads(executor);
    }
    
    private boolean accept(int row, int col, int val) {
        
        // Check row and column
        for (int i = 0; i < 9; i++ ) {
            if (this.grid[row][i] == val || this.grid[i][col] == val) {
                return false;
            }
        }
        
        // Check box
        int boxStartRow = 3 * (row / 3);
        int boxStartCol = 3 * (col / 3);
        
        for (int i = boxStartRow; i < boxStartRow + 3; i++) {
            for (int j = boxStartCol; j < boxStartCol + 3; j++) {
		if (this.grid[i][j] == val) {
                    return false;
		}
            }
        }
        
        return true;
    }

    private void solveFromCell(int row, int col) {
                
        if (row == 9) {
                        
            int[][] newSolution = makeNewSolution(this.grid);
            this.solutions.add(newSolution);
            
            return;
        }
                               
        if (this.initialClue[row][col]) {
           if (col == 8) {
              solveFromCell(row + 1, 0);
              return;
           }   
           solveFromCell(row, col + 1);
           return;
        }
    
        for (int val = 1; val <= 9; val++) {  
            
            if (this.accept(row, col, val)) {

                this.grid[row][col] = val;
                
                if (col == 8) {
                    solveFromCell(row + 1, 0);
                }
                else {
                    solveFromCell(row, col + 1);
                }  
            }
        }
        
        this.grid[row][col] = 0;                                
    }    
    
    public void solve(boolean useMultithreading) {
                
        preSolve();
        
        System.out.println("Solving...\n");
        
        if (useMultithreading) {
            solveUsingMultithreading(); 
        }
        else {
            solveFromCell(0, 0);
        }    
    }
    
    public void printSolution(int[][] grid) {
        
        int gridSize = grid.length;
        
        for (int i = 0; i < gridSize; i++) {
            
            System.out.println();
            
            if (i == 3 || i == 6) {
                System.out.println("----------------------------");
            }

            for (int j = 0; j < gridSize; j++) {
                
                System.out.print(grid[i][j]);
                
                if (j == 2 || j == 5) {
                    System.out.print(" | ");
                }
                else {
                    System.out.print("  ");
                }
            }
            
            if (i != 2 && i != 5 && i != 8) {
                System.out.print("\n        |         |");
            }
        }
        
        System.out.println();
        
    }
       
    public void printSolutions() {
        
        if (solutions.isEmpty()) {
            System.out.println("No Solutions");
            return;
        }
        
        System.out.println("\nAll possible solutions:");
        
        for (int i = 1; i <= solutions.size(); i++) {
            System.out.println("\nSolution " + i + ":");
            System.out.println("-----------");
            printSolution(solutions.get(i - 1));
            System.out.println();
        }
        
        System.out.println("Successfully found all possible solutions.\n");
    }  
}
