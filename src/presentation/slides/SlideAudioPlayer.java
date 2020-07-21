/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.slides;

import java.net.URL;
import javafx.scene.media.Media;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author eofir
 */
public class SlideAudioPlayer {
    
    private String audioURL;
    private int audioStart;
    private int audioEnd;
    
    private void loadAudio(){
        setAudio(audioURL);
        setAudioStart(audioStart);
        setAudioEnd(audioEnd);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Audio">
    protected boolean audioMode;
    protected Media audioMedia;
    protected MediaPlayer audioPlayer;

    /**
     * This function is called when the audio ends.
     */
    protected void endOfAudio() {
    }

    /**
     * Add the url for an audio file to this slide.
     *
     * @param url The url String to the audio file. For example
     * "/PackageName/resources/narration/slide0.mp3".
     */
    protected void setAudio(String url) {
        if (url == null) {
            return;
        }
        if ("".equals(url)) {
            return;
        }
        audioURL = url;
        reloadAudio();
    }

    void reloadAudio() {
//        if (audioPlayer!=null){
//            audioPlayer.stop();
//        }
//        URL resource = getClass().getResource(audioURL);
//        audioMedia = new Media(resource.toExternalForm());
//        markers.setAudioMedia(audioMedia);
//        audioPlayer = new MediaPlayer(audioMedia);
//
//        audioPlayer.setOnEndOfMedia(() -> {
//            if (slidesPane != null) {
//                endOfAudio();
//                slidesPane.finishedSlide(Slide.this);
//            }
//        });
//
//        audioPlayer.setOnMarker((MediaMarkerEvent evt) -> {
//            String name = evt.getMarker().getKey();
//            runMethodByName(name, 0);
//        });
    }

    /**
     * Set the time when the audio file begins the playing
     *
     * @param audioStart starting time in millis
     */
    public void setAudioStart(int audioStart) {
        if (audioPlayer != null) {
            audioPlayer.setStartTime(Duration.millis(audioStart));
        }
    }

    /**
     * Set the time when the audio file ends the playing. When it ends, the
     * function endOfAudio() is called.
     *
     * @param audioEnd ending time in millis
     */
    public void setAudioEnd(int audioEnd) {
        if (audioPlayer != null) {
            audioPlayer.setStopTime(Duration.millis(audioEnd));
        }
    }

    public double getAudioEnd() {
        if (audioPlayer != null) {
            return audioPlayer.getStopTime().toMillis();
        }
        return -1;
    }

    /**
     * Sets the rate in which the audio is played
     *
     * @param rate
     */
    void setAudioRate(double rate) {
        if (audioPlayer != null && rate > 0) {
            audioPlayer.setRate(rate);
        }
    }

    /**
     * Jump back to the beginning of the audio.
     */
    void restartAudio() {
        if (audioMode && audioPlayer != null) {
            audioPlayer.seek(audioPlayer.getStartTime());
        }
    }

    public void reloadTrack() {
        loadAudio();
    }
    
    // </editor-fold>
    
}
