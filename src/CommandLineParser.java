package sudoku;

/**
 * Filename:    CommandLineParser.java
 * Purpose:     Parse the command-line arguments given to the Sudoku
 *              application.
 * 
 * @author      Andrew Blackwood
 * @version     1.0, 22/09/2016
 */
public class CommandLineParser {
        
    public CommandLineParser() {}
    
    public boolean checkMultithreading(String[] args) {
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p") && i != args.length - 1) {
                switch (args[i+1]) {
                    case "0":
                        return false;
                    case "1":
                        return true;
                    default:
                        ErrorHandler error = new ErrorHandler();
                        error.printAndExit("Error: -p argument for"
                                + " multithreading requires subsequent argument"
                                + " of 0 for false or 1 for true.", true, 1);                                                                
                }
            }
        } 
        
        return false;
    }
    
    public String getFilePath(String[] args) {
        
        for (String arg : args) {
            if (arg.length() > 2) {
                return arg;
            }
        }
        
        ErrorHandler error = new ErrorHandler();
        error.printAndExit("Error: Sudoku file path not found in command line"
                + " arguments.", true, 1); 
        
        return "";
    }  
}
