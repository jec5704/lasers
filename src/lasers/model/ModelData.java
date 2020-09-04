package lasers.model;

/**
 * Use this class to customize the data you wish to send from the model
 * to the view when the model changes state.
 *
 * @author RIT CS
 * @author Vedant Juneja
 * @author Julio Cuello
 */
public class ModelData {

    public enum Action {
        ADD,
        REMOVE,
        VERIFY,
        VERIFYERROR,
        QUIT,
        HELP,
        DISPLAY,
        CREATEBOARD,
        SOLVE,
        HINT
    }

    /** type of the tile */
    private final Action action;
    private String message;
    private int row;
    private int col;


    /**
     * Create a new update card.
     * @param message the message that the method sends
     * @param action type of the tile
     */
    public ModelData(Action action, String message) {
        this.message= message;
        this.action = action;
    }

    /**
     * second constructor for the actions that send back a row and a column
     * @param action: action or method performed
     * @param message: the message that the method sends to the user
     * @param row: row where the action happens
     * @param col: column where the action happens
     */
    public ModelData(Action action, String message, int row, int col){
        this.action = action;
        this.message = message;
        this.row = row;
        this.col = col;
    }
    /**
     * Get the row.
     *
     * @return the row
     */
    public String getMessage() { return this.message;}

    /**
     * getter for the row where an action happens
     * @return: row in the board
     */
    public int getRow(){return this.row;}

    /**
     * getter for the column where the action happens
     * @return: column of the action
     */
    public int getCol(){return this.col;}
    /**
     * Type of the tile
     *
     * @return enum tile
     */
    public Action getAction(){ return this.action; }
}
