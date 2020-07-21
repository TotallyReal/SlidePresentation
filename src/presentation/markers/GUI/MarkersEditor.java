/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.markers.GUI;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import presentation.markers.MarkerSet;
import presentation.markers.MarkerSetInterface;
import presentation.markers.Markers;
import presentation.slides.Slide;
import presentation.slides.SlidesPane;

public class MarkersEditor extends Pane implements Initializable {

  Markers markers;
  SlidesPane parent;

  GridPane grid;

  Pane markersButtons;
  //Parent timeNavigation;

  private int row;

  public MarkersEditor(SlidesPane pane) {

    parent = pane;

    setOnKeyPressed(parent::keyPressed);

    initGrid();
    //createMarkersInterfaceComponents();

    // timePane = new TimeNavigation(pane);
    ScrollPane scrollPane = new ScrollPane(grid);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    markers = null;
    scrollPane.setMaxHeight(500);
    VBox vbox = new VBox(scrollPane);//, markersButtons);//, timePane);
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TimeNavigation.fxml"));
      VBox box = new VBox();
      fxmlLoader.setRoot(box);
      fxmlLoader.setController(this);//new FXMLTimeNavigationController());
      //timeNavigation = FXMLLoader.load(getClass().getResource("FXMLTimeNavigation.fxml"));
      //timeNavigation = 
      fxmlLoader.load();
      vbox.getChildren().add(box);//timeNavigation);
    } catch (IOException ex) {
      Logger.getLogger(MarkersEditor.class.getName()).log(Level.SEVERE, null, ex);
    }

