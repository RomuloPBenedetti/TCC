package automata.support;

import automata.control.TransparentDottedFXController;
import javafx.concurrent.Worker;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import javafx.scene.control.Dialog;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static automata.support.Util.s;
import static java.awt.event.KeyEvent.*;

/**
 * Created by romulo on 24/06/16.
 */
public class JavascriptMsgr {

    private static JavascriptMsgr currentMsgr;
    private static List<String> IDs = new ArrayList<>();
    private static String currentID;
    private static WebEngine engine;
    private static Rectangle primaryScreenBounds;
    private static Robot robot;
    private static String code, xml;
    private static String pathPrefx = ".." + s;

    public JavascriptMsgr(WebEngine engine){
        this.engine = engine;
        engine.getLoadWorker().stateProperty().
            addListener((observable, oldValue, newValue) -> {
                try {
                    if (newValue == Worker.State.SUCCEEDED) {
                        JSObject jsobj = (JSObject) engine.executeScript("window");
                        jsobj.setMember("java", this);
                    }
                    currentMsgr = this;
                    robot = new Robot();
                }catch (Exception e){ e.printStackTrace(); }
            });
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice monitor = environment.getDefaultScreenDevice();
        primaryScreenBounds = monitor.getConfigurations()[0].getBounds();
    }

    /*************************************************************
     *
     * to JS
     *
     *************************************************************/

    public void setClickImageInBlockly(String imagesrc, int width, int height){
        String src = StringEscapeUtils.escapeEcmaScript( pathPrefx +imagesrc);
        engine.executeScript("setImageButtonByID(\"" + currentID + "\", " +
                String.valueOf(width) + ", " + String.valueOf(height) + ", \"" +
                src + "\")");
    }

    public synchronized void getCode(){
        com.sun.javafx.application.PlatformImpl.runAndWait(() ->
            code = (String) engine.executeScript("runAndGetCode()"));
        //System.out.println(code);
    }

    public void loadBlocks(String xml) {
        com.sun.javafx.application.PlatformImpl.runAndWait(() ->{
            String escaped = StringEscapeUtils.escapeEcmaScript(xml);
            engine.executeScript("loadBlocks(\""+ escaped +"\")");
        });
    }

    public String saveBlocks() {
        com.sun.javafx.application.PlatformImpl.runAndWait(() ->
                xml = (String) engine.executeScript("saveBlocks()"));
        return xml;
    }

    /*************************************************************
     *
     * from JS
     *
     *************************************************************/

    public void StartclickCapture(String ID) {
        currentID = ID;
        IDs.add(ID);
        TransparentDottedFXController tdfx = new TransparentDottedFXController();
        tdfx.loadDottedAlert();
    }

