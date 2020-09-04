package lasers.model;

import lasers.backtracking.Backtracker;
import lasers.backtracking.Configuration;
import lasers.backtracking.SafeConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The model of the lasers safe.  You are free to change this class however
 * you wish, but it should still follow the MVC architecture.
 *
 * @author RIT CS
 * @author Vedant Juneja
 * @author Julio Cuello
 */
public class LasersModel {
    /** the safe */
    private String[][] board;
    /** the row dimensions of the safe */
    private int ROW_DIM;
    /** the column dimensions of the safe */
    private int COLUMN_DIM;
    /** scanner to read the file */
    private Scanner scanner;
    /** the observers who are registered with this model */
    private List<Observer<LasersModel, ModelData>> observers;
    /** HashSet of the list of numbered pillars */
    HashSet<String> numbers = new HashSet<>(Arrays.asList("0", "1", "2", "3", "4"));

    /**
     * constructor for the LasersModel that reads through a file at first
     * @param filename: filename of the safe that we will use
     * @throws FileNotFoundException if file doesn't exist
     */
    public LasersModel(String filename) throws FileNotFoundException {
        this.observers = new LinkedList<>();
        scanner = new Scanner(new File(filename));

        String[] dims = scanner.nextLine().split(" ");
        this.ROW_DIM = Integer.parseInt(dims[0]);
        this.COLUMN_DIM = Integer.parseInt(dims[1]);

        this.board = new String[ROW_DIM][COLUMN_DIM];
    }

    /**
     * second constructor that is used when creating a new  safeConfig
     * @param board: board that the model will have
     * @param oldModel: model from the parent of the safeConfig creating this model
     */
    public LasersModel(String[][] board, LasersModel oldModel){
        this.board = board;
        this.ROW_DIM = oldModel.ROW_DIM;
        this.COLUMN_DIM= oldModel.COLUMN_DIM;
        this.observers = new LinkedList<>(oldModel.observers);

    }

    /**
     * Add a new observer.
     *
     * @param observer the new observer
     */
    public void addObserver(Observer<LasersModel, ModelData > observer) {
        this.observers.add(observer);
    }

    /**
     * Notify observers the model has changed.
     *
     * @param data optional data the model can send to the view
     */
    private void notifyObservers(ModelData data){
        for (Observer<LasersModel, ModelData> observer: observers) {
            observer.update(this, data);
        }
    }
    /**
     * Displaying the help message
     *
     * @return a string of the help message
     */
    public void helpMessage() {
        StringBuilder str = new StringBuilder();
        str.append("a|add r c: Add laser to (r,c)");
        str.append("\n");
        str.append("d|display: Display safe");
        str.append("\n");
        str.append("h|help: Print this help message");
        str.append("\n");
        str.append("q|quit: Exit program");
        str.append("\n");
        str.append("r|remove r c: Remove laser from (r,c)");
        str.append("\n");
        str.append("v|verify: Verify safe correctness");
        notifyObservers(new ModelData(ModelData.Action.HELP,str.toString(),-19,-19));
    }
    /**
     * Create the board.
     */
    public void createBoard() {
        StringBuilder boardReader = new StringBuilder();
        for (int i = 0; i < ROW_DIM; i++) {
            String line = scanner.nextLine();
            boardReader.append(line);
            boardReader.append(" ");
        }

        String[] boardArray = boardReader.toString().split(" ");
        int row = 0;
        for (int i = 0; i < boardArray.length; i++) {
            if (i % COLUMN_DIM == 0 && i != 0) {
                row++;
            }
            board[row][i % COLUMN_DIM] = boardArray[i];
        }

        // let the view know it can start displaying the board
        notifyObservers(new ModelData(ModelData.Action.CREATEBOARD,"Board created"));
    }

    /**
     * method that notifies the view that the user called for the display method
     */
    public void display(){notifyObservers(new ModelData(ModelData.Action.DISPLAY,null,-1,-1));}

    /**
     * method that notifies the view the user wants to quit the game
     */
    public void quit(){notifyObservers(new ModelData(ModelData.Action.QUIT,"quit",-30,-30));}

    /**
     * setter for the board
     * @param setter: board you want this board to be equal to
     */
    public void setBoard(String[][] setter){
        board = setter;
    }

    /**
     * method that gets a value at a certain coordinate
     * @param row: row of the value we want to know
     * @param col: column of the value we want to know
     * @return: value of the tile
     */
    public String getValue(int row, int col){
        return board[row][col];
    }

    /**
     * getter for the Column dimensions of the board
     * @return: column dimension of the board
     */
    public int getCOLUMN_DIM() {
        return COLUMN_DIM;
    }

