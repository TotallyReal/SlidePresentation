package presentation.slides;

//import animationEditor.animationData.AnimationData;
//import animationEditor.objectData.AnimationNode;
//import animationEditor.objectData.ObjectData;
//import animationEditor.objectData.SimpleObjectData;
//import animationEditor.animationData.AnimationData;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

// presentation imports
import presentation.markers.Markers;
import presentation.markers.MarkerSet;
import presentation.markers.MarkerSetInterface;
import presentation.DragPane;
import presentation.Parameter.ParamNode;
import presentation.Parameter.Parameter;
import presentation.animation.AnimationData;
import presentation.animation.AnimationNode;
import presentation.animation.ObjectData;
import presentation.animation.SimpleObjectData;
import presentation.video.VideoAnimation;

/**
 *
 * @author eofir
 */
public abstract class Slide implements AnimationNode {

  public static final int PREF_WIDTH = 1280;
  public static final int PREF_HEIGHT = 720;

  private double RATE = 1;

  private int step = 0;
  protected DragPane mainPane;
  protected boolean videoMode; //need to get rid of this
  BooleanProperty playingProp = new SimpleBooleanProperty(false);
  private double height, width;

  protected Markers markers;

  public static enum Status {
    EMPTY,
    INITIALIZED,
    STARTED,
  }

  protected Status status = Status.EMPTY;

