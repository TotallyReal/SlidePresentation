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
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import presentation.markers.Markers;

/**
 *
 * @author eofir
 */
public class SlideData {

    int audioStart = 0;
    int audioEnd = 1;
    String audioURL = "";
    String markerURL = "";

    public SlideData() {
        audioStart = 0;
        audioEnd = 1;
        audioURL = "";
        markerURL = "";
    }

    public SlideData(int audioStart, int audioEnd, String audioURL, String markerURL) {
        this.audioStart = audioStart;
        this.audioEnd = audioEnd;
        this.audioURL = audioURL;
        this.markerURL = markerURL;
    }

    public void save(String url) {
        if (url == null) {
            return;
        }
        try {
            url = url.endsWith(".xml") ? url : url + ".xml";
//            File file = new File(url);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
            FileOutputStream fos = new FileOutputStream(url);
            XMLEncoder e = new XMLEncoder(fos);

            e.writeObject(this);
            e.close();
            SlidesPane.out.println("Slide data was saved to " + url);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void save(File file){
        if (file == null || !file.getName().endsWith(".xml")){
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            XMLEncoder e = new XMLEncoder(fos);

            e.writeObject(this);
            e.close();
            SlidesPane.out.println("Slide data was saved to " + file.getName());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static SlideData load(String url) {
        if (url.startsWith("src/")) {
            //         URL resource = getClass().getClassLoader().getResource("tryme/data/slide1_data.xml");
//            File f = new File(resource.toExternalForm());
        }
        FileInputStream fis = null;
        url = url.endsWith(".xml") ? url : url + ".xml";
        File file = new File(url);
        //String toString = getClass().getClassLoader().getResource(url.substring(4)).toString();
        SlideData data = null;
        if (file.exists()) {
            try {
                //markerMap = new HashMap<String, /*HashMap<String, Integer>*/ MarkerSet>();
                fis = new FileInputStream(file);
                XMLDecoder decoder = new XMLDecoder(fis);
                data = (SlideData) decoder.readObject();
                decoder.close();
                SlidesPane.out.println("Loaded slide data successfully");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else { //file doesn't exist - create it
            String name = file.getName();
            if (name.endsWith("_data.xml")) {
                name = name.substring(0, name.length() - 9);
            } else {
                System.exit(0);
            }
            data = new SlideData();
            data.audioStart = 1000;
            data.audioEnd = 5000;
            data.markerURL = file.getParentFile().getParent() + "\\markers\\" + name + "_markers.xml";
            Markers.createEmptyMarkersFile(data.markerURL);
            data.audioURL = "";
            data.save(url);

        }
        return data;
    }

    public static SlideData load(ResourceDir resDir, String name) {
        if (resDir==null) {
            return null;
        }
        File file = resDir.getDataFile(name);
        //FileInputStream fis = null;
        //String toString = getClass().getClassLoader().getResource(url.substring(4)).toString();
        SlideData data = null;
        if (file.exists()) {
            data = (SlideData) ResourceDir.readObjectXML(file);
            if (data!=null)
                SlidesPane.out.println("Loaded slide data successfully");
        } else { //file doesn't exist - create it
            data = new SlideData();
            data.audioStart = 10000;
            data.audioEnd = 60000;
            File markersFile = resDir.getMarkersFile(name);
            data.markerURL = name + "_markers.xml";
            Markers.createEmptyMarkersFile(markersFile);
            data.audioURL = "";
            if (ResourceDir.saveObjectXML(file, data)){
                SlidesPane.out.println( "Slide data was saved to " + file.getName());
            } else {
                return null;
            }
            
        }
        return data;
    }

    public int getAudioStart() {
        return audioStart;
    }

    public void setAudioStart(int audioStart) {
        this.audioStart = audioStart;
    }

    public int getAudioEnd() {
        return audioEnd;
    }

    public void setAudioEnd(int audioEnd) {
        this.audioEnd = audioEnd;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public void setAudioURL(String audioURL) {
        this.audioURL = audioURL;
    }

    public String getMarkerURL() {
        return markerURL;
    }

    public void setMarkerURL(String markerURL) {
        this.markerURL = markerURL;
    }
}
