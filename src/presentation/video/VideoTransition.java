/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.video;

import javafx.animation.Transition;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import presentation.slides.SlidesPane;

/**
 *
 * @author eofir
 */
public class VideoTransition extends Transition {

    private MediaPlayer player;
    int start, end;
    double rate;
    VideoNode node;
    private boolean played;

    public VideoTransition(VideoNode node, MediaPlayer player, int start, int end) {
        this.node = node;
        this.player = player;
        this.start = start;
        this.end = end;
        played = false;
        this.setCycleDuration(Duration.millis(end - start));
        rate = 1;
        this.statusProperty().addListener(evt -> {
            Status status = this.getStatus();
            switch (status) {
                case PAUSED:
                    //player.pause();
                    break;
                case RUNNING:
                    if (!played) {
                        played = true;
                        player.setStopTime(Duration.millis(end));
                        player.seek(Duration.millis(start));
                    }
                    player.play();
                    break;
                case STOPPED:
                    //player.pause();
                    played = false;
                    break;
            }
        });
        player.rateProperty().bind(this.rateProperty());
        player.rateProperty().addListener(evt -> {
            SlidesPane.out.println("RATE = " + player.getRate());
        });
    }

    /**
     * Set the start time of this video.
     *
     * Make sure the start time is always before the stop time. If you change
     * both of these parameters, change the stop before the start.
     *
     * @param start
     */
    public void setStart(int start) {
        this.start = start;
        setCycleDuration(Duration.millis(end - start));
    }

    /**
     * Set the stop time of this video.
     *
     * Make sure the start time is always before the stop time. If you change
     * both of these parameters, change the stop before the start.
     *
     * @param end
     */
    public void setEnd(int end) {
        this.end = end;
        setCycleDuration(Duration.millis(end - start));
    }

    @Override
    public void jumpTo(Duration duration) {
        player.seek(duration.add(Duration.millis(start)));
        SlidesPane.out.println("Jump to with video transition");
    }

    @Override
    protected void interpolate(double frac) {
        SlidesPane.out.println("seeking "+(start*(1-frac)+end*frac));
        player.seek(Duration.millis(start*(1-frac)+end*frac));

    }

}
