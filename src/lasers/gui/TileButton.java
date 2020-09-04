package lasers.gui;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.HashMap;

/**
 * The class that gives each string its respective image
 * @author Vedant Juneja
 * @author Julio Cuello
 */

public class TileButton extends Button {
    private HashMap<String, Image> hashMap = new HashMap<>();
    public TileButton(String tile) {
        Image beam = new Image(getClass().getResourceAsStream(
                "resources/beam.png"));
        hashMap.put("*", beam);
        Image white = new Image(getClass().getResourceAsStream(
                "resources/white.png"));
        hashMap.put(".", white);
        Image laser = new Image(getClass().getResourceAsStream(
                "resources/laser.png"));
        hashMap.put("L", laser);
        Image pillar0 = new Image(getClass().getResourceAsStream(
                "resources/pillar0.png"));
        hashMap.put("0", pillar0);
        Image pillar1 = new Image(getClass().getResourceAsStream(
                "resources/pillar1.png"));
        hashMap.put("1", pillar1);
        Image pillar2 = new Image(getClass().getResourceAsStream(
                "resources/pillar2.png"));
        hashMap.put("2", pillar2);
        Image pillar3 = new Image(getClass().getResourceAsStream(
                "resources/pillar3.png"));
        hashMap.put("3", pillar3);
        Image pillar4 = new Image(getClass().getResourceAsStream(
                "resources/pillar4.png"));
        hashMap.put("4", pillar4);
        Image pillarX = new Image(getClass().getResourceAsStream(
                "resources/pillarX.png"));
        hashMap.put("X", pillarX);
        Image yellow = new Image(getClass().getResourceAsStream(
                "resources/yellow.png"));

        this.setBackground(new Background(new BackgroundImage(white,BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT)));
        if (tile.equals("L")){
            this.setBackground(new Background(new BackgroundImage(yellow,BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT)));
        }
        this.setGraphic(new ImageView(hashMap.get(tile)));
    }

    /**
     * method that gets the hashmap where we store the image of each respective value
     * @return
     */
    public HashMap<String, Image> getHashMap() {
        return hashMap;
    }
}
