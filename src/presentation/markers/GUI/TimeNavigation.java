/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.markers.GUI;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import presentation.slides.SlidesPane;

/**
 *
 * @author eofir
 */
public class TimeNavigation extends Pane implements Initializable {

    Button timeB, twoBackB, twoForwardB;
    Button prevAnimB, nextAnimB, prevSlideB, nextSlideB;
    Button jumpToB;
    TextField timeField, jumpTo;
    ToggleButton autoSlide, playPauseB, audioModeB;
    ComboBox rate;
    Pane timeButtons;
    SlidesPane parent;

    public TimeNavigation(SlidesPane pane) {

        parent = pane;

        createTimeControlsComponents();

    }

    private static final boolean RUN_AUDIO = true;

    private ImageView icon(String name){
        return new ImageView("presentation/markers/GUI/icons/"+name);
    }
    
    private void createTimeControlsComponents() {

        nextSlideB = new Button();
        nextSlideB.setGraphic(icon("next_skip.png"));//new ImageView("presentation/markers/GUI/icons/next_skip.png"));
        prevSlideB = new Button();
        prevSlideB.setGraphic(new ImageView("presentation/markers/GUI/icons/prev_skip.png"));
        nextAnimB = new Button();
        nextAnimB.setGraphic(new ImageView("presentation/markers/GUI/icons/next.png"));
        prevAnimB = new Button();
        prevAnimB.setGraphic(new ImageView("presentation/markers/GUI/icons/prev.png"));

        playPauseB = new ToggleButton("Pause");
        playPauseB = new ToggleButtonPicture(
                new ImageView("presentation/markers/GUI/icons/play.png"),
                new ImageView("presentation/markers/GUI/icons/pause.png")
        );
        playPauseB.setSelected(true);
        twoBackB = new Button("-2");
        twoBackB.setPrefHeight(40);
        twoBackB.setPrefWidth(40);
        twoForwardB = new Button("+2");
        twoForwardB.setPrefHeight(40);
        twoForwardB.setPrefWidth(40);
        //audioModeB = new ToggleButton("Audio Mode");
        audioModeB = new ToggleButtonPicture(
                new ImageView("presentation/markers/GUI/icons/mouse.png"),
                new ImageView("presentation/markers/GUI/icons/sound.png")
        );
        audioModeB.setSelected(true);

        timeB = new Button("Get time");
        timeField = new TextField("" + 0);
        timeField.setEditable(false);
        jumpToB = new Button("Jump to:");
        jumpTo = new TextField("0");

        rate = new ComboBox();
        rate.getItems().addAll("1", "2", "4");
        rate.setValue("1");

//        updateB = new Button("update");
        autoSlide = new ToggleButton("Automatic Slides");
        autoSlide.setSelected(true);
        autoSlide.requestFocus();

        for (Region node : new Region[]{nextSlideB, prevSlideB, nextAnimB, prevAnimB, playPauseB,
            twoBackB, twoForwardB, audioModeB}) {
            node.setPrefSize(40, 40);
        }

        GridPane timeGrid = new GridPane();

//        timeGrid.addRow(0, prevSlideB, prevAnimB, nextAnimB, nextSlideB);
//        timeGrid.addRow(1, playPauseB, twoBackB, twoForwardB, audioModeB);
//        GridPane.setFillWidth(twoBackB, true);
//        GridPane.setFillWidth(twoForwardB, false);
//        timeGrid.addRow(2, timeB, timeField, jumpToB, jumpTo);
//        timeGrid.addRow(3, new Label("Rate:"), rate);
//        timeGrid.add(autoSlide, 2, 3, 2, 1);
        VBox box = new VBox(
                new HBox(prevSlideB, prevAnimB, twoBackB, playPauseB, twoForwardB, nextAnimB, nextSlideB),
                new HBox(timeB, timeField),
                new HBox(jumpToB, jumpTo),
                new HBox(new Label("Rate:"), rate, autoSlide)
        );

        timeButtons = timeGrid;
        this.getChildren().add(box);

        createButtonActions();        
    }

    private void createButtonActions() {

        timeB.setOnAction(evt -> timeField.setText("" + parent.getTime()));
        nextSlideB.setOnAction(evt -> parent.nextSlide());
        prevSlideB.setOnAction(evt -> parent.previousSlide());
        nextAnimB.setOnAction(evt -> parent.nextAnimation());
        prevAnimB.setOnAction(evt -> parent.previousAnimation());
        twoBackB.setOnAction(evt -> parent.jumpMillis(-2000));
        twoForwardB.setOnAction(evt -> parent.jumpMillis(2000));
        autoSlide.setOnAction(evt -> parent.setAutoSlide(autoSlide.isSelected()));

        playPauseB.setOnAction(evt -> { //change to property
            boolean wasPaused = playPauseB.isSelected();
            if (wasPaused) {
                parent.play();
            } else {
                parent.pause();
            }
            parent.setAutoSlide(autoSlide.isSelected());
        });

        audioModeB.setOnAction(evt -> { //change to property
            boolean audioSel = audioModeB.isSelected();
            //audioModeB.getText().equals("Audio Mode");
            parent.setAudioMode(audioModeB.isSelected());
            parent.setAutoSlide(autoSlide.isSelected());

        });

        rate.setOnAction(evt -> {
            String val = rate.getValue().toString();
            int t = 1;
            if (val.equals("2")) {
                t = 2;
            }
            if (val.equals("4")) {
                t = 4;
            }
            parent.setRate(t);
        });

        jumpToB.setOnAction(evt -> {
            String text = jumpTo.getText();
            try {
                int time = Integer.parseInt(text);
                if (time >= 0) {
                    parent.jumpToTime(time);
                }
            } catch (NumberFormatException ex) {
                SlidesPane.out.println("jumpTo is not a number");
            }
        });
    }

    public void setAudioMode(boolean mode) {
        if (mode) {
            //audioModeB.setText("Audio Mode");
            audioModeB.setSelected(true);
        } else {
            //audioModeB.setText("Click Mode");
            audioModeB.setSelected(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void nextAnimation() {
        parent.nextAnimation();
    }

    public void nextSlide() {
        parent.nextSlide();
    }

    public void previousAnimation() {
        parent.previousAnimation();
    }

    public void previousSlide() {
        parent.previousSlide();
    }

    public void removeMarkersClicked() {
        SlidesPane.out.println("REMOVING");
    }

    public void playPauseClicked() {
        boolean wasPaused = playPauseB.isSelected();
        if (wasPaused) {
            parent.play();
        } else {
            parent.pause();
        }
        parent.setAutoSlide(autoSlide.isSelected());
    }

}

class ToggleButtonPicture extends ToggleButton {

    ImageView unpressed, pressed;

    ToggleButtonPicture(ImageView unpressed, ImageView pressed) {
        this.unpressed = unpressed;
        this.pressed = pressed;
        this.setGraphic(unpressed);
        this.selectedProperty().addListener(evt -> {
            if (this.isSelected()) {
                this.setGraphic(pressed);
            } else {
                this.setGraphic(unpressed);
            }
        });

    }

}
