/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.slides;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eofir
 */
public class ResourceDir {

    private boolean valid;
    File resDir = null;

    public boolean isValid() {
        return valid;
    }

    private boolean isValidDir(File dir) {
        if (dir != null) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!dir.isDirectory()) {
                return false;
            }
            File markersFile = new File(dir, "markers");
            File narrationFile = new File(dir, "narration");
            File dataFile = new File(dir, "data");

            markersFile.mkdir();
            narrationFile.mkdir();
            dataFile.mkdir();
            return true;

        }
        return false;
    }

    public ResourceDir(File dir) {
        valid = isValidDir(dir);
        resDir = new File(dir.toString());
    }

    public File getResourceDir() {
        return new File(resDir.toString());
    }

    public File getDataFile(String name) {
        return new File(resDir, "\\data\\" + name + "_data.xml");
    }

    public File getMarkersFile(String name) {
        return new File(resDir, "\\markers\\" + name + "_markers.xml");
    }

    public File getNarrationFile(String name) {
        return new File(resDir, "\\narration\\" + name + ".mp3");
    }

    /**
     * saves an object from an xml file.
     * If the file is not a valid xml file or the object is null, returns false;
     * If the file is valid, but doesn't exist, then creates it and save the object.
     * 
     * @param file The xml file to which the object is saved
     * @param obj The object to save
     * @return true if the object was saved successfully and false otherwise
     */
    public static boolean saveObjectXML(File file, Object obj){
        if (file==null || !file.toString().endsWith(".xml") || obj == null)
            return false;
        
        try {
            FileOutputStream fos = new FileOutputStream(file);
            XMLEncoder e = new XMLEncoder(fos);

            e.writeObject(obj);
            e.close();
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Reads an object from an xml file
     * 
     * @param file an XML file to read the object from
     * @return the object if it was read successfully and null otherwise
     */
    public static Object readObjectXML(File file) {
        if (file==null || !file.exists() || !file.toString().endsWith(".xml"))
            return null;
        FileInputStream fis = null;
        Object obj = null;
        try {
            //markerMap = new HashMap<String, /*HashMap<String, Integer>*/ MarkerSet>();
            fis = new FileInputStream(file);
            XMLDecoder decoder = new XMLDecoder(fis);
            obj = decoder.readObject();
            decoder.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return obj;
    }

}
