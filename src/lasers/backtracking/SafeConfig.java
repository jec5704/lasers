package lasers.backtracking;

import lasers.model.LasersModel;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the lasers.model
 * package and/or incorporate it into another class.
 *
 * @author RIT CS
 * @author Vedant Juneja
 * @author Julio Cuello
 */

public class SafeConfig implements Configuration {
    private LasersModel model;
    private int finalRow;
    private int finalCol;

    /**
     * private Safeconfig that is used for the first safeconfig
     * @param filename: string wth the safe file that we will read
     * @throws FileNotFoundException
     */
    public SafeConfig(String filename) throws FileNotFoundException {
        this.model = new LasersModel(filename);
        model.createBoard();
        finalRow = 0;
        finalCol = -1;
    }

    /**
     * the second safeConfig called by the successors
     * @param board: the board that corresponds to this successor
     * @param finalRow: the last row we checked
     * @param finalCol: last column we checked
     * @param oldModel: the model from the old safeConfig
     * @param laser: boolean that determines if the successor added a laser or not in order for the board tobe updated
     */

    private SafeConfig(String[][]board, int finalRow, int finalCol, LasersModel oldModel, boolean laser){
        this.finalRow = finalRow;
        this.finalCol = finalCol;
        this.model= new LasersModel(board, oldModel);
        if (laser){
            model.add(finalRow, finalCol);
        }
    }

    /**
     * this method uses an algorithm in order to get all the successors from a board
     * @return: a list of all the successors for this safeConfig
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        List<Configuration> successors = new ArrayList<>(2);
        int row = finalRow;
        int col = finalCol;
        col++;

        if(col == model.getCOLUMN_DIM()) {
            col = 0;
            row++;
        }

        if(row < model.getROW_DIM()) {
            String[][] copy = new String[model.getROW_DIM()][model.getCOLUMN_DIM()];
            String[][] laserCopy = new String[model.getROW_DIM()][model.getCOLUMN_DIM()];
            for(int i=0; i<model.getROW_DIM(); i++) {
                for (int j = 0; j < model.getCOLUMN_DIM(); j++) {
                    assert false;
                    copy[i][j] = model.getValue(i,j);
                    laserCopy[i][j] = model.getValue(i,j);
                }
            }

            if (model.getNumbers().contains(model.getValue(row, col))) {
                if (getEmptyTiles(row, col) != null) {
                    List<Integer> freeTiles = getEmptyTiles(row, col);
                    if (freeTiles.size() >= 2){
                        successors.add(new SafeConfig(laserCopy, freeTiles.get(0), freeTiles.get(1), model, true));
                    }
                }
            }

            if (laserCopy[row][col].equals(".")){
                successors.add(new SafeConfig(laserCopy, row, col, model, true));
            }

            successors.add(new SafeConfig(copy, row, col, model, false));
        }
        return successors;
    }

    /**
     * getter for the board from the config
     */
    public String[][] getBoard(){return model.getBoard();}

