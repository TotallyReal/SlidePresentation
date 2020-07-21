package presentation.slides;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import presentation.DragPane;
import presentation.Parameter.Parameter;
import presentation.markers.GUI.MarkersEditor;

public class SlidesPane extends Pane {

  public static PrintStream out;
  static {
    setPrintStream(null);
  }
  
  public static void setPrintStream(PrintStream stream){
    if (stream==null){      
      out = new PrintStream(new OutputStream(){
        @Override
        public void write(int b) throws IOException {
        }
      });
    } else {
      out = stream;
    }
  }
  
  private Slide slides[];
  private boolean autoSlide = true;
  private boolean finishedSlide = true;
  private int slideNum;
  private double rate;
  //private final BorderPane border;
  private final Pane slides_pane;

  private final MarkersEditor markersStage;

  public SlidesPane() {

    setOnKeyPressed(this::keyPressed);
    setOnMousePressed(this::mousePressed);
    rate = 1;
    markersStage = new MarkersEditor(this);
    createMarkersStage();
    slides_pane = new Pane();
    BorderPane.setAlignment(slides_pane, Pos.CENTER);

    super.getChildren().add(slides_pane);
  }

  private ResourceDir resourceDir = null;
  private File imageDir = null;
  private Parameter C;
  private File paramFile;

  public ResourceDir getResourceDir() {
    return resourceDir;
  }

  public SlidesPane(File dir) {
    resourceDir = new ResourceDir(dir);

    setOnKeyPressed(this::keyPressed);
    setOnMousePressed(this::mousePressed);
    rate = 1;
    markersStage = new MarkersEditor(this);
    createMarkersStage();
    slides_pane = new Pane();
    BorderPane.setAlignment(slides_pane, Pos.CENTER);

    super.getChildren().add(slides_pane);
  }
  
  public Parameter getParameter(){
    return C;
  }