  public Slide(double prefWidth, double prefHeight) {
    mainPane = new DragPane();
//        mainPane.setPrefWidth(prefWidth);
//        mainPane.setPrefHeight(prefHeight);
//        mainPane.setMinSize(prefWidth, prefHeight);
    height = prefHeight;
    width = prefWidth;
    markers = new Markers();

    playingProp.addListener(new ChangeListener() {
      @Override
      public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if (newValue == Boolean.TRUE) {
          play();
        } else {
          pause();
        }
      }

    });

  }

  public Slide() {
    this(1220, 690);
  }

  public boolean isEnglish() {
    return false;
  }

  public double getHeight() {
    Scene scene = mainPane.getScene();
    if (scene != null) {
      return scene.getHeight();
    }
    return height;
  }

  public double getWidth() {
    Scene scene = mainPane.getScene();
    if (scene != null) {
      return scene.getWidth();
    }
    return width;
  }

  protected void centerNode(Node node) {
    Bounds bb = node.getBoundsInParent();
    double xx = bb.getMinX() + bb.getWidth() / 2;
    double yy = bb.getMinY() + bb.getHeight() / 2;
    node.setLayoutX(node.getLayoutX() + getWidth() / 2 - xx);
    node.setLayoutY(node.getLayoutY() + getHeight() / 2 - yy);
    // MOB.centerNodeAt(node, getWidth() / 2, getHeight() / 2);
  }

  protected void simpleCenter(Node node) {
    node.setTranslateX(getWidth() / 2);
    node.setTranslateY(getHeight() / 2);
  }

  protected void centerNodes(Node... nodes) {
    if (nodes != null) {
      for (Node node : nodes) {
        centerNode(node);
      }
    }
  }

  protected void centerMainPane() {
    mainPane.setLayoutX(getWidth() / 2);
    mainPane.setLayoutY(getHeight() / 2);
  }

  private SlideData data;

  protected Parameter getParameter() {
    return slidesPane.getParameter();
  }

  protected Object C(String paramName, Object def) {
    return slidesPane.getParameter().C(paramName, def);
  }

  protected Object C(String paramName) {
    return slidesPane.getParameter().C(paramName);
  }

  protected boolean setParameter(String paramName, Object obj) {
    return slidesPane.getParameter().setParameterByPath(paramName, obj);
  }

  /**
   * loads the slide from the data in the file getSlideDataURL();
   */
  public void loadSlide() {
    ResourceDir resDir = slidesPane.getResourceDir();
    data = null;
    if (resDir != null) {
      String name = getSlideName();
      data = SlideData.load(resDir, name);

      loadAudio(resDir.getNarrationFile(name));

      markers.setPossibleName(getAnimationNames());
      markers.loadMarkers(resDir.getMarkersFile(name));
    } else {
      String dataURL = getSlideDataURL();
      if (dataURL == null) {
        return;
      } else {
        data = SlideData.load(dataURL);
      }

      loadAudio();

      markers.setPossibleName(getAnimationNames());
      markers.loadMarkers(data.markerURL);
    }
  }

  public void saveSlide() {
    if (audioPlayer != null) {
      data.setAudioStart((int) audioPlayer.getStartTime().toMillis());
      data.setAudioEnd((int) audioPlayer.getStopTime().toMillis());
    }
    ResourceDir resDir = slidesPane.getResourceDir();
    if (resDir != null) {
      data.save(resDir.getDataFile(getSlideName()));
    } else {
      String dataURL = getSlideDataURL();
      if (dataURL != null) {
        data.save(dataURL);
      }
    }
  }

  private void loadAudio(File audioFile) {
    if (audioFile != null && audioFile.exists()) {
      setAudio(audioFile);
      setAudioStart(data.audioStart);
      setAudioEnd(data.audioEnd);
    }
  }

  private void loadAudio() {
    setAudio(data.audioURL);
    setAudioStart(data.audioStart);
    setAudioEnd(data.audioEnd);
  }

  /**
   * Called every time the slide starts from the beginning. It then runs 1.
   *
   * @initText (if it is not in video mode) 2. @initMedia 3. @initVisuals
   *
   * Then, if this slide is on Audio mode, it starts playing the audio. Finally,
   * it makes the slide visible.
   *
   */
  public void startSlide() {

    mainPane.setVisible(true);
    mainPane.applyCss();
    mainPane.layout();
    restartVisuals();
    if (audioMode && audioPlayer != null) {
      audioPlayer.seek(audioPlayer.getStartTime());
      audioPlayer.play();
    }
  }

  /**
   * Returns the list of names of all the declared methods which returns an
   * object which is either a subclass of Animation or of VideoAnimation.
   *
   * @return
   */
  private List<String> getAnimationNames() {
    Method[] methods = this.getClass().getDeclaredMethods();
    Stream<Method> stream = Arrays.stream(methods);
    List<String> names = stream.filter(obj -> {
      Method method = (Method) obj;
      if (!Modifier.isPublic(method.getModifiers())) {
        return false;
      }
      Class<?> returnType = method.getReturnType();

      return (Animation.class.isAssignableFrom(returnType)
              || VideoAnimation.class.isAssignableFrom(returnType));
    }).map(method -> method.getName()).collect(Collectors.toList());
    return names;
  }

  /**
   * Called when the slide is being closed.
   *
   * Stops the audio, clears the slide and hides it.
   */
  public void closeSlide() {
    if (audioMode) {
      audioPlayer.stop();
    }
    mainPane.setVisible(false);
    mainPane.getChildren().clear();
    status = Status.EMPTY;
    step = 0;
  }

  /**
   * Clear and rebuild all the visual components using initVisuals()
   */
  public void restartVisuals() {
    step = 0;
    if (status != Status.INITIALIZED) {
      mainPane.getChildren().clear();
      currentAnimation.clear();
      currentVideo.clear();

      //need to remove videoMode entirely
      if (!videoMode) {
        initText();
      }

      initVisuals();
      status = Status.INITIALIZED;
    }

  }

  public Image getImage(String name) {
    return slidesPane.getImage(name);
  }

  public File getImageLib() {
    return slidesPane.getImageLib();
  }

  /**
   * Initialize the visuals.
   *
   * Should create here all the visual components of the slide, and possibly
   * some of the effects.
   */
  protected void initVisuals() {

  }

  /**
   * Initialize text based components.
   *
   * In case you don't use video recorded writing, this is the place to
   * initialize the text based components. Will not be called if videoMode =
   * true.
   */
  protected void initText() {

  }

  /**
   * clear the slide from the visual objects and stop the audio if is playing.
   */
  public void clearSlide() {
    mainPane.getChildren().clear();
    step = 0;
    if (audioMode) {
      audioPlayer.stop();
    }
  }

  /**
   *
   * @return the main pane on which the objects of this slide can be viewed
   */
  public DragPane getPane() {
    return mainPane;
  }

  /**
   * Runs the next animation of this slide. The order of the methods is by their
   * audioStart
   *
   * @return true if there is a next step. If in audio mode then return false
   */
  public boolean nextAnimation() {
    if (audioMode) {
      return false;
    }
    MarkerSetInterface marker = markers.getMarkerByIndex(step);
    if (marker != null) {
      runMethodByName(marker.getMarkerName(), 0);
      step++;
      if (step < markers.size()) {
        return true;
      }
    }
    return false;
  }

  public boolean previousAnimation() {
    if (step == 0) {
      //go to previous slide
      return false;
    }
    jumpToAnimation(step - 1);
    return 0 != step;
  }

  /**
   * Jump to the animation given in the index.
   *
   * If before is true, then jumps to before the animation. If the index is
   * negative, jumps to the beginning, and if it is bigger than the number of
   * animations jump to the end.
   *
   * @param index The index of the animation to jump to before it.
   */
  public void jumpToAnimation(int index) {

    restartVisuals();
    if (index < 0) {
      step = 0;
      return;
    }
    if (index > markers.size()) {
      step = markers.size();
    } else {
      step = index;
    }
    int counter = 0;
    Iterable<MarkerSetInterface> animList = markers.getMarkersList();
    Iterator<MarkerSetInterface> it = animList.iterator();
    while (counter < step) {
      MarkerSetInterface next = it.next();
      runMethodByName(next.getMarkerName(), 5000000);
      counter++;
    }
  }

  boolean playing = true;
  protected double sqSize = 35;

  protected double getFullScreenWidth() {
    return sqSize * 35;
  }

  protected double getFullScreenHeight() {
    return sqSize * 20;
  }

  /**
   *
   * @return the rate in which the audio and animations are played
   */
  public double getRate() {
    return RATE;
  }

  /**
   * Set the rate in which the audio and animations are played
   *
   * @param t
   */
  public void setRate(double t) {
    if (t > 0) {
      double oldRate = RATE;
      RATE = t;
      if (audioMode) {
        audioPlayer.setRate(t);
      }
      for (Animation anim : currentAnimation) {
        anim.setRate(anim.getRate() * RATE / oldRate);
      }
      for (VideoAnimation video : currentVideo) {
        video.setRate(video.getRate() * RATE / oldRate);
      }
    }
  }

  /**
   * Needs to return a url with the SlideData of this slide. Later on, this
   * should be done via an interface. For the first time, first use
   * SlideData.save.
   *
   * @return
   */
  protected abstract String getSlideDataURL();

  protected String getSlideName() {
    return getClass().getSimpleName();
  }
  //------------------------------------------------------------------
  // <editor-fold defaultstate="collapsed" desc="Animation">
  //private List<Method> methods;
  private List<Animation> currentAnimation = new ArrayList<>(4);
  private List<VideoAnimation> currentVideo = new ArrayList<>(4);

  /**
   * Search for the method with the given name and runs it. Runs the method
   * starting from time millis. Use millis = 0 to run from the start. If
   * millis>total duration, than just sumps to the end of animation.
   *
   * The return type of the method should only be void or a type of animation.
   * In the case it is an animation, it is automatically played (see
   *
   * @runMethod).
   *
   * @param markerName The name of the method to be run.
   * @param millis the time in millis from which to start the animation. use
   * millis=0 to run from the start.
   */
  //problem with marker which are not video related inside a video!!!
  public void runMethodByName(String markerName, double millis) {   //change back to private?
    Method method;
    //try {
    //method = aClass.getDeclaredMethod(markerName);
//            runMethod(method, markerName, millis);
//        } catch (NoSuchMethodException | SecurityException ex) {
//            Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
//        }
    //}

    /**
     * Runs the method from time millis. If millis is nonpositive, runs from the
     * start. If millis>total duration, than jumps to the end of the animation.
     * Otherwise just jump to millis and start the animation from there.
     *
     * The return type of the method should only be void or a type of animation.
     * In the case it is an animation, it is automatically played.
     *
     * Remark: should update so that the play would fit the narration.
     *
     * @param method the method to be run
     * @param millis the time in millis from which to start the animation. use
     * millis=0 to run from the start.
     */
    //private void runMethod(Method method, String markerName, double millis) {
//        try {
//            status = Status.STARTED;
//            SlidesPane.out.println("\n----------------------------");
//            SlidesPane.out.println("running " + markerName);
//            MarkerSetInterface marker = markers.getMarker(markerName);
//            AnimationData data = marker.getAnimationData();
//            Animation anim = null;
//            //create the animation
//            if (data != null) {
//                anim = data.createAnimation((AnimationNode) this);
//            } else {
//                method = getClass().getDeclaredMethod(markerName);
//                Object obj = method.invoke(this);
//                if (obj != null) {
//                    if (obj instanceof VideoAnimation) {
//                        VideoAnimation video = (VideoAnimation) obj;
//                        video.jumpTo(millis);
//                        if (audioMode && marker.isCompressed()) {
//                            double maxTime = getAnimationMaxTime(markerName);
//                            if (maxTime == 0) { //never should happen, but just in case
//                                return;
//                            }
//                            video.compressTo(maxTime, false);
//                        }
//                        video.setRate(video.getRate() * getRate());
//                        video.play();
//                        SlidesPane.out.println("Should set marker status for video animation");
//
//                        currentVideo.add(video);
//                        video.setOnFinished(() -> currentVideo.remove(video));
//                        SlidesPane.out.println("On finish for video animation?????");
//                    }
//                }
//                if (obj instanceof Animation) {
//                    anim = (Animation) obj;
//                }
//            }
//            //edit animation according to time constraints
//            if (anim != null) {
//                anim.jumpTo(Duration.millis(millis));
//                if (audioMode && marker.isCompressed()) { //compress the animation
//                    double maxTime = getAnimationMaxTime(markerName);
//                    if (maxTime == 0) { //never should happen, but just in case
//                        return;
//                    }
//                    double total = anim.getTotalDuration().toMillis();
//                    if (total > maxTime) {
//                        anim.setRate(anim.getRate() * total / maxTime);
//                    }
//                }
//                anim.setRate(anim.getRate() * getRate());
//                if (anim.getCurrentTime().compareTo(anim.getTotalDuration()) < 0) {
//                    currentAnimation.add(anim);
//                    anim.setOnFinished(evt -> currentAnimation.remove((Animation) evt.getSource()));
//                } else {
//                    anim.setOnFinished(evt -> {
//                        SlidesPane.out.println("finished " + markerName + "\n");
//                    });
//                }
//
//                anim.play();
//                marker.setStatus(anim.statusProperty());
//            }
//            //SlidesPane.out.println("finished " + method.getName() + "\n");
//        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
//                | NoSuchMethodException | SecurityException ex) {
//            Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
//        }
    MarkerSetInterface marker = markers.getMarker(markerName);
    Animation anim = createMarkerAnimation(marker, millis);
//        anim.jumpTo(anim.getCurrentTime().subtract(Duration.ONE));
    if (anim != null) {
      if (anim.getCurrentTime().compareTo(anim.getTotalDuration()) < 0) {
        currentAnimation.add(anim);
        anim.setOnFinished(evt -> currentAnimation.remove((Animation) evt.getSource()));
      } else {
        anim.setOnFinished(evt -> {
          SlidesPane.out.println("finished " + markerName + "\n");
        });
      }
    }
//        marker.setStatus(anim.statusProperty());
//        anim.play();
  }

  private Animation createMarkerAnimation(MarkerSetInterface marker, double millis) {
    if (marker == null) {
      return null;
    }
    status = Status.STARTED;
    AnimationData data = marker.getAnimationData();
    Animation anim = null;
    //create the animation
    SlidesPane.out.println("\n----------------------------");
    SlidesPane.out.println("running " + marker.getMarkerName());
    if (data != null) {
      anim = data.createAnimation((AnimationNode) this);
    } else {
      try {
        Method method = getClass().getDeclaredMethod(marker.getMarkerName());
        Object obj = method.invoke(this);
        if (obj != null) {
          if (obj instanceof VideoAnimation) {
            VideoAnimation video = (VideoAnimation) obj;
            video.jumpTo(millis);
            if (audioMode && marker.isCompressed()) {
              double maxTime = getAnimationMaxTime(marker.getMarkerName());
              if (maxTime == 0) { //never should happen, but just in case
                return null;
              }
              video.compressTo(maxTime, false);
            }
            video.setRate(video.getRate() * getRate());
            video.play();
            SlidesPane.out.println("Should set marker status for video animation");

            currentVideo.add(video);
            video.setOnFinished(() -> currentVideo.remove(video));
            SlidesPane.out.println("On finish for video animation?????");
          }
        }
        if (obj instanceof Animation) {
          anim = (Animation) obj;
        }
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException
              | IllegalArgumentException | InvocationTargetException ex) {
        Logger.getLogger(Slide.class
                .getName()).log(Level.SEVERE, null, ex);
      }
    }
    //edit animation according to time constraints
    if (anim != null) {
      double FAST_RATE = 5000;
      if (millis > 1) {     //instead of jumpTo()
        //anim.jumpTo(Duration.millis(millis));
        if (anim.getTotalDuration().toMillis() <= millis || !audioMode) {
          anim.setRate(FAST_RATE);
          anim.play();
        } else {
          double animRate = anim.getRate();
          if (marker.isCompressed()) { //compress the animation
            double maxTime = getAnimationMaxTime(marker.getMarkerName());
            if (maxTime == 0) { //never should happen, but just in case
              return null;
            }
            double total = anim.getTotalDuration().toMillis();
            if (total > maxTime) {
              animRate = animRate * total / maxTime;
            }
          }
          anim.setRate(FAST_RATE);
          Animation envelope = anim;
          double ANIM_RATE = animRate;
          marker.setStatus(anim.statusProperty());
          anim.currentTimeProperty().addListener((obj, oldValue, newValue) -> {
            if (envelope.getCurrentTime().toMillis() >= millis) {
              envelope.setRate(ANIM_RATE * getRate());
            }
          });
        }
        return anim;
      } else {
        if (marker.isCompressed()/* && audioMode*/) { //compress the animation
          double maxTime = getAnimationMaxTime(marker.getMarkerName());
          if (maxTime == 0) { //never should happen, but just in case
            return null;
          }
          double total = anim.getTotalDuration().toMillis();
          if (total > maxTime) {
            anim.setRate(anim.getRate() * total / maxTime);
          }
        }
        anim.setRate(anim.getRate() * getRate());
        marker.setStatus(anim.statusProperty());
        anim.play();
        return anim;
      }

    }
    return null;
  }

  private void animJumpTo(Animation anim, double millis) {
    if (millis < 1) {
      return;
    }
    if (anim.getTotalDuration().toMillis() <= millis) {
      anim.setRate(RATE);
    }
  }

  /**
   * return in millis the max time allowed for this marker.
   *
   * This is the minimum time from the start of the current marker up to the
   * minimum between the end of the markerEnd (if exists), the marker beginning
   * of the next marker (if exists) and the end of the audio.
   *
   * @param markerName
   * @return
   */
  private double getAnimationMaxTime(String markerName) {
    int index = markers.getMarkerIndex(markerName);
    if (index == -1) {
      return -1;
    }
    MarkerSetInterface current = markers.getMarkerByIndex(index);
    double diff = current.getMarkerEnd() - current.getMarkerStart();
    if (!audioMode) {
      return diff;
    }
    MarkerSetInterface next = markers.getMarkerByIndex(index + 1);
    double time = getAudioEnd() - current.getMarkerStart();
    if (0 < diff && diff < time) {
      return diff;
    }
    if (next != null) {
      diff = next.getMarkerStart() - current.getMarkerStart();
      if (0 < diff && diff < time) {
        return diff;
      }
    }
    return time;
  }

  // </editor-fold>
  //------------------------------------------------------------------
  // <editor-fold defaultstate="collapsed" desc="Audio">
  protected boolean audioMode;
  protected Media audioMedia;
  protected MediaPlayer audioPlayer;

  /**
   * This function is called when the audio ends.
   */
  protected void endOfAudio() {
  }

  private String audioURL;

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

  protected void setAudio(File audioFile) {
    reloadAudio();
  }

  void reloadAudio() {
    if (audioPlayer != null) {
      audioPlayer.stop();
    }
    ResourceDir resDir = slidesPane.getResourceDir();
    if (resDir != null) {
      File narrationFile = resDir.getNarrationFile(getSlideName());
      audioMedia = new Media(narrationFile.toURI().toString());
    } else {
      URL resource = getClass().getResource(audioURL);
      audioMedia = new Media(resource.toExternalForm());
    }
    markers.setAudioMedia(audioMedia);
    audioPlayer = new MediaPlayer(audioMedia);

    audioPlayer.setOnEndOfMedia(() -> {
      if (slidesPane != null) {
        endOfAudio();
        slidesPane.finishedSlide(Slide.this);
      }
    });

    audioPlayer.setOnMarker((MediaMarkerEvent evt) -> {
      String name = evt.getMarker().getKey();
      runMethodByName(name, 0);
    });
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

  public int getAudioEnd() {
    if (audioPlayer != null) {
      return (int) audioPlayer.getStopTime().toMillis();
    }
    return -1;
  }

  public int getAudioStart() {
    if (audioPlayer != null) {
      return (int) audioPlayer.getStartTime().toMillis();
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
  //------------------------------------------------------------------
  // <editor-fold defaultstate="collapsed" desc="Markers">
  /**
   * Leave this function here to save the markers from old version slides. Once
   * this is done, delete this function.
   *
   * @param url
   * @param markerSets
   */
  protected void saveMarkers(String url, MarkerSet[] markerSets) {
    markers.saveMarkers(url, markerSets);
  }

  public Markers getMarkers() {
    return markers;
  }

  // </editor-fold>
  //------------------------------------------------------------------
  // <editor-fold defaultstate="collapsed" desc="Time manipulation - Audio Mode">
  /**
   * Return the time in millis from the start of the audio file (not the start
   * of the playing).
   *
   * @return time from start in millis
   */
  public int getTime() {
    if (audioMode && audioPlayer != null) {
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
    if (audioMode && audioPlayer != null) {
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
    if (audioMode && markers != null) {
      boolean nowPlaying = isPlaying();
      pause();
      for (Animation anim : currentAnimation) {
        anim.stop();
      }
      currentAnimation.clear();
//            if (currentAnimation != null) {
//                currentAnimation.stop();
//            }
      Duration total = audioPlayer.getTotalDuration();
      if (total.lessThan(Duration.millis(millis))) {
        return;
      }
      SlidesPane.out.println("\n--------------------------\n");
      SlidesPane.out.println("jumping to time " + millis + ".");
      restartVisuals();
      Iterator<MarkerSetInterface> it;
      it = markers.getMarkersList().iterator();
      while (it.hasNext()) {
        MarkerSetInterface next = it.next();
        if (next.getMarkerStart() < millis) {
          runMethodByName(next.getMarkerName(), millis - next.getMarkerStart());
        } else {
          break;
        }
      }
      audioPlayer.seek(Duration.millis(millis));
      if (nowPlaying) {
        play();
      } else {
        pause();
      }
    }
  }

  /**
   * Jump back to the beginning of the slide
   */
  public void toBeginning() {
    if (status != Status.INITIALIZED) {
      restartVisuals();
    }
    restartAudio();
  }

  /**
   * Jump to the end of the slide. In audio mode, play all the way to the end
   * and pause. In click mode, play all the animations.
   */
  void toEnd() {

    if (audioMode) {
      //jumpToTime(?????)
    } else {
      jumpToAnimation(markers.size());
    }
  }

  /**
   * Pause if play and play if paused
   */
  public void play_pause() {
    if (playing) {
      pause();
    } else {
      play();
    }
  }

  /**
   * Pause the playing of the audio (if is being played)
   */
  public void pause() {
    if (audioMode) {
      audioPlayer.pause();
    }
    for (Animation anim : currentAnimation) {
      anim.pause();
    }
    for (VideoAnimation video : currentVideo) {
      video.pause();
    }
    SlidesPane.out.println("Pause");
    playing = false;
  }

  /**
   * Start the playing of the audio (if is paused)
   */
  public void play() {
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
  // </editor-fold>

  private SlidesPane slidesPane = null;

  public void setSlidesPane(SlidesPane pane) {
    slidesPane = pane;
  }

  public boolean isPlaying() {
    return (playing && audioMode);
  }

  boolean getAudioMode() {
    return audioMode;
  }

  public void setAudioMode(boolean newAudioMode) {
    if (audioPlayer == null) {
      newAudioMode = false;
    }
    if (audioMode == newAudioMode) {
      return;
    }
    if (audioMode && audioPlayer != null) {
      pause();
    }
    if (status != Status.EMPTY) {
      toBeginning();
    }
    audioMode = newAudioMode;
  }

  /**
   * Returns the subobject with the specified name. The default is to return the
   * public field with that name, and null if there isn't such (or if there is
   * and the field itself is null)
   *
   * @param name
   * @return the given subobject with the specified name
   */
  public Object getSubObject(String name) {
    try {
      Field field = this.getClass().getField(name);
      if (Modifier.isPublic(field.getModifiers())) {
        return field.get(this);
      }
    } catch (NoSuchFieldException | SecurityException | IllegalAccessException ex) {
      Logger.getLogger(AnimationNode.class
              .getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  /**
   * Returns a list of the possible subobjects of this class and null if there
   * aren't any. An implementing class should override this method. NOTE: static
   * methods cannot really be override, only hidden. So when calling this method
   * make sure to do it with the subclass, and not the superclass. A default
   * implementation is given by getSubObject(Class) returning all the
   * AnimationNode and Node public fields in the class.
   *
   * @return
   */
  public static List<ObjectData> getSubobjects() {
    return null;
//        Class<?> cls = MethodHandles.lookup().lookupClass();
//        return AnimationNode.getSubobjects(cls);
  }

  /**
   * return list of object data for all AnimationNode and Node public fields in
   * the specified class. If a field is both an AnimationNode and a Node, that
   * it is added once only as an Animation node
   *
   *
   * @param cls
   * @return
   */
  public static List<ObjectData> getSubobjects(Class cls) {
    Field[] fields = cls.getFields();
    List<ObjectData> list = Arrays.stream(fields)
            .filter(field -> Modifier.isPublic(field.getModifiers()))//only public
            .map(field -> new SimpleObjectData(field.getName(), "general", field.getType())) //map to ObjectData
            //                .filter(data
            //                        -> ( AnimationNode.class.isAssignableFrom(data.getObjectClass()) //leave only AnimationNode and Node objects
            //                || Node.class.isAssignableFrom(data.getObjectClass())))
            .collect(Collectors.toList());

    Stream<Field> stream = Arrays.stream(fields);
    Stream<Field> filter = stream.filter(field -> Modifier.isPublic(field.getModifiers()));
    Stream<SimpleObjectData> map = filter.map(field -> new SimpleObjectData(field.getName(), "general", field.getType()));
    Object[] arr = map.toArray();
    return list;
  }

  @Override
  public Object getSubobject(String name) {
    try {
      Field field = this.getClass().getField(name);
      return field.get(this);
    } catch (NoSuchFieldException | SecurityException
            | IllegalArgumentException | IllegalAccessException ex) {
      Logger.getLogger(Slide.class
              .getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  /**
   * Returns a list of possible animation methods and null if there aren't any.
   * An implementing class should override this method. NOTE: static methods
   * cannot really be override, only hidden. So when calling this method make
   * sure to do it with the subclass, and not the superclass. A default
   * implementation is given by getAnimationList(Class) returning all the
   * methods which has Animation (or subclass) as a return type;
   *
   * @return
   */
  public static List<Method> getAnimationList() {
    return null;
  }

  public static List<Method> getAnimationList(Class cls) {
    Method[] methods = cls.getDeclaredMethods();
    Stream<Method> stream = Arrays.stream(methods);
    List<Method> list = stream.filter(obj -> {
      Method method = (Method) obj;
      Class<?> returnType = method.getReturnType();
      return (Animation.class
              .isAssignableFrom(returnType));
    }).collect(Collectors.toList());
    return list;
  }

  private void createFeature() {
    System.out.println("This is a new feature");
  }

  // ------------------------- param node --------------------------------
  List<ParamNode> paramNodes;
  protected String slideName = "SLIDE";

  /**
   * For every public node, create a parameter with translate x and y
   */
  public void createParamNodes() {
    slideName = getClass().getSimpleName();
    paramNodes = new ArrayList<>();
    try {
      Field[] fields = this.getClass().getFields();
      for (Field field : fields) {
        if (ParamNode.class.isAssignableFrom(field.getType())) {
          ParamNode pNode = (ParamNode) field.get(this);
          if (pNode != null) {
            paramNodes.add(pNode);
          }
          continue;
        }
        if (!Node.class.isAssignableFrom(field.getType())) {
          continue;
        }
        Node node = (Node) field.get(this);
        if (node != null) {
          ParamNode pNode = new ParamNode(node, field.getName());
          pNode.addLocationParameters();
          paramNodes.add(pNode);
        }
      }
    } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
      Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
    }
    createDefaults();
    loadParamNodes();
  }

  public void createDefaults() {
    if (paramNodes == null) {
      return;
    }
    Parameter param = getParameter().search(slideName, true);
    for (ParamNode pNode : paramNodes) {
      pNode.createDefaults(param);
    }
  }

  public void loadParamNodes() {
    if (paramNodes == null) {
      return;
    }
    Parameter param = getParameter().search(slideName, true);
    for (ParamNode pNode : paramNodes) {
      pNode.loadFrom(param);
    }
  }

  public void saveParamNodes() {
    if (paramNodes == null) {
      return;
    }
    Parameter param = getParameter().search(slideName, true);
    for (ParamNode pNode : paramNodes) {
      pNode.saveTo(param);
    }
  }

  //  Old code. Keep here for just in case.....
  //------------------------------------------------------------------
  // <editor-fold defaultstate="collapsed" desc="Video">
  /* public void printVideoTimes() {
        SlidesPane.out.println("-----------------T-------------------");
        double d = 0;
        if (videoPlayer != null) {
            d = videoPlayer.getCurrentTime().toMillis();
            SlidesPane.out.println("Old Video Time " + d);
        }
        if (currentVideo != null) {
            d -= currentVideo.getTime();
            SlidesPane.out.println("New Video Time " + currentVideo.getTime());
        }
        SlidesPane.out.println("Difference is " + d);
    }

    protected MediaView videoViewer;
    protected MediaPlayer videoPlayer;

    protected void setVideo(String url, int startMillis) {
        if (url == null) {
            return;
        }
        URL resource = getClass().getResource(url);
        Media videoMedia = new Media(resource.toExternalForm());

        addVideo(videoMedia, 3, startMillis);
    }

    protected void addVideo(Media media, int rate) {
        addVideo(media, rate, 0);

    }

    protected void addVideo(Media media, int rate, int startMillis) {
        if (media == null) {
            return;
        }
        videoPlayer = new MediaPlayer(media);
        videoViewer = new MediaView(videoPlayer);

        mainPane.getChildren().add(videoViewer);
        videoViewer.setFitHeight(sqSize * 20);
        videoViewer.setFitWidth(sqSize * 35);
        videoViewer.setPreserveRatio(true);
        //videoPlayer.setRate(rate);
        //videoPlayer.setStartTime(Duration.millis(startMillis));

    }
   */

 /*public void playVideoPart(int startMillis, int endMillis, double timeMillis) {
        if (startMillis >= endMillis) {
            return;
        }
        videoPlayer.setStopTime(Duration.millis(endMillis));
        videoPlayer.seek(Duration.millis(startMillis));
        SlidesPane.out.println("seeking time " + startMillis + " ending time " + endMillis);
        videoPlayer.setRate((endMillis - startMillis) / (double) (timeMillis));
        SlidesPane.out.println("rate is " + videoPlayer.getRate());
        SlidesPane.out.println("Time is " + audioPlayer.getCurrentTime());

        videoPlayer.play();
        //printTime();
    }

    public void runVideoPiece(String name) {
        if (videoPlayer == null) {
            return;
        }
        double time = getAnimationMaxTime(name);
        int index = markers.getMarkerIndex(name);
        if (index == -1) {
            return;
        }
        MarkerSet current = markers.getMarkerByIndex(index);
        if (time != -1) {
            playVideoPart(current.videoMarkerStart, current.videoMarkerEnd, time);
        }

    }*/
  /**
   * Return the maximal possible time of the animation for the marker by the
   * given name. If the marker has start and end, just returns the difference.
   * Otherwise, returns the difference from the start of this marker to the
   * start of the next.
   *
   * If there is no marker by the given name, or there is no end parameter to
   * this marker and there is no next marker, return -1.
   *
   *
   * @param markerName the name of the current marker
   * @return maximal possible time of the animation. -1 if can't compute the
   * time.
   */
//    void setVideoRate(int rate) {
//        if (videoPlayer != null) {
//            videoPlayer.setRate(rate);
//        }
//    }
  // </editor-fold>
}
