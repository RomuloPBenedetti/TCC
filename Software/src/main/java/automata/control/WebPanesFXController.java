package automata.control;

import automata.support.JavascriptMsgr;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.*;

import static automata.support.Util.blocklyString;
import static automata.support.Util.loader;
import static automata.support.Util.s;

public class WebPanesFXController extends GenericFXController {

    @FXML private WebView browser;

    private JavascriptMsgr javascriptMsgr;

    public void postInitializeTasks (Stage stage){
        super.postInitializeTasks(stage);
        loadWebContent(blocklyString +"/"+ "index.html");
        System.out.println(blocklyString +"/"+ "index.html");
    }

    public void loadWebContent (String url) {
        WebEngine webEngine = browser.getEngine();
        webEngine.setOnAlert(event -> showAlert(event.getData()));
        webEngine.setPromptHandler(param -> showValueConfirmation(param.getMessage()));
        try{
            //System.out.println(loader.getResource(url).toExternalForm());
            webEngine.load(loader.getResource(url).toExternalForm());
            browser.setZoom(1.0);
            javascriptMsgr = new JavascriptMsgr(webEngine);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void closeAction (MouseEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void runCode (MouseEvent event) {
        javascriptMsgr.getCode();
    }

    @FXML
    public void loadBlocks () {

        File file = fileChose().showOpenDialog(stage);
        if (file != null) {
            try {
                String line;
                StringBuffer xml = new StringBuffer();
                BufferedReader br = new BufferedReader(new FileReader(file));

                while ((line = br.readLine()) != null)
                    xml.append(line);

                javascriptMsgr.loadBlocks(xml.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void saveBlocks () {
        File file = fileChose().showSaveDialog(stage);
        String xml = javascriptMsgr.saveBlocks();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(xml);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        System.out.println(message);
    }

    private String showValueConfirmation (String message){
        TextInputDialog valConfirmDialog = new TextInputDialog();
        valConfirmDialog.getDialogPane().setContentText(message);
        return valConfirmDialog.showAndWait().get();
    }

    public FileChooser fileChose () {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser;
    }

}
