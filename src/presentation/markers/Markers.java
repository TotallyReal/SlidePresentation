package presentation.markers;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableMap;
import javafx.scene.media.Media;
import javafx.util.Duration;
import presentation.slides.ResourceDir;
import presentation.slides.SlidesPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author eofir
 */
public class Markers implements MarkersInterface {

    private List<MarkerSetInterface> markersList;
    private String markersURL;
    Media audioMedia;
    HashMap<String, Boolean> names;
    //List<String> possibleNames;

    public Markers() {
        names = new HashMap<>();
        markersList = new ArrayList<>();
    }

    public void setAudioMedia(Media audioMedia) {
        this.audioMedia = audioMedia;

        ObservableMap<String, Duration> markers = audioMedia.getMarkers();
        markersList.forEach((set) -> {
            markers.put(set.getMarkerName(), Duration.millis(set.getMarkerStart()));
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Save and Load">
    /**
     * Loads the markers from a file. The file should contain the markers in the
     * form of List<MarkerSet> . If successful, saves the url for future
     * actions.
     *
     * @param url
     */
    public void loadMarkers(String url) {
        markersURL = url;
        FileInputStream fis;
        File file = new File(url);
        List<MarkerSet> temp;
        try {
            if (file.exists()) {
                //markerMap = new HashMap<String, /*HashMap<String, Integer>*/ MarkerSet>();
                fis = new FileInputStream(file);
                try (XMLDecoder decoder = new XMLDecoder(fis)) {
                    temp = (List<MarkerSet>) decoder.readObject();
//            markersList = new ArrayList<>();
//            markersList.addAll(markerMap.values());
                }
            } else { //file does not exists - create it and make an empty marker list
                temp = new ArrayList<>();
                saveMarkers(); //creates new file with empty marker list
            }
            List<MarkerSetInterface> temp2 = new ArrayList<>(temp.size());
            for (MarkerSet set : temp) {
                set.newVersion();
                temp2.add((MarkerSetInterface) set);
            }
            setMarkers(temp2);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Markers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Loads the markers from a file. The file should contain the markers in the
     * form of List<MarkerSet> . If successful, saves the url for future
     * actions.
     *
     * @param url
     */
    public void loadMarkers(File file) {
        List<MarkerSet> temp;
        if (file.exists()) {
            temp = (List<MarkerSet>) ResourceDir.readObjectXML(file);
        } else { //file does not exists - create it and make an empty marker list
            temp = new ArrayList<>();
            saveMarkers(file); //creates new file with empty marker list
        }
        markersFile = file;
        List<MarkerSetInterface> temp2 = new ArrayList<>(temp.size());
        for (MarkerSet set : temp) {
            set.newVersion();
            temp2.add((MarkerSetInterface) set);
        }
        setMarkers(temp2);
    }

    /**
     * If a url was already provided using a previous save(url) or load(url)
     * function, then reload it again.
     */
    public void reloadMarkers() {
        if (markersFile != null) {
            loadMarkers(markersFile);
        } else {
            if (markersURL != null) {
                loadMarkers(markersURL);
            }
        }
    }

    public static void createEmptyMarkersFile(String url) {
        Markers markers = new Markers();
        markers.saveMarkers(url);
    }

    public static void createEmptyMarkersFile(File file) {
        Markers markers = new Markers();
        List<MarkerSet> temp = new ArrayList<>(markers.markersList.size());
        for (MarkerSetInterface set : markers.markersList) {
            temp.add((MarkerSet) set);
        }
        if (ResourceDir.saveObjectXML(file, temp)) {
            SlidesPane.out.println("Markers were saved to " + file.getName());
        }
    }

    /**
     * Saves the markers to the given url. If the url doesn't end with ".xml",
     * then it is added automatically. If the markers were save successfully,
     * save the url for future actions
     *
     * @param url A filed to save the markers
     */
    public void saveMarkers(String url) {
        if (url == null) {
            return;
        }
        FileOutputStream out = null;
        try {
            url = url.endsWith(".xml") ? url : url + ".xml";
            FileOutputStream fos = new FileOutputStream(url);
            try (XMLEncoder e = new XMLEncoder(fos)) {
                List<MarkerSet> temp = new ArrayList<>(markersList.size());
                for (MarkerSetInterface set : markersList) {
                    temp.add((MarkerSet) set);
                }

                e.writeObject(temp);
            }
            markersURL = url;
            SlidesPane.out.println("Markers were saved to " + markersURL);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Markers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Saves the markers to the given file. If the file is not an xml file, does
     * nothing.
     *
     * @param url A filed to save the markers
     */
    public void saveMarkers(File file) {
        List<MarkerSet> temp = new ArrayList<>(markersList.size());
        for (MarkerSetInterface set : markersList) {
            temp.add((MarkerSet) set);
        }
        if (ResourceDir.saveObjectXML(file, temp)) {
            SlidesPane.out.println("Markers were saved to " + file.getName());
        }
    }

    File markersFile = null;

    public void saveMarkers() {
        if (markersFile != null) {
            saveMarkers(markersFile);
            return;
        }
        if (markersURL != null) {
            saveMarkers(markersURL);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="OLD">
    /**
     * Updates the list of markers and save it to a file (see saveMarkers(url)).
     * If successful, saves the url for future actions.
     *
     * @param url A filed to save the markers
     * @param markers an array of markers to save
     */
    public void saveMarkers(String url, MarkerSet[] markers) {
        //markerMap = new HashMap<String, /*HashMap<String, Integer>*/ MarkerSet>();
        markersList = new ArrayList<>();
        markersList.addAll(Arrays.asList(markers));
        sortList();
        saveMarkers(url);
    }

    /**
     * Read the markers from a simple url. Each line in the file should have the
     * form name, startAudio, endAudio,startVideo,endVideo
     *
     * @param url The url of the file
     */
    public void setMarkers(String url) {
        markersList = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(url));
            bufferedReader.lines().forEach(s -> {
                String[] line = s.split(",");
                markersList.add(new MarkerSet(line[0], Integer.parseInt(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[3])));
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        setMarkers(markersList);
    }

    /**
     * Set the markers to be the array parameter
     *
     * @param markerSets
     */
    public void setMarkers(MarkerSet[] markerSets) {

        markersList = new ArrayList<>(Arrays.asList(markerSets));
        setMarkers(markersList);

    }

    // </editor-fold>
    /**
     * Attach the list of markers to the audio file
     *
     * @param markerSets
     */
    protected void setMarkers(List<MarkerSetInterface> markerSets) {
        //for the jump function
        markersList = new ArrayList(markerSets.size() * 2);

        if (audioMedia != null) {
            ObservableMap<String, Duration> markers = audioMedia.getMarkers();
            markers.clear();
        }
        markerSets.forEach((markerSet) -> {
            if (createNewMarker(markerSet)) {
                SlidesPane.out.println("Created marker with the name " + markerSet.getMarkerName());
            } else {
                SlidesPane.out.println("couldn't create marker " + markerSet.getMarkerName());
            }
        });

        sortList();

    }

    public void removeEmptyMarkers() {
        //markersList.removeIf(marker -> (marker.getMarkerStart() <= 0));
        Iterator<MarkerSetInterface> it = markersList.iterator();
        while (it.hasNext()) {
            MarkerSetInterface marker = it.next();
            if (marker.getMarkerStart() <= 0) {
                names.replace(marker.getMarkerName(), Boolean.FALSE);
                it.remove();
                if (audioMedia != null) {
                    audioMedia.getMarkers().remove(marker.getMarkerName());
                }
            }
        }
        //setMarkers(markersList);
    }

    private void updateMarker(MarkerSetInterface set) {
        if (set == null || audioMedia == null) {
            return;
        }
        String name = set.getMarkerName();
        for (MarkerSetInterface markerSet : markersList) {
            if (name.equals(markerSet.getMarkerName())) {
                markerSet.copy(set);
                ObservableMap<String, Duration> markers = audioMedia.getMarkers();
                markers.remove(name);
                markers.put(name, Duration.millis(markerSet.getMarkerStart()));
                SlidesPane.out.println("Updated marker " + name);
                break;
            }
        }
    }

    private void sortList() {
        Collections.sort(markersList, (ms1, ms2) -> ms1.getMarkerStart() - ms2.getMarkerStart());
    }

    /**
     *
     * @return a list of the MarkerSet s.
     */
    @Override
    public List<MarkerSetInterface> getMarkersList() {
        return markersList;
    }

    /**
     * If the string is not null, and doesn't end with ".xml", then adds it to
     * the end of the string
     *
     * @param url The url of a file
     * @return the url \ url+".xml". If the url=null then returns null.
     */
    private String xmled(String url) {
        if (url == null) {
            return null;
        }
        if (url.endsWith(".xml")) {
            return url;
        } else {
            return url + ".xml";
        }
    }

    public MarkerSetInterface getMarker(String name) {
        Iterator<MarkerSetInterface> it = markersList.iterator();
        while (it.hasNext()) {
            MarkerSetInterface next = it.next();
            if (next.getMarkerName().equals(name)) {
                return next;
            }
        }
        return null;
    }

    public int getMarkerIndex(String name) {
        Iterator<MarkerSetInterface> it = markersList.iterator();
        int index = 0;
        while (it.hasNext()) {
            MarkerSetInterface next = it.next();
            if (next.getMarkerName().equals(name)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * Return the marker at the given index. If the index is out of bounds,
     * return null
     *
     * @param index
     * @return
     */
    public MarkerSetInterface getMarkerByIndex(int index) {
        if (index < 0 || index >= markersList.size()) {
            return null;
        }
        return markersList.get(index);
    }

    public boolean hasMarker(String name) {
        return getMarkerIndex(name) > -1;
    }

    public MarkerSet createNewMarker(String name) {
        MarkerSet set = new MarkerSet();
        set.setMarkerName(name);
        if (createNewMarker(set)) {
            return set;
        } else {
            return null;
        }
    }

    boolean createNewMarker(MarkerSetInterface set) {
        Boolean isUsed = names.get(set.getMarkerName());
        if (isUsed == null) { //not a possible name
            return false;
        }
        if (isUsed) { //already using this name
            return false;
        }
        //name is free to use
        //set.update();
        //SlidesPane.out.println("Why did I have this update here?!");
        
        names.replace(set.getMarkerName(), Boolean.TRUE);
        markersList.add(set);
        if (audioMedia != null) {
            ObservableMap<String, Duration> markers = audioMedia.getMarkers();
            markers.put(set.getMarkerName(), Duration.millis(set.getMarkerStart()));
        }
        set.markerStartProperty().addListener((observable, oldValue, newValue) -> {
            updateMarkerStart(set);
        });
        return true;
    }

    private void updateMarkerStart(MarkerSetInterface set) {
        //if the marker still exists, then update it.
        //should later change it so that when we remove the marker, also remove this listener
        if (audioMedia != null) {
            ObservableMap<String, Duration> markers = audioMedia.getMarkers();
            if (markers.remove(set.getMarkerName()) != null) {
                markers.put(set.getMarkerName(), Duration.millis(set.getMarkerStart()));
            }
        }
        sortList();
    }

    public void setPossibleName(List<String> animationNames) {
        if (animationNames == null) {
            return;
        }
        names.clear();
        animationNames.forEach(name -> {
            names.put(name, Boolean.FALSE);
        });
    }

    /**
     *
     * @return a list of possible name sorted alphabetically
     */
    public List<String> getPossibleNames() {
        List<String> posNames = new ArrayList(names.size());
        names.forEach((name, bool) -> {
            if (!bool) {
                posNames.add(name);
            }
        });
        Collections.sort(posNames);
        return posNames;
    }

    public int size() {
        return markersList.size();
    }

}