  public void saveParameter() {
    try (FileWriter writer = new FileWriter(paramFile)) {
      SlidesPane.out.println("Saving the parameters...");
      writer.write(C.getRepString());
      SlidesPane.out.println("Done");
    } catch (IOException ex) {
      Logger.getLogger(SlidesPane.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void saveParameter(File file) {
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(C.getRepString());
    } catch (IOException ex) {
      Logger.getLogger(SlidesPane.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void loadParameter(File file) {
    if (file == null || !file.toString().endsWith(".param")) {
      return;
    }
    paramFile = file;
    C = Parameter.createRootParameter();
    if (!file.exists()) {
      saveParameter();
    } else {
      try {
        String str = new String(Files.readAllBytes(Paths.get(paramFile.getPath())));
        Parameter param = Parameter.loadFromRepString(str);
        if (param != null) {
          C = param;
        }
      } catch (FileNotFoundException ex) {
        Logger.getLogger(SlidesPane.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(SlidesPane.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public void setImageDirectory(File dir) {
    if (dir != null && dir.exists() && dir.isDirectory()) {
      imageDir = dir;
    }
  }

  public Image getImage(String name) {
    if (imageDir != null) {
      return new Image(new File(imageDir, name).toURI().toString());
    }
    return null;
  }

  public File getImageLib() {
    return new File(imageDir.toString());
  }

  private void createMarkersStage() {
    //border.setRight(markersStage);
    Stage stage = new Stage();
    stage.setWidth(markersStage.getWidth());
    markersStage.setPrefHeight(700);
    stage.setScene(new Scene(markersStage));
    stage.show();
  }

  /**
   * Set the slides to show according to their order.
   *
   * @param slides
   */
  public void setSlides(Slide[] slides) {
    this.slides = slides;
    slides_pane.getChildren().clear();
    ObservableList<Node> kids = slides_pane.getChildren();
    DragPane pane;
    for (Slide slide : slides) {
      pane = slide.getPane();
      slide.setSlidesPane(this);
      slide.loadSlide();
      kids.add(pane);
      AnchorPane.setBottomAnchor(pane, 0.0);
      AnchorPane.setTopAnchor(pane, 0.0);
      AnchorPane.setLeftAnchor(pane, 0.0);
      AnchorPane.setRightAnchor(pane, 0.0);
      pane.setVisible(false);
      //slide.restartVisuals();
    }
    slideNum = -1;
  }

  Bounds getSlidesPaneDimension() {
    return slides_pane.getBoundsInLocal();
  }

  public void preLoadVisuals() {
    if (slides != null) {
      for (Slide slide : slides) {
        SlidesPane.out.println("loading visuals for " + slide);
        slide.restartVisuals();
      }
    }
  }

  /**
   * Key shortcuts -,+,* : normal speed, fast very fast.
   *
   * Right: next animation
   *
   * Enter: next slide,
   *
   * Left: beginning of slide,
   *
   * Escape: previous slide
   *
   * P: pause\play
   *
   * T: print time
   *
   * F10: view\hide interface
   *
   * @param event
   */
  public void keyPressed(KeyEvent event) {

    if (slides == null) {
      SlidesPane.out.println("No slides");
      return;
    }

    KeyCode code = event.getCode();

    SlidesPane.out.println("You pressed the key (" + event.getCode().getName() + ")");
    if (code == KeyCode.F12) {
      setRate(32);
    }
    if (code == KeyCode.MULTIPLY) {
      setRate(12);
    }
    if (code == KeyCode.ADD) {
      setRate(2);
    }
    if (code == KeyCode.SUBTRACT) {
      setRate(1);
    }
    if (code == KeyCode.RIGHT || code == KeyCode.PAGE_DOWN) {
      nextAnimation();
    }
    if (code == KeyCode.LEFT || code == KeyCode.PAGE_UP) {
      previousAnimation();
    }
    if (code == KeyCode.B) {
      SlidesPane.out.println("\n--------------------------\n");
      SlidesPane.out.println("Restarting slide " + slideNum + " ("
              + slides[slideNum].getClass().toString() + ")");
      finishedSlide = false;
      slides[slideNum].toBeginning();
    }
    if (code == KeyCode.ENTER) {
      nextSlide();
    }
    if (code == KeyCode.BACK_SPACE) {
      previousSlide();
    }
    if (code == KeyCode.ESCAPE) {
      ((Stage) markersStage.getScene().getWindow()).close();
      Stage stage = (Stage) getScene().getWindow();
      // do what you have to do
      stage.close();
    }
    if (code == KeyCode.P) {
      slides[slideNum].play_pause();
    }
    if (code == KeyCode.T) {
      SlidesPane.out.println("current time is " + slides[slideNum].getTime());
    }
    if (code == KeyCode.F10) {
      Window window = this.getScene().getWindow();
      Stage stage = (Stage) window;
      boolean full = stage.isFullScreen();
      markersStage.setVisible(full);
      stage.setFullScreen(!full);
//            if (markersStage.isShowing()) {
//                markersStage.hide();
//            } else {
//                markersStage.show();
//            }
    }

    event.consume();
  }

  /**
   * When mouse is pressed, print out the (x,y) coordinates of the mouse.
   *
   * @param evt
   */
  private void mousePressed(MouseEvent evt) {
    SlidesPane.out.println("mouse!");
    if (evt.getButton() == MouseButton.SECONDARY) {
      SlidesPane.out.println("mouse : X=" + evt.getX() + ", and Y=" + evt.getY());
    }
    evt.consume();
  }

  //--------------------------------------------------------------------
  // <editor-fold defaultstate="collapsed" desc="Navigation functions">
  public void nextAnimation() {
    if (finishedSlide) {
      nextSlide();
    } else {
      finishedSlide = !slides[slideNum].nextAnimation();
    }
  }

  public void previousAnimation() {
    Slide slide = getCurrentSlide();
    if (slide == null) {
      return;
    }
    boolean hasPrevious = slide.previousAnimation();
    if (!hasPrevious && slideNum > 0) {
      previousSlide();
      getCurrentSlide().toEnd();
    } else {
      finishedSlide = false;
    }
    SlidesPane.out.println("Need to implement previous animation method");
  }

  /**
   * Go to the next slide.
   */
  public void nextSlide() {
    closeSlide();
    if (slideNum < slides.length - 1) {
      startSlide(slideNum + 1);
    } else {
      SlidesPane.out.println("No more slides.");
      //markersStage.close();
      Stage stage = (Stage) getScene().getWindow();
      saveParameter();
      stage.close();
    }
  }

  /**
   * Jump back to previous slide. If this is the first slide, then restart it.
   */
  public void previousSlide() {
    closeSlide();
    startSlide(slideNum > 0 ? slideNum - 1 : 0);
  }

  /**
   * Closes the current slide.
   */
  private void closeSlide() {
    Slide slide = getCurrentSlide();
    if (slide != null) {
      SlidesPane.out.println("\n--------------------------\n");
      SlidesPane.out.println("Closing Slide #" + slideNum + " ("
              + slides[slideNum].getClass().toString() + ")");
      slides[slideNum].closeSlide();
    }
  }

  /**
   * Start the slide at the given index.
   *
   * @param index
   */
  private void startSlide(int index) {
    if (index < 0 || index >= slides.length) {
      return;
    }
    slideNum = index;
    SlidesPane.out.println("\n--------------------------\n");
    SlidesPane.out.println("Started Slide #" + slideNum + " ("
            + slides[slideNum].getClass().getSimpleName() + ")");
    slides[slideNum].setRate(getRate());
    markersStage.setSlide(slides[slideNum]);
    slides[slideNum].setAudioMode(getAudioMode());
    slides[slideNum].startSlide();
    finishedSlide = (slides[slideNum].markers.size() == 0);
  }

  /**
   *
   * @return the current viewing slide
   */
  public Slide getCurrentSlide() {
    if (slides == null) {
      return null;
    }
    if (-1 < slideNum && slideNum < slides.length) {
      return slides[slideNum];
    }
    return null;
  }

  //leave this function or not?
  /**
   * Called by the Slide when it is done playing.
   *
   * @param slide
   */
  void finishedSlide(Slide slide) {
    if (getCurrentSlide() != slide) {
      return;
    }
    if (!slide.getAudioMode() || autoSlide) { //go to the next slide
      nextSlide();
    }
  }
  // </editor-fold>

  //--------------------------------------------------------------------
  // <editor-fold defaultstate="collapsed" desc="Audio Time navigation">
  public void setAutoSlide(boolean auto) {
    autoSlide = auto;
  }

  public void jumpMillis(double millis) {
    Slide slide = getCurrentSlide();
    if (slide != null) {
      slide.jumpMillis(millis);
    }
  }

  public void jumpToTime(double time) {
    Slide slide = getCurrentSlide();
    SlidesPane.out.println("Jumping to time " + time);
    if (slide != null) {
      slide.jumpToTime(time);
    }
  }

  /**
   * Return the time in millis from the start of the audio file of the current
   * slide (not the start of the playing). If there is no slide, returns -1
   *
   * @return time from start in millis
   */
  public int getTime() {
    Slide slide = getCurrentSlide();
    if (slide != null) {
      return slide.getTime();
    }
    return -1;
  }

  public void setRate(double rate) {
    if (rate > 0) {
      this.rate = rate;
      Slide slide = getCurrentSlide();
      if (slide != null) {
        slide.setRate(rate);
      }
    }
    if (rate == 0) {
      pause();
    }
  }

  public double getRate() {
    return rate;
  }

  public boolean isPlaying() {
    Slide slide = getCurrentSlide();
    if (slide == null) {
      return false;
    } else {
      return slide.isPlaying();
    }
  }

  public void pause() {
    if (isPlaying()) {
      Slide slide = getCurrentSlide();
      if (slide != null) {
        slide.pause();
      }
    }
  }

  public void play() {
    if (!isPlaying()) {
      Slide slide = getCurrentSlide();
      if (slide != null) {
        slide.play();
      }
    }
  }

  boolean audioMode = false;

  public void setAudioMode(boolean audioMode) {
    if (this.audioMode != audioMode) {
      this.audioMode = audioMode;

      Slide slide = getCurrentSlide();
      if (slide != null) {
        slide.setAudioMode(audioMode);
      }

    }
  }

  public boolean getAudioMode() {
    return audioMode;
//        Slide slide = getCurrentSlide();
//        if (slide != null) {
//            return slide.getAudioMode();
//        }
//        return false;
  }

  // </editor-fold>
  public void reloadTrack() {
    Slide slide = getCurrentSlide();
    if (slide != null) {
      slide.loadSlide();
    }
  }

  public void setAudioTimes(int audioStart, int audioEnd) {
    Slide slide = getCurrentSlide();
    if (slide != null) {
      slide.setAudioStart(audioStart);
      slide.setAudioEnd(audioEnd);
    }

  }
}
