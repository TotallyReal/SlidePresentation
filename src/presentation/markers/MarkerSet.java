/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.markers;

//import animationEditor.animationData.AnimationData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;

import presentation.animation.AnimationData;

/**
 *
 * @author eofir
 */
public class MarkerSet implements MarkerSetInterface {

    private final StringProperty markerName = new SimpleStringProperty("empty_marker");
    public String name = "empty_marker";
    //delete these integer variables. make sure this doesn't affect saved files.
    public int audioMarkerStart = 0;
    public int audioMarkerEnd = 0;
    private AnimationData animData;
    //need to remove the video markers. need to be careful since there are 
    //files which already saved this type of class
    public int videoMarkerStart = 0;
    public int videoMarkerEnd = 0;

    public MarkerSet() {
        this("empty_marker", 0, -1, 0, 0);
    }

    public void newVersion() {
        audioMarkerStart = 0;
        audioMarkerEnd = 0;
        videoMarkerStart = 0;
        videoMarkerEnd = 0;
        if (name != null) {
            if (!name.equals("empty_marker")) {
                markerName.setValue(name);
            }
        }
        name = "empty_marker";
    }

    @Override
    public void setMarkerName(String name) {
        markerName.set(name);
    }

    @Override
    public String getMarkerName() {
        return markerName.get();
    }

    public StringProperty markerNameProperty() {
        return markerName;
    }

    /**
     * Create a new marker set.
     *
     * This marker will activate when the audio reaches audioMarkerStart (in
     * millies). It will then play the video part between videoMarkerStart and
     * videoMarkerEnd.
     *
     * If the time needed for that is longer then the time between
     * audioMarkerStart and audioMarkerEnd, then the video will be speeded up so
     * it would be contained properly. If it takes less time, than it is played
     * in the regular speed.
     *
     *
     * @param name The name of the Marker
     * @param audioMarkerStart Audio start in millis
     * @param audioMarkerEnd Audio end in millis. Put here -1 if you want the
     * audio end marker to be the audio beginning of the next marker
     * @param videoMarkerStart Video start in millis
     * @param videoMarkerEnd Video end in millis
     */
    public MarkerSet(String name, int audioMarkerStart, int audioMarkerEnd,
            int videoMarkerStart, int videoMarkerEnd) {
        //this.name = name;
//        this.audioMarkerStart = audioMarkerStart;
//        this.audioMarkerEnd = audioMarkerEnd;
//        this.videoMarkerStart = videoMarkerStart;
//        this.videoMarkerEnd = videoMarkerEnd;
        setMarkerName(name);
        setMarkerStart(audioMarkerStart);
        setMarkerEnd(audioMarkerEnd);
    }

    /**
     * Create a new marker set.
     *
     * This marker will activate when the audio reaches audioMarkerStart (in
     * millies). He will then play the video part between videoMarkerStart and
     * videoMarkerEnd.
     *
     * If the time needed for that is longer then the time between
     * audioMarkerStart and the audio start of the next marker, then the video
     * will be speeded up so it would be contained properly. If it takes less
     * time, than it is played in the regular speed.
     *
     * @param name The name of the Marker
     * @param audioMarker Audio start in millis. Audio end will be determined by
     * the next marker's audio start
     * @param videoMarkerStart Video start in millis
     * @param videoMarkerEnd Video end in millis
     */
    public MarkerSet(String name, int audioMarker, int videoMarkerStart, int videoMarkerEnd) {
        this(name, audioMarker, -1, videoMarkerStart, videoMarkerEnd);
    }

    public void copy(MarkerSetInterface set) {
        //this.name = set.getMarkerName();
//        this.audioMarkerStart = set.getAudioMarkerStart;
//        this.audioMarkerEnd = set.audioMarkerEnd;
//        this.videoMarkerStart = set.videoMarkerStart;
//        this.videoMarkerEnd = set.videoMarkerEnd;
        setMarkerName(set.getMarkerName());
        setMarkerStart(set.getMarkerStart());
        setMarkerEnd(set.getMarkerEnd());
        setCompressed(set.isCompressed());
    }

    private final IntegerProperty markerStart = new SimpleIntegerProperty(0);
    private final IntegerProperty markerEnd = new SimpleIntegerProperty(-1);
    private final BooleanProperty exact = new SimpleBooleanProperty(false);

    public boolean isExact() {
        return exact.get();
    }

    public void setExact(boolean value) {
        exact.set(value);
    }

    public BooleanProperty exactProperty() {
        return exact;
    }

    @Override
    public int getMarkerStart() {
        return markerStart.get();
    }

    @Override
    public final void setMarkerStart(int value) {
        markerStart.set(value);
    }

    @Override
    public IntegerProperty markerStartProperty() {
        return markerStart;
    }

    @Override
    public int getMarkerEnd() {
        return markerEnd.get();
    }

    @Override
    public final void setMarkerEnd(int value) {
        markerEnd.set(value);
    }

    @Override
    public IntegerProperty markerEndtProperty() {
        return markerEnd;
    }

//    public void update() {
//        if (audioMarkerStart > 0) {
//            setMarkerStart(audioMarkerStart);
//        }
//        if (audioMarkerEnd > audioMarkerStart) {
//            setMarkerEnd(audioMarkerEnd);
//        }
//        setExact(false);
//    }
    final private ReadOnlyObjectProperty<Status> status = new SimpleObjectProperty();

    @Override
    public void setStatus(ReadOnlyObjectProperty<Animation.Status> status) {
        if (status != null) {
            ((ObjectProperty) this.status).bind(status);
        }
    }

    @Override
    public ReadOnlyObjectProperty<Status> statusProperty() {
        return status;
    }
    private final BooleanProperty compressed = new SimpleBooleanProperty(false);

    @Override
    public boolean isCompressed() {
        return compressed.get();
    }

    @Override
    public void setCompressed(boolean value) {
        compressed.set(value);
    }

    public BooleanProperty compressedProperty() {
        return compressed;
    }

    @Override
    public AnimationData getAnimationData() {
        return animData;
    }

    @Override
    public void setAnimationData(AnimationData animData) {
        this.animData = animData;
    }


}