    /**
     * getter for the row dimensions of the board
     * @return: row dimensions
     */
    public int getROW_DIM() {
        return ROW_DIM;
    }

    /**
     * get for the set that stores the different values of the pillars
     * @return a hashSet that has all numbered pillars
     */
    public HashSet<String> getNumbers() {
        return numbers;
    }

    /**
     * helper method for both the add and remove methods
     *
     * @param row: row to start changing tile values from
     * @param col: column to start changing tile values from
     * @param oldValue: value to change
     * @param newValue: new value for the tile
     */
    public void tileChanger(int row, int col, String oldValue, String newValue) {
        for (int c = col + 1; c < COLUMN_DIM; c++) {
            if (board[row][c].equals(oldValue) || board[row][c].equals(newValue)) {
                board[row][c] = newValue;
            } else {
                break;
            }
        }
        for (int c = col - 1; c >= 0; c--) {
            if (board[row][c].equals(oldValue) || board[row][c].equals(newValue)) {
                board[row][c] = newValue;
            } else {
                break;
            }
        }
        for (int r = row + 1; r < ROW_DIM; r++) {
            if (board[r][col].equals(oldValue) || board[r][col].equals(newValue)) {
                board[r][col] = newValue;
            } else {
                break;
            }
        }
        for (int r = row - 1; r >= 0; r--) {
            if (board[r][col].equals(oldValue) || board[r][col].equals(newValue)) {
                board[r][col] = newValue;
            } else {
                break;
            }
        }
    }
    /**
     * this method verifies if all tiles are covered
     * correctly on the safe board
     *
     * @return a boolean denoting if verified or not
     */
    public boolean verify() {
        for (int i = 0; i < ROW_DIM; i++) {
            for (int j = 0; j < COLUMN_DIM; j++) {
                if (board[i][j].equals(".")) {
                    sendError(i, j);
                    return false;
                }
                if (numbers.contains(board[i][j])) {
                    if (Integer.parseInt(board[i][j]) != getAdjoiningLasers(i, j)) {
                        sendError(i, j);
                        return false;
                    }
                }
                if (!laserChecker(i, j)) {
                    return false;
                }
            }
        }
        notifyObservers(new ModelData(ModelData.Action.VERIFY,"Safe is fully verified!",-7,-7));
        return true;
    }
    /**
     * this is a helper method for the verify method
     * @param row: row where the pillar who's lasers we are checking is
     * @param col: column where the pillar who's lasers we are checking is
     * @return: counter of how many lasers the pillar has adjacent to it
     */
    public int getAdjoiningLasers(int row, int col) {
        int counter = 0;
        if ((row - 1) >= 0) {
            if (board[row - 1][col].equals("L")) {
                counter++;
            }
        }
        if ((row + 1) < ROW_DIM) {
            if (board[row + 1][col].equals("L")) {
                counter++;
            }
        }
        if ((col + 1) < COLUMN_DIM) {
            if (board[row][col + 1].equals("L")) {
                counter++;
            }
        }
        if ((col - 1) >= 0) {
            if (board[row][col - 1].equals("L")) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * this is a helper function for the verify method that prints the error statements
     *
     * @param i: row where error occurred
     * @param j: column where error occurred
     */
    public void sendError(int i, int j) {
        notifyObservers(new ModelData(ModelData.Action.VERIFYERROR,"Error verifying at  (" + i + ", " + j + ")",i,j));
    }
    /**
     * this method is a helper function for the verify that makes sure no lasers point at each other
     * @param i: row for the laser we are checking
     * @param j: column for the laser we are checking
     * @return: true if there are no lasers pointing to other lasers
     */
    public boolean laserChecker(int i, int j) {
        if (board[i][j].equals("L")) {
            for (int c = j + 1; c < COLUMN_DIM; c++) {
                if (board[i][c].equals("L") && c != j) {
                    sendError(i, j);
                    return false;
                } else if (!board[i][c].equals(".") && !board[i][c].equals("*")) {
                    break;
                }
            }
            for (int c = j - 1; c >= 0; c--) {
                if (board[i][c].equals("L") && c != j) {
                    sendError(i, j);
                    return false;
                } else if (!board[i][c].equals(".") && !board[i][c].equals("*")) {
                    break;
                }
            }
            for (int r = i + 1; r < ROW_DIM; r++) {
                if (board[r][j].equals("L") && r != i) {
                    sendError(i, j);
                    return false;
                } else if (!board[r][j].equals(".") && !board[r][j].equals("*")) {
                    break;
                }
            }
            for (int r = i - 1; r >= 0; r--) {
                if (board[r][j].equals("L") && r != i) {
                    sendError(i, j);
                    return false;
                } else if (!board[r][j].equals(".") && !board[r][j].equals("*")) {
                    break;
                }
            }
        }
        return true;
    }

    /**
     * checking if coordinates entered are in range of the board
     * @param row x coordinate
     * @param col y coordinate
     * @return true if coordinates are out of range
     */
    public boolean checkCoordinateRange(int row, int col) {
        return (row < 0 || row >= getROW_DIM() || col < 0 || col >= getCOLUMN_DIM());
    }

    /**
     * getter for the board 2d array
     * @return: 2d array
     */

    public String[][] getBoard(){return this.board;}
    /**
     * method for adding a laser to the board
     *
     * @param row x coordinate
     * @param col x coordinate
     */
    public void add(int row, int col) {
        if (checkCoordinateRange(row, col)) {
            notifyObservers(new ModelData(ModelData.Action.ADD,"Error adding laser at (" + row + ", " + col + ")",row,col));
        }
        else if (board[row][col].equals(".") || board[row][col].equals("*")) {
            tileChanger(row, col, ".", "*");
            board[row][col] = "L";
            notifyObservers(new ModelData(ModelData.Action.ADD,"Laser added at ("+row+", " +col+")",row,col));
        }
        else notifyObservers(new ModelData(ModelData.Action.ADD,"Error adding laser at (" + row + ", " + col + ")",row,col));

    }

    /**
     * method to solve the safe in the GUI when the "Solve" button is clicked
     *
     * @param filename name of the file being read to create the safe
     */
    public void solve(String filename){
        Backtracker bt = new Backtracker(false);
        Configuration init = null;

        try {
            init = new SafeConfig(filename);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        Optional<Configuration> sol = bt.solve(init);
        SafeConfig config = (SafeConfig) sol.get();
        setBoard(config.getBoard());
        notifyObservers(new ModelData(ModelData.Action.SOLVE, filename + " solved!"));
    }

    /**
     * method for removing a laser to the board
     *
     * @param row x coordinate
     * @param col y coordinate
     */
    public void remove(int row, int col) {
        if (checkCoordinateRange(row, col)) {
            notifyObservers(new ModelData(ModelData.Action.REMOVE,"Error removing laser at (" + row + ", " + col + ")",row,col));
        }
        else if (board[row][col].equals("L")) {
            tileChanger(row, col, "*", ".");
            board[row][col] = ".";
            refreshBoard();
            notifyObservers(new ModelData(ModelData.Action.REMOVE,"Laser removed at (" + row + ", " + col + ")",row,col));
        }
        else notifyObservers(new ModelData(ModelData.Action.REMOVE,"Error removing laser at (" + row + ", " + col + ")",row,col));
    }

    /**
     * method for activating all lasers again when a laser is removed
     */
    public void refreshBoard() {
        for (int row = 0; row < ROW_DIM; row++) {
            for (int col = 0; col < COLUMN_DIM; col++) {
                if (board[row][col].equals("L")) {
                    tileChanger(row,col,".","*");
                }
            }
        }
    }

    /**
     * the String representation for the safe
     *
     * @return: string representing
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("  ");
        // build the column number
        for (int col = 0; col < this.COLUMN_DIM; ++col) {
            str.append(col%10);
            str.append(" ");
        }
        str.append("\n");
        str.append("  ");
        for (int col = 0; col < this.COLUMN_DIM; ++col) {
            if(col == COLUMN_DIM-1){
                str.append("-");
                break;
            }
            str.append("--");
        }
        str.append("\n");
        // build the rows with number and values
        for (int row = 0; row < this.ROW_DIM; ++row) {
            str.append(row%10).append("|");
            for (int col = 0; col < this.COLUMN_DIM; ++col) {
                str.append(this.board[row][col]);
                str.append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    }

    /**
     * It goes through all configurations that are returned by SafeSolverConfig
     * and sets the current model equal to the model of the next config where the
     * number of lasers is one more than that of the current configuration
     *
     * @param filename name of the file
     * @return ext config where the number of lasers is one more
     * than that of the current configuration
     * @throws FileNotFoundException if file not found
     */
    public Configuration hint(String filename) throws FileNotFoundException {
        SafeConfig init = new SafeConfig(filename);
        Backtracker bt = new Backtracker(false);
        init.setModel(this);
        int num = init.getNumLasers();
        List<Configuration> path = bt.solveWithPath(init);
        if (path != null) {
            for (Configuration config : path) {
                if (((SafeConfig) config).getNumLasers() == num + 1) {
                    notifyObservers(new ModelData(ModelData.Action.HINT, "The next laser has been added as hint."));
                    return config;
                }
            }
        }
        notifyObservers(new ModelData(ModelData.Action.HINT, "there is no solution"));
        return null;
    }

}
