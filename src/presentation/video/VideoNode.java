/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.video;

import java.net.URL;
import javafx.geometry.Rectangle2D;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 *
 * @author eofir
 */
public class VideoNode extends MediaView {

    MediaPlayer player = null;
    double rate = 1;

    public VideoNode() {
        this.setPreserveRatio(true);
    }

    /**
     *
     * @param url = /packagename/...
     */
    public VideoNode(String url) {
        URL resource = getClass().getResource(url);
        Media media = new Media(resource.toExternalForm());
        player = new MediaPlayer(media);
        this.setMediaPlayer(player);

    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public VideoAnimation createVideoAnimation(int startMillis, int endMillis) {
        return new VideoAnimation(this, player, startMillis, endMillis);
    }

    public VideoTransition createVideoTransition(int start, int end) {
        return VideoNode.this.createVideoTransition(start, end, 1);
    }

    public VideoTransition createVideoTransition(int start, int end, double rate) {
        return new VideoTransition(this, player, start, end);

    }

    public static final double SQ_SIZE = 32.5;//35*16d/17;

    /**
     * Set the viewport of the video in the coordinates of the squares from
     * notability
     *
     * @param minX
     * @param minY
     * @param width
     * @param height
     */
    public void setViewportInSquares(double minX, double minY, double width, double height) {
        this.setViewport(new Rectangle2D(SQ_SIZE * minX, SQ_SIZE * minY, SQ_SIZE * width, SQ_SIZE * height));
    }

}
