package automata.control;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public abstract class GenericFXController implements Initializable {

    @FXML private Group root;
    @FXML private StackPane transparentPane;
    @FXML private AnchorPane roundBorderPane;


    protected Double initX, initY, maxWidth, maxHeight;
    protected final Double minWidth = 800.0 , minHeight = 600.0;
    protected Rectangle2D primaryScreenBounds;
    protected Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initX = 0.0;
        initY = 0.0;
    }

    public void postInitializeTasks (Stage stage){
        primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        windowsLimitsInitialization();
        this.stage = stage;
    }

    public void windowsLimitsInitialization ()
    {
        maxWidth = primaryScreenBounds.getWidth();
        maxHeight = primaryScreenBounds.getHeight();
        transparentPane.setMaxSize(maxWidth, maxHeight);
        roundBorderPane.setMaxSize(maxWidth, maxHeight);
    }


    /***************************************************************************

     Acoes da interface

     **************************************************************************/


    @FXML
    private void closeAction (MouseEvent event) {
        stage.close();
    }

    @FXML
    private void closeAction (Event event) {
        stage.close();
    }

    @FXML
    protected void maximizeAction (MouseEvent event){
        String style = "fx-background-color: rgb(232, 232, 232);";
        if(stage.isMaximized()) {
            stage.setMaximized(false);
            style = style + "-fx-pref-width: " + minWidth + ";" +
                    "-fx-pref-height: " + minHeight + ";" +
                    "-fx-min-width: " + minWidth + ";" +
                    "-fx-min-height: " + minHeight + ";" +
                    "-fx-background-radius: 10;";
            transparentPane.setMinWidth(minWidth + 50.0);
            transparentPane.setMinHeight(minHeight + 50.0);
            roundBorderPane.setStyle(style);
        }
        else {
            stage.setMaximized(true);
            style = style + "-fx-pref-width: " + maxWidth + ";" +
                    "-fx-pref-height: " + maxHeight + ";" +
                    "-fx-min-width: " + maxWidth + ";" +
                    "-fx-min-height: " + maxHeight + ";" +
                    "-fx-background-radius: 0;";
            transparentPane.setMinWidth(maxWidth);
            transparentPane.setMinHeight(maxHeight);
            roundBorderPane.setMinWidth(maxWidth);
            roundBorderPane.setMinHeight(maxHeight);
            roundBorderPane.setStyle(style);
        }
    }

    @FXML
    private void minimizeAction (MouseEvent event){
        stage.setIconified(true);
    }

    @FXML
    private void resizeEvent (MouseEvent event) {
        if((event.getScreenX() < stage.getX()+31) &&
                (event.getScreenX() > stage.getX()+24) &&
                (event.getScreenY() < stage.getY() + minHeight+10)  )
            stage.getScene().setCursor(Cursor.H_RESIZE);
        else
            stage.getScene().setCursor(Cursor.DEFAULT);
    }


    @FXML
    private void moveWindowInitialPosition (MouseEvent event) {
        initX = (stage.getX() - event.getScreenX());
        initY = (stage.getY() - event.getScreenY());
    }

    @FXML
    private void moveWindow (MouseEvent event) {
        stage.setX(event.getScreenX() + initX);
        stage.setY(event.getScreenY() + initY);
    }

}
