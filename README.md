# SudokuSolver
A Java application that finds all possible solutions to any 9x9 Sudoku puzzle.

## Preparing your Sudoku file
Convert your starting Sudoku grid that needs solving into a text file with the following formatting:
  * Each line of the file represents a different row of the Sudoku grid.
  * The squares on each row of the grid are separated by a comma in the file.
    * Note: Do NOT include a comma at the end of each line, otherwise the application will think there are 10 columns.
  * Each empty cell is represented in the file by either no character at all, space(s), an 'X' or 'x'.

## Running the application
* Open a command line in the 'dist' folder of the repository. 
* Execute the folliwing command:<br/>
  `Java –jar sudoku.jar –p n filename`
  * The parameter ***p*** is used to indicate to the application whether or not it should use multithreading to solve the Sudoku puzzle.  If included in the command, it should be followed by the parameter ***n***, where ***n*** should be 0 or 1 ('no' or 'yes', respectively).  If ***p*** and ***n*** are not included in the command, the application will not use multithreading.
  * The parameter ***filename*** should be the filename of your sudoku file.