    /**
     * this method checks wether the current successor is on the right track towards a complete board
     * @return: true if the successor is valid and false if it's not
     */
    @Override
    public boolean isValid() {
        for (int i = 0; i < model.getROW_DIM(); i++) {
            for (int j = 0; j < model.getCOLUMN_DIM(); j++) {
                if(!model.laserChecker(i, j)){
                    return false;
                }
                if((model.getNumbers().contains(model.getValue(i,j)))){
                    if((model.getAdjoiningLasers(i,j) > Integer.parseInt(model.getValue(i,j)))){
                        return false;
                    }
                    if (getETiles(i,j) == 0 && model.getAdjoiningLasers(i,j) != Integer.parseInt(model.getValue(i,j))){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * the method that makes sure that the complete safe is a solution to the board
     *
     * @return if the configuration has reached its goal or not
     */
    @Override
    public boolean isGoal() {
        //return model.verify();
        if(finalRow != model.getROW_DIM()-1 && finalCol != model.getCOLUMN_DIM()-1){
            return false;
        }
        for (int i = 0; i < model.getROW_DIM(); i++) {
            for (int j = 0; j < model.getCOLUMN_DIM(); j++) {
                if (model.getValue(i,j).equals(".")){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * the String representation for the safeConfig
     *
     * @return: string representing
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("  ");
        // build the column number
        for (int col = 0; col < model.getCOLUMN_DIM(); ++col) {
            str.append(col%10);
            str.append(" ");
        }
        str.append("\n");
        str.append("  ");
        for (int col = 0; col < model.getCOLUMN_DIM(); ++col) {
            if(col == model.getCOLUMN_DIM()-1){
                str.append("-");
                break;
            }
            str.append("--");
        }
        str.append("\n");
        // build the rows with number and values
        for (int row = 0; row < model.getROW_DIM(); ++row) {
            str.append(row%10).append("|");
            for (int col = 0; col < model.getCOLUMN_DIM(); ++col) {
                str.append(model.getBoard()[row][col]);
                str.append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    }

    /**
     * method used for making sure the amount of lasers next to a numbers pillar is the right one
     * @param row: row where the pillar is
     * @param col: column of the pillar
     * @return: list of the coordinates where we will add a laser
     */
    public List<Integer> getEmptyTiles(int row, int col){
        int counter = 0;
        List<Integer> freeTiles = new ArrayList<>();
        if ((row - 1) >= 0) {
            if (model.getBoard()[row - 1][col].equals(".")) {
                counter++;
                freeTiles.add(row-1);
                freeTiles.add(col);
            }
        }
        if ((row + 1) < model.getROW_DIM()) {
            if (model.getBoard()[row + 1][col].equals(".")) {
                counter++;
                freeTiles.add(row+1);
                freeTiles.add(col);
            }
        }
        if ((col + 1) < model.getCOLUMN_DIM()) {
            if (model.getBoard()[row][col + 1].equals(".")) {
                counter++;
                freeTiles.add(row);
                freeTiles.add(col+1);
            }
        }
        if ((col - 1) >= 0) {
            if (model.getBoard()[row][col - 1].equals(".")) {
                counter++;
                freeTiles.add(row);
                freeTiles.add(col-1);
            }
        }

        if (counter == (Integer.parseInt(model.getValue(row, col)) - model.getAdjoiningLasers(row, col))){
            return freeTiles;
        }
        return null;
    }

    /**
     * method calculating empty tiles near a pillar
     * @param row: row where the pillar is
     * @param col: column of the pillar
     * @return: number of  empty tiles near a pillar
     */
    public int getETiles(int row, int col){
        int counter = 0;

        if ((row - 1) >= 0) {
            if (model.getBoard()[row - 1][col].equals(".")) {
                counter++;
            }
        }
        if ((row + 1) < model.getROW_DIM()) {
            if (model.getBoard()[row + 1][col].equals(".")) {
                counter++;
            }
        }
        if ((col + 1) < model.getCOLUMN_DIM()) {
            if (model.getBoard()[row][col + 1].equals(".")) {
                counter++;
            }
        }
        if ((col - 1) >= 0) {
            if (model.getBoard()[row][col - 1].equals(".")) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Setter method for the model
     * @param model model to be set
     */
    public void setModel(LasersModel model) {
        this.model = model;
    }

    /**
     * Setter method for model
     * @return the model
     */
    public LasersModel getModel(){
        return model;
    }

    /**
     * Getting number of lasers on the board
     *
     * @return number of lasers on the board
     */
    public int getNumLasers(){
        int c = 0;
        for (int i = 0; i < model.getROW_DIM(); i++) {
            for (int j = 0; j < model.getCOLUMN_DIM(); j++) {
                if (model.getValue(i,j).equals("L")){
                    c++;
                }
            }
        }
        return c;
    }
}
