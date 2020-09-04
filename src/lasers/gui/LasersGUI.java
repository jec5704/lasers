package lasers.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import lasers.backtracking.Backtracker;
import lasers.backtracking.Configuration;
import lasers.backtracking.SafeConfig;
import lasers.backtracking.SafeSolver;
import lasers.model.*;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the lasers.lasers.model
 * and receives updates from it.
 *
 * @author RIT CS
 * @author Vedant Juneja
 * @author Julio Cuello
 */
public class LasersGUI extends Application implements Observer<LasersModel, ModelData> {
    /** The UI's connection to the lasers.lasers.model */
    private LasersModel model;
    private Button check;
    private Button hint;
    private Button solve;
    private Button restart;
    private Button load;
    private Button[][] buttonsArray;
    private final static int BUTTON_W = 75;
    private final static int BUTTON_L = 25;

    private String filename;
    /** this can be removed - it is used to demonstrates the button toggle */
    private static boolean status = true;
    private Label topLabel;

    /**
     * method that initializes the gui
     */
    @Override
    public void init() {
        // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        try {
            Parameters params = getParameters();
            filename = params.getRaw().get(0);
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
        model.createBoard();
    }

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param button the button control
     * @param bgImgName the name of the image file
     */
    private void setButtonBackground(Button button, String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image( getClass().getResource("resources/" + bgImgName).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    /**
     * This is a private demo method that shows how to create a button
     * and attach a foreground image with a background image that
     * toggles from yellow to red each time it is pressed.
     *
     * @param stage the stage to add components into
     */
    private void buttonDemo(Stage stage) {
        // this demonstrates how to create a button and attach a foreground and
        // background image to it.
        Button button = new Button();
        Image laserImg = new Image(getClass().getResourceAsStream("resources/laser.png"));
        ImageView laserIcon = new ImageView(laserImg);
        button.setGraphic(laserIcon);
        setButtonBackground(button, "yellow.png");
        button.setOnAction(e -> {
            // toggles background between yellow and red
            if (!status) {
                setButtonBackground(button, "yellow.png");
            } else {
                setButtonBackground(button, "red.png");
            }
            status = !status;
        });

        Scene scene = new Scene(button);
        stage.setScene(scene);
    }

    /**
     * method that makes the gridpane where the buttons will be
     * @return: gridpane of TileButtons
     */
    public GridPane makeGridPane(){
        buttonsArray = new TileButton[model.getROW_DIM()][model.getROW_DIM()];
        GridPane grid = new GridPane();
        grid.setPrefWidth(500);
        for (int row=0; row<model.getROW_DIM(); ++row) {
            for (int col=0; col<model.getCOLUMN_DIM(); ++col) {
                TileButton button = new TileButton(model.getValue(row, col));
                buttonsArray[row][col] = button;
                // Adding buttons to the grid
                grid.add(button, col, row);
                int finalCol = col;
                int finalRow = row;
                button.setOnAction(e->{
                    if(!model.getValue(finalRow, finalCol).equals("L")){
                        model.add(finalRow, finalCol);
                    }
                    else{
                        model.remove(finalRow, finalCol);
                    }
                });
            }
        }
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    /**
     * method that makes the hbox of the buttons that will be used to check the board, display
     * solve or give a hint of the next move in the board
     * @return: hbox of buttons
     */
    public HBox makeHBox(){
        HBox hBox = new HBox();
        hBox.setPrefHeight(55);

        check = new Button("Check");
        check.setPrefSize(BUTTON_W, BUTTON_L);

        hint = new Button("Hint");
        hint.setPrefSize(BUTTON_W, BUTTON_L);

        solve = new Button("Solve");
        solve.setPrefSize(BUTTON_W, BUTTON_L);

        restart = new Button("Restart");
        restart.setPrefSize(BUTTON_W, BUTTON_L);

        load = new Button("Load");
        load.setPrefSize(BUTTON_W, BUTTON_L);

        hBox.getChildren().addAll(check, hint, solve, restart, load);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(15);

        return hBox;
    }

    /**
     * method that puts everything together and gives the buttons functionality
     * @param stage: stage where the application will run
     */
    public void Layout (Stage stage){
        BorderPane pane = new BorderPane();
        pane.setCenter(makeGridPane());

        BorderPane labelPane = new BorderPane();
        topLabel = new Label(filename + " loaded");
        topLabel.setPrefHeight(50);
        topLabel.setFont(new Font(20));
        labelPane.setCenter(topLabel);
        pane.setTop(labelPane);

        pane.setBottom(makeHBox());
        FileChooser fileChooser = new FileChooser();
        check.setOnAction(e->{
            model.verify();
        });

        load.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            filename = selectedFile.getAbsolutePath();
            try {
                this.model = new LasersModel(filename);
            } catch (FileNotFoundException fnfe) {
                System.out.println(fnfe.getMessage());
                System.exit(-1);
            }
            this.model.addObserver(this);
            model.createBoard();
            try {
                start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        solve.setOnAction(e-> {
            try {
                model.solve(filename);
                start(stage);
                model.solve(filename);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        hint.setOnAction(e -> {
            try {
                Configuration config = model.hint(filename);
                if (config != null){
                    this.model = ((SafeConfig)config).getModel();
                }
                start(stage);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        restart.setOnAction(e -> {
            try {
                this.model = new LasersModel(filename);
            } catch (FileNotFoundException fnfe) {
                System.out.println(fnfe.getMessage());
                System.exit(-1);
            }
            this.model.addObserver(this);
            model.createBoard();
            try {
                start(stage);
                topLabel.setText(filename + " has been reset.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(pane);
        stage.setScene(scene);
    }

    /**
     * The initialization of all GUI component happens here.
     *
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) {
        Layout(stage);
    }

    /**
     * initialization of the UI components
     * @param stage: stage where everything will be
     */
    @Override
    public void start(Stage stage){
        init(stage);  // do all your UI initialization here

        stage.setTitle("Lasers GUI");
        stage.show();
    }

    /**
     * method that updates the board
     * @param model model that stores the safe
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(LasersModel model, ModelData data) {
        if(data.getAction().equals(ModelData.Action.CREATEBOARD)){
            topLabel = new Label();
        }
        else if(data.getAction().equals(ModelData.Action.VERIFYERROR)){
            if(model.getValue(data.getRow(),data.getCol()).equals(".")){
                buttonsArray[data.getRow()][data.getCol()].setGraphic(new ImageView(new Image(getClass().getResourceAsStream(
                       "resources/red.png"))));
            }
            setButtonBackground(buttonsArray[data.getRow()][data.getCol()], "red.png");
        }
        else if(data.getAction().equals(ModelData.Action.ADD) || data.getAction().equals(ModelData.Action.REMOVE)){
            for (int row=0; row<model.getROW_DIM(); ++row) {
                for (int col=0; col<model.getCOLUMN_DIM(); ++col) {
                    if(!model.getValue(row,col).equals("L")) {
                        if (model.getValue(row,col).equals(".")) {
                            TileButton button = new TileButton(model.getValue(row, col));
                            buttonsArray[row][col].setGraphic(new ImageView(button.getHashMap().get(model.getValue(row, col))));
                        }
                        setButtonBackground(buttonsArray[row][col],"white.png");
                    }
                    else {
                        setButtonBackground(buttonsArray[row][col],"yellow.png");
                    }
                }
            }

            for (int row=0; row<model.getROW_DIM(); ++row){
                TileButton button = new TileButton(model.getValue(row, data.getCol()));
                buttonsArray[row][data.getCol()].setGraphic(new ImageView(button.getHashMap().get(model.getValue(row,data.getCol()))));
                if(model.getValue(row,data.getCol()).equals("L")){
                    setButtonBackground(buttonsArray[row][data.getCol()],"yellow.png");
                }
            }

            for (int col=0; col<model.getCOLUMN_DIM(); ++col){
                TileButton button = new TileButton(model.getValue(data.getRow(), col));
                buttonsArray[data.getRow()][col].setGraphic(new ImageView(button.getHashMap().get(model.getValue(data.getRow(),col))));
                if(model.getValue(data.getRow(),col).equals("L")){
                    setButtonBackground(buttonsArray[data.getRow()][col],"yellow.png");
                }
            }
        }
        topLabel.setText(data.getMessage());
    }
}