    public boolean clickIn(String src, int times) {
        try {
            Thread.currentThread().sleep(100);
            System.out.println(src);
            BufferedImage target = ImageIO.read(new File(src.substring(3)));
            BufferedImage screenshot = robot.createScreenCapture(primaryScreenBounds);
            ImageSearch is = new ImageSearch(target);

            int[] clickPoint; Boolean triedDesktop = false, seenAllScreens = false;

            while((clickPoint = is.search(screenshot))[0] == -1 || (triedDesktop && seenAllScreens)) {
                if(!triedDesktop) {
                    screenshot = goToDesktop(); triedDesktop = true;
                }
                if(!seenAllScreens) {
                    screenshot = goToAnotherWindows(); seenAllScreens = true;
                }
            }
            if (clickPoint[0] == -1) {
                alert("nao encontrou onde clicar " + src);
                return false;
            }
            else{
                clickPoint[0] = clickPoint[0] + target.getWidth()/2;
                clickPoint[1] = clickPoint[1] + target.getHeight()/2;
                System.out.println(clickPoint.toString() + " " + times);
                click (clickPoint, times);
                return true;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean type( String text){
        text = StringEscapeUtils.unescapeEcmaScript(text);
        TypeAString(text);
        return true;
    }

    public void waitImg (String imgSrc, int milisec){
        try {
            BufferedImage target = null;
            target = ImageIO.read(new File(imgSrc.substring(3)));
            BufferedImage screenshot = robot.createScreenCapture(primaryScreenBounds);
            ImageSearch is = new ImageSearch(target);

            while(is.search(screenshot)[0] == -1){
                screenshot = robot.createScreenCapture(primaryScreenBounds);
                System.out.println("tried");
                Thread.sleep(milisec);
            }
            System.out.println("match!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*************************************************************
     *
     * automation methods
     *
     *************************************************************/

    public BufferedImage goToDesktop(){
        return null;
    }

    public BufferedImage goToAnotherWindows(){
        return null;
    }

    public void click (int[] clickPoint, int times) {
        robot.mouseMove(clickPoint[0],clickPoint[1]);
        System.out.println("clicked");
        if (times == 1) {
            robot.mousePress( InputEvent.BUTTON1_MASK );
            robot.mouseRelease( InputEvent.BUTTON1_MASK );
        } else {
            robot.mousePress( InputEvent.BUTTON1_MASK );
            robot.mouseRelease( InputEvent.BUTTON1_MASK );
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.mousePress( InputEvent.BUTTON1_MASK );
            robot.mouseRelease( InputEvent.BUTTON1_MASK );
        }
    }

    /*************************************************************
     *
     * helper
     *
     *************************************************************/

    public static String getCurrentID() {
        return String.valueOf(IDs.indexOf(currentID));
    }

    public static JavascriptMsgr getCurrentMsgr() {
        return currentMsgr;
    }

    private void alert(String message){
        Dialog<Void> alert = new Dialog<>();
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.showAndWait();
    }

    public void TypeAString(CharSequence characters) {
        int length = characters.length();
        for (int i = 0; i < length; i++) {
            char character = characters.charAt(i);
            typeAChar(character);
        }
    }

    public void typeAChar(char character) {
        switch (character) {
            case 'a': doType(VK_A); break; case 'b': doType(VK_B); break;
            case 'c': doType(VK_C); break; case 'd': doType(VK_D); break;
            case 'e': doType(VK_E); break; case 'f': doType(VK_F); break;
            case 'g': doType(VK_G); break; case 'h': doType(VK_H); break;
            case 'i': doType(VK_I); break; case 'j': doType(VK_J); break;
            case 'k': doType(VK_K); break; case 'l': doType(VK_L); break;
            case 'm': doType(VK_M); break; case 'n': doType(VK_N); break;
            case 'o': doType(VK_O); break; case 'p': doType(VK_P); break;
            case 'q': doType(VK_Q); break; case 'r': doType(VK_R); break;
            case 's': doType(VK_S); break; case 't': doType(VK_T); break;
            case 'u': doType(VK_U); break; case 'v': doType(VK_V); break;
            case 'w': doType(VK_W); break; case 'x': doType(VK_X); break;
            case 'y': doType(VK_Y); break; case 'z': doType(VK_Z); break;
            case 'A': doType(VK_SHIFT, VK_A); break; case 'B': doType(VK_SHIFT, VK_B); break;
            case 'C': doType(VK_SHIFT, VK_C); break; case 'D': doType(VK_SHIFT, VK_D); break;
            case 'E': doType(VK_SHIFT, VK_E); break; case 'F': doType(VK_SHIFT, VK_F); break;
            case 'G': doType(VK_SHIFT, VK_G); break; case 'H': doType(VK_SHIFT, VK_H); break;
            case 'I': doType(VK_SHIFT, VK_I); break; case 'J': doType(VK_SHIFT, VK_J); break;
            case 'K': doType(VK_SHIFT, VK_K); break; case 'L': doType(VK_SHIFT, VK_L); break;
            case 'M': doType(VK_SHIFT, VK_M); break; case 'N': doType(VK_SHIFT, VK_N); break;
            case 'O': doType(VK_SHIFT, VK_O); break; case 'P': doType(VK_SHIFT, VK_P); break;
            case 'Q': doType(VK_SHIFT, VK_Q); break; case 'R': doType(VK_SHIFT, VK_R); break;
            case 'S': doType(VK_SHIFT, VK_S); break; case 'T': doType(VK_SHIFT, VK_T); break;
            case 'U': doType(VK_SHIFT, VK_U); break; case 'V': doType(VK_SHIFT, VK_V); break;
            case 'W': doType(VK_SHIFT, VK_W); break; case 'X': doType(VK_SHIFT, VK_X); break;
            case 'Y': doType(VK_SHIFT, VK_Y); break; case 'Z': doType(VK_SHIFT, VK_Z); break;
            case '`': doType(VK_BACK_QUOTE); break;  case '0': doType(VK_0); break;
            case '1': doType(VK_1); break;           case '2': doType(VK_2); break;
            case '3': doType(VK_3); break;           case '4': doType(VK_4); break;
            case '5': doType(VK_5); break;           case '6': doType(VK_6); break;
            case '7': doType(VK_7); break;           case '8': doType(VK_8); break;
            case '9': doType(VK_9); break;           case '-': doType(VK_MINUS); break;
            case '=': doType(VK_EQUALS); break;
            case '~': doType(VK_SHIFT, VK_BACK_QUOTE); break;
            case '!': doType(VK_EXCLAMATION_MARK); break;
            case '@': doType(VK_AT); break;
            case '#': doType(VK_NUMBER_SIGN);break;
            case '$': doType(VK_DOLLAR); break;
            case '%': doType(VK_SHIFT, VK_5); break;
            case '^': doType(VK_CIRCUMFLEX); break;
            case '&': doType(VK_AMPERSAND); break;
            case '*': doType(VK_ASTERISK); break;
            case '(': doType(VK_LEFT_PARENTHESIS); break;
            case ')': doType(VK_RIGHT_PARENTHESIS); break;
            case '_': doType(VK_UNDERSCORE); break;
            case '+': doType(VK_PLUS); break;
            case '\t': doType(VK_TAB); break;
            case '\n': doType(VK_ENTER); break;
            case '[': doType(VK_OPEN_BRACKET); break;
            case ']': doType(VK_CLOSE_BRACKET); break;
            case '\\': doType(VK_BACK_SLASH); break;
            case '{': doType(VK_SHIFT, VK_OPEN_BRACKET); break;
            case '}': doType(VK_SHIFT, VK_CLOSE_BRACKET); break;
            case '|': doType(VK_SHIFT, VK_BACK_SLASH); break;
            case ';': doType(VK_SEMICOLON); break;
            case ':': doType(VK_COLON); break;
            case '\'': doType(VK_QUOTE); break;
            case '"': doType(VK_QUOTEDBL); break;
            case ',': doType(VK_COMMA); break;
            case '<': doType(VK_SHIFT, VK_COMMA); break;
            case '.': doType(VK_PERIOD); break;
            case '>': doType(VK_SHIFT, VK_PERIOD); break;
            case '/': doType(VK_SLASH); break;
            case '?': doType(VK_SHIFT, VK_SLASH); break;
            case ' ': doType(VK_SPACE); break;
            default: throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    private void doType(int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    private void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) { return; }
        robot.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        robot.keyRelease(keyCodes[offset]);
    }
}
