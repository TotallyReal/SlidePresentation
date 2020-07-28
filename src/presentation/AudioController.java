/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation;

import java.util.Iterator;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import presentation.markers.MarkerSetInterface;
import presentation.markers.Markers;
import presentation.slides.Slide;
import presentation.slides.SlidesPane;
import presentation.video.VideoAnimation;

/**
 *
 * @author eofir
 */
public class AudioController {

  Slide slide;
  SlidesPane slidePane;
  protected Media audioMedia;
  protected MediaPlayer audioPlayer;
  protected Markers markers;
  protected boolean playing;

  /**
   * Return the time in millis from the start of the audio file (not the start
   * of the playing).
   *
   * @return time from start in millis
   */
  public int getTime() {
    if (audioPlayer != null) {
      return (int) audioPlayer.getCurrentTime().toMillis();
    }
    return 0;
  }

  /**
   * Jump by @seconds (i.e. to current time+second)
   *
   * @param seconds
   */
  public void jumpSeconds(double seconds) {
    jumpMillis(seconds * 1000);
  }

  /**
   * Jump by @millis (i.e. to current time+millis)
   *
   * @param millis
   */
  public void jumpMillis(double millis) {
    if (audioPlayer != null) {
      double time = audioPlayer.getCurrentTime().toMillis();
      jumpToTime((int) (time + millis));
    }
  }

  /**
   *
   * Jump to time millis from the start of the audio file
   *
   * @param millis
   */
  public void jumpToTime(double millis) {
    if (millis < 0) {
      millis = 0;
    }
    if (markers != null) {
    /*  boolean nowPlaying = isPlaying();
      pause();
      for (Animation anim : currentAnimation) {
        anim.stop();
      }
      slide.currentAnimation.clear();*/
//            if (currentAnimation != null) {
//                currentAnimation.stop();
//            }
      Duration total = audioPlayer.getTotalDuration();
      if (total.lessThan(Duration.millis(millis))) {
        return;
      }
      SlidesPane.out.println("\n--------------------------\n");
      SlidesPane.out.println("jumping to time " + millis + ".");
      slide.restartVisuals();
      Iterator<MarkerSetInterface> it;
      it = markers.getMarkersList().iterator();
      while (it.hasNext()) {
        MarkerSetInterface next = it.next();
        if (next.getMarkerStart() < millis) {
          slide.runMethodByName(next.getMarkerName(), millis - next.getMarkerStart());
        } else {
          break;
        }
      }
      audioPlayer.seek(Duration.millis(millis));
    /*  if (nowPlaying) {
     //   play();
      } else {
      //  pause();
      }*/
    }
  }

  /**
   * Pause if play and play if paused
   */
  public void play_pause() {
    if (playing) {
    //  pause();
    } else {
     // play();
    }
  }

  /**
   * Pause the playing of the audio (if is being played)
   */
 /* public void pause() {
    audioPlayer.pause();
    for (Animation anim : currentAnimation) {
      anim.pause();
    }
    for (VideoAnimation video : currentVideo) {
      video.pause();
    }
    SlidesPane.out.println("Pause");
    playing = false;
  }
*/
  /**
   * Start the playing of the audio (if is paused)
   */
/*  public void play() {
    if (audioMode) {
      audioPlayer.play();
    }
    for (Animation anim : currentAnimation) {
      anim.play();
    }
    for (VideoAnimation video : currentVideo) {
      video.unpause();
    }
    SlidesPane.out.println("Play");
    playing = true;
  }

  public BooleanProperty playingProperty() {
    return playingProp;
  }
*/
}
