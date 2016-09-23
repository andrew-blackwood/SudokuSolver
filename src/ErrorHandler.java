package sudoku;

/**
 * Filename:    ErrorHandler.java
 * Purpose:     Print error message and exit system.
 * 
 * @author      Andrew Blackwood
 * @version     1.0, 22/09/2016
 */
public class ErrorHandler {
        
    public ErrorHandler() {}
    
    public void printAndExit(String errorMessage, boolean exit, int exitParam) {
        
        System.out.println(errorMessage);
        
        if (exit && exitParam >= -1 && exitParam <= 1) {
            System.exit(exitParam);
        }
    }

    public void print(String errorMessage) {
        this.printAndExit(errorMessage, false, 0);
    }
} 