    super.getChildren().add(vbox);
    // this.setScene(new Scene(scrollPane, 400, 350));
  }

  private Slide slide;

  public void setSlide(Slide slide) {
    if (slide == null) {
      return;
    }
    this.slide = slide;
    this.markers = slide.getMarkers();
    createMarkersList();
    audioEnd.setText(slide.getAudioEnd() + "");
    audioStart.setText(slide.getAudioStart() + "");
  }

  private void keyPressed(KeyEvent event) {
    parent.keyPressed(event);
    event.consume();
  }

  @FXML
  private Label rateLabel;
  @FXML
  private Slider rateSlider;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setOnlyNumbers(audioStart);
    setOnlyNumbers(audioEnd);

    audioStart.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
      if (!newPropertyValue) {
        parent.setAudioTimes(Integer.parseInt(audioStart.getText()), Integer.parseInt(audioEnd.getText()));
      }
    });

    audioEnd.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
      if (!newPropertyValue) {
        parent.setAudioTimes(Integer.parseInt(audioStart.getText()), Integer.parseInt(audioEnd.getText()));
      }
    });
  }

  @FXML
  private void dropSlider() {
    double d = rateSlider.getValue();
    rateLabel.setText("Rate: " + d);
    parent.setRate(d);
  }

  //---------------------------- markers grid --------------------------------
  private void initGrid() {
    grid = new GridPane();

    ColumnConstraints col0 = new ColumnConstraints();
    ColumnConstraints col1 = new ColumnConstraints();
    ColumnConstraints col2 = new ColumnConstraints();
    ColumnConstraints col3 = new ColumnConstraints();
    col1.setPrefWidth(60);
    col2.setPrefWidth(60);
    col3.setHalignment(HPos.CENTER);
    grid.getColumnConstraints().addAll(col0, col1, col2, col3);
  }

  public void setMarkers(Markers markers) {
    //parentSlide = slide;
    this.markers = markers;
    createMarkersList();
  }

  private void createMarkersList() {

    grid.getChildren().clear();
    createMarkersHeaders();

    for (MarkerSetInterface set : markers.getMarkersList()) {
      addMarkerLine(set);
    }

  }

  private void createMarkersHeaders() {
    grid.add(new Text("name"), 0, 0, 1, 2);
    VBox audioBox = new VBox(new Text("Audio"));
    audioBox.setAlignment(Pos.CENTER);
    grid.add(audioBox, 1, 0, 2, 1);
    grid.add(new Text("Start"), 1, 1);
    grid.add(new Text("End"), 2, 1);
    grid.add(new Text("Compressed"), 3, 1);

    row = 2;
  }

  private void addMarkerLine(MarkerSetInterface set) {
    MarkerLine mLine = new MarkerLine(set);
    grid.addRow(row, mLine.markerName, mLine.startAudio, mLine.endAudio, mLine.box);

    //create a single context menu for all items?
    ContextMenu contextMenu = new ContextMenu();

    MenuItem setTime = new MenuItem("Set current time");
    contextMenu.getItems().add(setTime);
    mLine.startAudio.setContextMenu(contextMenu);

    setTime.setOnAction(evt -> {
      SlidesPane.out.println("" + evt.getSource());
      int millis = parent.getTime();
      millis = 100 * (int) (1 + millis / 100);
      mLine.startAudio.setText(millis + "");
    });

    set.statusProperty().addListener(evt -> {
      Animation.Status status = set.statusProperty().get();

      switch (status) {
        case STOPPED:
          mLine.markerName.setFill(Color.BLACK);
          break;
        case PAUSED:
          mLine.markerName.setFill(Color.RED);
          break;
        case RUNNING:
          mLine.markerName.setFill(Color.GREEN);
          break;
      }

    });
    row++;
  }

  private class MarkerLine {

    Text markerName;
    TextField startAudio, endAudio;
    CheckBox box;
    MarkerSetInterface marker;

    MarkerLine(MarkerSetInterface marker) {
      this.marker = marker;
      markerName = new Text(marker.getMarkerName());

      //---------------------------- startAudio --------------------------
      startAudio = new TextField(marker.getMarkerStart() + "");
      setOnlyNumbers(startAudio);

      startAudio.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
        if (!newPropertyValue) {
          marker.setMarkerStart(getNumber(startAudio));
          //markers.updateMarker(marker);
          //sortMarkers();
        }
      });

      //---------------------------- endAudio --------------------------
      endAudio = new TextField(marker.getMarkerEnd()/*audioMarkerEnd*/ + "");
      endAudio.setOnAction(evt -> marker.setMarkerEnd(getNumber(endAudio)));//marker.audioMarkerEnd = getNumber(endAudio));
      setOnlyNumbers(endAudio);

      endAudio.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
        if (!newPropertyValue) {
          marker.setMarkerEnd(getNumber(endAudio));
        }
      });

      //-------------------------- compressed --------------------------
      box = new CheckBox();
      box.setSelected(marker.isCompressed());
      box.setOnAction(evt -> {
        marker.setCompressed(box.isSelected());
      });
    }

    private int getNumber(TextField field) {
      String text = field.getText();
      return Integer.parseInt(text);
    }

  }

  public static void setOnlyNumbers(TextField field) {
    field.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d*")) {
        field.setText(newValue.replaceAll("[^\\d]", ""));
      }
    });
  }

  // ----------------------------  marker buttons -----------------------------
  @FXML
  private void openAddMarkerDialog() {
    if (markers != null) {
      List<String> possibleNames = markers.getPossibleNames();
      if (possibleNames.isEmpty()) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("New Marker");
        alert.setHeaderText(null);
        alert.setContentText("There are no possible name for new markers");

        alert.showAndWait();
      } else {

        ChoiceDialog<String> dialogCombo = new ChoiceDialog<>(possibleNames.get(0), possibleNames);
        dialogCombo.setTitle("New Marker");
        dialogCombo.setHeaderText("Please insert new marker's name");
        dialogCombo.setContentText("New Name:");

        Optional<String> result = dialogCombo.showAndWait();
        if (result.isPresent()) {
          MarkerSet set = markers.createNewMarker(result.get());
          if (set == null) {
            SlidesPane.out.println("Already has this marker name");
          } else {
            addMarkerLine(set);
          }
        }
      }
    }
  }

  @FXML
  private void removeEmptyMarkers() {
    if (markers != null) {
      markers.removeEmptyMarkers();
      setMarkers(markers);
    }
  }

  @FXML
  private void saveMarkers() {
    if (markers != null) {
      markers.saveMarkers();
    }
    if (slide != null) {
      slide.saveSlide();
    }
  }

  @FXML
  private void reloadMarkers() {
    if (markers != null) {
      markers.reloadMarkers();
    }
  }

  @FXML
  private void sortMarkers() {
    createMarkersList();
    SlidesPane.out.println("Implement sortMarkers()");
  }

  // ---------------------------  time navigation  -----------------------------
  @FXML
  public void nextAnimation() {
    parent.nextAnimation();
  }

  @FXML
  public void nextSlide() {
    parent.nextSlide();
  }

  @FXML
  public void previousAnimation() {
    parent.previousAnimation();
  }

  @FXML
  public void previousSlide() {
    parent.previousSlide();
  }

  @FXML
  ToggleButton playPauseB;

  @FXML
  public void playPauseClicked() {
    boolean wasPaused = playPauseB.isSelected();
    if (wasPaused) {
      parent.play();
    } else {
      parent.pause();
    }
    //parent.setAutoSlide(autoSlide.isSelected());
  }

  @FXML
  ToggleButton clickAudioB;

  @FXML
  private void clickAudioPressed() {
    parent.setAudioMode(!clickAudioB.isSelected());
  }

  public void setAudioMode(boolean mode) {
    clickAudioB.setSelected(!mode);
  }

  @FXML
  public void reloadTrack() {
    parent.reloadTrack();
  }

  // --------------------- text fields ------------------------------
  @FXML
  private TextField timeField, jumpToField, audioStart, audioEnd;

  @FXML
  private void writeTime() {
    timeField.setText("" + parent.getTime());
  }

  @FXML
  private void jumpToTime() {
    String text = jumpToField.getText();
    try {
      int time = Integer.parseInt(text);
      if (time >= 0) {
        parent.jumpToTime(time);
      }
    } catch (NumberFormatException ex) {
      SlidesPane.out.println("jumpTo is not a number");
    }
  }

  //------------------ OLD -------------------------------
  /*
    private void createMarkersInterfaceComponents() {
        //create Markers list
        createMarkersHeaders();

        addB = new Button("Add Marker");
        addB.setOnAction(evt -> openAddMarkerDialog());
        removeEmptyB = new Button("Remove Empty");
        removeEmptyB.setOnAction(evt -> removeEmptyMarkers());
        editAnimB = new Button("Edit Animation");
        editAnimB.setDisable(true);

        saveB = new Button("Save");
        reloadB = new Button("Reload");
        sortB = new Button("Sort");
        saveB.setOnAction(evt -> {
            if (markers != null) {
                markers.saveMarkers();
            }
        });
        reloadB.setOnAction(evt -> {
            if (markers != null) {
                markers.reloadMarkers();
            }
        });

        sortB.setOnAction(evt -> {
            sortMarkers();
        });

        markersButtons = new VBox(
                new HBox(addB, removeEmptyB, editAnimB),
                new HBox(saveB, reloadB, sortB)
        );
    }
   */
}
