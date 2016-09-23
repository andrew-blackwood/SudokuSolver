package sudoku;

/**
 * Filename:    SudokuSolverRunnable.java
 * Purpose:     A runnable to be run by a sudoku solver thread.
 * 
 * @author      Andrew Blackwood
 * @version     1.0, 22/09/2016
 */
public class SudokuSolverRunnable implements Runnable {
  
    private final SudokuHandler sudokuHandler;
    private final int row;
    private final int col;
    private final int threadValue;
    
    public SudokuSolverRunnable(SudokuHandler sudokuHandler, int row, int col, 
                                int threadValue){
        this.sudokuHandler = sudokuHandler;
        this.row = row;
        this.col = col;
        this.threadValue = threadValue;
    }

    @Override
    public void run() {
        this.sudokuHandler.solveFromCellMT(this.row, this.col, 
                                           this.threadValue, true);
    }
}
