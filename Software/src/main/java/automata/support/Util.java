package automata.support;

import javafx.fxml.FXMLLoader;
import javafx.scene.text.Font;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Util {

    public static final String s = File.separator;

    private static final List<String> fontsString =
        new ArrayList<>( Arrays.asList(new String[]{
                "fonts/Roboto-Thin.ttf",
                "fonts/Roboto-Black.ttf",
                "fonts/Roboto-Regular.ttf",
                "fonts/Roboto-Medium.ttf",
                "fonts/Roboto-Light.ttf"}
    ));
    public static List<InputStream> fonts = new ArrayList<>();

    public static final String loggingFile = "logfile.log";
    public static final Logger logger= Logger.getLogger(Util.class.getName());

    private static final String cssString ="WebPane.css";
    private static final String fxmlString = "WebPane.fxml";

    public static final ClassLoader loader = Util.class.getClassLoader();
    public static final FXMLLoader fxmlLoader = new FXMLLoader();
    public static InputStream fxml, css;

    private static final Locale ptBR = new Locale("pt", "BR");
    private static final ResourceBundle pt = ResourceBundle.getBundle("Gui_pt", ptBR);
    public static ResourceBundle rb;

    public static FileHandler fileHandler;

    public static final String Html = "blocklyResource/index.html";

    public static ZipFile zip;
    public static String currentJarPath;
    public static String blocklyString = "blocklyResource";

    public static void loadRessources()
    {
        try{
            CodeSource src = Util.class.getProtectionDomain().getCodeSource();
            URI jar = src.getLocation().toURI();
            currentJarPath = jar.getPath();
            zip = new ZipFile(new File(currentJarPath));
            fxml = getISFromZip(fxmlString);
            css = getISFromZip(cssString);
            blocklyString = "blockly";
            Util.externalizeResources();
        } catch (IOException | URISyntaxException ex) {
            logger.log(Level.SEVERE, "can't find the Jar path itself. \n" +
            ex.getMessage(), ex);
        } finally {
            if(zip == null) {
                fontsString.forEach(f -> fonts.add(loader.getResourceAsStream(f)));
                fxml = loader.getResourceAsStream(fxmlString);
                css = loader.getResourceAsStream(cssString);
            }
        }
        loadLocale();
        loadFonts();
        loadLogger();
    }

    public static void loadLocale ()
    {
        fxmlLoader.setResources(pt); rb = pt;
    }

    public static void loadFonts ()
    {
        fonts.forEach(f -> Font.loadFont(f, 15));
    }

    private static InputStream getISFromZip (String name)
    {
        try {
            return zip.getInputStream(zip.getEntry(name));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void externalizeResources()
    {
        String jarname = new java.io.File(Util.class.getProtectionDomain().getCodeSource()
                .getLocation().getPath()).getName();
        InputStream instream = Util.class.getClassLoader().getResourceAsStream(jarname);
        ZipInputStream zstream = new ZipInputStream(instream);
        ZipEntry entry;

        try {
            while ((entry = zstream.getNextEntry()) != null){
                if(entry.getName().contains("blocklyResource") || entry.getName().contains("images")) {
                    String entryPath = entry.getName();
                    if(entry.isDirectory()){
                        File dir = new File(entryPath);
                        dir.mkdir();
                    }else{
                        extractFile(zstream, entryPath);
                    }
                }
            }
            if (!new File("blockly").exists()) {
                new File("blocklyResource").renameTo(new File("blockly"));
            } else {
                delete(new File("blocklyResource"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            buf.write(bytesIn, 0, read);
        }
        buf.close();
    }

    private static void loadLogger() {
        try {
            fileHandler = new FileHandler(loggingFile,true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
    }

    public static void unloadResources()
    {
        try {
            delete(new File("blockly"));
            delete(new File("image"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void delete(File folder) throws IOException {
        if (folder.isDirectory()) {
            for (File file : folder.listFiles())
                delete(file);
        }
        if (!folder.delete())
            throw new FileNotFoundException("Failed to delete file: " + folder);
    }
}
