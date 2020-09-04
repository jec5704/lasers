package lasers.ptui;

import lasers.model.LasersModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author RIT CS
 * @author Vedant Juneja
 * @author Julio Cuello
 */
public class ControllerPTUI  {
    /** The UI's connection to the lasers.lasers.model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     * @param model The laser model
     */
    public ControllerPTUI(LasersModel model) {
        this.model = model;
        this.model.createBoard();
    }

    /**
     * checking if exactly 2 coordinates are entered by the user
     * @param s input string
     * @return true if the number of coordinates is not 2
     */
    public boolean checkInputSize(String[] s) {
        return s.length != 3;
    }

    /**
     * Run the main loop.  This is the entry point for the controller
     * @param inputFile The name of the input command file, if specified
     */
    public void run(String inputFile) throws FileNotFoundException {
        Scanner scanner;
        boolean readingUserInput;
        if (inputFile == null){
            scanner = new Scanner(System.in);
            readingUserInput = true;
        }
        else {
            scanner = new Scanner(new File(inputFile));
            readingUserInput = false;
        }
        while (true){
            if(readingUserInput){
                System.out.print("> ");
            }
            if (!scanner.hasNextLine()){
                scanner = new Scanner(System.in);
                readingUserInput = true;
                continue;
            }

            String[] input = scanner.nextLine().split(" ");

            if(input[0].equals("") || input.equals(" ")){
                continue;
            }

            String command = input[0];
            switch (command.charAt(0)) {
                case 'q':
                    model.quit();
                case 'a':
                    if (checkInputSize(input)) {
                        System.out.println("Incorrect coordinates");
                        break;
                    }
                    int rowAdd = Integer.parseInt(input[1]);
                    int colAdd = Integer.parseInt(input[2]);
                    model.add(rowAdd, colAdd);
                    break;
                case 'd':
                    model.display();
                    break;
                case 'r':
                    if (checkInputSize(input)) {
                        System.out.println("Incorrect coordinates");
                        break;
                    }
                    int rowRemove = Integer.parseInt(input[1]);
                    int colRemove = Integer.parseInt(input[2]);
                    model.remove(rowRemove, colRemove);
                    break;
                case 'h':
                    model.helpMessage();
                    break;
                case 'v':
                    model.verify();
                    break;
                default:
                    System.out.println("Unrecognized command: " + command);
                    break;
            }
        }
    }
}
