package automata.control;

import automata.support.JavascriptMsgr;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static automata.support.Util.loader;
import static automata.support.Util.s;


public class TransparentDottedFXController implements Initializable {

    @FXML private Group root;
    @FXML private StackPane transparentDottedBorderPane;
    @FXML private AnchorPane selectedAreaPane;

    protected Double initX, initY, maxWidth, maxHeight;
    protected final Double minWidth = 800.0 , minHeight = 600.0;
    protected Rectangle primaryScreenBounds;
    private Stage stage;
    private NativeKeyListener nativeKeyListener;

    private Boolean capture = false;
    private BufferedImage screenshot, cutArea;

    /*****************************************************************************************
     *
     *  INITIALIZATION
     *
     ****************************************************************************************/
    public void loadDottedAlert() {
        try {
            TransparentDottedFXController controller;
            FXMLLoader fxmlLoader = new FXMLLoader();
            InputStream instream = loader.getResourceAsStream("TransparentDotted.fxml");
            Group group = fxmlLoader.load(instream);
            Scene scene = new Scene(group);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            Stage newStage = new Stage();
            newStage.setTitle("automata");
            newStage.setScene(scene);
            newStage.initStyle(StageStyle.TRANSPARENT);
            controller = fxmlLoader.getController();
            controller.postInitializeTasks(newStage);
            newStage.setAlwaysOnTop(true);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initX = 0.0;
        initY = 0.0;
    }


    public void postInitializeTasks (Stage stage) {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice monitor = environment.getDefaultScreenDevice();
        primaryScreenBounds = monitor.getConfigurations()[0].getBounds();
        windowsLimitsInitialization();
        this.stage = stage;
        maximizeScreen();
        startGlobalKeyListenner();
        addDragListenner();
    }

    public void windowsLimitsInitialization ()
    {
        maxWidth = primaryScreenBounds.getWidth();
        maxHeight = primaryScreenBounds.getHeight();
        transparentDottedBorderPane.setMaxSize(maxWidth, maxHeight);
    }

    private void addDragListenner() {
        transparentDottedBorderPane.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            selectedAreaPane.setPrefHeight(0.0);
            selectedAreaPane.setPrefWidth(0.0);
            selectedAreaPane.setLayoutX(mouseEvent.getX());
            selectedAreaPane.setLayoutY(mouseEvent.getY());
        });

        transparentDottedBorderPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            selectedAreaPane.setPrefHeight(mouseEvent.getY() - selectedAreaPane.getLayoutY());
            selectedAreaPane.setPrefWidth(mouseEvent.getX() - selectedAreaPane.getLayoutX());
        });
    }

    private void startGlobalKeyListenner (){
        try {
            GlobalScreen.registerNativeHook();
            nativeKeyListener = new GlobalKeyListenner(this);
            GlobalScreen.addNativeKeyListener(nativeKeyListener);
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);

        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    private void maximizeScreen() {
        if(stage.isMaximized()) {
            stage.setMaximized(false);
            transparentDottedBorderPane.setMinWidth(minWidth + 50.0);
            transparentDottedBorderPane.setMinHeight(minHeight + 50.0);
        }
        else {
            stage.setMaximized(true);
            transparentDottedBorderPane.setMinWidth(maxWidth);
            transparentDottedBorderPane.setMinHeight(maxHeight);
        }
    }

    /*****************************************************************************************
     *
     *  SCREEN EVENTS
     *
     ****************************************************************************************/

    public void enterCaptureScreenMode() {
        String style = "-fx-background-color: rgba(0, 0, 0, 0);\n" +
                "    -fx-border-color: yellow ;\n" +
                "    -fx-border-width: 2px ;\n" +
                "    -fx-border-style: dashed;\n" +
                "    -fx-border-radius: 10;";
        transparentDottedBorderPane.setStyle(style);
        getScreenshot();
    }



    public void exitCaptureMode() {
        cutScreenshot();
        saveCuttedScreenshot();
        setImageOnBlock();
        String style = "-fx-background-color: rgba(0, 0, 0, 0);\n" +
                "    -fx-border-color: red ;\n" +
                "    -fx-border-width: 2px ;\n" +
                "    -fx-border-style: dashed;\n" +
                "    -fx-border-radius: 10;";
        transparentDottedBorderPane.setStyle(style);
        getScreenshot();
        closeScreen();
    }

    /*****************************************************************************************
     *
     *  CAPTURE STEPS
     *
     ****************************************************************************************/

    private void getScreenshot ()  {
        Platform.runLater( () -> stage.hide());
        try {
            Thread.currentThread().sleep(100);
            screenshot = new Robot().createScreenCapture(primaryScreenBounds);
        } catch (AWTException | InterruptedException e) { e.printStackTrace(); }
        Platform.runLater( () -> stage.show());
        WritableImage writableImage =
                new WritableImage(screenshot.getWidth(), screenshot.getHeight());
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage back = new BackgroundImage(
                SwingFXUtils.toFXImage(screenshot,writableImage),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, backgroundSize);
        transparentDottedBorderPane.setBackground(new Background(back));
    }

    private void cutScreenshot() {
        int x = (int) selectedAreaPane.getLayoutX();
        int y = (int) selectedAreaPane.getLayoutY();
        int width = (int) selectedAreaPane.getWidth();
        int height = (int) selectedAreaPane.getHeight();
        cutArea = screenshot.getSubimage(x,y,width,height);
    }

    private void saveCuttedScreenshot() {
        try {
            if(!new File("images").exists())
                new File("images").mkdir();
            if(new File("images" + s + "area" + JavascriptMsgr.getCurrentID() + ".png").exists())
                new File("images" + s + "area" + JavascriptMsgr.getCurrentID() + ".png").delete();
            ImageIO.write(cutArea, "png", new File("images" + s + "area" + JavascriptMsgr.getCurrentID() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setImageOnBlock() {
        String src = "images" + s + "area" + JavascriptMsgr.getCurrentID() + ".png";
        JavascriptMsgr.getCurrentMsgr().setClickImageInBlockly(src, cutArea.getWidth(), cutArea.getHeight());
    }


    /*****************************************************************************************
     *
     *  OTHER TASKS
     *
     ****************************************************************************************/

    public void closeScreen() {
        try {
            GlobalScreen.unregisterNativeHook();
            GlobalScreen.removeNativeKeyListener(nativeKeyListener);
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        Platform.runLater( () -> stage.close() );
    }

}

/**********************************************************************************************
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * * * * * * * * * * * * * * * *  GLOBAL KEY LISTENNER CLASS * * * * * * * * * * * * * * * * *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *********************************************************************************************/

class GlobalKeyListenner implements NativeKeyListener
{
    private TransparentDottedFXController transparentDottedFXController;
    private List<Integer> keyBuffer = new ArrayList<>();
    private Boolean captureMode = false;

    public GlobalKeyListenner(TransparentDottedFXController transparentDottedFXController) {
        this.transparentDottedFXController = transparentDottedFXController;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        //System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        Platform.runLater(() ->{
            if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
                Platform.runLater(() -> transparentDottedFXController.closeScreen() );
            }

            if (e.getKeyCode() == NativeKeyEvent.VC_ENTER) {
                transparentDottedFXController.exitCaptureMode();
                captureMode = false;
            }
        });
        keyBuffer.add(new Integer(e.getKeyCode()));
        if (keyBuffer.contains(NativeKeyEvent.VC_CONTROL_L) &&
                keyBuffer.contains(NativeKeyEvent.VC_SHIFT_L) &&
                keyBuffer.contains(NativeKeyEvent.VC_C) && !captureMode){
            transparentDottedFXController.enterCaptureScreenMode();
            captureMode = true;
        }

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        //System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        keyBuffer.remove(new Integer(e.getKeyCode()));
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        //System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
    }

    public TransparentDottedFXController getTransparentDottedFXController() {
        return transparentDottedFXController;
    }
}
