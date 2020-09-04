package lasers.ptui;

import java.io.FileNotFoundException;

import lasers.model.LasersModel;
import lasers.model.ModelData;
import lasers.model.Observer;

/**
 * This class represents the view portion of the plain text UI.  It
 * is initialized first, followed by the controller (ControllerPTUI).
 * You should create the model here, and then implement the update method.
 *
 * @author Sean Strout @ RIT CS
 * @author Vedant Juneja
 * @author Julio Cuello
 */
public class LasersPTUI implements Observer<LasersModel, ModelData> {
    /** The UI's connection to the model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the lasers.lasers.model and initialize the view.
     * @param filename the safe file name
     */
    public LasersPTUI(String filename) {
        try {
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }
    /**
     * Accessor for the model the PTUI create.
     *
     * @return the model
     */
    public LasersModel getModel() { return this.model; }

    /**
     * method that updates the view depending on the message recieved form the model
     * @param model: model that stores the board that the view will have
     * @param data optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(LasersModel model, ModelData data) {
        if (data.getAction().equals(ModelData.Action.CREATEBOARD) || data.getAction().equals(ModelData.Action.DISPLAY)){
            System.out.println(model);
        }
        else if(data.getAction().equals(ModelData.Action.HELP) || data.getAction().equals(ModelData.Action.VERIFY)){
            System.out.println(data.getMessage());
        }
        else if(data.getAction().equals(ModelData.Action.QUIT)){
            System.exit(0);
        }
        else{
            System.out.println(data.getMessage());
            System.out.println(model);
        }
    }
}
