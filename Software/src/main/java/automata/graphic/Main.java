package automata.graphic;

import automata.control.WebPanesFXController;
import automata.support.Util;
import javafx.application.Application;
import javafx.fxml.LoadException;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Level;

import static automata.support.Util.fxmlLoader;
import static automata.support.Util.logger;

public class Main extends Application {

    private Scene scene;
    private Group group;
    private WebPanesFXController controller;

    @Override
    public void start (Stage stage) throws Exception
    {
        try {
            group = fxmlLoader.load(Util.fxml);
            scene = new Scene(group);
            stage.setTitle("Automata");
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            controller = fxmlLoader.getController();
            controller.postInitializeTasks(stage);
            stage.show();
        } catch (LoadException e){
            logger.log(Level.SEVERE, "controller class not defined" +
                        e.toString(), e);
        }
    }

    public static void load ()
    {
        Util.loadRessources();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Util.unloadResources();
        }, "Shutdown-thread"));
        launch();
    }

    public static void main (String[] args){
        load();
    }
}

